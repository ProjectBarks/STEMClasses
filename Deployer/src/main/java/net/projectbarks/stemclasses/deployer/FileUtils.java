package net.projectbarks.stemclasses.deployer;

import java.io.*;

/**
 * Created by brandon on 12/14/14.
 */
public class FileUtils {

    public static void copyFolder(File src, File dest) throws IOException {
        if(src.isDirectory()) {
            if (src.getName().equals("Backup")) {
                return;
            }
            if(!dest.exists()) {
                dest.mkdir();
            }
            String files[] = src.list();
            for (String file : files) {
                copyFolder(new File(src, file), new File(dest, file));
            }
        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0){
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }

    public static boolean deleteDirectory(File directory) {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
        }
        return(directory.delete());
    }
}
