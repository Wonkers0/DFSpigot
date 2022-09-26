import eventTypes from './eventTypes.js'
import threadCodes from './index.js'

console.log(eventTypes)

var codeEditor;

window.onload = () => {
  codeEditor = CodeMirror.fromTextArea(
    document.querySelector("#code"),
    {
      theme: "dracula",
      lineNumbers: true,
      mode: "text/x-java",
      autoCloseBrackets: true
    }
  )
}

function decodeTemplate(data) { // Permanently borrowed from grog 😊
  const compressData = atob(data)
  const uint = compressData.split('').map(function(e) {
    return e.charCodeAt(0)
  });
  const binData = new Uint8Array(uint)
  const string = pako.inflate(binData, { to: 'string' })
  return JSON.parse(string)
}

var mainFunc, libraries, root, code, specifics, loopID, threadCode
export var threads = [];

export function getRoot(base64){
  try { 
    return decodeTemplate(base64.match(/h4sI(A{5,20})[a-z0-9+_/=]+/i)[0]).blocks[0]
  }
  catch(e){
    console.log(e)
    return null  
  }
}

export function generate() {
  libraries = [
    "me.wonk2.utilities.*",
    "me.wonk2.utilities.enums.*",
    "me.wonk2.utilities.values.*",
    "org.bukkit.boss.BossBar",
    "org.bukkit.command.CommandSender",
    "org.bukkit.command.Command",
    "org.bukkit.command.CommandExecutor",
    "org.bukkit.entity.LivingEntity",
    "org.bukkit.entity.Player", // IfPlayer.invokeAction takes in a Player, not a LivingEntity
    "org.bukkit.Location",
    "org.bukkit.Material", // Event Item, don't remove
    "org.bukkit.inventory.ItemStack", // Event Item, don't remove
    "org.bukkit.event.Listener",
    "org.bukkit.event.EventHandler",
    "org.bukkit.event.block.Action",
    "org.bukkit.plugin.java.JavaPlugin",
    "org.bukkit.scheduler.BukkitRunnable",
    "java.util.*",
    "java.util.concurrent.atomic.AtomicBoolean"
  ]
  code = [
    "public class DFPlugin extends JavaPlugin implements Listener, CommandExecutor",
    "public static HashMap<String, TreeMap<Integer, BossBar>> bossbarHandler = new HashMap<>();",
    "public static Location origin = new Location(null, 0, 0, 0);",
    "public static JavaPlugin plugin;",
    ""
  ]

  for(let threadData of threads){
    let decodedJson = decodeTemplate(threadData.match(/h4sI(A{5,20})[a-z0-9+_/=]+/i)[0])
    console.log(decodedJson)

    root = decodedJson.blocks[0];
  

    
    let isEvent = (root.block == "event" || root.block == "entity_event")
    let rootEvent = "null"
    let specificsDefaults = 
    {
      "targets":{
        "default": "event.getPlayer()"
      },
      "item": "new ItemStack(Material.AIR)",
      "cancelled": "event.isCancelled()"
    }
    if(isEvent){
      rootEvent = eventTypes[root.action]["name"].split(".")
      rootEvent = rootEvent[rootEvent.length - 1]
      specifics = eventTypes[root.action]["specifics"]
      
      for(let key of Object.keys(specificsDefaults))
        if(specifics[key] == null) specifics[key] = specificsDefaults[key]
    }
    else specifics = specificsDefaults
    
    threadCode = threadCodes(root, rootEvent, specifics)[isEvent ? "event" : root.block]
    switch(root.block){
      case "event":
      case "entity_event":
        mainFunc = threadCode[1][6]; break
      case "func":
        mainFunc = threadCode[0]; break
      case "process":
        mainFunc = threadCode[0][3][1]; break
    }
    console.log(specifics["targets"])
    
    if(isEvent){
      if(eventTypes[root.action]["check"] != null){
        let temp = [`if(${eventTypes[root.action]["check"]})`]
        mainFunc.push(temp)
        mainFunc = temp
      }
      libraries.push(`org.bukkit.event.${eventTypes[root.action]["name"]}`)
    }

    spigotify(decodedJson.blocks)
    if(root.block == "func") mainFunc.push("return false;")
    code = code.concat(threadCode)
    code.push("")
  }
  code = code.concat(
    [
      "@Override",
      [ 
        "public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)",
        "return true;"
      ],
      "",
      "@Override",
      [
        "public void onEnable()",
        "plugin = this;",
        "",
        "DFUtilities.getManagers(this);",
        "getServer().getPluginManager().registerEvents(this, this);",
        "getServer().getPluginManager().registerEvents(new DFListeners(), this);",
        `this.getCommand("dfspigot").setExecutor(new DFListeners());`
      ]
    ]
  )
  codeEditor.getDoc().setValue(`package me.wonk2;\n\n${libraries.map(lib => `import ${lib};`).join("\n")}\n\n${formatChildren(code[0], code, "  ")}`)
}


let mainTarget = "none"
function spigotify(thread) {
  let bannedBlocks = ["event", "process", "func", "entity_event"]
  let ifStatements = ["if_player", "if_var", "if_game"]
  var nonTargetDependant = ["game_action", "repeat", "control"]
  for (let i = 1; i < thread.length; i++) {
    let codeBlock = thread[i]
    if (bannedBlocks.includes(codeBlock.block)) {
      console.error("INVALID INPUT: Found 1 or more root blocks inside this thread!")
      return
    }

    if (codeBlock.id == "bracket") {
      if (codeBlock.direct == "close"){
        mainFunc = findParent(threadCode, findParent(threadCode, mainFunc)); // Statement is wrapped in a for loop
        mainTarget = "none"
      } 
      continue
    }

    if(actionSpecifics(codeBlock) == false){
      loopID = parseFloat((Math.random() * 99999999).toFixed(3));
      let actionSyntax = `${blockClasses()[codeBlock.block]}.invokeAction(${blockParams(codeBlock)[codeBlock.block]})`
      if (!ifStatements.includes(codeBlock.block) && codeBlock.block != "repeat"){
        if(mainTarget == "none" && !nonTargetDependant.includes(codeBlock.block)){
          let temp = [`for(LivingEntity target : ${selectionSyntax(codeBlock.target)})`]
          mainFunc.push(temp)
          mainFunc = temp
          
          mainTarget = codeBlock.target
          mainFunc.push(`${actionSyntax};\n`)
          mainFunc = findParent(threadCode, temp)
          mainTarget = "none"
        }
        else mainFunc.push(`${actionSyntax};\n`)
      }
      else{
        let temp = codeBlock.block == "repeat" ? [`while(${actionSyntax})`] : [`if(${actionSyntax})`]
        
        mainFunc.push(!nonTargetDependant.includes(codeBlock.block) ? 
        [`for(LivingEntity target : ${selectionSyntax(codeBlock.target)})`, temp] : temp)
        
        temp.push("") // When formatting this will add a newline & make the if-statement more readable
        if(codeBlock.block == "repeat"){
          newImport(["me.wonk2.utilities.internals.LoopData"])
          mainFunc.push(`LoopData.newData(${loopID}d + threadID);`)
        }
        mainFunc = temp
  
        mainTarget = nonTargetDependant.includes(codeBlock.block) ? "none" : codeBlock.target
      }

      newImport([`me.wonk2.utilities.actions.${blockClasses()[codeBlock.block]}`]) 
    }
  }
  let formattedLibraries = ""
  for (let k = 0; k < libraries.length; k++) 
    formattedLibraries += `import ${libraries[k]};\n`
}

function formatChildren(element, children, indent) {
  // Second check is for methods
  if(children[0].split('(')[0] != 'for') element += "{" 
  // Don't do brackets for if statements/for loops with only 1 element inside
  
  for (let i = 1; i < children.length; i++) {
    if (Array.isArray(children[i])) {
      element +=
        "\n" +
        formatChildren(indent + children[i][0].replaceAll("{indent}", indent), children[i], indent + "  ")
    } else {
      element += "\n" + indent + children[i].replaceAll("{indent}", indent)
    }
  }

  return children[0].split('(')[0] != 'for' ? 
    element + "\n" + indent.replace("  ", "") + "}" :
    element + indent.replace("  ", "")
}

function getCodeArgs(codeBlock) {
  if(codeBlock.action == null) return null;
  
  let args = []
  let slots = []
  let tags = {}
  for (let i = 0; i < codeBlock.args.items.length; i++) {
    let arg = codeBlock.args.items[i].item
    let slot = codeBlock.args.items[i].slot

    if (arg.id != "bl_tag") {
      if(arg.id != "g_val") args.push(`new DFValue(${javafyParam(arg, slot, codeBlock)}, ${slot}, DFType.${arg.id.toUpperCase()})`)
      else{
        let paramInfo = javafyParam(arg, slot, codeBlock)
        args.push(`new DFValue(${paramInfo[0]}, DFType.${paramInfo[1]})`)
      } 
      slots.push(slot)
    } else tags[arg.data.tag] = arg.data.option
  }

  //Format both args & tags into hashmaps
  let argMap = ""

  let tagMap = ""
  let tagKeys = Object.keys(tags)
  let tagValues = Object.values(tags)

  for (let i = 0; i < Math.max(slots.length, tagKeys.length); i++) {
    if (i < slots.length) argMap += `\n{indent}  put(${slots[i]}, ${args[i]});`
    if (i < tagKeys.length) tagMap += `\n{indent}  put("${removeQuotes(tagKeys[i])}", "${removeQuotes(tagValues[i])}");`
  }
  argMap = slots.length == 0 ? `new HashMap<>(){}` : `new HashMap<>(){{${argMap}\n{indent}}}`
  tagMap = tagKeys.length == 0 ? `new HashMap<>(){}` : `new HashMap<>(){{${tagMap}\n{indent}}}`
  let actionName = `${codeBlock.block.replaceAll("_", "").toUpperCase()}:${codeBlock.action.replaceAll(/( $)|^ /gi, "")}`;

  return `ParamManager.formatParameters(${argMap}, \n{indent}${tagMap}, "${actionName}", localVars)`
}

function newImport(newLibraries) {
  for (let i = 0; i < newLibraries.length; i++) {
    let lib = newLibraries[i]
    if (!libraries.includes(lib)) libraries.push(lib)
  }
}

function textCodes(str) {
  str = expressions(str)
  
  let targetCodes = {
    "%default": `targets.get("default").getName()`
  };

  for (let i = 0; i < Object.keys(targetCodes).length; i++) {
    let temp = Object.keys(targetCodes)[i]
    str = str.replaceAll(temp, `" + ${targetCodes[temp]} + "`)
  }


  return str.replaceAll(`"" + `, "").replaceAll(`+ ""`, "").replaceAll("Â§", "§")
}

function expressions(str){
  let seenPercentage = false
  let countedBrackets = 0
  
  let startIndex, endIndex, percentIndex

  let percentCodes = ["%var"]
  for(let i = 0; i < str.length; i++){
    let char = str[i]
    
    if(char == '%' && (startIndex == -1 || (!percentCodes.includes(str.substring(percentIndex, startIndex))))){
      seenPercentage = true
      percentIndex = i
      startIndex = -1
      endIndex = -1
    }

    if((char == '(' || char == ')') && seenPercentage){
      countedBrackets += (char == '(' ? 1 : -1)
      if(countedBrackets == 1 && startIndex == -1){
        startIndex = i
      } 
      else if(countedBrackets == 0){
        endIndex = i
        let prefix = str.substring(percentIndex, startIndex)
        let content = str.substring(startIndex + 1, endIndex)
        
        if(percentCodes.includes(prefix)){
          switch(prefix){
            case "%var":
              let varName = expressions(removeQuotes(content));
              str = str.replaceBetween(percentIndex, endIndex + 1, `" + DFUtilities.parseTxt(DFVar.getVar(new DFVar("${varName}", DFVar.getVarScope("${varName}", localVars)), localVars)) + "`)
              break
          }
        }
      }
    }
  }
  
  return str;
}


function removeQuotes(text) {
  return text.replaceAll(`"`, `\\"`)
}

function javafyParam(arg, slot, codeBlock) {
  switch (arg.id) {
    case "txt":
      return `"${textCodes(removeQuotes(arg.data.name))}"`
    case "num":
      return arg.data.name + "d"
    case "snd":
      return `new DFSound("${arg.data.sound}", ${arg.data.pitch}f, ${arg.data.vol}f)`
    case "loc":
      let loc = arg.data.loc
      newImport(["org.bukkit.Bukkit"])
      return `new Location(Bukkit.getWorlds().get(0), ${loc.x}, ${loc.y}, ${loc.z}, ${loc.yaw}, ${loc.pitch})`
    case "item":
      return `DFUtilities.parseItemNBT("${removeQuotes(arg.data.item)}")`
    case "pot":
      newImport(["org.bukkit.potion.PotionEffect", "org.bukkit.potion.PotionEffectType"])
      let potion = arg.data
      return `new PotionEffect(PotionEffectType.${potionEffects()[potion.pot]}, ${potion.dur}, ${potion.amp})`
    case "var":
      return `new DFVar("${textCodes(removeQuotes(arg.data.name))}", ${varScopes()[arg.data.scope]})`
    case "g_val":
      return gameValues(arg.data, codeBlock)
    case "vec":
      newImport(["org.bukkit.util.Vector"])
      return `new Vector(${arg.data.x}, ${arg.data.y}, ${arg.data.z})`
  }
}

function potionEffects() {
  return {
    "Absorption": "ABSORPTION",
    "Conduit Power": "CONDUIT_POWER",
    "Dolphin's Grace": "DOLPHINS_GRACE",
    "Fire Resistance": "FIRE_RESISTANCE",
    "Haste": "FAST_DIGGING",
    "Health Boost": "HEALTH_BOOST",
    "Hero of the Village": "HERO_OF_THE_VILLAGE",
    "Instant Health": "HEAL",
    "Invisibility": "INVISIBLITY",
    "Jump Boost": "JUMP",
    "Luck": "LUCK",
    "Night Vision": "NIGHT_VISION",
    "Regeneration": "REGENERATION",
    "Resistance": "DAMAGE_RESISTANCE",
    "Saturation": "SATURATION",
    "Slow Falling": "SLOW_FALLING",
    "Speed": "SPEED",
    "Strength": "INCREASE_DAMAGE",
    "Water Breathing": "WATER_BREATHING",
    "Bad Luck": "UNLUCK",
    "Bad Omen": "BAD_OMEN",
    "Blindness": "BLINDNESS",
    "Glowing": "GLOWING",
    "Hunger": "HUNGER",
    "Instant Damage": "HARM",
    "Levitation": "LEVITATION",
    "Mining Fatigue": "SLOW_DIGGING",
    "Nausea": "CONFUSION",
    "Poison": "POISON",
    "Slowness": "SLOWNESS",
    "Weakness": "WEAKNESS",
    "Wither": "WITHER"
  };
}

function varScopes() {
  return {
    "unsaved": "Scope.GLOBAL",
    "local": "Scope.LOCAL",
    "save": "Scope.SAVE"
  };
}

function blockClasses() {
  return {
    "player_action": "PlayerAction",
    "set_var": "SetVariable",
    "game_action": "GameAction",
    "if_player": "IfPlayer",
    "if_var": "IfVariable",
    "if_game": "IfGame",
    "repeat": "Repeat",
    "control": "Control"
  }
}

function blockParams(codeBlock) {
  return {
    "player_action": `${getCodeArgs(codeBlock)}, "${codeBlock.action.replaceAll(/( $)|^ /gi, "")}", target`,
    "set_var": `${getCodeArgs(codeBlock)}, "${codeBlock.action.replaceAll(/( $)|^ /gi, "")}", target, localVars`,
    "game_action": `${getCodeArgs(codeBlock)}, "${codeBlock.action.replaceAll(/( $)|^ /gi, "")}", ${selectionSyntax(codeBlock.target)}[0]`,
    "if_player": `${getCodeArgs(codeBlock)}, "${codeBlock.action.replaceAll(/( $)|^ /gi, "")}", (Player) target`,
    "if_var": `${getCodeArgs(codeBlock)}, "${codeBlock.action.replaceAll(/( $)|^ /gi, "")}", target, localVars`,
    "if_game": `${getCodeArgs(codeBlock)}, "${codeBlock.action.replaceAll(/( $)|^ /gi, "")}", ${selectionSyntax(codeBlock.target)}[0], specifics`,
    "repeat": `${getCodeArgs(codeBlock)}, "${codeBlock.action.replaceAll(/( $)|^ /gi, "")}", localVars, ${loopID}d + threadID`,
    "control": ""
  }
}

function actionSpecifics(codeBlock){
  console.log(threadCode)
  console.log(root)
  let dict = {
    "Wait": `try {Thread.sleep(DFUtilities.getWait(${getCodeArgs(codeBlock)}));} catch (InterruptedException e) {e.printStackTrace();}`,
    "Return": `return;`,
    "Skip": `continue;`,
    "StopRepeat": `break;`,
    "End": root.block == "func" ? "return true;" : "return;",
    "CancelEvent": ["event.setCancelled(true);", `specifics.put("cancelled", event.isCancelled());`],
    "UncancelEvent": ["event.setCancelled(false);", `specifics.put("cancelled", event.isCancelled());`]
  }
  
  switch(codeBlock.block){
    case "call_func":
      mainFunc.push(`if(${codeBlock.data}(threadID, localVars, targets, specifics)) ${root.block == "func" ? "return true" : "return"};`); return true
  }

  console.log(codeBlock.block)

  if(Object.keys(dict).includes(codeBlock.action)){
    if(Array.isArray(dict[codeBlock.action])){
      for(let temp of dict[codeBlock.action])
        mainFunc.push(temp)
    }
    else mainFunc.push(dict[codeBlock.action])
  }
  else return false
  return true
}

function gameValues(gVal, codeBlock){
  let target = (gVal.target == null ? "default" : gVal.target).toLowerCase()
  let selection = target == "allplayers" ? "target" : `targets.get("${target}")`
  
  if(selection == "target" && nonTargetDependants.includes(codeBlock.block)) 
    selection = `${selectionSyntax(codeBlock.target)}[0]`

  return {
    "Location": [`${selection}.getLocation()`, "LOC"],
    "Current Health": [`${selection}.getHealth()`, "NUM"],
    "Maximum Health": [`${selection}.getAttribute(Attribute.GENERIC_MAX_HEALTH)`, "NUM"],
    "Absorption Health": [`${selection}.getAbsorptionAmount()`, "NUM"],
    "Food Level": [`${selection}.getFoodLevel()`, "NUM"],
    "Food Saturation": [`${selection}.getSaturation`, "NUM"],
    "Food Exhaustion": [`${selection}.getExhaustion()`, "NUM"],
    "Attack Damage": [`${selection}.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)`, "NUM"],
    "Attack Speed": [`${selection}.getAttribute(Attribute.GENERIC_ATTACK_SPEED)`, "NUM"],
    "Armor Points": [`${selection}.getAttribute(Attribute.GENERIC_ARMOR)`, "NUM"],
    "Armor Toughness": [`${selection}.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS)`, "NUM"],
    "Invulnerability Ticks": [`${selection}.getNoDamageTicks()`, "NUM"],
    "Experience Level": [`${selection}.getLevel()`, "NUM"],
    "Experience Progress": [`${selection}.getExp() * 100`, "NUM"],
    "Fire Ticks": [`${selection}.getFireTicks()`, "NUM"],
    "Freeze Ticks": [`${selection}.getFreezeTicks()`, "NUM"],
    "Remaining Air": [`${selection}.getRemainingAir()`, "NUM"],
    "Fall Distance": [`${selection}.getFallDistance()`, "NUM"],
    "Held Slot": [`${selection}.getInventory().getHeldItemSlot()`, "NUM"],
    "Ping": [`${selection}.getPing()`, "NUM"],
    "Item Usage Progress": [``, "NUM"], //TODO
    "Steer Sideways Movement": [``, "NUM"], //TODO: This might require ProtocolLib!
    "Steer Forward Movement": [``, "NUM"], //TODO: This might require ProtocolLib!
    "Event Item": [`specifics.get("item")`, "ITEM"]
  }[gVal.type]
}

function selectionSyntax(target) {
  if(target == mainTarget && mainTarget != "none") return `new LivingEntity[]{target}`
  if(target == "AllPlayers"){
    newImport(["org.bukkit.Bukkit"])
    return "Bukkit.getOnlinePlayers().toArray(new LivingEntity[0])"
  }

  return target == null ?
    `new LivingEntity[]{targets.get("default")}` :
    `new LivingEntity[]{targets.get("${target.toLowerCase()}")}`
}

function findParent(parentArray, arr){
  for(let i = 0; i < parentArray.length; i++){
    if(!Array.isArray(parentArray[i])) continue
    
    if(JSON.stringify(parentArray[i]) == JSON.stringify(arr)) return parentArray
    else if (Array.isArray(parentArray[i])){
      let potentialReturn = findParent(parentArray[i], arr)
      if(potentialReturn != null) return potentialReturn
    }
  }

  return null
}

function findMostNestedArr(array, result, nest){
  for(let element of array)
    if(Array.isArray(element))
      result = findMostNestedArr(element, result, nest + 1)

  if(result[1] < nest) result = [array, nest]
  return result
}

String.prototype.replaceBetween = function(start, end, what) { //https://stackoverflow.com/questions/14880229/how-to-replace-a-substring-between-two-indices
  return this.substring(0, start) + what + this.substring(end);
};
