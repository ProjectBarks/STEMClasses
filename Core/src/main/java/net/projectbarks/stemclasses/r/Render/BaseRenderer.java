package net.projectbarks.stemclasses.r.Render;

import lombok.Getter;
import lombok.Setter;
import net.projectbarks.stemclasses.r.R;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;

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
public abstract class BaseRenderer {
    //Todo add text gen lib
    protected static final Color GRAY = Color.getHSBColor(227, .06f, .63f);

    protected Font customFont;
    @Getter
    @Setter
    private boolean debug;

    public BaseRenderer() throws IOException, FontFormatException {
        this(Font.createFont(Font.TRUETYPE_FONT, BaseRenderer.class.getResourceAsStream("/font.ttf")));
        debug = false;
    }

    public BaseRenderer(Font font) {
        this.customFont = font;
    }

    public Image drawIcon(String text) {
        return drawIcon(text, 0);
    }

    public Image drawIcon(String text, float percent) {
        return drawIcon(text, R.config.getColor(), R.config.isAnimate(), percent);
    }

    public Image drawIcon(String text, Color color, boolean animate, float percent) {
        float scale = getScale();
        float smaller = scale - (float) (scale <= 1 ? scale * .1 : scale * .2);
        float w = (10 * (text.length() <= 2 ? 2 : text.length() + 1)) * scale, h = 20 * scale;
        float wSmall = w/scale*smaller, hSmall = h/scale*smaller;

        BufferedImage bi = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) bi.getGraphics();

        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g.translate((w - wSmall)/2, (h - hSmall)/2);
        draw(g, new Rectangle2D.Float(0, 0, wSmall, hSmall), text, color, scale - (float) (scale <= 1 ? scale * .1 : scale * .2), animate, percent);
        return bi;
    }

    protected abstract void draw(Graphics2D g2d, Rectangle2D bounds, String text, Color color, float scale, boolean animate, float percent);

    protected Area centerInside(Area parent, Area transform) {
        return centerInside(parent, transform, transform);
    }

    protected Area centerInside(Area parent, Area transform, Area bounding) {
        transform.transform(AffineTransform.getTranslateInstance(
                (parent.getBounds2D().getWidth() - bounding.getBounds2D().getWidth()) / 2,
                (parent.getBounds2D().getHeight() - bounding.getBounds2D().getBounds2D().getHeight()) / 2));
        return transform;
    }

    protected Area scale(Area area, float scale) {
        scale(area, scale, scale);
        return area;
    }

    protected Area scale(Area area, float w, float h) {
        area.transform(AffineTransform.getScaleInstance(w, h));
        return area;
    }

    public Area generateCenteredText(Area parent, String s, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        float x = (float) ((parent.getBounds2D().getWidth() - fm.stringWidth(s)) / 2);
        float y = (float) (fm.getAscent() + (parent.getBounds2D().getHeight() - (fm.getAscent() + fm.getDescent())) / 2);
        return area(fm.getFont().createGlyphVector(fm.getFontRenderContext(), s).getOutline(x, y));
    }

    protected Area area(Shape shape) {
        return new Area(shape);
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
        if (debug) {
            nativeScale = 20;
        }
        return nativeScale > 1 ? nativeScale : alternativeScale;
    }
}
