package net.projectbarks.stemclasses.r;

import lombok.Getter;
import lombok.Setter;
import net.projectbarks.stemclasses.letterday.LetterDay;
import org.joda.time.DateTime;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

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
public class Config {

    @Getter
    @Setter
    private boolean outline = true, autoUpdate = true, animate = true;
    @Getter
    @Setter
    private Color color = Color.BLACK;
    @Getter
    @Setter
    private Integer grade = LetterDay.GRADE_9_10;

    public void load(String path) {
        Properties props = new Properties();
        InputStream is;

        try {
            File f = new File(path, "config.properties");
            is = new FileInputStream(f);
            props.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setOutline(Boolean.valueOf(props.getProperty("OUTLINE", Boolean.toString(outline))));
        setAutoUpdate(Boolean.valueOf(props.getProperty("AUTOUPDATE", Boolean.toString(autoUpdate))));
        setAnimate(Boolean.valueOf(props.getProperty("ANIMATE", Boolean.toString(animate))));
        setColor(new Color(new Integer(props.getProperty("COLOR", String.valueOf(color.getRGB())))));
        setGrade(Integer.valueOf(props.getProperty("GRADE", String.valueOf(grade))));
    }

    public void save(String path) {
        Properties props = new Properties();
        props.setProperty("OUTLINE", Boolean.toString(outline));
        props.setProperty("AUTOUPDATE", Boolean.toString(autoUpdate));
        props.setProperty("ANIMATE", Boolean.toString(animate));
        props.setProperty("COLOR", String.valueOf(color.getRGB()));
        props.setProperty("GRADE", String.valueOf(grade));
        try {
            File file = new File(path, "config.properties");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            props.store(new FileOutputStream(file), "Saved: " + new DateTime().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
