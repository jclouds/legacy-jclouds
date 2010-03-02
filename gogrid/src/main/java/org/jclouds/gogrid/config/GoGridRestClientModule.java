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
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import org.jclouds.Constants;
import org.jclouds.concurrent.ExpirableSupplier;
import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.date.TimeStamp;
import org.jclouds.gogrid.domain.*;
import org.jclouds.gogrid.functions.internal.CustomDeserializers;
import org.jclouds.gogrid.handlers.GoGridErrorHandler;
import org.jclouds.gogrid.services.*;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
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
    protected GridJobAsyncClient provideJobClient(RestClientFactory factory) {
        return factory.create(GridJobAsyncClient.class);
    }

    @Provides
    @Singleton
    public GridJobClient provideJobClient(GridJobAsyncClient client) throws IllegalArgumentException,
            SecurityException, NoSuchMethodException {
        return SyncProxy.create(GridJobClient.class, client);
    }

    @Provides
    @Singleton
    protected GridIpAsyncClient provideIpClient(RestClientFactory factory) {
        return factory.create(GridIpAsyncClient.class);
    }

    @Provides
    @Singleton
    public GridIpClient provideIpClient(GridIpAsyncClient client) throws IllegalArgumentException,
            SecurityException, NoSuchMethodException {
        return SyncProxy.create(GridIpClient.class, client);
    }

    @Provides
    @Singleton
    protected GridLoadBalancerAsyncClient provideLoadBalancerClient(RestClientFactory factory) {
        return factory.create(GridLoadBalancerAsyncClient.class);
    }

    @Provides
    @Singleton
    public GridLoadBalancerClient provideLoadBalancerClient(GridLoadBalancerAsyncClient client) throws IllegalArgumentException,
            SecurityException, NoSuchMethodException {
        return SyncProxy.create(GridLoadBalancerClient.class, client);
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

    @Provides
    @Singleton
    @com.google.inject.name.Named(Constants.PROPERTY_GSON_ADAPTERS)
    public Map<Class, Object> provideCustomAdapterBindings() {
        Map<Class, Object> bindings = Maps.newHashMap();
        bindings.put(ObjectType.class, new CustomDeserializers.ObjectTypeAdapter());
        bindings.put(LoadBalancerOs.class, new CustomDeserializers.LoadBalancerOsAdapter());
        bindings.put(LoadBalancerState.class, new CustomDeserializers.LoadBalancerStateAdapter());
        bindings.put(LoadBalancerPersistenceType.class, new CustomDeserializers.LoadBalancerPersistenceTypeAdapter());
        bindings.put(LoadBalancerType.class, new CustomDeserializers.LoadBalancerTypeAdapter());
        bindings.put(IpState.class, new CustomDeserializers.IpStateAdapter());
        bindings.put(JobState.class, new CustomDeserializers.JobStateAdapter());
        return bindings;
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
        bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(
               GoGridErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(
               GoGridErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(
               GoGridErrorHandler.class);
    }

    protected void bindRetryHandlers() {
        // TODO
    }

}