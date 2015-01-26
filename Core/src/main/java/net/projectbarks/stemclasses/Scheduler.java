package net.projectbarks.stemclasses;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

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
 * Written By: brandon on 1/12/15
 */
public class Scheduler {

    @Getter
    private static ScheduledExecutorService pool;

    static {
        pool = Executors.newSingleThreadScheduledExecutor();
    }

    public static void run(final Runnable runnable, long delay, long interval, TimeUnit intervalUnit) {
        if (delay < 0 && interval < 0) {
            pool.schedule(runnable, 15, intervalUnit);
        } else if (interval < 0) {
            pool.schedule(runnable, delay, intervalUnit);
        } else {
             pool.scheduleAtFixedRate(runnable, 0l, interval, intervalUnit);
        }
    }
}
