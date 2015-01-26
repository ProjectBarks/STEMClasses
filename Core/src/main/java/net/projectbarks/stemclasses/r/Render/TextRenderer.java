package net.projectbarks.stemclasses.r.Render;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Written By: brandon on 1/26/15
 */
public class TextRenderer extends BaseRenderer {

    private Rectangle2D cachedBounds;
    private Font cachedFont;

    public TextRenderer() throws IOException, FontFormatException {
        super();
        cachedBounds = null;
        cachedFont = null;
    }

    @Override
    protected void draw(Graphics2D g2d, Rectangle2D bounds, String text, Color color, float scale, boolean animate, float percent) {
        g2d.setFont(find(g2d, bounds));
        Area colored = generateCenteredText(area(bounds), text, g2d), grey = (Area) colored.clone();
        if (animate) {
            grey.subtract(area(new Rectangle2D.Float(0,
                    (float) bounds.getHeight() - ((float)bounds.getHeight() * (1f - percent)),
                    (float) bounds.getWidth(),
                    (float) bounds.getHeight() * (1f - percent))));
            colored.subtract(grey);
            g2d.setColor(GRAY);
            g2d.fill(grey);
        }
        g2d.setColor(color);
        g2d.fill(colored);
    }

    private Font find(Graphics2D g2d, Rectangle2D bounds) {
        if (cachedFont != null && cachedBounds != null && cachedBounds.equals(bounds)) {
            return cachedFont;
        }

        float size = (float) bounds.getHeight();
        Boolean up = null;
        while (true) {
            cachedFont = customFont.deriveFont(Font.PLAIN, size);
            int testHeight = g2d.getFontMetrics(cachedFont).getHeight();
            if (testHeight < bounds.getHeight() && up != Boolean.FALSE) {
                size += .1;
                up = Boolean.TRUE;
            } else if (testHeight > bounds.getHeight() && up != Boolean.TRUE) {
                size -= .1;
                up = Boolean.FALSE;
            } else {
                break;
            }
        }
        return cachedFont;
    }
}
