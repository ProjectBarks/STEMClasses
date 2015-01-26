package net.projectbarks.stemclasses.r;

import net.projectbarks.stemclasses.r.Render.BaseRenderer;
import net.projectbarks.stemclasses.r.Render.OutlineRenderer;
import net.projectbarks.stemclasses.r.Render.SolidRenderer;
import net.projectbarks.stemclasses.r.Render.TextRenderer;

import java.awt.*;
import java.awt.geom.Rectangle2D;

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

    public static BaseRenderer draw;
    public static final Config config;
    public static final Text text;

    static {
        try {
            draw = new BaseRenderer() {
                private final BaseRenderer outline = new OutlineRenderer(), solid = new SolidRenderer(), letter = new TextRenderer();

                @Override
                public Image drawIcon(String text, Color color, boolean animate, float percent) {
                    switch (config.getRenderMode()) {
                        case 1:
                            return solid.drawIcon(text, color, animate, percent);
                        case 2:
                            return letter.drawIcon(text, color, animate, percent);
                        default:
                            return outline.drawIcon(text, color, animate, percent);
                    }
                }

                @Override
                protected void draw(Graphics2D g2d, Rectangle2D bounds, String text, Color color, float scale, boolean animate, float percent) {

                }
            };
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        config = new Config();
        text = new Text();
    }

}
