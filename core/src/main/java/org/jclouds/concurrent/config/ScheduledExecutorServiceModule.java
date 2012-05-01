package org.jclouds.concurrent.config;

import static org.jclouds.concurrent.config.ExecutorServiceModule.shutdownOnClose;
import static org.jclouds.concurrent.config.ExecutorServiceModule.getStackTraceHere;

import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.concurrent.config.ExecutorServiceModule.DescribedFuture;
import org.jclouds.concurrent.config.ExecutorServiceModule.DescribingExecutorService;
import org.jclouds.lifecycle.Closer;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Provides an {@link ScheduledExecutorService} to run periodical tasks such as virtual machine monitoring, etc.
 * <p>
 * This module is not registered by default in the context because some providers do not allow to spawn threads.
 * 
 * @author Ignasi Barrera
 * 
 * @see ExecutorServiceModule
 *
 */
public class ScheduledExecutorServiceModule extends AbstractModule {

    static class DescribingScheduledExecutorService extends DescribingExecutorService implements ScheduledExecutorService {

        public DescribingScheduledExecutorService(ScheduledExecutorService delegate) {
            super(delegate);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            return new DescribedScheduledFuture(((ScheduledExecutorService) delegate)
                .schedule(command, delay, unit), command.toString(), getStackTraceHere());
        }

        @Override
        public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            return new DescribedScheduledFuture<V>(((ScheduledExecutorService) delegate)
                .schedule(callable, delay, unit), callable.toString(), getStackTraceHere());
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay,
            long period, TimeUnit unit) {
            return new DescribedScheduledFuture(((ScheduledExecutorService) delegate)
                .scheduleAtFixedRate(command, initialDelay, period, unit), command.toString(), getStackTraceHere());
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay,
            long delay, TimeUnit unit) {
            return new DescribedScheduledFuture(((ScheduledExecutorService) delegate)
                .scheduleWithFixedDelay(command, initialDelay, delay, unit), command.toString(), getStackTraceHere());
        }
    }

    static class DescribedScheduledFuture<T> extends DescribedFuture<T> implements ScheduledFuture<T> {

        public DescribedScheduledFuture(ScheduledFuture<T> delegate, String description,
            StackTraceElement[] submissionTrace) {
            super(delegate, description, submissionTrace);
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return ((ScheduledFuture<T>) delegate).getDelay(unit);
        }

        @Override
        public int compareTo(Delayed o) {
            return ((ScheduledFuture<T>) delegate).compareTo(o);
        }
    }

    static ScheduledExecutorService addToStringOnSchedule(ScheduledExecutorService executor) {
        if (executor != null) {
           return new DescribingScheduledExecutorService(executor);
        }
        return executor;
    }

    @Provides
    @Singleton
    @Named(Constants.PROPERTY_SCHEDULER_THREADS)
    ScheduledExecutorService provideScheduledExecutor(@Named(Constants.PROPERTY_SCHEDULER_THREADS) final int count,
        final Closer closer) {
        return shutdownOnClose(addToStringOnSchedule(newScheduledThreadPoolNamed("scheduler thread %d", count)), closer);
    }

    @VisibleForTesting
    static ScheduledExecutorService newScheduledThreadPoolNamed(String name, int maxCount) {
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat(name)
            .setThreadFactory(Executors.defaultThreadFactory()).build();
       return maxCount == 0 ? Executors.newSingleThreadScheduledExecutor(factory)
                            : Executors.newScheduledThreadPool(maxCount, factory);
    }

    @Override
    protected void configure() {

    }

}
