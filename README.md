# 💎 [DFSpigot](https://dfspigot.wonk2.repl.co)
A tool for translating DiamondFire templates into Java Spigot Dependency Code.&nbsp;

If you wish to contribute, go ahead, but please follow the code standards laid out by the existing project files, and use the current systems I've created.
*(Unless you are trying to improve them, in which case, go for it.)*


## 📚 Utility Classes
The **java classes** found in this repository need to be imported to provide the methods that your plugin needs to run. But first, read the sections below to learn how to generate your DFPlugin.java class. This class will communicate with the other classes found in this repo to move your games from DiamondFire to a *server of your own*. 👍

### 💾 Setting up your project
Before you can compile the code, you need to first set up your project in a standard IDE. This tutorial covers [the IntelliJ IDE](https://www.jetbrains.com/idea/download/download-thanks.html?platform=windows&code=IIC), so install this first.

Once installed, you should see this welcome screen when opening the IDE for the first time:

<img src="https://user-images.githubusercontent.com/106038003/179754749-bd1dd846-dc9e-4969-adad-cf449aefd0ec.png" width=500px; height=350px; alt="welcomeMenu">

I have provided a starter project with all the boilerplate code you need. Download it [here](https://drive.google.com/drive/folders/17_R8zd2wP7fS9Sk1wV10HNqDKZxBQKew?usp=sharing). **[OUTDATED]**<br>

After downloading, Click on `"Open or Import"` from the welcome screen and import the project from the google drive.
<br>
Alternatively, if you've left the welcome screen or this isn't your first time using IntelliJ, navigate to `File -> Open` and import the project from there.

## ⭐ Downloading Spigot
At first, IntelliJ might not recognize some of the dependencies in your project. We'll worry about that later. Spigot, which is the main API powering minecraft plugins is not like other maven dependencies, as it's not part of the maven public repository, which means you will need to have it locally stored on your computer in order to compile properly.

To get started, download [BuildTools](https://www.spigotmc.org/wiki/buildtools/). This will let you download spigot later on. After you've downloaded it, it'll probably be lying somewhere in your *Downloads* folder.

Open up the windows command prompt (or any equivalent on other operating systems), and type `cd <BuildTools Directory Path`. The directory path should be pointing to the folder that BuildTools.jar is in, not BuildTools itself. So for example: `C:\Users\Wonk\Downloads` instead of `C:\Users\Wonk\Downloads\BuildTools.jar`

If you don't know where your BuildTools ended up, you can view so from your browser's downloads tab. On most browsers, the shortcut CTRL + J will open it up.

Once you've typed the command, you should see that your command directory has changed. Next, type `java -jar BuildTools.jar --rev 1.19`
Downnloading might take a few minutes, but don't close the tab until it's done.

### 🔄 Reloading Maven Dependencies
Now in order to load your dependencies, navigate to the right hand side of your screen, and click the button with the maven logo & trademark on it.

<img src="https://user-images.githubusercontent.com/106038003/179745648-7885d6f1-25dd-45fa-b743-60bb19eabd8e.png" height=500px; alt="mvnButton">

This will open a menu on the right, in which you need to click the arrows spinning counterclockwise which say "Reload All Maven Projects", fouund in the top left of the menu.

<img src="https://user-images.githubusercontent.com/106038003/181191826-ce61cf87-fdbd-4baf-9c80-ab8fcd4e6104.png" height=300px; alt="mvnReload">

Reloading could take a bit on lower-end machines, but you can view the progress in the bottom right.

<img src="https://user-images.githubusercontent.com/106038003/181192690-5bb6174d-6581-4907-a83f-ba3f9fc5a206.png" height=60px;>
<br>


## 📦 Generating your DFPlugin.java class
In order to generate the code for your plugin, you first need to copy the template data from the line of code that you want to import. To do this, go up to the starting block of your code line, and break it while shifting. This will give you an enderchest containing the template data that you need, but before continuing, **make sure to place back the enderchest** to avoid losing the code. You will keep the item even after placing it back.
<br><br>
While holding the enderchest in your hand, run the command `/i nbt` in chat. This will send a large message with some gray text at the bottom.


<img src="https://user-images.githubusercontent.com/106038003/179759270-3ab19a91-d937-4e7d-9895-906abb05672d.png" height=350px; alt="nbtMsg">

Open your chat and click on `"Click to copy unformatted NBT"`. This will copy the data to your clipboard. Next, visit the [project's website](https://dfspigot.wonk2.repl.co/), and scroll down until you see an input field saying `"Insert Template Data Here..."`:

<img src="https://user-images.githubusercontent.com/106038003/179760177-955f575e-23c7-47bc-8d73-f0d9bd90974b.png" alt="nbtInput">

Paste the template data that you've previously copied by `Right Clicking -> Paste`. Now click "Generate Code". The code will appear in the text area below. Copy & Paste this in your DFPlugin.java file inside your IDE.
<br>

### 🎁 Importing Commands
TODO: Fill out this section once commands are supported.

## ⚠️ Please keep in mind that generating code from code lines containing unsupported actions will yield errors. You can view a list of all the supported actions [here](https://github.com/Wonkers0/DFSpigot/blob/main/supported_actions.md).
<br><br>
### 📙 How to compile your plugin
For compiling your plugin, visit [this](https://github.com/Wonkers0/DFSpigot/blob/main/compiling_tutorial.md)
