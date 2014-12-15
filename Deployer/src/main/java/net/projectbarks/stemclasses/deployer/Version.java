package net.projectbarks.stemclasses.deployer;

import lombok.Getter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

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
public class Version implements Comparable<Version> {

    @Getter
    private double version;
    @Getter
    private String versionRaw;
    @Getter
    private List<String> downloads;

    public Version(String versionRaw) {
        this.versionRaw = versionRaw;
        String[] values = versionRaw.substring(1, versionRaw.length()).split("\\.");
        this.version = 0;
        this.version += Integer.parseInt(values[0]);
        this.version += Integer.parseInt(values[1]) * 0.1;
        this.version += Integer.parseInt(values[2]) * 0.01;
        downloads = new ArrayList<String>();
    }

    public void addDownload(String download) {
        downloads.add(download);
    }

    public void download(File path) throws IOException {
        for (String download : downloads) {
            String[] split = download.split("/");
            URL website = new URL(download);
            URLConnection con = website.openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(30 * 1000);
            ReadableByteChannel rbc = Channels.newChannel(con.getInputStream());
            FileOutputStream fos = new FileOutputStream(new File(path, split[split.length - 1]));
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }

    @Override
    public int compareTo(Version o) {
        return Double.compare(getVersion(), o.getVersion());
    }
}
