package net.projectbarks.stemclasses.notification;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.util.ArrayList;

public class NSUserNotificationCenter {
    private static final boolean D = true;
    private static final String LIBRARY_NAME = "OSXNotification";
    private static NSUserNotificationCenterCInterface library = null;
    private static NSUserNotificationCenter instance = null;
    private static Callback activate;
    private static Callback deliver;
    private static Callback present;
    private static Callback defaultActivateCallback = new Callback() {
        public void callback(Pointer n) {
            System.out.println("no delegate defined.");
        }
    };
    private static Callback defaultDeliverCallback = new Callback() {
        public void callback(Pointer n) {
            System.out.println("no delegate defined.");
        }
    };
    private static Callback defaultPresentCallback = new Callback() {
        public byte callback(Pointer n) {
            System.out.println("no delegate defined.");
            return 0;
        }
    };

    private NSUserNotificationCenter() {
        if (library == null)
            library = (NSUserNotificationCenterCInterface) Native.loadLibrary("OSXNotification", NSUserNotificationCenterCInterface.class);
    }

    public static NSUserNotificationCenter getInstance() {
        if (instance == null) {
            instance = new NSUserNotificationCenter();
            instance.setDelegate(null);
        }
        return instance;
    }

    public ArrayList<NSUserNotification> getDeliveredNotifications() {
        int count = library.NSUserNotificationCenterGetDeliveredNotificationsCount();
        ArrayList mNotifications = new ArrayList();
        for (int i = 0; i < count; i++) {
            Pointer tmp = library.NSUserNotificationCenterGetDeliveredNotification(i);
            mNotifications.add(new NSUserNotification(tmp));
        }
        return mNotifications;
    }

    public void deliverNotification(NSUserNotification notice) {
        library.NSUserNotificationCenterDeliverNotification(notice.getPointer());
    }

    public ArrayList<NSUserNotification> getScheduledNotifications() {
        int count = library.NSUserNotificationCenterGetScheduledNotificationsCount();
        ArrayList mNotifications = new ArrayList();
        for (int i = 0; i < count; i++) {
            Pointer tmp = library.NSUserNotificationCenterGetScheduledNotification(i);
            mNotifications.add(new NSUserNotification(tmp));
        }
        return mNotifications;
    }

    public void scheduleNotification(NSUserNotification notice) {
        library.NSUserNotificationCenterScheduleNotification(notice.getPointer());
    }

    public void removeScheduledNotification(NSUserNotification notice) {
        library.NSUserNotificationCenterRemoveScheduledNotification(notice.getPointer());
    }

    public void removeDeliveredNotification(NSUserNotification notice) {
        library.NSUserNotificationCenterRemoveDeliveredNotification(notice.getPointer());
    }

    public void removeAllDeliveredNotifications() {
        library.NSUserNotificationCenterRemoveAllDeliveredNotifications();
    }

    public void setDefaultDelegate() {
        library.NSUserNotificationCenterSetDefaultDelegate();
    }

    public void setDelegate(final NSUserNotificationCenterDelegate delegate) {
        if (delegate != null) {
            System.out.println("delegate is not null");
            activate = new Callback() {
                public void callback(Pointer n) {
                    delegate.didActivateNotification(new NSUserNotification(n));
                }
            };
            deliver = new Callback() {
                public void callback(Pointer n) {
                    delegate.didDeliverNotification(new NSUserNotification(n));
                }
            };
            present = new Callback() {
                public byte callback(Pointer n) {
                    return delegate.shouldPresentNotification(new NSUserNotification(n));
                }
            };
        } else {
            activate = defaultActivateCallback;
            deliver = defaultDeliverCallback;
            present = defaultPresentCallback;
        }
        library.NSUserNotificationCenterSetDelegate(activate, deliver, present);
    }

    private static abstract interface NSUserNotificationCenterCInterface extends Library {
        public abstract void NSUserNotificationCenterScheduleNotification(Pointer paramPointer);

        public abstract int NSUserNotificationCenterGetDeliveredNotificationsCount();

        public abstract Pointer NSUserNotificationCenterGetDeliveredNotification(int paramInt);

        public abstract void NSUserNotificationCenterRemoveScheduledNotification(Pointer paramPointer);

        public abstract void NSUserNotificationCenterDeliverNotification(Pointer paramPointer);

        public abstract int NSUserNotificationCenterGetScheduledNotificationsCount();

        public abstract Pointer NSUserNotificationCenterGetScheduledNotification(int paramInt);

        public abstract void NSUserNotificationCenterRemoveDeliveredNotification(Pointer paramPointer);

        public abstract void NSUserNotificationCenterRemoveAllDeliveredNotifications();

        public abstract void NSUserNotificationCenterSetDefaultDelegate();

        public abstract void NSUserNotificationCenterSetDelegate(Callback paramCallback1, Callback paramCallback2, Callback paramCallback3);
    }
}