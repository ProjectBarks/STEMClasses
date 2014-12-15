package net.projectbarks.stemclasses.r;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

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
public class ClockRenderer {

    public Image drawIcon(String text) {
        return drawIcon(text, 0);
    }

    public Image drawIcon(String text, double percent) {
        return drawIcon(text, R.config.getColor(), R.config.isOutline(), R.config.isAnimate(), percent);
    }

    public Image drawIcon(String text, Color color, boolean outline, boolean animate, double percent) {
        int w = (9 * (text.length() <= 2 ? 2 : text.length() + 1)), h = 18;
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setFont(new Font("Arial", Font.BOLD, outline ? 10 : 12));
        Area top, bottom;
        if (outline) {
            top = new Area(new RoundRectangle2D.Double(0, 0, w, h, h, h));
            top.subtract(new Area(new RoundRectangle2D.Double(1.5, 1.5, w - 3, h - 3, h, h)));
            bottom = (Area) top.clone();
            top.subtract(new Area(new Arc2D.Double(-10, -10, w + 20, h + 20, 90, 360 * percent, Arc2D.PIE)));
            top.add(getCenteredString(text, g, w, h));
            if (!animate) {
                bottom.add(getCenteredString(text, g, w, h));
            } else {
                bottom.subtract(top);
            }
        } else {
            double perW = w * (1 - percent), perH = h * (1 - percent);
            top = new Area(new RoundRectangle2D.Double((w - perW) / 2, (h - perH) / 2, perW, perH, perH, perH));
            top.subtract(getCenteredString(text, g, w, h));
            bottom = new Area(new RoundRectangle2D.Double(0, 0, w, h, h, h));
            bottom.subtract(getCenteredString(text, g, w, h));
            if (animate) {
                bottom.subtract(top);
            }
        }
        if (animate) {
            g.setColor(Color.getHSBColor(227, .06f, .63f));
            g.fill(bottom);
            g.setColor(color);
            g.fill(top);
        } else {
            g.setColor(color);
            g.fill(bottom);
        }
        return bi;
    }

    private Area getCenteredString(String s, Graphics2D g, int w, int h) {
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(s)) / 2;
        int y = (fm.getAscent() + (h - (fm.getAscent() + fm.getDescent())) / 2);
        return new Area(g.getFont().createGlyphVector(g.getFontRenderContext(), s).getOutline(x, y));
    }
}
