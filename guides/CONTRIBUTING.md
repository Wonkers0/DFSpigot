# Hello 👋

### I'm glad you want to contribute to this project; 
### This is a basic introductory guide to Git to set up your project from the dev branch. If you already know how to use Git, you can skip to the [code documentation.](https://github.com/Wonkers0/DFSpigot/blob/main/guides/docs.md)

### ⚠ Make sure you clone the `dev` branch, **NOT** the `main` branch. Also make your pull requests there, not here.
<br>

>If you don't yet have Git installed on your computer, you can get it [here](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
<br><br>

# Setting up your project
First, set up a new plugin like you would for a typical project. In IntelliJ, you can do this by using the [Minecraft Development](https://plugins.jetbrains.com/plugin/8327-minecraft-development) plugin, and
selection `Minecraft` when making a new project, like so:

![image](https://user-images.githubusercontent.com/106038003/205464945-002f2330-7c70-4c2a-b0dd-9d9753f0cc3b.png)

*Choose "Spigot Plugin" and use JDK 17*


Once you've created the project, it should look like this:
![image](https://user-images.githubusercontent.com/106038003/205465192-28178b69-8cfb-4cc0-885d-f4d9e078e000.png)

### Delete `pom.xml`, `.gitignore` and the `src` folder

<hr />
<br>

At the bottom of the screen, you should see a bar with a bunch of buttons, click `Terminal`
![image](https://user-images.githubusercontent.com/106038003/205467057-eec50a00-2bff-4c1e-a53e-24df593a3805.png)

In the terminal, type `git clone --branch dev https://github.com/Wonkers0/DFSpigot.git`
A new folder called `DFSpigot` should have appeared. If you can't see it yet, click on any file or directory and it should update.

After having deleted the `src` folder and `pom.xml` & `.gitignore` files that came with creating your project, you should see this, where "GithubTest" is replaced with the name of your project.

![image](https://user-images.githubusercontent.com/106038003/205467276-c523f503-8131-4dda-a6f2-1c7370540b78.png)

### Move everything out of the `DFSpigot` folder and delete it.


## 🥳 You're done! To familiarize yourself with the existing code, visit the [documentation](https://github.com/Wonkers0/DFSpigot/blob/main/guides/docs.md)

