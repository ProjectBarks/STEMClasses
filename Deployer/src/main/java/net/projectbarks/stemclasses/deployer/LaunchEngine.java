package net.projectbarks.stemclasses.deployer;

import lombok.Getter;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.projectbarks.stemclasses.deployer.runnables.VersionChecker;
import net.projectbarks.stemclasses.deployer.runnables.VersionDownloader;
import net.projectbarks.stemclasses.deployer.views.InvisibleDisplay;
import net.projectbarks.stemclasses.deployer.views.LoggingDisplay;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Written By: brandon on 12/8/14
 */
public class LaunchEngine {

    public static final String PATH;
    public static final String USERNAME = "ProjectBarks";
    public static final String PROJECT = "STEMClasses";
    public static final String PREFIX = "https://api.github.com/";
    public static final String REPOS = "%srepos/%s/%s/releases";

    @Getter
    private static LoggingDisplay display;
    @Getter
    private static Logger logger;
    
    static {
        PATH = buildPath("Users", System.getProperty("user.name"), "Library", "Application Support", "STEM Classes");
        display = LoggingDisplay.display();
        logger = display.getLogger();
    }

    public static void main(String[] args) {
        InvisibleDisplay pane = InvisibleDisplay.display();
        VersionChecker checker = new VersionChecker(logger);
        checker.run();
        logger.log(Level.INFO, getJavaPath());
        if (checker.isOutOfDate()) {
            pause();
            //pane.getPane().setText("DOWNLOADING");
            new VersionDownloader(logger).run();
        }
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(new String[]{getJavaPath(), "-jar", PATH + "STEMClasses.jar"});
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.log(Level.SEVERE, sw.toString());
        }
        if (!display.isFailed()) {
            //pane.getPane().setText("WELCOME");
            pause();
            System.exit(0);
        } else {
            //pane.getPane().setText("ERROR");
            pause();
            pane.getFrame().dispose();
        }
    }

    public static Version getLatestVersion() throws IOException {
        InputStream is = new URL(String.format(REPOS, PREFIX, USERNAME, PROJECT)).openStream();
        JSONArray array = (JSONArray) JSONValue.parse(is);
        List<Version> downloads = new ArrayList<Version>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = (JSONObject) array.get(0);
            Version version = new Version(object.getAsString("tag_name"));
            JSONArray assets = (JSONArray) object.get("assets");
            for (int i2 = 0; i2 < assets.size(); i2++) {
                version.addDownload(((JSONObject) assets.get(i2)).getAsString("browser_download_url"));
            }
            downloads.add(version);
        }
        Collections.sort(downloads);
        return downloads.get(downloads.size() - 1);
    }

    private static String buildPath(String... paths) {
        StringBuilder builder = new StringBuilder((paths.length * 2) + 1);
        builder.append(File.separator);
        for (String path : paths) {
            builder.append(path);
            builder.append(File.separator);
        }
        return builder.toString();
    }

    private static void pause() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.log(Level.INFO, e.getLocalizedMessage());
        }
    }

    private static String getJavaPath() {
        return System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
    }
}
