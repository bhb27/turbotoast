/*
 * Copyright (C) 2016 Felipe de Leon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bhb27.turbotoast;

import android.content.Context;

import android.os.Environment;
import android.util.Log;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;

import com.bhb27.turbotoast.Constants;

public class Tools implements Constants {

    /**
     * Debugging TAG
     */
    public final static String TAG = "Turbotoast";

    public static String getChargingType() {
        return StringreadFile(BATTERY_CHARGING_TYPE);
    }

    public static boolean getBoolean(String name, boolean defaults, Context context) {
        try {
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getBoolean(name, defaults);
        } catch (Exception ignored){
            return false;
        }
    }

    public static String StringreadFile(String file) {
        return readFile(file, true);
    }

    /**
     * Read any file from storage
     *
     * @param file   path to the file
     * @param asRoot read as root
     * @return content of file
     */
    public static String readFile(String file, boolean asRoot) {

        StringBuilder s = null;
        FileReader fileReader = null;
        BufferedReader buf = null;
        try {
            fileReader = new FileReader(file);
            buf = new BufferedReader(fileReader);

            String line;
            s = new StringBuilder();
            while ((line = buf.readLine()) != null) s.append(line).append("\n");
        } catch (FileNotFoundException ignored) {
            Log.e(TAG, "File does not exist " + file);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read " + file);
        } finally {
            try {
                if (fileReader != null) fileReader.close();
                if (buf != null) buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return s == null ? null : s.toString().trim();
    }

}