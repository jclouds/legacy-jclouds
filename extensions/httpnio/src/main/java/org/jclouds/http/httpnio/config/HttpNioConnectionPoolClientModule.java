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
package org.jclouds.http.httpnio.config;

import java.net.InetSocketAddress;

import org.apache.http.nio.NHttpConnection;
import org.jclouds.command.pool.FutureCommandConnectionRetry;
import org.jclouds.http.HttpConstants;
import org.jclouds.http.HttpFutureCommandClient;
import org.jclouds.http.httpnio.config.internal.NonSSLHttpNioConnectionPoolClientModule;
import org.jclouds.http.httpnio.config.internal.SSLHttpNioConnectionPoolClientModule;
import org.jclouds.http.httpnio.pool.HttpNioConnectionPoolClient;
import org.jclouds.http.httpnio.pool.HttpNioFutureCommandConnectionRetry;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;

/**
 * Configures {@link HttpNioConnectionPoolClient}
 * 
 * @author Adrian Cole
 */
public class HttpNioConnectionPoolClientModule extends AbstractModule {

    @Named(HttpConstants.PROPERTY_HTTP_SECURE)
    boolean isSecure;

    @Override
    protected void configure() {
	requestInjection(this);
	if (isSecure)
	    install(new SSLHttpNioConnectionPoolClientModule());
	else
	    install(new NonSSLHttpNioConnectionPoolClientModule());
	bind(new TypeLiteral<FutureCommandConnectionRetry<NHttpConnection>>() {
	}).to(HttpNioFutureCommandConnectionRetry.class);
	bind(HttpFutureCommandClient.class).to(
		HttpNioConnectionPoolClient.class);
    }

    @Singleton
    @Provides
    protected InetSocketAddress provideAddress(
	    @Named(HttpConstants.PROPERTY_HTTP_ADDRESS) String address,
	    @Named(HttpConstants.PROPERTY_HTTP_PORT) int port) {
	return new InetSocketAddress(address, port);
    }

}
