package net.projectbarks.stemclasses.deployer.views;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
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
 * Written By: brandon on 12/15/14
 */
@AllArgsConstructor
public class InvisibleDisplay {

    @Getter
    private JFrame frame;
    @Getter
    private ContentPane pane;

    public static InvisibleDisplay display() {
        JFrame editorFrame;
        editorFrame = new JFrame("Java Mac OS X Translucency Demo");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int w = 400, h = 400;
        ContentPane contentPane = null;
        try {
            contentPane = new ContentPane(ImageIO.read(InvisibleDisplay.class.getResource("/logo.png")), w, h);
        } catch (IOException e) {
            e.printStackTrace();
        }
        editorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editorFrame.setLocation((int) ((dim.getWidth() / 2) - w/2), (int) ((dim.getHeight() / 2) - h/2));
        editorFrame.setSize(w, h);
        editorFrame.setUndecorated(true);
        editorFrame.setBackground(new Color(0, 255, 0, 0));
        editorFrame.setContentPane(contentPane);
        editorFrame.setLayout(new BorderLayout());

        editorFrame.setVisible(true);
        return new InvisibleDisplay(editorFrame, contentPane);
    }

    public static class ContentPane extends JPanel implements ActionListener {

        private BufferedImage logo;
        private int w, h, i;

        public ContentPane(BufferedImage logo, int w, int h)  {
            setOpaque(false);
            this.logo = logo;
            this.w = w;
            this.h = h;
            this.i = 0;
            new Timer(5, this).start();
        }

        @Override
        protected void paintComponent(Graphics gCast) {
            super.paintComponent(gCast);
            Graphics2D g = (Graphics2D) gCast;

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setFont(new Font("Arial", Font.BOLD, 36));

            i++;
            int inc = (int) Math.abs(Math.round(25f * Math.sin(i * 1f / 40f)));
            BufferedImage resize = logo.getSubimage(0, 0, logo.getWidth() - inc, logo.getHeight() - inc);
            g.drawImage(logo, (w - resize.getWidth()) / 2, (h - resize.getHeight()) / 2, resize.getWidth(), resize.getHeight(), null);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            repaint();
        }
    }

}
