/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.bhb27.turbotoast.root;

import android.util.Log;

import com.bhb27.turbotoast.Tools;
import com.bhb27.turbotoast.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by willi on 14.12.14.
 */

/**
 * Here you have different functions which will help you with root commands.
 * I think they are self explained and do no need any further descriptions.
 */
public class RootUtils {

    private static SU su;

    public static boolean rooted() {
        return existBinary("su");
    }

    public static boolean rootAccess() {
        SU su = getSU();
        su.runCommand("echo /testRoot/");
        return !su.denied;
    }

    public static boolean busyboxInstalled() {
        return existBinary("busybox") || existBinary("toybox");
    }

    private static boolean existBinary(String binary) {
        for (String path: System.getenv("PATH").split(":")) {
            if (!path.endsWith("/")) path += "/";
            if (new File(path + binary).exists() || Tools.existFile(path + binary, true))
                return true;
        }
        return false;
    }

    public static String getKernelVersion() {
        return runCommand("uname -r");
    }

    public static void mount(boolean writeable, String mountpoint) {
        runCommand(writeable ? "mount -o remount,rw " + mountpoint + " " + mountpoint :
                "mount -o remount,ro " + mountpoint + " " + mountpoint);
        runCommand(writeable ? "mount -o remount,rw " + mountpoint :
                "mount -o remount,ro " + mountpoint);
    }

    public static void closeSU() {
        if (su != null) su.close();
        su = null;
    }

    public static String runCommand(String command) {
        return getSU().runCommand(command);
    }

    private static SU getSU() {
        if (su == null) su = new SU();
        else if (su.closed || su.denied) su = new SU();
        return su;
    }

    /*
     * Based on AndreiLux's SU code in Synapse
     * https://github.com/AndreiLux/Synapse/blob/master/src/main/java/com/af/synapse/utils/Utils.java#L238
     */
    public static class SU {

        private Process process;
        private BufferedWriter bufferedWriter;
        private BufferedReader bufferedReader;
        private final boolean root;
        private boolean closed;
        private boolean denied;
        private boolean firstTry;

        public SU() {
            this(true);
        }

        public SU(boolean root) {
            this.root = root;
            try {
                Log.i(Tools.TAG, root ? "SU initialized" : "SH initialized");
                firstTry = true;
                process = Runtime.getRuntime().exec(root ? "su" : "sh");
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            } catch (IOException e) {
                Log.e(Constants.TAG, root ? "Failed to run shell as su" : "Failed to run shell as sh");
                denied = true;
                closed = true;
            }
        }

        public synchronized String runCommand(final String command) {
            try {
                StringBuilder sb = new StringBuilder();
                String callback = "/shellCallback/";
                bufferedWriter.write(command + "\necho " + callback + "\n");
                bufferedWriter.flush();

                int i;
                char[] buffer = new char[256];
                while (true) {
                    sb.append(buffer, 0, bufferedReader.read(buffer));
                    if ((i = sb.indexOf(callback)) > -1) {
                        sb.delete(i, i + callback.length());
                        break;
                    }
                }
                firstTry = false;
                return sb.toString().trim();
            } catch (NullPointerException e) {
                Log.e(Constants.TAG, "catch NullPointerException running as Su");
            } catch (IOException e) {
                closed = true;
                e.printStackTrace();
                if (firstTry) denied = true;
            } catch (ArrayIndexOutOfBoundsException e) {
                denied = true;
            } catch (Exception e) {
                e.printStackTrace();
                denied = true;
            }
            return null;
        }

        public void close() {
            try {
                bufferedWriter.write("exit\n");
                bufferedWriter.flush();

                process.waitFor();
                Log.i(Constants.TAG, root ? "SU closed: " + process.exitValue() : "SH closed: " + process.exitValue());
                closed = true;
            } catch (NullPointerException e) {
                Log.e(Constants.TAG, "catch NullPointerException close Su");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
