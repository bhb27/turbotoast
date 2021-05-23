/*
 * Copyright (C) 2016 Felipe de Leon fglfgl27@gmail.com
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
package com.bhb27.turbotoast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import com.bhb27.turbotoast.Tools;
import com.bhb27.turbotoast.BuildConfig;
import com.bhb27.turbotoast.Constants;
import com.bhb27.turbotoast.root.RootUtils;

public class AboutActivity extends Activity {
    // in order of appearance
    TextView version_number, email, email_summary, xda, git, git_summary;
    ImageView ic_gmail, ic_xda, ic_git;
    private Context AboutContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_fragment);
        AboutContext = this;
        final String SUBJECT = getString(R.string.app_name) + " " +
                BuildConfig.VERSION_NAME + " (" + Build.MODEL + " " + Build.VERSION.RELEASE + ")";

        final String BODY = "\n\n\nPower folders listing:\n" +
                (Tools.getBoolean("Root", true, AboutContext) ?
                        RootUtils.runCommand("ls " + Constants.BATTERY_PARAMETERS) : listfiles(Constants.BATTERY_PARAMETERS));

        LinearLayout layout = (LinearLayout) findViewById(R.id.aboutLayout);
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setFillAfter(true);
        animation.setDuration(500);
        layout.startAnimation(animation);

        //textview
        version_number = (TextView) findViewById(R.id.version_number);
        email = (TextView) findViewById(R.id.email);
        email_summary = (TextView) findViewById(R.id.email_summary);
        xda = (TextView) findViewById(R.id.xda);
        git = (TextView) findViewById(R.id.git);
        git_summary = (TextView) findViewById(R.id.git_summary);

        version_number.setText(BuildConfig.VERSION_NAME);

        //Link Strings
        final String email_link = Constants.email_link;
        final String xda_link = Constants.xda_link;
        final String git_link = Constants.git_link;

        xda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(xda_link)));
                } catch (ActivityNotFoundException ex) {
                    Tools.DoAToast(getString(R.string.no_browser), AboutContext);
                }
            }
        });

        git.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(git_link)));
                } catch (ActivityNotFoundException ex) {
                    Tools.DoAToast(getString(R.string.no_browser), AboutContext);
                }
            }
        });

        git_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(git_link)));
                } catch (ActivityNotFoundException ex) {
                    Tools.DoAToast(getString(R.string.no_browser), AboutContext);
                }
            }
        });

        //imageview
        ic_gmail = (ImageView) findViewById(R.id.ic_gmail);
        ic_xda = (ImageView) findViewById(R.id.ic_xda);
        ic_git = (ImageView) findViewById(R.id.ic_git);

        ic_xda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(xda_link)));
                } catch (ActivityNotFoundException ex) {
                    Tools.DoAToast(getString(R.string.no_browser), AboutContext);
                }
            }
        });

        ic_git.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(git_link)));
                } catch (ActivityNotFoundException ex) {
                    Tools.DoAToast(getString(R.string.no_browser), AboutContext);
                }
            }
        });
    }

    public static String listfiles(String directoryName) {
        File directory = new File(directoryName);
        String resultList = "No Root" + "\n";
        File[] fList = directory.listFiles();
        if (fList != null) {
            for (File file: fList) {
                if (file.isFile()) {
                    resultList = resultList + file.getName() + "\n";
                }
            }
        } else resultList = "Can\'t read Power status";
        return resultList;
    }
}
