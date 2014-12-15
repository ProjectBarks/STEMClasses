package net.projectbarks.stemclasses.deployer.views;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.*;

/**
 * Created by brandon on 12/12/14.
 */
public class LoggingDisplay {
    @Getter
    private JPanel panel1;
    private JButton closeButton;
    private JButton clearButton;
    private JTextArea loggingArea;
    private JScrollPane scroll;
    @Getter
    private Logger logger;
    @Getter
    private boolean failed;

    public static LoggingDisplay display() {
        JFrame frame = new JFrame("STEMClasses Updater");
        LoggingDisplay display = new LoggingDisplay();
        frame.setContentPane(display.getPanel1());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(560, 300);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setAutoRequestFocus(true);
        frame.setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint());
        display.init();
        return display;
    }

    public void init() {
        failed = false;
        final JFrame frame = (JFrame) SwingUtilities.getRoot(panel1);
        logger = Logger.getLogger("Deployer");
        logger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                setFormatter(new Formatter() {
                    @Override
                    public String format(LogRecord record) {
                        if (record.getLevel().equals(Level.SEVERE)) {
                            failed = true;
                        }
                        return "\n[" + record.getLevel() + "]: " + formatMessage(record);
                    }
                });
                if (isLoggable(record)) {
                    loggingArea.append(getFormatter().format(record));
                    scroll.getVerticalScrollBar().setMinimum(scroll.getVerticalScrollBar().getMaximum());
                }
            }

            @Override
            public void flush() {}

            @Override
            public void close() throws SecurityException {}
        });
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loggingArea.setText("");
            }
        });
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                String ObjButtons[] = {"Yes", "No"};
                int PromptResult = JOptionPane.showOptionDialog(null, "Are you sure you want to exit?\nThe update process will stop!",
                        "Online Examination System", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
                        ObjButtons, ObjButtons[1]);
                if (PromptResult == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }
}
