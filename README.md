# OurvleDownloader
Beta version 2.0 of course_mat_downloader created using Javafx. The applicataion provides a GUI interface for user login, viewing and downloading course content from the UWI Mona's OurVLE website through their provided API. 

## Dependencies
* [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [jfoenix-8.0.1.jar](http://www.jfoenix.com/download/jfoenix-8.0.1.jar)
* json.jar

## Compatibility
The source code and .jar has been tested on both linux and windows OS (windows 8.1 and newer and linux kernel version 4.13.0.), and requires JDK or JRE 1.8.0 minimum to run.

## Installation
No real installation required. Both the .jar file and .exe can be placed in any directory the user sees fit. A config file will be generated in the same directory the application is opened in on start up. It is recommended to create a specified directory for the application (.jar or .exe) file and create desktop shortcut for easy access to the application.

## Usage

### Windows
Double click the .jar or .exe file or the application to run it.

### Linux
Run the .jar file from the command line in linux using the java command.
  - eg. java -Dfile.encoding=UTF-8 -jar /path/to/jarFile/from/root/OurvleDownloader.jar
