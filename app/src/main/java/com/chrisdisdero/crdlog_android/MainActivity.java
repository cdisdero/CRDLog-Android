package com.chrisdisdero.crdlog_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chrisdisdero.crdlog.CRDLog;
import com.chrisdisdero.crdlog.CRDLogContentInterface;

/**
 * The main {@link android.app.Activity} of the example app.
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
public class MainActivity extends AppCompatActivity {

    //region Private members

    /**
     * Log tag for this class.
     */
    private final String TAG = MainActivity.class.getCanonicalName();

    /**
     * Reference to the CRDLog for the app.
     */
    private final CRDLog log = MyApplication.getApp().getLog();

    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Log a message about creating this activity.
        log.info(TAG, "onCreate: started");

        // Get reference to the text view for displaying log content.
        final TextView textViewLog = (TextView)findViewById(R.id.textViewLog);
        textViewLog.setMovementMethod(new ScrollingMovementMethod());

        // Get reference to the clear button and define its action.
        final Button buttonClear = (Button)findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Clear the log.
                log.clear();

                // Reset the TextView text and scroll point.
                textViewLog.setText("");
                textViewLog.scrollTo(0, 0);

                // Update the TextView.
                updateTextViewLog(textViewLog);
            }
        });

        final Button buttonWriteLog = (Button)findViewById(R.id.buttonWriteLog);
        buttonWriteLog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Write a message to the log.
                log.info(TAG, "onCreate: writing message to log");

                // Update the TextView.
                updateTextViewLog(textViewLog);
            }
        });

        // Update the text view with the current log content.
        updateTextViewLog(textViewLog);
    }

    //endregion

    //region Private methods

    /**
     * Updates the {@link TextView} with the current log content.
     *
     * @param textViewLog A reference to the {@link TextView} to update.
     */
    private void updateTextViewLog(final TextView textViewLog) {

        // Get the current log content.
        log.get(new CRDLogContentInterface() {

            @Override
            public void onLogContent(final String logContent) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        // Get the text that's currently in the TextView, if any.
                        final String currentText = textViewLog.getText().toString();

                        // If there is text in the TextView...
                        if (currentText.length() > 0) {

                            // Compute the difference between the current log content and the text in the TextView.
                            String diff = logContent.replace(currentText, "");

                            // Append only the content that's been added.
                            textViewLog.append(diff);

                        } else {

                            // There is no text in the TextView, so just set the text to the current log content.
                            textViewLog.setText(logContent);
                        }
                    }
                });
            }
        });
    }

    //endregion
}
