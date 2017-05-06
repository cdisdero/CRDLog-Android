package com.chrisdisdero.crdlog;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class that represents an app-wide logging facility.
 *
 * @author cdisdero
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
public class CRDLog {

    //region Private members

    /**
     * Log tag for this class.
     */
    private static final String TAG = CRDLog.class.getCanonicalName();

    /**
     * The execution queue used to queue up operations on the log file.
     */
    private ExecutorService executor = null;

    /**
     * The log file reference.
     */
    private File logFile = null;

    /**
     * Flag to disable writing messages to the log file.
     */
    private AtomicBoolean disableLogFileWrites = new AtomicBoolean(false);

    /**
     * Instance of {@link CRDLogHeaderInterface} which will provide a header for the log file when needed.
     */
    private CRDLogHeaderInterface logHeaderProvider = null;

    //endregion

    //region Constructors

    /**
     * Instantiates a new {@link CRDLog} object.
     *
     * @param logFile A reference to the {@link File} representing the log file on disk.
     */
    public CRDLog(File logFile, CRDLogHeaderInterface logHeaderProvider) {

        // Store a reference to the log file passed in.
        this.logFile = logFile;

        // Store a reference to the log header provider passed in.
        this.logHeaderProvider = logHeaderProvider;

        // Start the execution queue for log operations.
        executor = Executors.newSingleThreadExecutor();
    }

    //endregion

    //region Public methods

    /**
     * Method to get the current contents of the log file.
     *
     * @param clearAfterGet Flag to indicate whether to clear the log file after getting the contents.
     * @param completion A completion handler that implements {@link CRDLogContentInterface}.
     */
    public void get(final boolean clearAfterGet, @Nullable final CRDLogContentInterface completion) {

        executor.submit(new Runnable() {

            @Override
            public void run() {

                // If the completion handler passed in is null, then clear if specified and exit.
                if (completion == null) {

                    if (clearAfterGet) {

                        clear();
                    }

                    return;
                }

                String logContent = null;

                if (logFile.exists()) {

                    FileInputStream fileInputStream = null;

                    try {

                        fileInputStream = new FileInputStream(logFile);

                    } catch (FileNotFoundException exception) {

                        Log.e(TAG, "Failed to create input stream for log file.", exception);
                    }

                    if ( fileInputStream != null ) {

                        boolean wasSuccessful = true;
                        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString = "";
                        StringBuilder stringBuilder = new StringBuilder();

                        try {

                            while ((receiveString = bufferedReader.readLine()) != null) {

                                stringBuilder.append(receiveString).append("\r\n");
                            }

                        } catch (IOException exception) {

                            wasSuccessful = false;
                            Log.e(TAG, "Failed to read contents of log file.", exception);
                        }

                        try {

                            fileInputStream.close();

                        } catch (IOException exception) {

                            wasSuccessful = false;
                            Log.e(TAG, "Failed to close log file.", exception);
                        }

                        if (wasSuccessful) {

                            logContent = stringBuilder.toString();

                            if (clearAfterGet) {

                                clear();
                            }
                        }
                    }
                }

                // Call the completion handler with the log file content found.
                completion.onLogContent(logContent);
            }
        });
    }

    /**
     * Method to get the current contents of the log file and then clear it.
     *
     * @param completion A completion handler that implements {@link CRDLogContentInterface}.
     */
    public void get(@Nullable final CRDLogContentInterface completion) {

        get(false, completion);
    }

    /**
     * Method to clear the log file.
     */
    public void clear() {

        executor.submit(new Runnable() {

            @Override
            public void run() {

                if (logFile.exists()) {

                    logFile.delete();
                }
            }
        });
    }

    /**
     * Method to log an informational message to the log file.
     *
     * @param tag The message tag to use.
     * @param format The message or format for the message.
     * @param args Zero or more format message parameters.
     */
    public void info(@NonNull String tag, @NonNull String format, Object... args) {

        String message = String.format(format, args);

        int logResult = Log.i(tag, message);
        if (logResult > 0) {

            log(tag, "info", message);
        }
    }

    /**
     * Method to log an informational message about a {@link Throwable} to the log file.
     *
     * @param tag The message tag to use.
     * @param throwable The {@link Throwable} to log.
     */
    public void info(@NonNull String tag, @NonNull Throwable throwable) {

        int logResult = Log.i(tag, "", throwable);
        if (logResult > 0) {

            String message = Log.getStackTraceString(throwable);
            log(tag, "info", message);
        }
    }

    /**
     * Method to log an warning message to the log file.
     *
     * @param tag The message tag to use.
     * @param format The message or format for the message.
     * @param args Zero or more format message parameters.
     */
    public void warn(@NonNull String tag, @NonNull String format, Object... args) {

        String message = String.format(format, args);

        int logResult = Log.w(tag, message);
        if (logResult > 0) {

            log(tag, "warn", message);
        }
    }

    /**
     * Method to log a warning message about a {@link Throwable} to the log file.
     *
     * @param tag The message tag to use.
     * @param throwable The {@link Throwable} to log.
     */
    public void warn(@NonNull String tag, @NonNull Throwable throwable) {

        int logResult = Log.w(tag, "", throwable);
        if (logResult > 0) {

            String message = Log.getStackTraceString(throwable);
            log(tag, "warn", message);
        }
    }

    /**
     * Method to log a debug message to the log file.
     *
     * @param tag The message tag to use.
     * @param format The message or format for the message.
     * @param args Zero or more format message parameters.
     */
    public void debug(@NonNull String tag, @NonNull String format, Object... args) {

        String message = String.format(format, args);

        int logResult = Log.d(tag, message);
        if (logResult > 0) {

            log(tag, "debug", message);
        }
    }

    /**
     * Method to log a debug message about a {@link Throwable} to the log file.
     *
     * @param tag The message tag to use.
     * @param throwable The {@link Throwable} to log.
     */
    public void debug(@NonNull String tag, @NonNull Throwable throwable) {

        int logResult = Log.d(tag, "", throwable);
        if (logResult > 0) {

            String message = Log.getStackTraceString(throwable);
            log(tag, "debug", message);
        }
    }

    /**
     * Method to log an error message to the log file.
     *
     * @param tag The message tag to use.
     * @param format The message or format for the message.
     * @param args Zero or more format message parameters.
     */
    public void error(@NonNull String tag, @NonNull String format, Object... args) {

        String message = String.format(format, args);

        int logResult = Log.e(tag, message);
        if (logResult > 0) {

            log(tag, "error", message);
        }
    }

    /**
     * Method to log an error message about a {@link Throwable} to the log file.
     *
     * @param tag The message tag to use.
     * @param throwable The {@link Throwable} to log.
     */
    public void error(@NonNull String tag, @NonNull Throwable throwable) {

        int logResult = Log.e(tag, "", throwable);
        if (logResult > 0) {

            String message = Log.getStackTraceString(throwable);
            log(tag, "error", message);
        }
    }

    /**
     * Enables/disables logging messages to the log file.
     *
     * @param enable True to enable logging to the log file, false to disable.
     */
    public synchronized void enableLogging(boolean enable) {

        disableLogFileWrites.set(!enable);
    }

    //endregion

    //region Private methods

    /**
     * Method to write the specified message information to the log file.
     *
     * @param messageTag The message tag to write.
     * @param messageType The message type to write.
     * @param messageContent The message content to write.
     */
    private void log(final String messageTag, final String messageType, final String messageContent) {

        executor.submit(new Runnable() {

            @Override
            public void run() {

                // Bail out if we are not allowing logfile writes.
                if (disableLogFileWrites.get()) {

                    return;
                }

                if (!logFile.exists()) {

                    try {

                        logFile.createNewFile();

                    } catch (IOException exception) {

                        Log.e(TAG, "Failed to create new log file.", exception);
                    }
                }

                // If the log file is empty, then set a flag to later write out a header.
                boolean writeHeader = logFile.length() == 0;

                FileOutputStream fileOutputStream = null;

                try {

                    fileOutputStream = new FileOutputStream(logFile, true);

                } catch (FileNotFoundException exception) {

                    Log.e(TAG, "Failed to create output stream for log file.", exception);
                }

                // Write the message to the log with a timestamp

                OutputStreamWriter writer = null;

                if (logFile != null) {

                    writer = new OutputStreamWriter(fileOutputStream);
                }

                if (writer != null) {

                    try {

                        if (writeHeader && (logHeaderProvider != null)) {

                            String header = logHeaderProvider.onProvideHeader();
                            if (header != null && header.length() > 0) {

                                writer.write(header);
                            }
                        }

                        writer.write(String.format("%1s (%2s) [%3s]: %4s\r\n", getDateTimeStamp(), messageType, messageTag, messageContent));

                    } catch (Exception exception) {

                        Log.e(TAG, "Failed to write message to log file.", exception);
                    }

                    try {

                        writer.flush();
                        writer.close();

                    } catch (IOException exception) {

                        Log.e(TAG, "Failed to close the log file.", exception);
                    }
                }
            }
        });
    }

    /**
     * Gets a stamp containing the current date and time to write to the log.
     *
     * @return The stamp for the current date and time.
     */
    private String getDateTimeStamp() {

        return new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SSS").format(Calendar.getInstance().getTime());
    }

    //endregion
}
