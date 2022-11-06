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

var mainFunc, libraries, root, code, specifics, threadCode, functions
export var threads = []

export function getRoot(base64) {
  try {
    return decodeTemplate(base64.match(/h4sI(A{5,20})[a-z0-9+_/=]+/i)[0]).blocks[0]
  }
  catch (e) {
    console.log(e)
    return null
  }
}

export function generate() {
  libraries = [
    "me.wonk2.utilities.*",
    "me.wonk2.utilities.enums.*",
    "me.wonk2.utilities.values.*",
    "me.wonk2.utilities.actions.*",
    "me.wonk2.utilities.actions.pointerclasses.brackets.*",
    "me.wonk2.utilities.internals.CodeExecutor",
    "net.md_5.bungee.api.ChatColor",
    "org.bukkit.boss.BossBar",
    "org.bukkit.command.CommandSender",
    "org.bukkit.command.Command",
    "org.bukkit.command.CommandExecutor",
    "org.bukkit.entity.LivingEntity",
    "org.bukkit.Location",
    "org.bukkit.Material", // Event Item, don't remove
    "org.bukkit.inventory.ItemStack", // Event Item, don't remove
    "org.bukkit.event.Listener",
    "org.bukkit.event.EventHandler",
    "org.bukkit.event.block.Action",
    "org.bukkit.plugin.java.JavaPlugin",
    "java.util.*"
  ]
  code = [
    "public class DFPlugin extends JavaPlugin implements Listener, CommandExecutor{",
    "public static HashMap<String, TreeMap<Integer, BossBar>> bossbarHandler = new HashMap<>();",
    "funcs here (if you see this something went wrong)",
    "public static Location origin = new Location(null, 0, 0, 0);",
    "public static JavaPlugin plugin;",
    ""
  ]

  functions = []

  for (let threadData of threads) {
    let decodedJson = decodeTemplate(threadData.match(/h4sI(A{5,20})[a-z0-9+_/=]+/i)[0])
    console.log(decodedJson)

    root = decodedJson.blocks[0];



    let isEvent = (root.block == "event" || root.block == "entity_event")
    let rootEvent = "null"
    let specificsDefaults =
    {
      "targets": {
        "default": "event.getPlayer()"
      },
      "item": "new ItemStack(Material.AIR)",
      "cancelled": "event.isCancelled()"
    }
    if (isEvent) {
      rootEvent = eventTypes[root.action]["name"].split(".")
      rootEvent = rootEvent[rootEvent.length - 1]
      specifics = setSpecificsDefaults(eventTypes[root.action]["specifics"], specificsDefaults)
    }
    else specifics = specificsDefaults
    console.log(specifics)

    threadCode = threadCodes(root, rootEvent, specifics)[isEvent ? "event" : root.block]
    switch (root.block) {
      case "event":
      case "entity_event":
        mainFunc = threadCode[1]; break
      case "func":
        functions.push(spigotify(decodedJson.blocks, root)[1].concat(["});"]))
        continue
      case "process":
        mainFunc = threadCode[0]; break
    }
    console.log(specifics["targets"])

    if (isEvent) {
      if (eventTypes[root.action]["check"] != null) {
        let temp = [`if(${eventTypes[root.action]["check"]})`]
        mainFunc.push(temp)
        mainFunc = temp
      }
      libraries.push(`org.bukkit.event.${eventTypes[root.action]["name"]}`)
      mainFunc.push(spigotify(decodedJson.blocks, root))
      let temp = specifics["cancelled"] == "false" ? "null" : "event"
      mainFunc.push(`}, targets, localVars, ${temp});`)
    }


    code = code.concat(threadCode)
    code.push("")
  }
  
  code[2] = functions.length == 0 ? 
    "public static HashMap<String, Object[]> functions = new HashMap<>();" :
    ["public static HashMap<String, Object[]> functions = new HashMap<>(){{"].concat(functions)
  if(functions.length != 0) code.splice(3, 0, "}};");
  
  code = code.concat(
    [
      "@Override",
      [
        "public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){",
        "return true;"
      ],
      "}",
      "",
      "@Override",
      [
        "public void onEnable(){",
        "plugin = this;",
        "",
        "DFListeners.updateArgInfo();",
        "DFUtilities.getManagers(this);",
        "getServer().getPluginManager().registerEvents(this, this);",
        "getServer().getPluginManager().registerEvents(new DFListeners(), this);",
        `this.getCommand("dfspigot").setExecutor(new DFListeners());`
      ],
      "}"
    ]
  )
  console.log(code)
  codeEditor.getDoc().setValue(`package me.wonk2;\n\n${libraries.map(lib => `import ${lib};`).join("\n")}\n\n${formatChildren(code[0], code, "  ")}` + "\n}")
}


function spigotify(jsonThread, root) {
  let thread = []
  let bannedBlocks = ["event", "process", "func", "entity_event"]
  for (let i = 1; i < jsonThread.length; i++) {
    let codeBlock = jsonThread[i]
    if (bannedBlocks.includes(codeBlock.block)) {
      console.error("INVALID INPUT: Found 1 or more root blocks inside this thread!")
      return
    }

    if(codeBlock.id == 'bracket' && codeBlock.direct == 'close') thread.push(codeBlock.type == 'norm' ? 'new ClosingBracket(),' : `new RepeatingBracket(),`)
    else if(codeBlock.direct != 'open') thread = thread.concat([[`new ${blockClasses()[codeBlock.block]}(`, `${blockParams(codeBlock, root.block == "func")}`], "),"])
  }

  let formattedLibraries = ""
  for (let k = 0; k < libraries.length; k++)
    formattedLibraries += `import ${libraries[k]};\n`

  let prefix = root.block == "func" ? [`put("${root.data}", new Object[]{`] : [`new Object[]{`]
  console.log(prefix.concat(thread))
  return [`CodeExecutor.executeThread(`, prefix.concat(thread)]
}

function formatChildren(element, children, indent) {
  for (let i = 1; i < children.length; i++) {
    if (Array.isArray(children[i])) {
      element +=
        "\n" +
        formatChildren(indent + children[i][0].replaceAll("{indent}", indent), children[i], indent + "  ")
    } else {
      element += "\n" + indent + children[i].replaceAll("{indent}", indent)
    }
  }

  return element
}

function getCodeArgs(codeBlock, isFunc) {
  if (codeBlock.action == null) return null;

  let args = []
  let slots = []
  let tags = {}
  for (let i = 0; i < codeBlock.args.items.length; i++) {
    let arg = codeBlock.args.items[i].item
    let slot = codeBlock.args.items[i].slot

    if (arg.id != "bl_tag") {
      if (arg.id != "g_val") args.push(`new DFValue(${javafyParam(arg, slot, codeBlock)}, ${slot}, DFType.${arg.id.toUpperCase()})`)
      else {
        let paramInfo = javafyParam(arg, slot, codeBlock)
        args.push(`new DFValue(${paramInfo[0]}, ${slot}, DFType.${paramInfo[1]})`)
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
    if (i < slots.length) argMap += `\n{indent}    put(${slots[i]}, ${args[i]});`
    if (i < tagKeys.length) tagMap += `\n{indent}    put("${removeQuotes(tagKeys[i])}", "${removeQuotes(tagValues[i])}");`
  }
  
  argMap = slots.length == 0 ? `new HashMap<>(){}` : `new HashMap<>(){{${argMap}\n{indent}  }}`
  tagMap = tagKeys.length == 0 ? `new HashMap<>(){}` : `new HashMap<>(){{${tagMap}\n{indent}  }}`
  let actionName = `${codeBlock.block.replaceAll("_", "").toUpperCase()}:${codeBlock.action.replaceAll(/( $)|^ /gi, "")}`;

  return `new ParamManager(\n{indent}  ${argMap}, \n{indent}  ${tagMap}, "${actionName}", ${isFunc ? "null" : "localVars"})`
}

function newImport(newLibraries) {
  for (let i = 0; i < newLibraries.length; i++) {
    let lib = newLibraries[i]
    if (!libraries.includes(lib)) libraries.push(lib)
  }
}

function hexCodes(str) {
  str = str.replaceAll("Â§", "§")

  //Hex Codes
  let matches = str.match(/§x(§[a-fA-F0-9]){6}/g)
  if (matches != null)
    for (let match of matches)
      str = str.replace(match, `" + ChatColor.of("${match.toLowerCase().replace("x", "#").replaceAll("§", "")}") + "`)

  return `"${str}"`.replaceAll(`"" + `, "").replaceAll(`+ ""`, "")
}

function setSpecificsDefaults(specifics, specificsDefaults) {
  for (let key of Object.keys(specificsDefaults)) {
    if (specifics[key] == null) specifics[key] = specificsDefaults[key]
    else if (specificsDefaults[key].constructor == Object)
      specifics[key] = setSpecificsDefaults(specifics[key], specificsDefaults[key])
  }
  return specifics
}


function removeQuotes(text) {
  return text.replaceAll(`"`, `\\"`).replaceAll(`\\'`, `\\\\'`)
}

function javafyParam(arg, slot, codeBlock) {
  switch (arg.id) {
    case "txt":
      return hexCodes(removeQuotes(arg.data.name)) // Surrounding quotes are added by hexCodes method before returning
    case "num":
      return `"${arg.data.name}"` // Needs to be a string to be processed for text codes during runtime
    case "snd":
      return `new DFSound("${arg.data.sound}", ${arg.data.pitch}f, ${arg.data.vol}f)`
    case "loc":
      let loc = arg.data.loc
      newImport(["org.bukkit.Bukkit"])
      return `new Location(Bukkit.getWorlds().get(0), ${loc.x}d, ${loc.y}d, ${loc.z}d, ${loc.yaw}f, ${loc.pitch}f)`
    case "item":
      return `DFUtilities.parseItemNBT("${removeQuotes(arg.data.item)}")`
    case "pot":
      newImport(["org.bukkit.potion.PotionEffect", "org.bukkit.potion.PotionEffectType"])
      let potion = arg.data
      return `new PotionEffect(PotionEffectType.${potionEffects()[potion.pot]}, ${potion.dur}, ${potion.amp})`
    case "var":
      return `new DFVar("${removeQuotes(arg.data.name)}", ${varScopes()[arg.data.scope]})`
    case "g_val":
      return gameValues(arg.data)
    case "vec":
      newImport(["org.bukkit.util.Vector"])
      return `new Vector(${arg.data.x}, ${arg.data.y}, ${arg.data.z})`
    case "part":
      newImport(["me.wonk2.utilities.values.DFParticle", "org.bukkit.Color"])
      let p = arg.data;
      let pd = p.data == null ? {} : p.data;


      let fields = ["size", "sizeVariation", "motionVariation", "colorVariation", "x", "y", "z"]

      return `new DFParticle("${p.particle}", ${p.cluster.amount}, ${p.cluster.horizontal}, ${p.cluster.vertical}, ${doublify(pd.size)}, ${doublify(pd.sizeVariation)}, ${pd.x != null ? `new Vector(${pd.x}, ${pd.y}, ${pd.z})` : "null"}, ${doublify(pd.motionVariation)}, ${pd.rgb == null ? "null" : `Color.fromRGB(${pd.rgb})`}, ${doublify(pd.colorVariation)}, ${pd.material != null ? `Material.valueOf(${pd.material})` : "null"})`

  }
}

function doublify(val) {
  return val == null ? "null" : val + "d"
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
    "saved": "Scope.SAVE"
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
    "control": "Control",
    "entity_action": "EntityAction",
    "call_func": "CallFunction"
  }
}

function blockParams(codeBlock, isFunc) {
  let target, action
  if(codeBlock.block == "entity_action" && codeBlock.target == null) target = `"LastEntity"` // TODO: Change "LastEntity" to selection once that is implemented
  else target = codeBlock.target == null ? `"default"` : `"${codeBlock.target.toLowerCase()}"` // TODO: Change "default" to selection once that is implemented

  let targets = isFunc ? "null" : "targets"
  let localVars = isFunc ? "null" : "localVars"
  let args = getCodeArgs(codeBlock, isFunc)
  let inverted = codeBlock["inverted"] == "NOT"
  if(codeBlock.action != null) action = codeBlock.action.replaceAll(/( $)|^ /gi, "")

  return {
    "player_action": `${target}, ${targets}, ${args}, "${action}"`,
    "set_var": `${target}, ${targets}, ${args}, "${action}", ${localVars}`,
    "game_action": `${target}, ${targets}, ${args}, "${action}"`,
    "if_player": `${target}, ${targets}, ${args}, "${action}", ${inverted}`,
    "if_var": `${target}, ${targets}, ${args}, "${action}", ${inverted}, ${localVars}`,
    "if_game": `${target}, ${targets}, ${args}, "${action}", ${inverted}, specifics`,
    "repeat": `null, null, ${args}, "${action}", ${inverted}, ${localVars}, ${Math.random() * Number.MAX_SAFE_INTEGER}d, threadID`,
    "control": `${target}, ${targets}, ${args}, "${action}"`,
    "entity_action": `${target}, ${targets}, ${args}, "${action}", ${localVars}`,
    "call_func": `functions.get("${codeBlock.data}")`
  }[codeBlock.block]
}

function gameValues(gVal) {
  let target = gVal.target == null ? `"default"` : `"${gVal.target.toLowerCase()}"`
  let selection = `DFUtilities.getTargets(${target})[0]`

  let gVals = {
    "Event Block Location": [`DFUtilities.getRelativeLoc(((Block) specifics.get("block")).getLocation())`, "LOC"],
    "Target Block Location": [`DFUtilities.getTargetBlock(${selection}}})`, "LOC"],
    "Target Block Side": [`${selection}.getFacing().getDirection()`, "VEC"],
    "X-Coordinate": [`DFUtilities.getRelativeLoc(${selection}.getLocation()).getX()`, "NUM"],
    "Y-Coordinate": [`DFUtilities.getRelativeLoc(${selection}.getLocation()).getY()`, "NUM"],
    "Z-Coordinate": [`DFUtilities.getRelativeLoc(${selection}.getLocation()).getZ()`, "NUM"],
    "Pitch": [`${selection}.getLocation().getPitch()`, "NUM"],
    "Yaw": [`${selection}.getLocation().getYaw()`, "NUM"],
    "Spawn Location": [`${selection}.getBedSpawnLocation()`, "LOC"],
    "Velocity": [`${selection}.getVelocity()`, "VEC"],
    "Direction": [`${selection}.getLocation().getDirection().normalize()`, "VEC"],
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
    "Steer Sideways Movement": [``, "NUM"], //TODO: This might require ProtocolLib!d
    "Steer Forward Movement": [``, "NUM"], //TODO: This might require ProtocolLib!
    "Event Item": [`specifics.get("item")`, "ITEM"]
  }

  if(!Object.keys(gVals).includes(gVal.type))
    return [`new GameValue(Value.${gVal.type.replaceAll(" ", "")}, ${target})`, "GAMEVAL"]
  else return gVals[gVal.type]
}

String.prototype.replaceBetween = function(start, end, what) { //https://stackoverflow.com/questions/14880229/how-to-replace-a-substring-between-two-indices
  return this.substring(0, start) + what + this.substring(end);
};
