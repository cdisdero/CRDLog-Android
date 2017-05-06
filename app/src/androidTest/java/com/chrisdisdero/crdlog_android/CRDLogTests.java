package com.chrisdisdero.crdlog_android;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.chrisdisdero.crdlog.CRDLog;
import com.chrisdisdero.crdlog.CRDLogContentInterface;
import com.chrisdisdero.crdlog.CRDLogHeaderInterface;
import com.chrisdisdero.crdtestexpectation.CRDTestExpectation;
import com.chrisdisdero.crdtestexpectation.CRDTestExpectationStatus;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Instrumentation tests for the {@link CRDLog} class.
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
@RunWith(AndroidJUnit4.class)
public class CRDLogTests {

    //region Private members

    /**
     * The logging TAG for this class.
     */
    private static final String TAG = CRDLogTests.class.getCanonicalName();

    /**
     * Test expectation to wait for and signal on asynchronous task completion.
     */
    private CRDTestExpectation expectation = null;

    /**
     * The default time to wait for a test expectation before timing out.
     */
    private long defaultTestTimeout = 5000;

    //endregion

    //region Utility methods

    /**
     * Counts the matches to the specified pattern in the specified string.
     *
     * @param pattern The {@link Pattern} to search for in the string.
     * @param string The string to search.
     *
     * @return The number of times the specified pattern is found in the string.
     */
    static int countMatches(Pattern pattern, String string) {

        Matcher matcher = pattern.matcher(string);

        int count = 0;
        int pos = 0;
        while (matcher.find(pos)) {

            count++;
            pos = matcher.start() + 1;
        }

        return count;
    }

    //endregion

    //region Tests

    @Test
    public void testBasicLogging() throws Exception {

        // Expected header to be written.
        final String expectedHeader = "Header written";

        // Expected log entries to be written.
        String[] expectedEntries = new String[]{

                "Log entry 1",
                "Log entry 2"
        };

        expectation = new CRDTestExpectation();

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        // Create a new log file.
        CRDLog log = new CRDLog(new File(appContext.getFilesDir(), "testlog.txt"), new CRDLogHeaderInterface() {

            @Override
            public String onProvideHeader() {

                // Add a flag to the test expectation indicating that the header was requested.
                expectation.put("header", true);

                // Return the header.
                return expectedHeader + "\n";
            }
        });

        // Clear the log file.
        log.clear();

        // Log info entries.
        for (String entry: expectedEntries) {

            log.info(TAG, entry);
        }

        // Get the log contents.
        log.get(new CRDLogContentInterface() {

            @Override
            public void onLogContent(String logContent) {

                // Return the current log content and signal success.
                expectation.put("content", logContent);
                expectation.fulfill(CRDTestExpectationStatus.SUCCESS);
            }
        });

        // Wait for up to 5 seconds or until signalled by the above callback onLogContent().
        CRDTestExpectationStatus status = expectation.waitFor(defaultTestTimeout);
        assertEquals("unexpected status", CRDTestExpectationStatus.SUCCESS, status);

        // Validate that the header was requested, since the log file is initially cleared.
        assertTrue("header not provided", (boolean)expectation.get("header"));

        // Validate the content - there should be exactly one occurrence of the header and one of each log entry.
        String actualContent = (String)expectation.get("content");
        assertNotNull("content null", actualContent);
        assertEquals("unexpected content", 1, countMatches(Pattern.compile(expectedHeader, Pattern.LITERAL), actualContent));
        for (String entry: expectedEntries) {

            assertEquals("unexpected content", 1, countMatches(Pattern.compile(entry, Pattern.LITERAL), actualContent));
        }

        // Clear the log
        log.clear();

        // Expected log entries to be written.
        expectedEntries = new String[]{

                "Log entry 3",
                "Log entry 4",
                "Log entry 5"
        };

        // Log info entries.
        for (String entry: expectedEntries) {

            log.info(TAG, entry);
        }

        // Get the log contents.
        log.get(new CRDLogContentInterface() {

            @Override
            public void onLogContent(String logContent) {

                // Return the current log content and signal success.
                expectation.put("content", logContent);
                expectation.fulfill(CRDTestExpectationStatus.SUCCESS);
            }
        });

        // Wait for up to 5 seconds or until signalled by the above callback onLogContent().
        status = expectation.waitFor(defaultTestTimeout);
        assertEquals("unexpected status", CRDTestExpectationStatus.SUCCESS, status);

        // Validate that the header was requested, since the log file is initially cleared.
        assertTrue("header not provided", (boolean)expectation.get("header"));

        // Validate the content - there should be exactly one occurrence of the header and one of each log entry.
        actualContent = (String)expectation.get("content");
        assertNotNull("content null", actualContent);
        assertEquals("unexpected content", 1, countMatches(Pattern.compile(expectedHeader, Pattern.LITERAL), actualContent));
        for (String entry: expectedEntries) {

            assertEquals("unexpected content", 1, countMatches(Pattern.compile(entry, Pattern.LITERAL), actualContent));
        }
    }

    @Test
    public void testBurstLogWrites() throws Exception {

        // Expected header to be written.
        final String expectedHeader = "Header written";

        // Total number of expected entries.
        final int expectedEntries = 500;

        // Expected entry format.
        final String expectedEntry = "Log entry %d.";

        expectation = new CRDTestExpectation();

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        // Create a new log file.
        CRDLog log = new CRDLog(new File(appContext.getFilesDir(), "testlog.txt"), new CRDLogHeaderInterface() {

            @Override
            public String onProvideHeader() {

                // Add a flag to the test expectation indicating that the header was requested.
                expectation.put("header", true);

                // Return the header.
                return expectedHeader + "\n";
            }
        });

        // Clear the log file.
        log.clear();

        // Log info entries.
        for (int i = 0; i < expectedEntries; i++) {

            log.info(TAG, String.format(expectedEntry, i));
        }

        // Get the log contents.
        log.get(new CRDLogContentInterface() {

            @Override
            public void onLogContent(String logContent) {

                // Return the current log content and signal success.
                expectation.put("content", logContent);
                expectation.fulfill(CRDTestExpectationStatus.SUCCESS);
            }
        });

        // Wait for up to 5 seconds or until signalled by the above callback onLogContent().
        CRDTestExpectationStatus status = expectation.waitFor(defaultTestTimeout);
        assertEquals("unexpected status", CRDTestExpectationStatus.SUCCESS, status);

        // Validate that the header was requested, since the log file is initially cleared.
        assertTrue("header not provided", (boolean)expectation.get("header"));

        // Validate the content - there should be exactly one occurrence of the header and one of each log entry.
        String actualContent = (String)expectation.get("content");
        assertNotNull("content null", actualContent);
        assertEquals("unexpected content", 1, countMatches(Pattern.compile(expectedHeader, Pattern.LITERAL), actualContent));
        for (int i = 0; i < expectedEntries; i++) {

            String entry = String.format(expectedEntry, i);
            assertEquals("unexpected content", 1, countMatches(Pattern.compile(entry, Pattern.LITERAL), actualContent));
        }
        String nonExistantEntry = String.format(expectedEntry, expectedEntries);
        assertEquals("unexpected content", 0, countMatches(Pattern.compile(nonExistantEntry, Pattern.LITERAL), actualContent));
    }

    @Test
    public void testEnableDisableLogging() throws Exception {

        // Expected header to be written.
        final String expectedHeader = "Header written";

        // Expected log entries to be written.
        String[] expectedEntries = new String[]{

                "Log entry 1",
                "Log entry 2"
        };

        expectation = new CRDTestExpectation();

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        // Create a new log file.
        CRDLog log = new CRDLog(new File(appContext.getFilesDir(), "testlog.txt"), new CRDLogHeaderInterface() {

            @Override
            public String onProvideHeader() {

                // Add a flag to the test expectation indicating that the header was requested.
                expectation.put("header", true);

                // Return the header.
                return expectedHeader + "\n";
            }
        });

        // Clear the log file.
        log.clear();

        // Log info entries.
        for (String entry: expectedEntries) {

            log.info(TAG, entry);
        }

        // Get the log contents.
        log.get(new CRDLogContentInterface() {

            @Override
            public void onLogContent(String logContent) {

                // Return the current log content and signal success.
                expectation.put("content", logContent);
                expectation.fulfill(CRDTestExpectationStatus.SUCCESS);
            }
        });

        // Wait for up to 5 seconds or until signalled by the above callback onLogContent().
        CRDTestExpectationStatus status = expectation.waitFor(defaultTestTimeout);
        assertEquals("unexpected status", CRDTestExpectationStatus.SUCCESS, status);

        // Validate that the header was requested, since the log file is initially cleared.
        assertTrue("header not provided", (boolean)expectation.get("header"));

        // Validate the content - there should be exactly one occurrence of the header and one of each log entry.
        String actualContent = (String)expectation.get("content");
        assertNotNull("content null", actualContent);
        assertEquals("unexpected content", 1, countMatches(Pattern.compile(expectedHeader, Pattern.LITERAL), actualContent));
        for (String entry: expectedEntries) {

            assertEquals("unexpected content", 1, countMatches(Pattern.compile(entry, Pattern.LITERAL), actualContent));
        }

        // Disable logging
        log.enableLogging(false);

        // Expected log entries to NOT be written.
        expectedEntries = new String[]{

                "Log entry 3",
                "Log entry 4",
                "Log entry 5"
        };

        // Log info entries.
        for (String entry: expectedEntries) {

            log.info(TAG, entry);
        }

        // Get the log contents.
        log.get(new CRDLogContentInterface() {

            @Override
            public void onLogContent(String logContent) {

                // Return the current log content and signal success.
                expectation.put("content", logContent);
                expectation.fulfill(CRDTestExpectationStatus.SUCCESS);
            }
        });

        // Wait for up to 5 seconds or until signalled by the above callback onLogContent().
        status = expectation.waitFor(defaultTestTimeout);
        assertEquals("unexpected status", CRDTestExpectationStatus.SUCCESS, status);

        // Validate that the header was requested, since the log file is initially cleared.
        assertTrue("header not provided", (boolean)expectation.get("header"));

        // Validate the content - there should be exactly one occurrence of the header and NONE of expected log entries because logging was disabled.
        actualContent = (String)expectation.get("content");
        assertNotNull("content null", actualContent);
        assertEquals("unexpected content", 1, countMatches(Pattern.compile(expectedHeader, Pattern.LITERAL), actualContent));
        for (String entry: expectedEntries) {

            assertEquals("unexpected content", 0, countMatches(Pattern.compile(entry, Pattern.LITERAL), actualContent));
        }

        // Re-enable logging
        log.enableLogging(true);

        // Log info entries.
        for (String entry: expectedEntries) {

            log.info(TAG, entry);
        }

        // Get the log contents.
        log.get(new CRDLogContentInterface() {

            @Override
            public void onLogContent(String logContent) {

                // Return the current log content and signal success.
                expectation.put("content", logContent);
                expectation.fulfill(CRDTestExpectationStatus.SUCCESS);
            }
        });

        // Wait for up to 5 seconds or until signalled by the above callback onLogContent().
        status = expectation.waitFor(defaultTestTimeout);
        assertEquals("unexpected status", CRDTestExpectationStatus.SUCCESS, status);

        // Validate that the header was requested, since the log file is initially cleared.
        assertTrue("header not provided", (boolean)expectation.get("header"));

        // Validate the content - there should be exactly one occurrence of the header and one each of the expected log entries because logging was enabled.
        actualContent = (String)expectation.get("content");
        assertNotNull("content null", actualContent);
        assertEquals("unexpected content", 1, countMatches(Pattern.compile(expectedHeader, Pattern.LITERAL), actualContent));
        for (String entry: expectedEntries) {

            assertEquals("unexpected content", 1, countMatches(Pattern.compile(entry, Pattern.LITERAL), actualContent));
        }
    }

    @Test
    public void testDifferentTypesLogWrites() throws Exception {

        // Expected header to be written.
        final String expectedHeader = "Header written";

        // Expected log entries to be written.
        HashMap<String, String[]> expectedEntries = new HashMap<>();
        expectedEntries.put("info", new String[]{"Info entry 1", "Info entry 2"});
        expectedEntries.put("warn", new String[]{"Warning entry 3"});
        expectedEntries.put("error", new String[]{"Error entry 4", "Error entry 5", "Error entry 6"});
        expectedEntries.put("debug", new String[]{"Debug entry 7"});

        expectation = new CRDTestExpectation();

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        // Create a new log file.
        CRDLog log = new CRDLog(new File(appContext.getFilesDir(), "testlog.txt"), new CRDLogHeaderInterface() {

            @Override
            public String onProvideHeader() {

                // Add a flag to the test expectation indicating that the header was requested.
                expectation.put("header", true);

                // Return the header.
                return expectedHeader + "\n";
            }
        });

        // Clear the log file.
        log.clear();

        // Log entries.
        String[] keys = expectedEntries.keySet().toArray(new String[]{});
        for (int i = 0; i < keys.length; i++) {

            String key = keys[i];
            String[] entries = expectedEntries.get(key);
            for (String entry: entries) {

                if (key.equalsIgnoreCase("info")) {

                    log.info(TAG, String.format(entry, i));

                } else if (key.equalsIgnoreCase("warn")) {

                    log.warn(TAG, String.format(entry, i));

                } else if (key.equalsIgnoreCase("error")) {

                    log.error(TAG, String.format(entry, i));

                } else if (key.equalsIgnoreCase("debug")) {

                    log.debug(TAG, String.format(entry, i));
                }
            }
        }

        // Get the log contents.
        log.get(new CRDLogContentInterface() {

            @Override
            public void onLogContent(String logContent) {

                // Return the current log content and signal success.
                expectation.put("content", logContent);
                expectation.fulfill(CRDTestExpectationStatus.SUCCESS);
            }
        });

        // Wait for up to 5 seconds or until signalled by the above callback onLogContent().
        CRDTestExpectationStatus status = expectation.waitFor(defaultTestTimeout);
        assertEquals("unexpected status", CRDTestExpectationStatus.SUCCESS, status);

        // Validate that the header was requested, since the log file is initially cleared.
        assertTrue("header not provided", (boolean)expectation.get("header"));

        // Validate the content.
        String actualContent = (String)expectation.get("content");
        assertNotNull("content null", actualContent);
        assertEquals("unexpected content", 1, countMatches(Pattern.compile(expectedHeader, Pattern.LITERAL), actualContent));

        for (int i = 0; i < keys.length; i++) {

            String key = keys[i];
            String[] entries = expectedEntries.get(key);
            for (String entry: entries) {

                String entryToFind = String.format("(%1s) [%2s]: %3s", key, TAG, entry);
                assertEquals("unexpected content", 1, countMatches(Pattern.compile(entryToFind, Pattern.LITERAL), actualContent));
            }
        }
    }

    //endregion
}
