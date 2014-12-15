package net.projectbarks.stemclasses.deployer.views;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * Created by brandon on 12/15/14.
 */
@AllArgsConstructor
public class InvisibleDisplay {

    public static InvisibleDisplay display() {
        JFrame editorFrame;
        editorFrame = new JFrame("Java Mac OS X Translucency Demo");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        ContentPane contentPane = new ContentPane();
        editorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        editorFrame.setLocation((int) ((dim.getWidth() / 2) - 200), (int) ((dim.getHeight() / 2) - 200));
        editorFrame.setSize(400, 400);
        editorFrame.setUndecorated(true);
        editorFrame.setBackground(new Color(0, 255, 0, 0));
        editorFrame.setContentPane(contentPane);
        editorFrame.setLayout(new BorderLayout());

        editorFrame.setVisible(true);
        return new InvisibleDisplay(editorFrame, contentPane);
    }

    @Getter
    private JFrame frame;
    @Getter
    private ContentPane pane;

    public static class ContentPane extends JPanel {

        private String txt;

        public ContentPane() {
            setOpaque(false);
            txt = "STEM CLASSES";
        }

        public void setText(String text) {
            txt = text;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics gCast) {
            super.paintComponent(gCast);
            Graphics2D g = (Graphics2D) gCast;
            Rectangle bounds = new Rectangle(400, 400);

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setFont(new Font("Arial", Font.BOLD, 36));

            FontMetrics fm = g.getFontMetrics();
            int sHeight = fm.getAscent() + fm.getDescent(), sWidth = fm.stringWidth(txt);
            int centerX = (int) ((bounds.getWidth() - fm.stringWidth(txt)) / 2);
            int centerY = (int) (fm.getAscent() + (bounds.getHeight() - (fm.getAscent() + fm.getDescent())) / 2);

            Area top = new Area(new RoundRectangle2D.Double(centerX - 10, centerY - sHeight - 3, sWidth + 20, sHeight + 20, sHeight + 20, sHeight + 20));
            top.subtract(new Area(new RoundRectangle2D.Double(centerX - 7, centerY - sHeight, sWidth + 14, sHeight + 14, sHeight + 14, sHeight + 14)));

            Composite original = g.getComposite();
            g.setComposite(makeComposite(.7f));
            g.setColor(Color.BLACK);
            g.fillOval(25, 25, 350, 350);
            g.setPaint(Color.WHITE);
            g.setStroke(new BasicStroke(8));
            g.drawOval(25, 25, 350, 350);
            g.setComposite(original);
            g.setColor(Color.BLUE);
            g.fill(top);
            g.setColor(Color.WHITE);
            g.fill(new Area(g.getFont().createGlyphVector(g.getFontRenderContext(), txt).getOutline(centerX, centerY)));
        }

        private AlphaComposite makeComposite(float alpha) {
            int type = AlphaComposite.SRC_OVER;
            return(AlphaComposite.getInstance(type, alpha));
        }
    }

}
