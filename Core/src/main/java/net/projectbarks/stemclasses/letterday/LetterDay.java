package net.projectbarks.stemclasses.letterday;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
public class LetterDay {

    public static final int GRADE_9_10 = 1, GRADE_11_12 = 2, DAY_HALF = 3, DAY_2_HOUR = 4, DAY_FULL = 5;
    private static SimpleDateFormat SDF = new SimpleDateFormat("HH:mm");
    private String letter;
    private LetterDayType type;
    private LocalDate date;
    private int gradeRange, length;
    private String[] times;

    public LetterDay() {
        this(null, null, new DateTime().toLocalDate());
    }

    public LetterDay(String letter, LetterDayType type, LocalDate date) {
        this.letter = letter;
        this.type = type;
        this.date = date;
        this.gradeRange = GRADE_9_10;
        this.length = DAY_FULL;
        if (letter != null && type != null) {
            updateProperties(gradeRange, length);
        }
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public void updateProperties(int gradeRange, int length) {
        if (gradeRange == GRADE_9_10) {
            if (length == DAY_FULL) {
                if (type.equals(LetterDayType.ONE_THROUGH_FOUR)) {
                    times = new String[]{"9:15", "10:49", "11:23!", "12:57", "14:35"};
                } else if (type.equals(LetterDayType.FIVE_THROUGH_SEVEN)) {
                    times = new String[]{"9:15", "10:49", "11:23!", "12:57", "13:51%", "14:35&"};
                } else {
                    times = new String[]{"8:35", "9:29", "10:23", "11:17", "11:51!", "12:45", "13:39", "14:35"};
                }
            } else if (length == DAY_HALF) {
                if (type.equals(LetterDayType.ONE_THROUGH_FOUR)) {
                    times = new String[]{"8:44", "9:52", "11:00", "11:33!", "12:35"};
                } else if (type.equals(LetterDayType.FIVE_THROUGH_SEVEN)) {
                    times = new String[]{"8:55", "10:13", "10:46!", "12:04", "12:35&"};
                } else {
                    times = new String[]{"8:19", "8:57", "9:35", "10:13", "10:46!", "11:24", "12:02", "12:35"};
                }
            } else if (length == DAY_2_HOUR) {
                if (type.equals(LetterDayType.ONE_THROUGH_FOUR)) {
                    times = new String[]{"10:44", "11:52", "13:00", "13:33!", "14:35"};
                } else if (type.equals(LetterDayType.FIVE_THROUGH_SEVEN)) {
                    times = new String[]{"10:55", "12:13", "12:46!", "14:04", "14:35&"};
                } else {
                    times = new String[]{"10:19", "10:57", "11:35", "12:13", "12:46!", "13:24", "14:02", "14:35"};
                }
            } else {
                throw new IndexOutOfBoundsException("Invalid day length!");
            }
        } else if (gradeRange == GRADE_11_12) {
            if (length == DAY_FULL) {
                if (type.equals(LetterDayType.ONE_THROUGH_FOUR)) {
                    times = new String[]{"9:15", "10:49", "12:23", "12:57!", "14:35"};
                } else if (type.equals(LetterDayType.FIVE_THROUGH_SEVEN)) {
                    times = new String[]{"9:15", "10:49", "12:23", "12:57!", "13:51%", "14:35&"};
                } else {
                    times = new String[]{"8:35", "9:29", "10:23", "11:17", "12:11", "12:45!", "13:39", "14:35"};
                }
            } else if (length == DAY_HALF) {
                if (type.equals(LetterDayType.ONE_THROUGH_FOUR)) {
                    times = new String[]{"8:44", "9:52", "11:00", "12:07", "12:35!"};
                } else if (type.equals(LetterDayType.FIVE_THROUGH_SEVEN)) {
                    times = new String[]{"8:55", "10:13", "11:31", "12:04!", "12:35&"};
                } else {
                    times = new String[]{"8:19", "8:57", "9:35", "10:13", "10:51", "11:24!", "12:02", "12:35"};
                }
            } else if (length == DAY_2_HOUR) {
                if (type.equals(LetterDayType.ONE_THROUGH_FOUR)) {
                    times = new String[]{"10:44", "11:52", "12:24!", "13:33", "14:35"};
                } else if (type.equals(LetterDayType.FIVE_THROUGH_SEVEN)) {
                    times = new String[]{"10:55", "12:13", "13:31", "14:04!", "14:35&"};
                } else {
                    times = new String[]{"10:19", "10:57", "11:35", "12:13", "12:51", "13:24!", "14:02", "14:35"};
                }
            } else {
                throw new IndexOutOfBoundsException("Invalid day length!");
            }
        } else {
            throw new IndexOutOfBoundsException("Invalid grade!");
        }
    }

    public boolean isLunch(int selection) {
        return times[selection].contains("!");
    }

    public boolean isAdvisory(int selection) {
        return times[selection].contains("%");
    }

    public boolean isSeminar(int selection) {
        return times[selection].contains("&");
    }

    public int getCurrentTimeDiff(int selection) {
        return getTime(times[selection]) - getTime(SDF.format(Calendar.getInstance().getTime()));
    }

    public int getTime(int selection) {
        return getTime(times[selection]);
    }

    public int getTotalTimes() {
        return times.length;
    }

    public LetterDayType getType() {
        return type;
    }

    public void setType(LetterDayType type) {
        this.type = type;
        updateProperties(gradeRange, length);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    private Integer getTime(String format) {
        String[] split = format.replaceAll("(!|%|&)", "").split(":");
        return (Integer.parseInt(split[0]) * 60) + Integer.parseInt(split[1]);
    }

}
