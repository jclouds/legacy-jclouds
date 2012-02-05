/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.demo.paas.service.taskqueue;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.jclouds.demo.paas.RunnableHttpRequest;
import org.jclouds.demo.paas.RunnableHttpRequest.Factory;
import org.jclouds.http.HttpCommandExecutorService;

import com.google.inject.Provider;

public class TaskQueue {
    protected final Factory httpRequestFactory;
    private final Timer timer;
    private final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<Runnable>();

    private TaskQueue(String name, long pollingIntervalMillis, Factory httpRequestFactory) {
        this.httpRequestFactory = httpRequestFactory;
        timer = new Timer(name);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Runnable task = tasks.poll();
                if (task != null) {
                    task.run();
                }
            }
        }, 0, pollingIntervalMillis);
    }

    public void add(final Runnable task) {
        tasks.add(task);
    }

    public Factory getHttpRequestFactory() {
        return httpRequestFactory;
    }

    public void destroy() {
        timer.cancel();
        tasks.clear();
    }

    public static Builder builder(HttpCommandExecutorService httpClient) {
        return new Builder(httpClient);
    }

    public static class Builder implements Provider<TaskQueue> {
        protected final HttpCommandExecutorService httpClient;
        protected String name = "default";
        protected long pollingIntervalMillis = TimeUnit.SECONDS.toMillis(1);

        private Builder(HttpCommandExecutorService httpClient) {
            this.httpClient = checkNotNull(httpClient, "httpClient");
        }

        public Builder name(String name) {
            this.name = checkNotNull(name, "name");
            return this;
        }

        public Builder period(TimeUnit period) {
            this.pollingIntervalMillis = checkNotNull(period, "period").toMillis(1);
            return this;
        }

        public Builder period(long pollingIntervalMillis) {
            checkArgument(pollingIntervalMillis > 0, "pollingIntervalMillis");
            this.pollingIntervalMillis = pollingIntervalMillis;
            return this;
        }

        public TaskQueue build() {
            return new TaskQueue(name, pollingIntervalMillis,
                    RunnableHttpRequest.factory(httpClient, format("taskqueue-%s", name)));
        }

        @Override
        public TaskQueue get() {
            return build();
        }
    }
}