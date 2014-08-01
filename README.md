android-white-label-app
=======================

Android White Label Application for the Voting Information Project

Requires: Android Studio v0.8.0+


##Build Status
![Build Status](https://travis-ci.org/votinginfoproject/android-white-label-app.svg?branch=develop)

Dependencies:
-------------

Min SDK Version: 15 (Ice Cream Sandwich 4.0.3)
Target SDK Version: 19 (Android KitKat)
Android Support Library v4 revision 19.1


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

Install the Java SDK on your system.


#### Installing Android SDK Dependencies

Launch Android Studio and open this project directory via
File -> Open project.

Then, in Android Studio, go to Tools->Android->SDK Manager and
install the following:
  - Android KitKat (API V19)-> SDK Platform
  - Extras -> Android Support Library (Rev. 19.1)
  - Extras -> Google Play Services


#### Running the app

Sync your gradle build file if your IDE asks you to, and then run the app via Run->Run.


Testing the app
---------------

#### Creating the VIPAndroidTests Run Configuration

In Android Studio, go to Run->Edit Configurations.  

In the window that appears, click the green '+' in the top left and select 'Android Tests'  
from the dropdown. 

In the box on the right, rename the configuration 'Unnamed' to 'VIPAndroidTests'.  
Ensure under the General tab that Module is set to 'app' and the  
selected radio button for the Tests option is 'All in Module'.  
In the Target Device section, ensure that 'Show chooser dialog' is selected, this will allow  
you to select the device to run the tests on at runtime.  Optionally check 'Use same device'  
if you generally only use one device/emulator to test on.


#### Running Tests

Ensure that the VIPAndroidTests run configuration is selected in the dropdown in the  
second toolbar at the top of Android Studio, then select Run->Run or click the 'Run' button  
just to the right of the run configuration dropdown.

Android Studio will display a green "Tests Succeeded" or a red "Tests Failed" popup  
in the bottom center of the IDE on test completion.

