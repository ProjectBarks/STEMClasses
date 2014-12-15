package net.projectbarks.stemclasses.notification;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
public class NSUserNotification {
    public static final byte BOOL_YES = 1;
    public static final byte BOOL_NO = 0;
    public static final int NSUserNotificationActivationTypeNone = 0;
    public static final int NSUserNotificationActivationTypeContentsClicked = 1;
    public static final int NSUserNotificationActivationTypeActionButtonClicked = 2;
    private static final boolean D = true;
    private static final String LIBRARY_NAME = "OSXNotification";
    private static final String CHAR_SET_ASCII = "US-ASCII";
    private static final String CHAR_SET_UTF8 = "UTF-8";
    private static final String CHAR_SET = "US-ASCII";
    private static final String NSDATE_STRING_FORMAT = "yyyy-MM-dd HH:mm:ss Z";
    private static NSUserNotificationCInterface library = null;
    private Pointer notification;
    private Pointer titleText;
    private Pointer subtitleText;
    private Pointer informativeText;
    private Pointer actionButtonText;
    private Pointer otherButtonText;
    private Pointer deliveryDate;
    private Pointer deliveryTimeZone;

    public NSUserNotification() {
        if (library == null) {
            System.setProperty("jna.library.path", getClass().getClassLoader().getResource("").getPath());
            library = (NSUserNotificationCInterface) Native.loadLibrary("OSXNotification", NSUserNotificationCInterface.class);
        }
        this.notification = library.NSUserNotificationAllocInit();
    }

    public NSUserNotification(Pointer object) {
        if (library == null) {
            library = (NSUserNotificationCInterface) Native.loadLibrary("OSXNotification", NSUserNotificationCInterface.class);
        }
        this.notification = object;
    }

    private static byte[] bytesFromString(String input) {
        return bytesFromString(input, "US-ASCII");
    }

    private static byte[] bytesFromString(String input, String charSet) {
        byte[] returnBytes = null;
        if ((input != null) && (!input.equals(""))) {
            try {
                returnBytes = input.getBytes(charSet);
            } catch (UnsupportedEncodingException e) {
                System.out.println(": Data error:" + e.getMessage());
            }
        }
        return returnBytes;
    }

    private static String stringFromBytes(byte[] input) {
        return stringFromBytes(input, "US-ASCII");
    }

    private static String stringFromBytes(byte[] input, String charSet) {
        String returnString = "";
        if (input != null) {
            try {
                returnString = new String(input, 0, input.length, charSet);
            } catch (UnsupportedEncodingException e) {
                System.out.println("Data error:" + e.getMessage());
            }
        }
        return returnString;
    }

    public Pointer getPointer() {
        return this.notification;
    }

    public String getTitle() {
        if (this.notification == null) {
            return null;
        }
        Pointer p = library.NSUserNotificationGetTitle(this.notification);
        return stringFromPointer(p);
    }

    public void setTitle(String input) {
        if (this.notification == null) {
            return;
        }
        byte[] inputBytes = bytesFromString(input + '\000');
        this.titleText = new Memory(inputBytes.length);
        this.titleText.write(0L, inputBytes, 0, inputBytes.length);
        library.NSUserNotificationSetTitle(this.notification, this.titleText);
    }

    public String getSubtitle() {
        if (this.notification == null) {
            return null;
        }
        Pointer p = library.NSUserNotificationGetSubtitle(this.notification);
        return stringFromPointer(p);
    }

    public void setSubtitle(String input) {
        if (this.notification == null) {
            return;
        }
        byte[] inputBytes = bytesFromString(input + '\000');
        this.subtitleText = new Memory(inputBytes.length);
        this.subtitleText.write(0L, inputBytes, 0, inputBytes.length);
        library.NSUserNotificationSetSubtitle(this.notification, this.subtitleText);
    }

    public String getText() {
        if (this.notification == null) {
            return null;
        }
        Pointer p = library.NSUserNotificationGetInformativeText(this.notification);
        return stringFromPointer(p);
    }

    public void setText(String input) {
        if (this.notification == null) {
            return;
        }
        byte[] inputBytes = bytesFromString(input + '\000');
        this.informativeText = new Memory(inputBytes.length);
        this.informativeText.write(0L, inputBytes, 0, inputBytes.length);
        library.NSUserNotificationSetInformativeText(this.notification, this.informativeText);
    }

    public void enableActionButton() {
        if (this.notification == null) {
            return;
        }
        library.NSUserNotificationSetHasActionButton(this.notification, (byte) 1);
    }

    public void disableActionButton() {
        if (this.notification == null) {
            return;
        }
        library.NSUserNotificationSetHasActionButton(this.notification, (byte) 0);
    }

    public boolean hasActionButton() {
        if (this.notification == null) {
            return false;
        }
        boolean enabled = false;
        if (library.NSUserNotificationHasActionButton(this.notification) == 1) {
            enabled = true;
        }
        return enabled;
    }

    public String getActionButtonTitle() {
        if (this.notification == null) {
            return null;
        }
        Pointer p = library.NSUserNotificationGetActionButtonTitle(this.notification);
        return stringFromPointer(p);
    }

    public void setActionButtonTitle(String input) {
        if (this.notification == null) {
            return;
        }
        byte[] inputBytes = bytesFromString(input + '\000');
        this.actionButtonText = new Memory(inputBytes.length);
        this.actionButtonText.write(0L, inputBytes, 0, inputBytes.length);
        library.NSUserNotificationSetActionButtonTitle(this.notification, this.actionButtonText);
    }

    public String getOtherButtonTitle() {
        if (this.notification == null) {
            return null;
        }
        Pointer p = library.NSUserNotificationGetOtherButtonTitle(this.notification);
        return stringFromPointer(p);
    }

    public void setOtherButtonTitle(String input) {
        if (this.notification == null) {
            return;
        }
        byte[] inputBytes = bytesFromString(input + '\000');
        this.otherButtonText = new Memory(inputBytes.length);
        this.otherButtonText.write(0L, inputBytes, 0, inputBytes.length);
        library.NSUserNotificationSetOtherButtonTitle(this.notification, this.otherButtonText);
    }

    public Calendar getDeliveryDate() {
        if (this.notification == null) {
            return null;
        }
        Pointer p = library.NSUserNotificationGetDeliveryDate(this.notification);
        String deliveryString = stringFromPointer(p);
        SimpleDateFormat NSDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        ParsePosition pos = new ParsePosition(0);
        Date deliveryDate = NSDateFormat.parse(deliveryString, pos);
        Calendar deliveryCalendar = Calendar.getInstance();
        deliveryCalendar.setTime(deliveryDate);
        return deliveryCalendar;
    }

    public void setDeliveryDate(Calendar date) {
        if (this.notification == null) {
            return;
        }
        Calendar now = Calendar.getInstance();
        DateFormat NSDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        String NSDateString = NSDateFormat.format(date.getTime());
        byte[] inputBytes = bytesFromString(NSDateString + '\000');
        if (date.after(now)) {
            this.deliveryDate = new Memory(inputBytes.length);
            this.deliveryDate.write(0L, inputBytes, 0, inputBytes.length);
            library.NSUserNotificationSetDeliveryDate(this.notification, this.deliveryDate);
        }
    }

    public Calendar getActualDeliveryDate() {
        if (this.notification == null) {
            return null;
        }
        Pointer p = library.NSUserNotificationGetActualDeliveryDate(this.notification);
        String deliveryString = stringFromPointer(p);
        SimpleDateFormat NSDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        ParsePosition pos = new ParsePosition(0);
        Date deliveryDate = NSDateFormat.parse(deliveryString, pos);
        Calendar deliveryCalendar = Calendar.getInstance();
        deliveryCalendar.setTime(deliveryDate);
        return deliveryCalendar;
    }

    public int getDeliveryRepeatInterval() {
        if (this.notification == null) {
            return -1;
        }
        return library.NSUserNotificationGetDeliveryRepeatInterval(this.notification);
    }

    public void setDeliveryRepeatInterval(int seconds) {
        if (this.notification == null) {
            return;
        }
        if (seconds >= 60) library.NSUserNotificationSetDeliveryRepeatInterval(this.notification, seconds);
    }

    public String getDeliveryTimeZone() {
        if (this.notification == null) {
            return null;
        }
        Pointer p = library.NSUserNotificationGetDeliveryTimeZone(this.notification);
        return stringFromPointer(p);
    }

    public void setDeliveryTimeZone(String input) {
        if (this.notification == null) {
            return;
        }
        byte[] inputBytes = bytesFromString(input + '\000');
        this.deliveryTimeZone = new Memory(inputBytes.length);
        this.deliveryTimeZone.write(0L, inputBytes, 0, inputBytes.length);
        library.NSUserNotificationSetDeliveryTimeZone(this.notification, this.deliveryTimeZone);
    }

    public boolean isPresented() {
        if (this.notification == null) {
            return false;
        }
        boolean presented = false;
        if (library.NSUserNotificationIsPresented(this.notification) == 1) {
            presented = true;
        }
        return presented;
    }

    public boolean isRemote() {
        if (this.notification == null) {
            return false;
        }
        boolean remote = false;
        if (library.NSUserNotificationIsRemote(this.notification) == 1) {
            remote = true;
        }
        return remote;
    }

    public String getSoundName() {
        if (this.notification == null) {
            return null;
        }
        Pointer p = library.NSUserNotificationGetSoundName(this.notification);
        return stringFromPointer(p);
    }

    public void setDefaultSoundName() {
        if (this.notification == null) {
            return;
        }
        library.NSUserNotificationSetDefaultSoundName(this.notification);
    }

    public int getActivationType() {
        if (this.notification == null) {
            return -1;
        }
        return library.NSUserNotificationGetActivationType(this.notification);
    }

    public void close() {
        if (this.notification == null) {
            return;
        }
        library.NSUserNotificationRelease(this.notification);
        this.notification = null;
    }

    private String stringFromPointer(Pointer p) {
        byte[] buffer = new byte[1024];
        int index = 0;
        byte tmp;
        while ((tmp = p.getByte(index)) != 0) {
            buffer[index] = tmp;
            index++;
        }
        String returnString = stringFromBytes(buffer);
        returnString = returnString.substring(0, index);
        return returnString;
    }

    private static abstract interface NSUserNotificationCInterface extends Library {
        public abstract Pointer NSUserNotificationAllocInit();

        public abstract void NSUserNotificationSetTitle(Pointer paramPointer1, Pointer paramPointer2);

        public abstract Pointer NSUserNotificationGetTitle(Pointer paramPointer);

        public abstract void NSUserNotificationSetSubtitle(Pointer paramPointer1, Pointer paramPointer2);

        public abstract Pointer NSUserNotificationGetSubtitle(Pointer paramPointer);

        public abstract void NSUserNotificationSetInformativeText(Pointer paramPointer1, Pointer paramPointer2);

        public abstract Pointer NSUserNotificationGetInformativeText(Pointer paramPointer);

        public abstract void NSUserNotificationSetHasActionButton(Pointer paramPointer, byte paramByte);

        public abstract byte NSUserNotificationHasActionButton(Pointer paramPointer);

        public abstract void NSUserNotificationSetActionButtonTitle(Pointer paramPointer1, Pointer paramPointer2);

        public abstract Pointer NSUserNotificationGetActionButtonTitle(Pointer paramPointer);

        public abstract void NSUserNotificationSetOtherButtonTitle(Pointer paramPointer1, Pointer paramPointer2);

        public abstract Pointer NSUserNotificationGetOtherButtonTitle(Pointer paramPointer);

        public abstract void NSUserNotificationSetDeliveryDate(Pointer paramPointer1, Pointer paramPointer2);

        public abstract Pointer NSUserNotificationGetDeliveryDate(Pointer paramPointer);

        public abstract Pointer NSUserNotificationGetActualDeliveryDate(Pointer paramPointer);

        public abstract void NSUserNotificationSetDeliveryRepeatInterval(Pointer paramPointer, int paramInt);

        public abstract int NSUserNotificationGetDeliveryRepeatInterval(Pointer paramPointer);

        public abstract void NSUserNotificationSetDeliveryTimeZone(Pointer paramPointer1, Pointer paramPointer2);

        public abstract Pointer NSUserNotificationGetDeliveryTimeZone(Pointer paramPointer);

        public abstract byte NSUserNotificationIsPresented(Pointer paramPointer);

        public abstract byte NSUserNotificationIsRemote(Pointer paramPointer);

        public abstract Pointer NSUserNotificationGetSoundName(Pointer paramPointer);

        public abstract void NSUserNotificationSetSoundName(Pointer paramPointer1, Pointer paramPointer2);

        public abstract void NSUserNotificationSetDefaultSoundName(Pointer paramPointer);

        public abstract byte NSUserNotificationGetActivationType(Pointer paramPointer);

        public abstract void NSUserNotificationRelease(Pointer paramPointer);
    }
}