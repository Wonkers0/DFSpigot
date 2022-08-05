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

var mainFunc, libraries, root, eventTypes, code

export function generate() {
  let decodedJson = decodeTemplate(document.getElementById("NBTInput").value.match(/h4sI(A{5,20})[a-z0-9+_/=]+/i)[0])
  console.log(decodedJson)

  root = decodedJson.blocks[0];
  eventTypes = {
    Leave: ["PlayerQuitEvent"],
    Join: ["PlayerJoinEvent"],
    RightClick: ["PlayerInteractEvent", "event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR"],
    LeftClick: ["PlayerInteractEvent", "event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR"],
    Sneak: ["PlayerToggleSneakEvent", "event.isSneaking()"],
    SwapHands: ["PlayerSwapHandItemsEvent"],
    CloseInv: ["InventoryCloseEvent"],
		StartFly: ["PlayerToggleFlightEvent", "event.isFlying()"],
		PickupItem: ["InventoryPickupItemEvent"],
		BreakBlock: ["BlockBreakEvent"],
		StartSprint: ["PlayerToggleSprintEvent", "event.isSprinting()"],
		ShootBow: ["EntityShootBowEvent", "event.getEntity() instanceof Player"],
		StopFly: ["PlayerToggleFlightEvent", "!event.isFlying()"],
		PlayerTakeDmg: ["EntityDamageEvent", "event.getEntity() instanceof Player"],
		ProjHit: ["ProjectileHitEvent", "event.getEntity().getShooter() instanceof Player"],
		KillPlayer: ["PlayerDeathEvent", "event.getEntity().isDead() && event.getDamager() instanceof Player"],
		ClickInvSlot: ["InventoryClickEvent", "event.getInventory() == event.getWhoClicked().getOpenInventory().getBottomInventory()"],
		Respawn: ["PlayerRespawnEvent"],
		DamageEntity: ["EntityDamageByEntityEvent", "event.getDamager() instanceof Player && !(event.getEntity() instanceof Player)"],
		PlayerHeal: ["EntityRegainHealthEvent", "event.getEntity() instanceof Player"],
		ClickPlayer: ["PlayerInteractEntityEvent", "event.getRightClicked() instanceof Player"],
		Consume: ["PlayerItemConsumeEvent"],
		Death: ["PlayerDeathEvent"],
		PlaceBlock: ["BlockPlaceEvent"],
		Walk: ["PlayerMoveEvent", "!DFUtilities.playerDidJump(event)"],
		Dismount: ["EntityDismountEvent", "event.getEntity() instanceof Player"],
		CloudImbuePlayer: ["AreaEffectCloudApplyEvent", "DFUtilities.cloudAffectedPlayer(event.getAffectedEntities())"], 
    // TODO: This event may need to change the code equivalent of the Default target from one target to multiple
		DropItem: ["PlayerDropItemEvent"],
		ChangeSlot: ["PlayerItemHeldEvent"],
		ClickEntity: ["PlayerInteractEntityEvent", "!(event.getRightClicked() instanceof Player)"],
		HorseJump: ["HorseJumpEvent"],
		ShootProjectile: ["ProjectileLaunchEvent", "event.getEntity().getShooter() instanceof Player"],
		Unsneak: ["PlayerToggleSneakEvent", "!event.isSneaking()"],
		Fish: ["PlayerFishEvent"],
		BreakItem: ["PlayerItemBreakEvent"],
		ClickMenuSlot: ["InventoryClickEvent", "event.getInventory() == event.getWhoClicked().getOpenInventory().getTopInventory()"],
		Riptide: ["PlayerRiptideEvent"],
		KillMob: ["EntityDamageByEntityEvent", "event.getDamager() instanceof Player && event.getEntity().isDead()"],
		EntityDmgPlayer: ["EntityDamageByEntityEvent", "event.getEntity() instanceof Player && !(event.getDamager() instanceof Player)"],
		StopSprint: ["PlayerToggleSprintEvent", "!event.isSprinting()"],
		Jump: ["PlayerMoveEvent", "DFUtilities.playerDidJump(event)"],
		ProjDmgPlayer: ["ProjectileHitEvent", "event.getHitEntity() instanceof Player"],
		PlayerDmgPlayer: ["EntityDamageByEntityEvent", "event.getEntity() instanceof Player && event.getDamager() instanceof Player"]
  }

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
    "org.bukkit.event.Listener",
    "org.bukkit.event.EventHandler",
    "org.bukkit.plugin.java.JavaPlugin",
    "org.bukkit.Bukkit",
    "java.util.*"
  ]
  libraries.push(`org.bukkit.event.player.${eventTypes[root.action][0]}`)

  code = [
    "public class DFPlugin extends JavaPlugin implements Listener, CommandExecutor",
    "public static HashMap<String, TreeMap<Integer, BossBar>> bossbarHandler = new HashMap<>();",
    "public static JavaPlugin plugin;",
    "",
    "@EventHandler",
    [
      "public void " + root.action + "(" + eventTypes[root.action][0] + " event)",
      "HashMap<String, DFValue> localVars = new HashMap<>();",
    ],
    "",
    "@Override",
    [
      "public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)",
      "return true;",
    ],
    "",
    "@Override",
    [
      "public void onEnable()",
      "plugin = this;",
      "",
      "DFUtilities.getManagers(this);",
      "getServer().getPluginManager().registerEvents(this, this);",
      "getServer().getPluginManager().registerEvents(new DFUtilities(), this);",
    ],
  ]

  mainFunc = code[5]
  if(eventTypes[root.action][1] != null){
    let temp = [`if(${eventTypes[root.action][1]})`]
    mainFunc.push(temp)
    mainFunc = temp

    newImport(["org.bukkit.event.block.Action"])
  }

  console.log(root)
  spigotify(decodedJson.blocks)
}


let mainTarget = null
function spigotify(thread) {
  console.log(root)
  let bannedBlocks = [["event"], "process", "function", "entity_event"]
  let ifStatements = ["if_player", "if_var"]
  var nonTargetDependant = ["game_action"]
  for (let i = 1; i < thread.length; i++) {
    let codeBlock = thread[i]
    if (bannedBlocks.includes(codeBlock.block)) {
      console.error("INVALID INPUT: Found 1 or more root blocks inside this thread!")
      return
    }

    if (codeBlock.id == "bracket") {
      if (codeBlock.direct == "close"){
        mainFunc = findParent(code, findParent(code, mainFunc)); // Statement is wrapped in a for loop
        mainTarget = null
      } 
      continue
    }

    let actionSyntax = `${blockClasses()[codeBlock.block]}.invokeAction(${blockParams(codeBlock)[codeBlock.block]})`
    if (!ifStatements.includes(codeBlock.block))
      if(mainTarget == null && !nonTargetDependant.includes(codeBlock.block)){
        let temp = [`for(LivingEntity target : ${selectionSyntax(codeBlock.target)})`]
        mainFunc.push(temp)
        mainFunc = temp
        
        mainTarget = codeBlock.target
        mainFunc.push(`${actionSyntax};\n`)
        mainFunc = findParent(code, temp)
        mainTarget = null
      }
      else{
        mainFunc.push(`${actionSyntax};\n`)
      }
        
    else {
      let temp = [`if(${actionSyntax})`]
      mainFunc.push([`for(LivingEntity target : ${selectionSyntax(codeBlock.target)})`, temp])
      temp.push("") // When formatting this will add a newline & make the if-statement more readable
      mainFunc = temp

      mainTarget = codeBlock.target
    }
    
    newImport([`me.wonk2.utilities.actions.${blockClasses()[codeBlock.block]}`])
    
    let formattedLibraries = ""
    for (let k = 0; k < libraries.length; k++) 
      formattedLibraries += `import ${libraries[k]};\n`

    codeEditor.getDoc().setValue(`package me.wonk2;\n\n${formattedLibraries}\n${formatChildren(code[0], code, "  ")}`);
    
  }
}

function formatChildren(element, children, indent) {
  // Second check is for methods
  if(children.length > 2 || findParent(code, children) == code) element += "{" 
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

  return children.length > 2 || findParent(code, children) == code ? 
    element + "\n" + indent.replace("  ", "") + "}" :
    element + indent.replace("  ", "")
}

function getCodeArgs(codeBlock) {
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
  let codes = {
    "%default": "event.getPlayer().getName()"
  };

  for (let i = 0; i < Object.keys(codes).length; i++) {
    let temp = Object.keys(codes)[i]
    str = str.replace(new RegExp(temp, "g"), `" + ${codes[temp]} + "`)
  }

  return str.replaceAll("Â§", "§")
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
      return `new Location(Bukkit.getServer().getWorlds().get(0), ${loc.x}, ${loc.y}, ${loc.z}, ${loc.yaw}, ${loc.pitch})`
    case "item":
      return `DFUtilities.parseItemNBT("${removeQuotes(arg.data.item)}")`
    case "pot":
      let potion = arg.data
      return `new PotionEffect(PotionEffectType.${potionEffects()[potion.pot]}, ${potion.dur}, ${potion.amp}, ${slot})`
    case "var":
      return `new DFVar("${textCodes(removeQuotes(arg.data.name))}", ${varScopes()[arg.data.scope]})`
    case "g_val":
      return gameValues(arg.data, codeBlock)
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
    "if_var": "IfVariable"
  }
}

function blockParams(codeBlock) {
  return {
    "player_action": `${getCodeArgs(codeBlock)}, "${codeBlock.action.replaceAll(/( $)|^ /gi, "")}", target`,
    "set_var": `${getCodeArgs(codeBlock)}, "${codeBlock.action.replaceAll(/( $)|^ /gi, "")}", target, localVars`,
    "game_action": `${getCodeArgs(codeBlock)}, "${codeBlock.action.replaceAll(/( $)|^ /gi, "")}", ${selectionSyntax(codeBlock.target)}[0]`,
    "if_player": `${getCodeArgs(codeBlock)}, "${codeBlock.action.replaceAll(/( $)|^ /gi, "")}", (Player) target`,
    "if_var": `${getCodeArgs(codeBlock)}, "${codeBlock.action.replaceAll(/( $)|^ /gi, "")}", target, localVars`
  }
}

function gameValues(gVal, codeBlock){
  let selection = {
    Default: "event.getPlayer()",
    AllPlayers: "target"
  }[gVal.target == null ? "default" : gVal.target]
  
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
    "Steer Forward Movement": [``, "NUM"] //TODO: This might require ProtocolLib!
  }[gVal.type]
}

function selectionSyntax(target) {
  let targetTypes = {
    default: "new LivingEntity[]{event.getPlayer()}",
    AllPlayers: "Bukkit.getOnlinePlayers().toArray(new LivingEntity[0])"
  };

  if(target == mainTarget && mainTarget != null) return `new LivingEntity[]{target}`
  
  return target == null ?
    targetTypes["default"] :
    targetTypes[target]
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
