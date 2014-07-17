android-white-label-app
=======================

Android White Label Application for the Voting Information Project

Requires: Android Studio v0.8.0+


Dependencies:
-------------

Min SDK Version: 15 (Ice Cream Sandwich 4.0.3)
Target SDK Version: 20 (Android L)
Android Support Library v4 revision 20
Java 8 (used for lambda support)


Adding API keys for the app:
----------------------------
Copy the api_keys.xml.template file to api_keys.xml: 
```
cp xmltemplates/api_keys.xml.template app/src/main/res/values/api_keys.xml
```
Edit api_keys.xml to add in your API keys from the [Google Developer Console](https://console.developers.google.com).  
Note that the Google Maps API key is the Anroid key, and the Civic Info API key is the browser key.


Building/Running the app:
-------------------------

#### Setting up the Java Environment

First, install Java 8 on your system. Once it is installed, we need to set the
JAVA8_HOME environment variable with the installed location. To find the install dir,
execute from a command line:
```
java -verbose
```

At the very end of the output you should see a line like:
```
[Loaded java.lang.Shutdown from /usr/lib/jvm/java-8-oracle/jre/lib/rt.jar]
```

The path you want is everything in the path before jre/lib/rt.jar,
so in the example above the java path would be:
```
/usr/lib/jvm/java-8-oracle
```

We now need to set the path we found above to the JAVA8_HOME environment variable.
To do this, open the file ~/.profile on Linux/Mac and add the following line at the end:
```
export JAVA8_HOME=<path we found above>
## Example for above path:
export JAVA8_HOME=/usr/lib/jvm/java-8-oracle
```

Save and quit the file, then log out of your desktop and log back in to load this variable.
To verify the path loads correctly, you can open a terminal after looging back
in and running:
```
env | grep JAVA
```

One of the printed lines should have JAVA8_HOME in it.


#### Installing Android SDK Dependencies

Launch Android Studio and open this project directory via
File -> Open project.

Then, in Android Studio, go to Tools->Android->SDK Manager and
install the following:
  - Android L (API V20)-> SDK Platform
  - Extras -> Android Support Library (Rev. 20)
  - Extras -> Google Play Services


#### Running the app

Sync your gradle build file if your IDE asks you to, and then run the app via Run->Run.


#### Testing the app

Select the VIPAndroidTests configuration in Android Studio  
and choose a device to run them on from the prompt that appears.

Android Studio will display a green "Tests Succeeded" or a red "Tests Failed" popup  
in the bottom center of the IDE on test completion.

