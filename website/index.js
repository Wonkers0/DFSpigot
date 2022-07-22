import {generate} from "./script.js";

document.querySelector('#generate').onclick = () => {generate()}
document.querySelector('#NBTInput').onkeyup = function(e) {
    // when enter
    if (e.keyCode === 13) {
        generate()
    }
}

document.querySelector('#navIcon').onclick = () => {location.href = "."}
document.querySelector('.help').onclick = () => {location.href = "https://github.com/Wonkers0/DFSpigot/blob/main/README.md"};

document.querySelector('#topGen').onclick = () => {document.getElementsByClassName("main3")[0].scrollIntoView({behavior:"smooth"});}


resize();
window.onresize = resize;

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
