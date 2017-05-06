package com.chrisdisdero.crdlog_android;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import com.chrisdisdero.crdlog.CRDLog;
import com.chrisdisdero.crdlog.CRDLogHeaderInterface;

import java.io.File;

/**
 * The {@link Application} object representing the app.
 *
 * @author cdisdero
 *
 *
Copyright © 2017 Christopher Disdero.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
public class MyApplication extends Application {

    //region Private members

    /**
     * Log tag for this class.
     */
    private final String TAG = MyApplication.class.getCanonicalName();

    /**
     * Reference to the app-wide {@link CRDLog} representing the log.
     */
    private CRDLog log = null;

    /**
     * Singleton reference to the {@link MyApplication} object for use across the app.
     */
    private static MyApplication application = null;

    //endregion

    //region Singleton accessor

    /**
     * Gets the {@link MyApplication} object for use anywhere in the app.
     *
     * @return The {@link MyApplication} object representing this application.
     */
    public static MyApplication getApp() {

        return application;
    }

    //endregion

    //region Public properties

    /**
     * Gets the {@link CRDLog} for logging application activity.
     *
     * @return The {@link CRDLog} log object.
     */
    public CRDLog getLog() {

        return log;
    }

    //endregion

    //region Overrides

    @Override
    public void onCreate() {

        super.onCreate();

        // Assign this application object to the singleton member.
        application = this;

        // Create a new app-wide log object and specify the header to use when needed.
        log = new CRDLog(new File(getApplicationContext().getFilesDir(), "applog.txt"), new CRDLogHeaderInterface() {

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
    }

    //endregion
}
