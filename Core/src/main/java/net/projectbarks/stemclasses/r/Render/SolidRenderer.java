package net.projectbarks.stemclasses.r.Render;

import java.awt.*;
import java.awt.geom.*;
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
 * Written By: brandon on 1/14/15
 */
public class SolidRenderer extends BaseRenderer {
    public SolidRenderer() throws IOException, FontFormatException {
        super();
    }

    @Override
    protected void draw(Graphics2D g2d, Rectangle2D bounds, String text, Color color, float scale, boolean animate, float percent) {
        g2d.setFont(customFont.deriveFont(Font.BOLD, 11f));
        float w = (float) (bounds.getBounds2D().getWidth() / scale);
        float h = (float) (bounds.getBounds2D().getHeight() / scale);

        Area top = area(w == h ? new Ellipse2D.Float(0, 0, w, h) : new RoundRectangle2D.Float(0, 0, w, h, h, h));
        Area bottom = scale((Area) top.clone(), scale * (1f - percent));
        Area font = generateCenteredText(area(bounds), text, g2d);
        centerInside(area(bounds), scale(top, scale));
        centerInside(top, bottom);

        bottom.subtract(font);
        top.subtract(font);

        if (animate) {
            top.subtract(bottom);
        }

        if (animate) {
            g2d.setColor(color);
            g2d.fill(bottom);
            g2d.setColor(GRAY);
            g2d.fill(top);
        } else {
            g2d.setColor(color);
            g2d.fill(top);
        }
    }
}
