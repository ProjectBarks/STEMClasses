package net.projectbarks.stemclasses.r;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;

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

    private Font customFont;

    public ClockRenderer() throws IOException, FontFormatException {
        this(Font.createFont(Font.TRUETYPE_FONT, ClockRenderer.class.getResourceAsStream("/font.ttf")));
    }

    public ClockRenderer(Font font) {
        this.customFont = font;
    }

    public Image drawIcon(String text) {
        return drawIcon(text, 0);
    }

    public Image drawIcon(String text, double percent) {
        return drawIcon(text, R.config.getColor(), R.config.isOutline(), R.config.isAnimate(), percent);
    }

    public Image drawIcon(String text, Color color, boolean outline, boolean animate, double percent) {
        float scale = getScale();
        int w = (9 * (text.length() <= 2 ? 2 : text.length() + 1)), h = 18;
        BufferedImage bi = new BufferedImage((int) (w * scale), (int) (h * scale), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_PURE);
        g.setFont(customFont.deriveFont(outline ? 11f : 12f));
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


        scaleAndCenter(top, scale);
        scaleAndCenter(bottom, scale);

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

    private float getScale() {
        float nativeScale = 1, alternativeScale = 1;

        //This is the native jvm way of getting the scale.
        try {
            Object property = Toolkit.getDefaultToolkit().getDesktopProperty("apple.awt.contentScaleFactor");
            nativeScale = property != null ? (Float) property : nativeScale;
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Here is the alternative way of getting the scale.
        //It appears to be more reliable
        if (nativeScale == 1) {
            GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            try {
                Field field = graphicsDevice.getClass().getDeclaredField("scale");
                if (field != null) {
                    field.setAccessible(true);
                    Object scale = field.get(graphicsDevice);
                    alternativeScale = scale instanceof Integer ? (Integer) scale : alternativeScale;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return nativeScale > 1 ? nativeScale : alternativeScale;
    }

    private void scaleAndCenter(Area area, float scale) {
        float subtract = (float) (scale <= 1 ? scale * .1 : scale * .2);
        Area correct = area.createTransformedArea(AffineTransform.getScaleInstance(scale, scale));
        area.transform(AffineTransform.getScaleInstance(scale - subtract, scale - subtract));
        double xOffset = ((correct.getBounds2D().getHeight() - area.getBounds2D().getHeight())/2);
        double yOffset = ((correct.getBounds2D().getHeight() - area.getBounds2D().getHeight())/2);
        area.transform(AffineTransform.getTranslateInstance(xOffset, yOffset));
    }
}
