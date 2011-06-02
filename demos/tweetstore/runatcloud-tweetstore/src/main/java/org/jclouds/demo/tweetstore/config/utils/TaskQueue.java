package org.jclouds.demo.tweetstore.config.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.google.inject.Provider;

public class TaskQueue {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements Provider<TaskQueue> {
        protected String name = "default";
        protected long taskPeriodMillis = TimeUnit.SECONDS.toMillis(1);

        public Builder name(String name) {
            this.name = checkNotNull(name, "name");
            return this;
        }

        public Builder period(TimeUnit period) {
            this.taskPeriodMillis = checkNotNull(period, "period").toMillis(1);
            return this;
        }

        public Builder period(long taskPeriodMillis) {
            checkArgument(taskPeriodMillis > 0, "taskPeriodMillis");
            this.taskPeriodMillis = taskPeriodMillis;
            return this;
        }
        
        public TaskQueue build() {
            return new TaskQueue(name, taskPeriodMillis);
        }

        @Override
        public TaskQueue get() {
            return build();
        }
    }

    private final Timer timer;
    private final long taskPeriodMillis;

    private TaskQueue(String name, long taskPeriodMillis) {
        timer = new Timer(name);
        this.taskPeriodMillis = taskPeriodMillis;
    }

    public void add(final Runnable task) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        }, 0, taskPeriodMillis);
    }
    
    public void destroy() {
        timer.cancel();
    }
}