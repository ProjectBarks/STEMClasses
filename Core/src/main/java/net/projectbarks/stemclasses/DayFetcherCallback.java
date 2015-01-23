package net.projectbarks.stemclasses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.projectbarks.stemclasses.DayDataFetcher;
import net.projectbarks.stemclasses.letterday.LetterDay;
import net.projectbarks.stemclasses.r.R;

import java.awt.*;

/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Written By: brandon on 1/9/15
 */
public class DayFetcherCallback implements DayDataFetcher.Callback {

    @Getter
    private TrayIcon trayIcon;
    @Getter
    private MenuItem period, letterDay, schedule;

    private String cache;

    public DayFetcherCallback(TrayIcon trayIcon, MenuItem period, MenuItem day, MenuItem schedule) {
        this.trayIcon = trayIcon;
        this.period = period;
        this.letterDay = day;
        this.schedule = schedule;
        period.setEnabled(false);
        letterDay.setEnabled(false);
        schedule.setEnabled(false);
    }

    @Override
    public void onFind(LetterDay day, String letter, int timeTillNextClass, double totalTime, boolean hasSchool) {
        if (hasSchool) {
            if (cacheCheck(timeTillNextClass + letter)) {
                return;
            }
            trayIcon.setImage(R.draw.drawIcon(String.valueOf(timeTillNextClass), (float) (timeTillNextClass / totalTime)));
            period.setLabel(String.format("Class Period: %s", letter));
            letterDay.setLabel(String.format(String.format("Letter Day: %s", day.getLetter())));
            schedule.setLabel(String.format(String.format("Schedule: %s", day.getType().getRepresentation())));
        } else {
            if (cacheCheck("OVER")) {
                return;
            }
            trayIcon.setImage(R.draw.drawIcon(R.text.TRAY));
            period.setLabel("School is Over");
            letterDay.setLabel("Until then, Have fun!");
            schedule.setLabel("See you later folks!");
        }
    }

    @Override
    public void onFailed(String message, DayDataFetcher.FailCause exception) {
        System.out.println(message);
        if (cacheCheck("FAILED")) {
            return;
        }
        trayIcon.setImage(R.draw.drawIcon("ERROR"));
        period.setLabel("It appears there");
        letterDay.setLabel("was an error");
        schedule.setLabel("Try connecting to wifi!");
    }

    private boolean cacheCheck(String cache) {
        if (this.cache != null && this.cache.equals(cache)) {
            return true;
        }
        this.cache = cache;
        return false;
    }
}
