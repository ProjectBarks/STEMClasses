package net.projectbarks.stemclasses;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class About extends JDialog {
    private JPanel contentPane;
    protected JLabel version;

    public About() {
        setContentPane(contentPane);
        setModal(true);
    }


    public static About open() {
        About dialog = new About();
        String version = dialog.getClass().getPackage().getImplementationVersion();;
        if (version == null) {
            version = "0.0.0";
        }
        dialog.pack();
        dialog.setSize(200, 300);
        dialog.setLocationRelativeTo(null);
        dialog.setAlwaysOnTop(true);
        dialog.version.setText(dialog.version.getText().replaceAll("x", version));
        dialog.setResizable(false);
        dialog.toFront();
        dialog.requestFocus();
        dialog.setAutoRequestFocus(true);
        dialog.setVisible(true);
        return dialog;
    }
}
