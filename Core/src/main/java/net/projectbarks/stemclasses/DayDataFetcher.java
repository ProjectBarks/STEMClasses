package net.projectbarks.stemclasses;

import lombok.Getter;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.projectbarks.stemclasses.letterday.LetterDay;
import net.projectbarks.stemclasses.letterday.LetterDayType;
import net.projectbarks.stemclasses.r.R;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class DayDataFetcher {

    private static final String CALENDAR = "http://www.dasd.org/site/handlers/icalfeed.ashx?MIID=165";
    private static Pattern IS_SCHEDULE = Pattern.compile("[A-l] Day");

    private String period;
    private LetterDay letterDay;
    private int timeDiff;
    @Getter
    private int dayLength;
    private double totalTime;

    public DayDataFetcher() {
        letterDay = null;
        dayLength = LetterDay.DAY_FULL;
    }

    public DayDataFetcher(final Callback callback) {
        this();
         Scheduler.run(new Runnable() {
             @Override
             public void run() {
                 pullDayInfo(callback);
             }
         }, 0, 1, TimeUnit.SECONDS);
    }

    public void pullDayInfo(Callback callback) {
        if (letterDay == null || !letterDay.getDate().equals(new DateTime().toLocalDate())) {
            try {
                findLetterDay();
            } catch (IOException e) {
                callback.onFailed(e.getMessage(), FailCause.NO_INTERNET_CONNECTION);
                return;
            } catch (ParserException e) {
                callback.onFailed(e.getMessage(), FailCause.FAILED_TO_PARSE);
                return;
            } catch (Exception e) {
                callback.onFailed(e.getMessage(), FailCause.UNKNOWN);
            }
        }
        try {
            updateTime();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);;
            callback.onFailed(sw.toString(), FailCause.UNKNOWN);
        }
        callback.onFind(letterDay, period, timeDiff, totalTime, (letterDay.getLetter() != null) && (timeDiff >= 0 && !period.equals("-1")));
    }

    private void findLetterDay() throws IOException, ParserException {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        letterDay = new LetterDay();
        try {
            URL oracle = new URL(CALENDAR);
            InputStreamReader reader = new InputStreamReader(oracle.openStream());
            Calendar calendar = new CalendarBuilder().build(reader);
            for (Object unknownComp : calendar.getComponents()) {
                Component component = (Component) unknownComp;
                Date date = ((DtStart) component.getProperty(Property.DTSTART)).getDate();
                Matcher matcher = IS_SCHEDULE.matcher(component.getProperty(Property.SUMMARY).getValue());
                if ((new DateTime(date).toLocalDate()).equals(letterDay.getDate()) && matcher.find()) {
                    letterDay.setLetter(String.valueOf(matcher.group(0).charAt(0)));
                    letterDay.setType(LetterDayType.match(letterDay.getLetter()));
                    break;
                }
            }
        } catch (MalformedURLException e) {
            letterDay = null;
            throw e;
        } catch (ParserException e) {
            letterDay = null;
            throw e;
        } catch (IOException e) {
            letterDay = null;
            throw e;
        }

    }

    private void updateTime() {
        if (letterDay == null) {
            return;
        } else if (letterDay.getType() == null) {
            return;
        }
        letterDay.updateProperties(getGradeRange(), getDayLength());
        int name = 0;
        for (int i = 0; i < letterDay.getTotalTimes(); i++) {
            timeDiff = letterDay.getCurrentTimeDiff(i);
            totalTime = letterDay.getTime(i);
            totalTime -= i != 0 ? letterDay.getTime(i - 1) : 0;
            name++;
            if (letterDay.isLunch(i)) {
                name--;
            }
            if (timeDiff > 0) {
                if (letterDay.isLunch(i)) {
                    period = R.text.LUNCH;
                } else if (letterDay.isAdvisory(i)) {
                    period = R.text.ADVISORY;
                } else if (letterDay.isSeminar(i)) {
                    period = R.text.SEMINAR;
                } else {
                    period = String.valueOf(name);
                }
                break;
            }
        }
    }

    public Integer getGradeRange() {
        return R.config.getGrade();
    }

    public void setGradeRange(int gradeRange) {
        R.config.setGrade(gradeRange);
        updateTime();
    }

    public void setDayLength(int dayLength) {
        this.dayLength = dayLength;
        updateTime();
    }

    //
    public static enum FailCause {
        NO_INTERNET_CONNECTION,
        FAILED_TO_PARSE,
        UNKNOWN
    }

    public static interface Callback {

        public void onFind(LetterDay day, String period, int timeTillNextClass, double totalTime, boolean hasSchool);

        public void onFailed(String message, FailCause exception);
    }

}
