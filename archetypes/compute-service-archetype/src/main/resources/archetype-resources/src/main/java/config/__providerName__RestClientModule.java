#set( $ucaseProviderName = ${providerName.toUpperCase()} )
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
package ${package}.config;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.GridO;
import org.jclouds.concurrent.ExpirableSupplier;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.RequiresHttp;
import org.jclouds.logging.Logger;
import org.jclouds.reference.GridOConstants;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.base.Supplier;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import static ${package}.reference.${providerName}Constants.*;

/**
 * Configures the ${providerName} connection.
 *
 * @author ${author}
 */
@RequiresHttp
@ConfiguresRestClient
public class ${providerName}RestClientModule extends AbstractModule {
    /*
     * TODO: modify configuration for ${providerName}Client
     */

    @Resource
    protected Logger logger = Logger.NULL;

    @Override
    protected void configure() {
        requestInjection(this);
    }

    @Provides
    @Singleton
    @${providerName}
    protected URI provideURI(@Named(${providerName}Constants.PROPERTY_${ucaseProviderName}_ENDPOINT) String endpoint) {
        return URI.create(endpoint);
    }

    // borrowing concurrency code to ensure that caching takes place properly
    @Provides
    @TimeStamp
    Supplier<Long> provideTimeStampCache(
            @Named(PROPERTY_${ucaseProviderName}_SESSIONINTERVAL) long seconds) {
        return new ExpirableSupplier<Long>(new Supplier<Long>() {
            public Long get() {
                return System.currentTimeMillis() / 1000;
            }
        }, seconds, TimeUnit.SECONDS);
    }

}