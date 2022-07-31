function decodeTemplate(data){ // Permanently borrowed from grog 😊
    const compressData = atob(base64data);
    const uint = compressData.split('').map(function(e) {
        return e.charCodeAt(0);
    });
    const binData = new Uint8Array(uint);
    const string = pako.inflate(binData,{to: 'string'});
    return JSON.parse(string);
}

let mainFunc, libraries, root, eventTypes, code;

export function generate() {
    let decodedJson = decodeTemplate(document.getElementById("NBTInput").value.match(/h4sI(A{5,20})[a-z0-9+_/=]+/i)[0]);
    console.log(decodedJson);

    root = decodedJson.blocks[0];
    eventTypes = {
        Leave: "PlayerQuitEvent",
        Join: "PlayerJoinEvent",
        RightClick: "PlayerInteractEvent",
        LeftClick: "PlayerInteractEvent",
        Sneak: "PlayerToggleSneakEvent",
        SwapHands: "PlayerSwapHandItemsEvent"
    };

    libraries = [
        "me.wonk2.utilities.*",
        "me.wonk2.utilities.enums.*",
        "me.wonk2.utilities.values.*",
        "org.bukkit.boss.BossBar",
        "org.bukkit.command.CommandSender",
        "org.bukkit.command.Command",
        "org.bukkit.command.CommandExecutor",
        "org.bukkit.entity.LivingEntity",
        "org.bukkit.event.Listener",
        "org.bukkit.event.EventHandler",
        "org.bukkit.plugin.java.JavaPlugin",
        "java.util.*"
    ];
    libraries.push(`org.bukkit.event.player.${eventTypes[root.action]}`);

    code = [
        "public class DFPlugin extends JavaPlugin implements Listener, CommandExecutor",
        "public static HashMap<String, TreeMap<Integer, BossBar>> bossbarHandler = new HashMap<>();",
        "public static JavaPlugin plugin;",
        "",
        "@EventHandler",
        [
            "public void " + root.action + "(" + eventTypes[root.action] + " event)",
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
    ];

    mainFunc = code[5];
    spigotify(decodedJson.blocks);
}


function spigotify(thread) {
    for (let i = 1; i < thread.length; i++) {
        let codeBlock = thread[i];
        let bannedBlocks = ["event", "process", "function", "entity_event"];
        if (bannedBlocks.includes(codeBlock.block)) {
            console.error("INVALID INPUT: Found 1 or more root blocks inside this thread!");
            return;
        }

        mainFunc.push(`${blockClasses()[codeBlock.block]}.invokeAction(${blockParams(codeBlock)[codeBlock.block]});\n`);
        newImport([`me.wonk2.utilities.actions.${blockClasses()[codeBlock.block]}`])

        let formattedLibraries = "";
        for (let k = 0; k < libraries.length; k++) {
            formattedLibraries += `import ${libraries[k]};\n`;
        }
        document.getElementById("code").innerHTML =
            `package me.wonk2;\n\n${formattedLibraries}\n${formatChildren(code[0], code, "  ")}`;   
    }
}

function formatChildren(element, children, indent) {
    element += "{";
    for (let i = 1; i < children.length; i++) {
        if (Array.isArray(children[i])) {
            element +=
                "\n" +
                formatChildren(indent + children[i][0], children[i], indent + "  ");
        } else {
            element += "\n" + indent + children[i].replaceAll("{indent}", indent);
        }
    }

    return (element + "\n" + indent.replace("  ", "") + "}");
}

function getCodeArgs(codeBlock) {
    let args = [];
    let slots = [];
    let tags = {};
    for (let i = 0; i < codeBlock.args.items.length; i++) {
        let arg = codeBlock.args.items[i].item;
        let slot = codeBlock.args.items[i].slot;

        if (arg.id != "bl_tag") {
            args.push(`new DFValue(${javafyParam(arg,slot)}, ${slot}, DFType.${arg.id.toUpperCase()})`);
            slots.push(slot);
        } else tags[arg.data.tag] = arg.data.option;
    }

    //Format both args & tags into hashmaps
    let argMap = "";

    let tagMap = "";
    let tagKeys = Object.keys(tags);
    let tagValues = Object.values(tags);

    for (let i = 0; i < Math.max(slots.length, tagKeys.length); i++) {
        if(i < slots.length) argMap += `\n{indent}  put(${slots[i]}, ${args[i]});`;
        if(i < tagKeys.length) tagMap += `\n{indent}  put("${removeQuotes(tagKeys[i])}", "${removeQuotes(tagValues[i])}");`;
    }
    argMap = `new HashMap<>(){{${argMap}\n{indent}}}`;
    tagMap = `new HashMap<>(){{${tagMap}\n{indent}}}`;

    return `ParamManager.formatParameters(${argMap}, \n{indent}${tagMap}, "${codeBlock.action.replaceAll(/( $)|^ /gi, "")}", localVars)`;
}

function newImport(newLibraries) {
    for (let i = 0; i < newLibraries.length; i++) {
        let lib = newLibraries[i];
        if (!libraries.includes(lib)) libraries.push(lib);
    }
}

function textCodes(str) {
    let codes = {
        "%default": "event.getPlayer().getName()"
    };

    for (let i = 0; i < Object.keys(codes).length; i++) {
        let temp = Object.keys(codes)[i];
        str = str.replace(new RegExp(temp, "g"), `" + ${codes[temp]} + "`);
    }

    return str;
}


function removeQuotes(text) {
    return text.replaceAll(`"`, `\\"`);
}

function javafyParam(arg,slot) {
    switch (arg.id) {
        case "txt":
            return `"${removeQuotes(textCodes(arg.data.name))}"`;
        case "num":
            return arg.data.name + "f";
        case "snd":
            return `new DFSound("${arg.data.sound}", ${arg.data.pitch}f, ${arg.data.vol}f)`;
        case "loc":
            let loc = arg.data.loc;
            return `new Location(Bukkit.getServer().getWorlds().get(0), ${loc.x}, ${loc.y}, ${loc.z}, ${loc.yaw}, ${loc.pitch})`;
        case "item":
            return `DFUtilities.parseItemNBT("${removeQuotes(arg.data.item)}")`;
        case "pot":
            let potion = arg.data;
            return `new PotionEffect(PotionEffectType.${potionEffects()[potion.pot]}, ${potion.dur}, ${potion.amp}, ${slot})`;
        case "var":
            return `new DFVar("${removeQuotes(arg.data.name)}", ${varScopes()[arg.data.scope]})`;
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

function blockClasses(){
    return {
        "player_action": "PlayerAction",
        "set_var": "SetVariable"
    }
}

function blockParams(codeBlock){
    console.log(codeBlock);
    return{
        "player_action": `${getCodeArgs(codeBlock)}, "${codeBlock.action.replaceAll(/( $)|^ /gi, "")}", new LivingEntity[]{${selectionSyntax(codeBlock.target)}}`,
        "set_var": `${getCodeArgs(codeBlock)}, "${codeBlock.action.replaceAll(/( $)|^ /gi, "")}", new LivingEntity[]{${selectionSyntax(codeBlock.target)}}, localVars`
    }
}

function selectionSyntax(target) {
    let targetTypes = {
        default: "event.getPlayer()",
        AllPlayers: "p"
    };

    return target == null ?
        "event.getPlayer()" :
        targetTypes[target];
}
