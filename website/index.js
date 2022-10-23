import {generate, threads, getRoot} from "./script.js";

document.querySelector('#generate').onclick = () => {generate()}
document.querySelector('#navIcon').onclick = () => {location.href = "."}
document.querySelector('.help').onclick = () => {location.href = "https://github.com/Wonkers0/DFSpigot/blob/main/README.md"};
document.querySelector('#topGen').onclick = () => {document.getElementsByClassName("main3")[0].scrollIntoView({behavior:"smooth"});}

var templates = {};

function pasteTemplate(el){
  if(event.shiftKey){
    el.remove()
    return;
  }
  
  navigator.clipboard.readText()
  .then(text => {
    let root = getRoot(text)
    if(root != null){
      console.log(templates[el])
      if(templates[el] != null) threads.splice(threads.indexOf(templates[el]), 1)
      templates[el] = text
      threads.push(text)

      let icons = {
        "event": "fa-solid fa-cloud-bolt",
        "func": "fa-solid fa-clock-rotate-left",
        "process": "fa-solid fa-arrows-split-up-and-left"
      }
      
      let icon = el.children[0];
      icon.classList = icons[root.block]
      icon.classList.add("pasteTemplate")
      el.firstElementChild.firstElementChild.innerHTML = root.data == null ? root.action : root.data;
    }
    else alert(`Not a valid template:\n\n${text}`)
  })
  .catch(err => {alert('Failed to read clipboard contents: ' + err);});
}
function showTooltip(el){
  if(el.children[0].innerHTML == "") return
  el.children[0].style.opacity = 1
  el.children[0].style.userSelect = "normal"
}
function hideTooltip(el){
  el.children[0].style.opacity = 0
  el.children[0].style.userSelect = "hidden"
}

let newTemplate = document.querySelector("#newTemplate")
newTemplate.onclick = newTemplateFunc

function newTemplateFunc(){
  let div = document.createElement("div")
  div.innerHTML = `<span class="iconWrapper" id="pasteTemplate"> <i class="fa-regular fa-paste pasteTemplate"> <p class="tooltip"></p> </i> </span>`
  let newNode = div.firstElementChild
  newNode.onclick = () => {pasteTemplate(newNode)}
  newNode.onmouseover = () => {showTooltip(newNode.firstElementChild)}
  newNode.onmouseleave = () => {hideTooltip(newNode.firstElementChild)}
  document.querySelector(".templateGrid").insertBefore(newNode, newTemplate)
  templates[newNode] = null;
}

newTemplateFunc()
resize()
window.onresize = resize

function resize(){
  if(window.innerWidth < 620){
    document.querySelector(".sideNav").innerHTML = `
                <a href="" class="navLink"><i class="fa-solid fa-circle-dollar-to-slot fa-2x"></i></a>
                <a href="https://github.com/Wonkers0/DFSpigot" class="navLink"><i class="fa-brands fa-github-square fa-2x"></i></a>
                <button class="discordBtn"><i class="fa-brands fa-discord"></i></button>`;
  }
  else{
    document.querySelector(".sideNav").innerHTML = `
                <a href="" class="navLink"><i class="fa-solid fa-circle-dollar-to-slot"></i> Donate</a>
                <a href="https://github.com/Wonkers0/DFSpigot" class="navLink"><i class="fa-brands fa-github-square"></i> Github Repo</a>
                <button class="discordBtn">
                    <i class="fa-brands fa-discord" style="margin-right: 8px;"></i> 
                    Discord
                </button>`;
  }

  document.querySelector('.discordBtn').onclick = () => {location.href = "https://discord.gg/6j5NhPuZ6B"};
}

export default function threadCodes(root, rootEvent, specifics){
  return {
  "event": [
      "@EventHandler",
      [
        `public void ${root.action} (${rootEvent} event){`,
        "double threadID = new Random().nextDouble();",
        "int funcStatus;",
        "HashMap<String, DFValue> localVars = new HashMap<>();",
        `HashMap<String, LivingEntity> targets = new HashMap<>(){{${Object.keys(specifics["targets"]).map(key => `\n{indent}  put("${key}", ${specifics["targets"][key]});`).join('')}\n{indent}}};`,
        "",
        `HashMap<String, Object> specifics = new HashMap<>(){{${Object.keys(specifics).filter(key => key != "targets").map(key => `\n{indent}  put("${key}", ${specifics[key]});`).join('')}\n{indent}}};`,
        ""
      ],
      "}"
    ],
  "func": [
      [
        `public int ${root.data} (double threadID, HashMap<String, DFValue> localVars, HashMap<String, LivingEntity> targets, HashMap<String, Object> specifics){`,
        "}"
      ]
    ],
  "process": [ // TODO: Add target hashmap for processes once they're implemented
      [
        "//Processes are not supported!"
      ]
    ]
  }
}
