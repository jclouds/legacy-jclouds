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
package org.jclouds.http.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.jclouds.http.HttpFutureCommandClient;
import org.jclouds.http.JavaUrlHttpFutureCommandClient;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
public class JavaUrlHttpFutureCommandClientModule extends AbstractModule {

    @Override
    protected void configure() {
        //note this is not threadsafe, so it cannot be singleton
        bind(HttpFutureCommandClient.class).to(JavaUrlHttpFutureCommandClient.class);
    }

    @Singleton
    @Provides
    protected URL provideAddress(@Named("jclouds.http.address") String address, @Named("jclouds.http.port") int port, @Named("jclouds.http.secure") boolean isSecure) throws MalformedURLException {

        return new URL(isSecure ? "https" : "http", address, port, "/");
    }


}