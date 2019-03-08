# CRDLog-Android

[![Release](https://jitpack.io/v/cdisdero/CRDLog-Android.svg)](https://jitpack.io/#cdisdero/CRDLog-Android)

Simple straightforward logging facility for Android projects.

- [Overview](#overview)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [Conclusion](#conclusion)
- [License](#license)

## Overview
This code library provides a simple way to enable logging of application activity and errors to a file and to the Android log at the same time from anywhere in your app.

## Requirements
- Android API 16 or higher
- Android Studio 3.3.1+
- Java 1.8+

## Installation
You can simply copy the following files from the GitHub tree into your app project:

  * `CRDLog.java`
    - Class providing the logging capabilities.

  * `CRDLogContentInterface.java`
    - An interface providing a way for getting the current contents of the log file as a String.
    
  * `CRDLogHeaderInterface.java`
    - An interface providing a way for supplying the log file header content as a String when needed.    

### JitPack
Alternatively, you can install it via [JitPack.io](https://jitpack.io/#cdisdero/CRDLog-Android)

To integrate CRDLog into your Android Studio app project, add the following to your root build.gradle at the end of repositories:

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Then, add this dependency to your app build.gradle file:

```
	dependencies {
		compile 'com.github.cdisdero:CRDLog-Android:1.0.2'
	}
```

## Usage
The library is easy to use.  There is an example of usage in the sample app included.  Just import CRDLog and CRDLogHeaderInterface classes in your Activity or Application-derived object and create a new instance of CRDLog:

```
import com.chrisdisdero.crdlog.CRDLog;
import com.chrisdisdero.crdlog.CRDLogHeaderInterface;

...

// Create a new log instance and specify the header to use when needed.
CRDLog log = new CRDLog(new File(getApplicationContext().getFilesDir(), "applog.txt"), new CRDLogHeaderInterface() {

    @Override
    public String onProvideHeader() {

        StringBuilder header = new StringBuilder();

        ApplicationInfo applicationInfo = getApplicationInfo();
        String appName = applicationInfo.loadLabel(getPackageManager()).toString();
        int targetApiLevel = applicationInfo.targetSdkVersion;
        header.append(String.format("App: %s\n", appName));
        header.append(String.format("Target API: %d\n", targetApiLevel));
        header.append(String.format("Device API: %d\n", Build.VERSION.SDK_INT));
        header.append(String.format("Device name: %s\n", Build.DEVICE));
        header.append(String.format("Device model: %s\n", Build.MODEL));
        header.append(String.format("Device manufacturer: %s\n", Build.MANUFACTURER));

        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {

            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            header.append(String.format("Available memory: %d\n", memoryInfo.availMem));
        }

        return header.toString();
    }
});

```

In this example, we pass a new File instance using the Application context's getFilesDir() to contain the 'applog.txt' log file.

We also pass a new instance of CRDLogHeaderInterface and implement the method onProvideHeader() to provide the header which will get written to the log whenever you log a message and the log is empty.

To start logging, use the `info`, `warn`, `debug`, and `error` methods:

```
// Write an 'info' message to the log.
log.info(TAG, "this is an %s message", "informational");

// Write a 'warning' message to the log.
log.warn(TAG, "this is a %s message", "warning");

// Write a 'debug' message to the log.
log.debug(TAG, "this is a %s message", "debug");

// Write an 'error' message to the log.
log.error(TAG, "this is an %s message", "error");
```

There's an additional overload of each these methods which takes an Throwable-derived object to log.  The method `Log.getStackTraceString()` is used to format a stack trace for the Throwable that is logged to the file:

```
...
} catch (IOException e) {

  // Write an Exception object to the log.
  log.error(TAG, e);
}
```

For all these methods that write to the log file, in addition, they call the corresponding android.util.Log method to log output to the console.  So for `CRDLog.info`, as an example, `Log.i` is called.  For `CRDLog.error`, `Log.e` is called, etc.

You can completely disable CRDLog from writing messages to the log file by calling the method `enableLogging`

```
// Enable logging to the log file
log.enableLogging(true);

...

// Disable logging to the log file and only log to the debug console
log.enableLogging(false);

...
```

When you call `enableLogging(false)` you are making CRDLog basically functionally equivalent to the android.util.Log methods for logging to the console.

The format of the log entries in the log file are as follows:

```
Current local time:       Type:        Tag:   Message:

[MM-dd-yyyy HH:mm:ss.SSS] (entry type) [TAG]  message
```

As an example, here is an info entry:

```
log.info("com.chrisdisdero.MyApp.MyClass", "This is info");
 
 
05-03-2017 19:37:53.397 (info) [com.chrisdisdero.MyApp.MyClass] This is info
```

Here is an example error entry:
```
log.error("com.chrisdisdero.MyApp.MyClass", "This is an error");
 
 
05-03-2017 19:37:53.397 (error) [com.chrisdisdero.MyApp.MyClass] This is an error

```

An exception is logged as:
```
log.error(TAG, new Exception("This is my exception"));
 
  
05-03-2017 19:37:53.397 (error) [com.chrisdisdero.MyApp.MyClass] java.lang.Exception: This is my exception
       at com.chrisdisdero.crdlog_android.MainActivity$2.onClick(MainActivity.java:93)
       at android.view.View.performClick(View.java:5637)
       at android.view.View$PerformClick.run(View.java:22429)
       at android.os.Handler.handleCallback(Handler.java:751)
       at android.os.Handler.dispatchMessage(Handler.java:95)
       at android.os.Looper.loop(Looper.java:154)
       at android.app.ActivityThread.main(ActivityThread.java:6119)
       at java.lang.reflect.Method.invoke(Native Method)
       at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:886)
       at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:776)
```

## Conclusion
I hope this small library is helpful to you in your next Android project.  I'll be updating as time and inclination permits and of course I welcome all your feedback.

## License
CRDLog-Android is released under an Apache 2.0 license. See LICENSE for details.
