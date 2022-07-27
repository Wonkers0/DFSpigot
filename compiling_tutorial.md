# 📙 How to compile your plugin
To compile all this to a jar for use on your server, you will need an IDE. You should've already installed one from the steps above, but if you've skipped them, we recommend [IntelliJ](https://www.jetbrains.com/idea/download/download-thanks.html?platform=windows&code=IIC). 
<br>
<br>
<br>
*(The following instructions are for **IntelliJ**)*
## Compiling for Maven Ⓜ️
If you are using maven, navigate to the right hand side of your screen, and click the button with the maven logo & trademark on it.


<img src="https://user-images.githubusercontent.com/106038003/179745648-7885d6f1-25dd-45fa-b743-60bb19eabd8e.png" height=500px; alt="mvnButton">

This will open a menu on the right side with some buttons at the top. Find the "m" letter and click it to open a terminal.


<img src="https://user-images.githubusercontent.com/106038003/179752913-1f8f6f33-62d6-49ae-b161-5d20b1d90c7a.png" height=500px; alt="mvnGoal">


In the terminal, you need to type `mvn clean package shade:shade`. Press enter, and wait for it to compile. You will then find your plugin jar at <br> 
`<intelliJ install directory>/IdeaProjects/<project name>/target/`

**The jar name is based off of the artifactId and version tags in your pom.xml** `(<artifactId>-<version>)`
