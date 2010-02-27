/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.gogrid.config;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Supplier;
import org.jclouds.concurrent.ExpirableSupplier;
import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.gogrid.GoGridAsyncClient;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.services.GridServerAsyncClient;
import org.jclouds.gogrid.services.GridServerClient;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientFactory;

import org.jclouds.gogrid.GoGrid;
import org.jclouds.gogrid.reference.GoGridConstants;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import static org.jclouds.gogrid.reference.GoGridConstants.*;

/**
 * Configures the GoGrid connection.
 *
 * @author Adrian Cole
 * @author Oleksiy Yarmula
 */
@RequiresHttp
@ConfiguresRestClient
public class GoGridRestClientModule extends AbstractModule {

    @Override
    protected void configure() {
        bindErrorHandlers();
        bindRetryHandlers();
    }

    @Provides
    @Singleton
    protected GridServerAsyncClient provideServerClient(RestClientFactory factory) {
        return factory.create(GridServerAsyncClient.class);
    }

    @Provides
    @Singleton
    public GridServerClient provideServerClient(GridServerAsyncClient client) throws IllegalArgumentException,
            SecurityException, NoSuchMethodException {
        return SyncProxy.create(GridServerClient.class, client);
    }

    @Provides
    @Singleton
    @GoGrid
    protected URI provideURI(@Named(GoGridConstants.PROPERTY_GOGRID_ENDPOINT) String endpoint) {
        return URI.create(endpoint);
    }

    @Provides
    @TimeStamp
    protected Long provideTimeStamp(@TimeStamp Supplier<Long> cache) {
        return cache.get();
    }

    /**
     * borrowing concurrency code to ensure that caching takes place properly
     */
    @Provides
    @TimeStamp
    Supplier<Long> provideTimeStampCache(
            @Named(PROPERTY_GOGRID_SESSIONINTERVAL) long seconds) {
        return new ExpirableSupplier<Long>(new Supplier<Long>() {
            public Long get() {
                return System.currentTimeMillis() / 1000;
            }
        }, seconds, TimeUnit.SECONDS);
    }

    protected void bindErrorHandlers() {
        // TODO
    }

    protected void bindRetryHandlers() {
        // TODO
    }

}