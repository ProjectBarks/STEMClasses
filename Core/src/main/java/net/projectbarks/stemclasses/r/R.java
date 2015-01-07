package net.projectbarks.stemclasses.r;

import java.awt.*;
import java.io.IOException;

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
public class R {

    public static ClockRenderer draw;
    public static final Config config;
    public static final Text text;

    static {
        try {
            draw = new ClockRenderer();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);

        }
        config = new Config();
        text = new Text();
    }


}
