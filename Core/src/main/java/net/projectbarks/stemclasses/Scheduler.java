package net.projectbarks.stemclasses;

import lombok.Getter;
import lombok.Setter;
import net.projectbarks.stemclasses.letterday.LetterDay;

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
    private static ScheduledThreadPoolExecutor pool;
    private static ExecutorService single;
    private static Map<Runnable, RunInstance> scheduled;

    static {
        pool = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(0);
        single = Executors.newCachedThreadPool();
        scheduled = new HashMap<Runnable, RunInstance>();
        single.submit(new Runnable() {
            @Override
            public void run() {
                long previous = 0;
                while (true) {
                    if (previous > System.currentTimeMillis()) {
                        for (Map.Entry<Runnable, RunInstance> entry : scheduled.entrySet()) {
                            entry.getValue().getFuture().cancel(true);
                            pool = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(0);
                            Scheduler.run(entry.getValue(), entry.getKey());
                        }
                    }

                    previous = System.currentTimeMillis();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void run(final Runnable runnable, long delay, long interval, TimeUnit intervalUnit) {
        RunInstance instance = new RunInstance(delay, interval, intervalUnit);
        run(instance, runnable);
        scheduled.put(runnable, instance);
    }

    public static void irregularCheck(final Runnable runnable) {
        if (!scheduled.containsKey(runnable)) {
            return;
        }
        RunInstance instance = scheduled.get(runnable);
        if (instance.getPrevious() > -1 && System.currentTimeMillis() - instance.getPrevious() <= 10) {
            instance.setFails(instance.getFails() + 1);
        } else {
            instance.setFails(0);
        }
        if (instance.getFails() >= 3) {
            instance.getFuture().cancel(true);
            run(instance, runnable);
            instance.setPrevious(-1);
            return;
        }
        instance.setPrevious(System.currentTimeMillis());
    }

    private static void run(RunInstance instance, Runnable runnable) {
        ScheduledFuture<?> future;
        if (instance.getDelay() < 0 && instance.getInterval() < 0) {
            future = pool.schedule(runnable, 15, TimeUnit.MILLISECONDS);
        } else if (instance.getInterval() < 0) {
            future = pool.schedule(runnable, instance.getDelay(), TimeUnit.MILLISECONDS);
        } else {
            future = pool.scheduleAtFixedRate(runnable, 0l, instance.getInterval(), TimeUnit.MILLISECONDS);
        }
        instance.setFuture(future);
    }

    private static class RunInstance {
        @Getter
        @Setter
        private ScheduledFuture<?> future;
        @Getter
        private long delay, interval;
        @Getter
        @Setter
        private long previous;
        @Getter
        @Setter
        private long fails;

        public RunInstance(Long delay,  Long interval, TimeUnit unit) {
            unit = unit == null ? TimeUnit.MILLISECONDS : unit;
            this.previous = -1;
            this.fails = 0;
            this.delay = delay == null ? -1 : unit.toMillis(delay);
            this.interval = interval == null ? -1 : unit.toMillis(interval);
        }
    }

}
