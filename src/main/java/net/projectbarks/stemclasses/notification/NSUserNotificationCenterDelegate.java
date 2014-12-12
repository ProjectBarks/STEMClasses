package net.projectbarks.stemclasses.notification;

public abstract interface NSUserNotificationCenterDelegate {
    public abstract void didActivateNotification(NSUserNotification paramNSUserNotification);

    public abstract void didDeliverNotification(NSUserNotification paramNSUserNotification);

    public abstract byte shouldPresentNotification(NSUserNotification paramNSUserNotification);
}