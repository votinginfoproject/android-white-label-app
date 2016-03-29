android-white-label-app
=======================

Android White Label Application for the Voting Information Project

Requires: 
Android Studio v0.8.0+

##Build Status
[![](https://travis-ci.org/votinginfoproject/android-white-label-app.svg?branch=develop)](https://travis-ci.org/votinginfoproject/android-white-label-app)

Dependencies:
-------------

Min SDK Version: 15 (Ice Cream Sandwich 4.0.3)

Target SDK Version: 22 (Android Lollipop 5.1)

Android Support Library v4 revision 22.2


Building/Running the app:
-------------------------

### Setting up the Java Environment

Install the [Java SDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html) on your system.


### Set up Git Repository

*  Install git.  For Windows users, you may wish to do so by installing [GitHub for Windows](https://windows.github.com/).  For Mac users, there is [GitHub for Mac](https://mac.github.com/).

*  Clone the project repository.  To do so with the GitHub application, click the plus sign in the upper left, and select to clone a repository.

    -  The project repository URL to clone with public access is:
    [https://github.com/votinginfoproject/android-white-label-app.git](https://github.com/votinginfoproject/android-white-label-app.git)


### Installing Android SDK Dependencies

*  Install [Android Studio](https://developer.android.com/sdk/installing/studio.html).

*  Launch Android Studio and select to 'Import Project' from the project directory cloned to above.

*  Then, in Android Studio, go to Tools->Android->SDK Manager and
install the following:

  -  Tools -> Android SDK Build-tools 22.2
  -  Android 5.1 (API v.22) -> SDK Platform and Google APIs
       - For running in an emulator, install one or both system images.  The Intel x86 system image will run much faster than ARM, especially if hardware acceleration is enabled.
  -  Extras -> Android Support Library
  -  Extras -> Android Support Repository
  -  Extras -> Google Play Services (v.22)
  -  Extras -> Google Repository
  -  For a faster emulator on Windows, Extras -> Intel x86 Emulator Accelerator


### Install device drivers and enable debug mode on device

*  Windows users will need to install the appropriate USB driver for their device in order to run the app on a device.  Please see the [list of available USB drivers](http://developer.android.com/tools/extras/oem-usb.html) and installation instructions.

* You can [enable debug mode on your device](http://developer.android.com/tools/device.html) to allow debugging over USB.


### Running in an emulator

The app will run on an Android emulator.  However, the map, directions, and distances to polling locations will not be available within the emulator, as these features depend on Google Play services, which are not available from within the emulator.

The emulator will run much faster with hardware acceleration enabled.  Please see the [directions on using the Android emulator and enabling hardware acceleration](http://developer.android.com/tools/devices/emulator.html).  For Windows, this uses the HAXM emulator accelerator package available under 'Extras' in the SDK Manager.

### Adding API keys for the app

*  Copy the `api_keys.xml.template` file from the `xmltemplates` directory to `app/src/main/res/values/api_keys.xml`.

    - On Windows, in the project directory at the command prompt, enter:
    
        copy xmltemplates\api_keys.xml.template /A app\src\main\res\values\api_keys.xml /A

    - For Mac or Linux, in the project directory in the console, enter:

        cp xmltemplates/api_keys.xml.template app/src/main/res/values/api_keys.xml

*  In the [Google Developer Console](https://console.developers.google.com), under the project APIs, enable:
    -  Directions API
    -  Google Civic Information API
    -  Google Maps Android API v2
    -  Google Analytics App Tracking SDK

*  Add your certificate fingerprint as an allowed Android application in the Google Developer Console

    -  [Get the SHA1 certificate fingerprint for your Android Studio installation.](https://developers.google.com/maps/documentation/android/start)  On Windows:
    
         -  Find the directory where Java is installed under Android Studio -> File -> Project Structure -> SDK location
         
         -  Open the command prompt and go into the Java bin directory:
            `cd <JDK directory>\bin`
            
         -  Use the Android keytool.exe program to get the SHA1 fingerprint for the debug key used to sign the app:
         
            ```
            keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
            ```
            
         -  Copy the SH1 fingerprint output by the above command.

    -  Go to Apis & auth -> Credentials -> Key for Android applications in the Google Developer Console.
    
    -  Add the following line under **Edit allowed Android applications**:
    
        -  `<SHA1 fingerprint>`;com.votinginfoproject.VotingInformationProject
    
*  Edit `app/src/main/res/values/api_keys.xml` in the project to add in your API keys from the [Google Developer Console](https://console.developers.google.com).

    -  Note that the Google Maps API uses the Android key, and the Google Directions and Civic Info APIs use the browser key.

### Set up Google Analytics
*  Copy the `app_tracker.xml.template` file from the `xmltemplates` directory to `app/src/main/res/xml/app_tracker.xml`.

    - On Windows, in the project directory at the command prompt, enter:

        copy xmltemplates\app_tracker.xml.template /A app\src\main\res\xml\app_tracker.xml /A

    - For Mac or Linux, in the project directory in the console, enter:

        cp xmltemplates/app_tracker.xml.template app/src/main/res/xml/app_tracker.xml

*  Edit `app/src/main/res/xml/app_tracker.xml` in the project to add in your [Google Analytics](http://www.google.com/analytics) tracking ID to the `ga_trackingId` field, if you have one.

### Set up Stoplight

 Due to the data from the Google Civic API changing frequently, it is beneficial to model responses to allow for uninterrupted development. 
 
 <b>This step is completely optional. If no spotlight API key is present the normal Google Civic API will be used instead.</b>
 
 Stoplight is a service that allows you to capture responses and it will generate the model based on the actual response. 
 
####Create the service
  
```
Create a Stoplight account at https://designer.stoplight.io/register 
Create a new workspace 'Pew' 
Create an API repo; name: VIP, initial protocol + host: https://www.googleapis.com, initial base path: /civicinfo/v2
Add an endpoint; Name: Voter Information, HTTP Method: GET, Path: /voterinfo
Authentication; API Key, add the key you obtained from 'Updating the Civic Info API Key'
```

####Add some data
 
 ```
 Under 'Responses' select "Add +"
 Select the 'Example' tab and paste in a JSON response
 Click 'Generate definition from example'
 Click 'Save endpoint'
 Select 'Mocking' and pick the endpoint you created to activate
 ```
You can create as many endpoints as you need and choose to mock with them or not mock at all and simply pass the request through to the actual API.


Example test data is located at:
[app/src/main/res/raw/test_response.json](app/src/main/res/raw/test_response.json)


### Running the app

Sync your gradle build file if your IDE asks you to, and then run the app via Run -> Run.


##Testing the app

### Creating the VIPAndroidTests Run Configuration

*  In Android Studio, go to Run -> Edit Configurations.

*  In the window that appears, click the green '+' in the top left and select 'Android Tests'
from the dropdown. 

*  In the box on the right, rename the configuration 'Unnamed' to 'VIPAndroidTests'.
Ensure under the General tab that Module is set to 'app' and the
selected radio button for the Tests option is 'All in Module'.
In the Target Device section, ensure that 'Show chooser dialog' is selected, this will allow
you to select the device to run the tests on at runtime.  Optionally check 'Use same device'
if you generally only use one device/emulator to test on.


### Running Tests

*  Ensure that the VIPAndroidTests run configuration is selected in the dropdown in the
second toolbar at the top of Android Studio, then select Run->Run or click the 'Run' button 
just to the right of the run configuration dropdown.

*  Android Studio will display a green "Tests Succeeded" or a red "Tests Failed" popup 
in the bottom center of the IDE on test completion.


##Troubleshooting Steps

If the project won't build, here are a few steps to try:

1.  Shelve any changes:  VCS -> Shelve Changes
2.  Sync the gradle files:  Tools -> Android -> Sync Project with Gradle Files
3.  Clean the project build:  Build -> Clean project

Note that if maps aren't displaying in the app, that's usually due to an issue with setting the applowed Android applications in the Google Developer Console.


##Deploying to the Play Store

### Rename the package
To publish the app in the Android Play Store, first rename the package so it will not conflict with other apps in the Play Store.

1.  Right-click on the package folder in Android Studio, in the project tool window on the left.
      - app -> src -> main -> java -> com.votinginfoproject.VotingInformationProject

2.  Select Refactor -> Rename from the context menu that opens.

3.  A prompt will ask if you really want to rename the package, or just the directory.  Select the blue default button to rename the package.

4.  In the dialog that opens, check the box to 'Search for text occurrences', and enter the new package name.

5.  Rebuild the app.

6.  Sign into the Google Developer Console and update the package name listed for the allowed Android application entries to use the new package name.

### Generate signed APK with release key

1.  In Android Studio, go to Build -> Generate Signed APK in the menu.
2.  Select to create a new key.  Note that the key store password and alias password should match.
3.  In the 'Build Type' drop-down on the last screen of the wizard, select 'release'.  Note the APK destination folder.
4.  Click 'Finish' to generate the signed APK, which you may find in the APK destination folder to upload to the Play Store.

### Update API settings for new package name and signing key
The new key generated to sign the app for release will need its SHA1 added to the Google Developer Console as an allowed Android application, as for the debug key above.
Follow the steps above for adding an allowed Android application, but now run the `keytool` command for the new signing key:

```
keytool -list -v -keystore "%USERPROFILE%\.android\<your_key.jks>" -alias <your_alias> -storepass <your_password> -keypass <your_password>
```

When adding the SHA1 to the Google Developer Console, note that the package name used should be the new name set above, and that the package name for the debug key will need to be changed to the new package name as well.