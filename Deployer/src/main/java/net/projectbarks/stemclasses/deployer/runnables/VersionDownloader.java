package net.projectbarks.stemclasses.deployer.runnables;

import net.projectbarks.stemclasses.deployer.FileUtils;
import net.projectbarks.stemclasses.deployer.LaunchEngine;
import net.projectbarks.stemclasses.deployer.Version;
import org.joda.time.DateTime;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by brandon on 12/12/14.
 */
public class VersionDownloader implements Runnable {

    private final Logger logger;
    
    public VersionDownloader(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void run() {
        boolean madeBackup = false;
        try {
            logger.log(Level.INFO, "Install Path {0}", LaunchEngine.PATH);
            logger.log(Level.INFO, "Checking For Directory...");
            File file = new File(LaunchEngine.PATH);
            if (!file.exists()) {
                //Creating files that don't exist
                logger.log(Level.WARNING, "Could not find directory!");
                logger.log(Level.INFO, "Creating Directory");
                if (file.mkdirs()) {
                    logger.log(Level.INFO, "Successfully created directory");
                } else {
                    throw new Exception("Failed to create directory!");
                }
            } else {
                madeBackup = true;
                logger.log(Level.INFO, "Found directory");

                //MAKE a backup
                logger.log(Level.INFO, "Starting Snapshot Process");
                File backup = new File(LaunchEngine.PATH, "Backup");
                if (backup.exists()) {
                    logger.log(Level.INFO, "Deleting Existing Backup");
                    FileUtils.deleteDirectory(backup);
                }
                logger.log(Level.INFO, "Creating backup");
                backup.mkdirs();
                FileUtils.copyFolder(new File(LaunchEngine.PATH), backup);
                logger.log(Level.INFO, "Backup complete!");

                //Deleting existing giles
                logger.log(Level.INFO, "Deleting Libraries");
                for (File delete : file.listFiles()) {
                    if (delete.getName().contains("config") || delete.getName().contains("Backup")) {
                        continue;
                    }
                    if (delete.isDirectory()) {
                        FileUtils.deleteDirectory(delete);
                    } else {
                        delete.delete();
                    }
                    logger.log(Level.INFO, "Deleted: {0}", delete.getName());
                }
            }
            logger.log(Level.INFO, "Checking for latest version");
            Version version = LaunchEngine.getLatestVersion();
            logger.log(Level.INFO, "Downloading version {0}", version.getVersionRaw());
            version.download(new File(LaunchEngine.PATH));
            logger.log(Level.INFO, "Download complete");

            logger.log(Level.INFO, "Saving version signature..");
            Properties properties = new Properties();
            properties.setProperty("VERSION", version.getVersionRaw());
            properties.store(new FileOutputStream(new File(file, "version.properties")), "Saved: " + new DateTime().toString());
            logger.log(Level.INFO, "Saved version signature!");
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            if (e instanceof IOException) {
                if (madeBackup) {
                    logger.log(Level.SEVERE, "TRY AGAIN WITH AN INTERNET CONNECTION");
                } else {
                    //TODO add code to open anyway
                }
            } else {
                logger.log(Level.SEVERE, sw.toString());
                logger.log(Level.WARNING, "Report this error to 17bbarker.stem@gmail.com");
            }
        }
    }
}
