/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.lifecycle.config;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.jclouds.lifecycle.Closer;
import org.testng.annotations.Test;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
@Test
public class LifeCycleModuleTest {

    @Test
    void testBindsExecutor() {
        Injector i = Guice.createInjector(new LifeCycleModule());
        assert i.getInstance(ExecutorService.class) != null;
    }

    @Test
    void testBindsCloser() {
        Injector i = Guice.createInjector(new LifeCycleModule());
        assert i.getInstance(Closer.class) != null;
    }

    @Test
    void testCloserClosesExecutor() throws IOException {
        Injector i = Guice.createInjector(new LifeCycleModule());
        ExecutorService executor = i.getInstance(ExecutorService.class);
        assert !executor.isShutdown();
        Closer closer = i.getInstance(Closer.class);
        closer.close();
        assert executor.isShutdown();
    }

    static class PreDestroyable {
        boolean isClosed = false;

        @Inject
        PreDestroyable(ExecutorService executor) {
            this.executor = executor;
        }

        ExecutorService executor;

        @PreDestroy
        public void close() {
            assert !executor.isShutdown();
            isClosed = true;
        }
    }

    @Test
    void testCloserPreDestroyOrder() throws IOException {
        Injector i = Guice.createInjector(new LifeCycleModule(), new AbstractModule() {
            protected void configure() {
                bind(PreDestroyable.class);
            }
        });
        ExecutorService executor = i.getInstance(ExecutorService.class);
        assert !executor.isShutdown();
        PreDestroyable preDestroyable = i.getInstance(PreDestroyable.class);
        assert !preDestroyable.isClosed;
        Closer closer = i.getInstance(Closer.class);
        closer.close();
        assert preDestroyable.isClosed;
        assert executor.isShutdown();
    }

    static class PostConstructable {
        boolean isStarted;

        @PostConstruct
        void start() {
            isStarted = true;
        }
    }

    @Test
    void testPostConstruct() {
        Injector i = Guice.createInjector(new LifeCycleModule(), new AbstractModule() {
            protected void configure() {
                bind(PostConstructable.class);
            }
        });
        PostConstructable postConstructable = i.getInstance(PostConstructable.class);
        assert postConstructable.isStarted;

    }

}
