/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.events.config;

import java.util.concurrent.ExecutorService;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.events.config.annotations.AsyncBus;
import org.jclouds.events.handlers.DeadEventLoggingHandler;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Configures the {@link EventBus} to be used in the platform.
 * <p>
 * This class will provide an {@link AsyncEventBus} to be used to provide a basic pub/sub system for
 * asynchronous operations.
 * 
 * @author Ignasi Barrera
 * 
 * @see ExecutorServiceModule
 * @see AsyncEventBus
 * @see EventBus
 * @see AsyncBus
 */
@ConfiguresEventBus
public class EventBusModule extends AbstractModule {
    /**
     * Provides an {@link AsyncEventBus} that will use the configured executor service to dispatch
     * events to subscribers.
     */
    @Provides
    @Singleton
    AsyncEventBus provideAsyncEventBus(
        @Named(Constants.PROPERTY_USER_THREADS) final ExecutorService executor,
        final DeadEventLoggingHandler deadEventsHandler) {
        AsyncEventBus asyncBus = new AsyncEventBus("jclouds-async-event-bus", executor);
        asyncBus.register(deadEventsHandler);
        return asyncBus;
    }

    /**
     * Provides asynchronous {@link EventBus}.
     */
    @Provides
    @Singleton
    EventBus provideSyncEventBus(final DeadEventLoggingHandler deadEventsHandler) {
        EventBus syncBus = new EventBus("jclouds-sync-event-bus");
        syncBus.register(deadEventsHandler);
        return syncBus;
    }

    /**
     * Configures the {@link EventBus} to be singleton and enables the {@link AsyncBus} annotation.
     */
    @Override
    protected void configure() {
        bind(EventBus.class).annotatedWith(AsyncBus.class).to(AsyncEventBus.class).in(Scopes.SINGLETON);
    }

}
