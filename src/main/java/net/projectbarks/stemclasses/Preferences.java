package net.projectbarks.stemclasses;

import net.projectbarks.stemclasses.r.R;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by brandon on 12/12/14.
 */
public class Preferences {
    private JPanel panel1;
    private JRadioButton solidRadioButton, outlineRadioButton, enabledRadioButton, disabledRadioButton;
    private JButton applyButton, cancelButton;
    private JLabel color, timePreview, notPreview;

    private int rotateValue;
    private boolean end;

    public static void display() {
        JFrame frame = new JFrame("Preferences");
        final Preferences preferences = new Preferences();
        frame.setContentPane(preferences.panel1);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setSize(360, 230);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
        frame.setAutoRequestFocus(true);
        frame.setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint());
        preferences.init();
    }

    public void init() {
        (R.config.isOutline() ? outlineRadioButton : solidRadioButton).setSelected(true);
        (R.config.isAnimate() ? enabledRadioButton : disabledRadioButton).setSelected(true);
        color.setForeground(R.config.getColor());

        final JFrame frame = (JFrame) SwingUtilities.getRoot(panel1);
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                R.config.setAnimate(enabledRadioButton.isSelected());
                R.config.setOutline(outlineRadioButton.isSelected());
                R.config.setColor(color.getForeground());
                frame.dispose();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                end = true;
                super.windowClosing(e);
            }
        });
        color.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                color.setForeground(JColorChooser.showDialog(panel1, "Choose a color", color.getForeground()));
            }
        });

        end = false;
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    for (int i = 45; i > 0; i--) {
                        if (end) {
                            return;
                        }
                        rotateValue = i;
                        update();
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();

        onClickDisableOther(solidRadioButton, outlineRadioButton);
        onClickDisableOther(outlineRadioButton, solidRadioButton);
        onClickDisableOther(enabledRadioButton, disabledRadioButton);
        onClickDisableOther(disabledRadioButton, enabledRadioButton);
        onUpdate(solidRadioButton);
        onUpdate(outlineRadioButton);
        onUpdate(enabledRadioButton);
        onUpdate(disabledRadioButton);
        onUpdate(color);
        update();
    }

    private void onClickDisableOther(final JRadioButton one, final JRadioButton two) {
        one.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                two.setSelected(!one.isSelected());
            }
        });
    }

    private void onUpdate(final JComponent component) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                update();
            }
        });
    }

    private void update() {
        timePreview.setIcon(new ImageIcon(R.draw.drawIcon("STEM", color.getForeground(), outlineRadioButton.isSelected(),
                enabledRadioButton.isSelected(), 0)));
        notPreview.setIcon(new ImageIcon(R.draw.drawIcon(rotateValue + "", color.getForeground(), outlineRadioButton.isSelected(),
                enabledRadioButton.isSelected(), (double) rotateValue / 55)));
    }

}
