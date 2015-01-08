package net.projectbarks.stemclasses;

import javafx.stage.Screen;
import lombok.Getter;
import net.projectbarks.stemclasses.letterday.LetterDay;
import net.projectbarks.stemclasses.notification.NSUserNotification;
import net.projectbarks.stemclasses.notification.NSUserNotificationCenter;
import net.projectbarks.stemclasses.r.R;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
public class STEMClasses implements ActionListener, ItemListener {

    @Getter
    private static String path;
    @Getter
    private static ScheduledExecutorService pool;
    private final SystemTray tray;
    private PopupMenu popup;
    private TrayIcon trayIcon;
    private MenuItem period, letterDay, schedule;
    private DayDataFetcher.Callback callback = new DayDataFetcher.Callback() {

        private String cache;

        @Override
        public void onFind(LetterDay day, String letter, int timeTillNextClass, double totalTime, boolean hasSchool) {
            if (hasSchool) {
                trayIcon.setImage(R.draw.drawIcon(String.valueOf(timeTillNextClass), timeTillNextClass / totalTime));
                period.setLabel(String.format("Class Period: %s", letter));
                letterDay.setLabel(String.format(String.format("Letter Day: %s", day.getLetter())));
                schedule.setLabel(String.format(String.format("Schedule: %s", day.getType().getRepresentation())));
            } else {
                trayIcon.setImage(R.draw.drawIcon(R.text.TRAY));
                period.setLabel("School is Over");
                letterDay.setLabel("Until then, Have fun!");
                schedule.setLabel("See you later folks!");
            }
        }

        @Override
        public void onFailed(String message, DayDataFetcher.FailCause exception) {
            trayIcon.setImage(R.draw.drawIcon("ERROR"));
            period.setLabel("It appears there");
            letterDay.setLabel("was an error");
            schedule.setLabel("Try connecting to wifi!");
        }
    };
    private Map<String, MenuItem> menuItems;
    private DayDataFetcher finder;

    private STEMClasses() {
        tray = SystemTray.getSystemTray();
        menuItems = new HashMap<String, MenuItem>();
        setupTray();
        finder = new DayDataFetcher(callback);
        itemClick(R.config.getGrade() == LetterDay.GRADE_9_10 ? R.text.GRADE_9_10 : R.text.GRADE_11_12);
        if (R.config.isAutoUpdate()) {
            getItem(R.text.AUTO_UPDATES, CheckboxMenuItem.class).setState(R.config.isAutoUpdate());
        }
        itemClick(R.text.DAY_LENGTH_NORMAL);
    }

    public static void main(String[] objects) throws URISyntaxException {
        System.setProperty("apple.awt.UIElement", "true");
        String[] pathSplit = STEMClasses.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().split(File.separator);
        path = "/";
        for (int i = 0; i < pathSplit.length - 1; i++) {
            String pathItem = pathSplit[i];
            path += pathItem;
            path += File.separator;
        }
        pool = Executors.newScheduledThreadPool(5);

        R.config.load(path);
        R.config.save(path);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                R.config.save(path);
            }
        });
        if (!SystemTray.isSupported()) {
            JOptionPane.showMessageDialog(null, R.text.ERROR_MSG_TRAY, R.text.ERROR_TITLE_TRAY, JOptionPane.ERROR_MESSAGE);
            return;
        } else if (!System.getProperty("os.name").toLowerCase().contains("mac")) {
            JOptionPane.showMessageDialog(null, R.text.ERROR_MSG_OS, R.text.ERROR_TITLE_OS, JOptionPane.ERROR_MESSAGE);
            return;
        }
        new STEMClasses();
    }

    private void setupTray() {
        popup = new PopupMenu();
        trayIcon = new TrayIcon(R.draw.drawIcon(R.text.TRAY), "STEM Scheduler");
        period = addItem(R.text.PERIOD, MenuItem.class);
        letterDay = addItem(R.text.LETTER_DAY, MenuItem.class);
        schedule = addItem(R.text.SCHEDULE, MenuItem.class);

        popup.addSeparator();
        addItem(R.text.ABOUT, MenuItem.class);
        Menu menu = addItem(R.text.SCHEDULE, Menu.class);
        Menu length = addItem(R.text.DAY_LENGTH, Menu.class, menu);
        addItem(R.text.DAY_LENGTH_LATE, CheckboxMenuItem.class, length);
        addItem(R.text.DAY_LENGTH_EARLY, CheckboxMenuItem.class, length);
        addItem(R.text.DAY_LENGTH_NORMAL, CheckboxMenuItem.class, length);
        Menu grade = addItem(R.text.GRADE, Menu.class, menu);
        addItem(R.text.GRADE_9_10, CheckboxMenuItem.class, grade);
        addItem(R.text.GRADE_11_12, CheckboxMenuItem.class, grade);
        popup.addSeparator();
        Menu updates = addItem(R.text.UPDATES, Menu.class);
        addItem(R.text.CHECK_UPDATES, MenuItem.class, updates);
        addItem(R.text.PREFERENCES, MenuItem.class);
        addItem(R.text.AUTO_UPDATES, CheckboxMenuItem.class, updates);
        addItem(R.text.QUIT, MenuItem.class);

        period.setEnabled(false);
        letterDay.setEnabled(false);
        schedule.setEnabled(false);
        trayIcon.setPopupMenu(popup);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        itemClick(((MenuItem) e.getSource()).getLabel());
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        itemClick(((MenuItem) e.getSource()).getLabel());
    }

    private void itemClick(String label) {
        if (label.equals(R.text.QUIT)) {
            System.exit(0);
        } else if (isText(label, R.text.GRADE_9_10, R.text.GRADE_11_12)) {
            checkSelected(label, R.text.GRADE_9_10, R.text.GRADE_11_12);
            if (getItem(R.text.GRADE_9_10, CheckboxMenuItem.class).getState()) {
                finder.setGradeRange(LetterDay.GRADE_9_10);
            } else {
                finder.setGradeRange(LetterDay.GRADE_11_12);
            }
        } else if (isText(label, R.text.DAY_LENGTH_NORMAL, R.text.DAY_LENGTH_LATE, R.text.DAY_LENGTH_EARLY)) {
            checkSelected(label, R.text.DAY_LENGTH_NORMAL, R.text.DAY_LENGTH_LATE, R.text.DAY_LENGTH_EARLY);
            if (getItem(R.text.DAY_LENGTH_NORMAL, CheckboxMenuItem.class).getState()) {
                finder.setDayLength(LetterDay.DAY_FULL);
            } else if (getItem(R.text.DAY_LENGTH_LATE, CheckboxMenuItem.class).getState()) {
                finder.setDayLength(LetterDay.DAY_2_HOUR);
            } else {
                finder.setDayLength(LetterDay.DAY_HALF);
            }
        } else if (label.equals(R.text.AUTO_UPDATES)) {
            R.config.setAutoUpdate(getItem(R.text.AUTO_UPDATES, CheckboxMenuItem.class).getState());
        } else if (label.equals(R.text.PREFERENCES)) {
            Preferences.display();
        } else if (label.equals(R.text.ABOUT)) {
            About.open();
        }
    }

    private <T extends MenuItem> T getItem(String name, Class<T> item) {
        if (!item.isInstance(menuItems.get(name))) {
            return null;
        }
        return ((T) menuItems.get(name));
    }

    private void checkSelected(String name, String... items) {
        for (String item : items) {
            CheckboxMenuItem checkboxMenuItem = getItem(item, CheckboxMenuItem.class);
            if (item.equalsIgnoreCase(name)) {
                checkboxMenuItem.setState(true);
                continue;
            }
            checkboxMenuItem.setState(false);
        }
    }

    private boolean isText(String value, String... tests) {
        for (String test : tests) {
            if (!test.equalsIgnoreCase(value)) {
                continue;
            }
            return true;
        }
        return false;
    }

    private <T extends MenuItem> T addItem(String name, Class<T> item) {
        return addItem(name, item, popup);
    }

    private <T extends MenuItem> T addItem(String name, Class<T> item, Menu menu) {
        try {
            T t = item.getConstructor(String.class).newInstance(name);
            t.addActionListener(this);
            if (t instanceof CheckboxMenuItem) {
                ((CheckboxMenuItem) t).addItemListener(this);
            }
            menu.add(t);
            menuItems.put(name, t);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}