# 💎 DFSpigot
A tool for translating DiamondFire templates into Java Spigot Dependency Code.&nbsp;

If you wish to contribute, go ahead, but please follow the code standards laid out by the existing project files, and use the current systems I've created.
*(Unless you are trying to improve them, in which case, go for it.)*


## 📚 Utility Classes
The **java classes** found in this repository need to be imported to provide the methods that your plugin needs to run. But first, read the sections below to learn how to generate your DFPlugin.java class. This class will communicate with the other classes found in this repo to move your games from DiamondFire to a *server of your own*. 👍

### 💾 Setting up your project
Before you can compile the code, you need to first set up your project in a standard IDE. This tutorial covers [the IntelliJ IDE](https://www.jetbrains.com/idea/download/download-thanks.html?platform=windows&code=IIC), so install this first.

Once installed, you should see this welcome screen when opening the IDE for the first time:

<img src="https://user-images.githubusercontent.com/106038003/179754749-bd1dd846-dc9e-4969-adad-cf449aefd0ec.png" width=500px; height=350px; alt="welcomeMenu">

I have provided a starter project with all the boilerplate code you need. Download it [here](https://drive.google.com/drive/folders/17_R8zd2wP7fS9Sk1wV10HNqDKZxBQKew?usp=sharing).<br>

After downloading, Click on `"Open or Import"` from the welcome screen and import the project from the google drive.
<br>
Alternatively, if you've left the welcome screen or this isn't your first time using IntelliJ, navigate to `File -> Open` and import the project from there.

#### TODO: Mention reloading dependencies & add a "Downloading spigot" section

### 📦 Generating your DFPlugin.java class
In order to generate the code for your plugin, you first need to copy the template data from the line of code that you want to import. To do this, go up to the starting block of your code line, and break it while shifting. This will give you an enderchest containing the template data that you need, but before continuing, **make sure to place back the enderchest** to avoid losing the code. You will keep the item even after placing it back.
<br><br>
While holding the enderchest in your hand, run the command `/i nbt` in chat. This will send a large message with some gray text at the bottom.


<img src="https://user-images.githubusercontent.com/106038003/179759270-3ab19a91-d937-4e7d-9895-906abb05672d.png" height=350px; alt="nbtMsg">

Open your chat and click on `"Click to copy unformatted NBT"`. This will copy the data to your clipboard. Next, visit the [project's website](https://dfspigot.wonk2.repl.co/), and scroll down until you see an input field saying `"Insert Template Data Here..."`:

![nbtInput](https://user-images.githubusercontent.com/106038003/179760177-955f575e-23c7-47bc-8d73-f0d9bd90974b.png)

Paste the template data that you've previously copied by `Right Clicking -> Paste`. Now click "Generate Code". The code will appear in the text area below. Copy & Paste this in your DFPlugin.java file inside your IDE.
<br>

### 🎁 Importing Commands
TODO: Fill out this section once commands are supported.

## ⚠️ Please keep in mind that generating code from code lines containing unsupported actions will yield errors. You can view a list of all the supported actions [here](https://github.com/Wonkers0/DFSpigot/blob/main/supported_actions.md).
<br><br>
### 📙 How to compile your plugin
For compiling your plugin, visit [this](https://github.com/Wonkers0/DFSpigot/blob/main/compiling_tutorial.md)
