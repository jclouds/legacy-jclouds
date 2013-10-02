/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.events.config;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.Constants;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.events.config.annotations.AsyncBus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * Unit tests for the {@link EventBusModule} class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "EventBusModuleTest")
public class EventBusModuleTest {
    private Injector injector;
    
    @BeforeClass
    public void setup() {
        ExecutorServiceModule userExecutorModule = new ExecutorServiceModule() {
            @Override
            protected void configure() {
               bindConstant().annotatedWith(Names.named(Constants.PROPERTY_IO_WORKER_THREADS)).to(1);
               bindConstant().annotatedWith(Names.named(Constants.PROPERTY_USER_THREADS)).to(1);
               super.configure();
            }
         };
         EventBusModule eventBusModule = new EventBusModule();
         injector = Guice.createInjector(userExecutorModule, eventBusModule);
    }
    
    public void testAsyncExecutorIsProvided() {
        assertNotNull(injector.getInstance(AsyncEventBus.class));
    }

    public void testAsyncAnnotatedEventBusIsBound() {
        Key<EventBus> eventBusKey = Key.get(EventBus.class, AsyncBus.class);
        EventBus eventBus = injector.getInstance(eventBusKey);

        assertNotNull(eventBus);
        assertTrue(eventBus instanceof AsyncEventBus);
    }

    public void testEventBusIsSingleton() {
        EventBus eventBus1 = injector.getInstance(EventBus.class);
        EventBus eventBus2 = injector.getInstance(EventBus.class);

        assertTrue(eventBus1 == eventBus2);
    }
}
