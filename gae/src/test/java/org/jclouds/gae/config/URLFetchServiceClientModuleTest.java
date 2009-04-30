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
package org.jclouds.gae.config;

import java.util.Properties;

import org.jclouds.gae.URLFetchServiceClient;
import org.jclouds.gae.config.URLFetchServiceClientModule;
import org.jclouds.http.HttpConstants;
import org.jclouds.http.HttpFutureCommandClient;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
@Test
public class URLFetchServiceClientModuleTest {

    public void testConfigureBindsClient() {
	final Properties properties = new Properties();
	properties.put(HttpConstants.PROPERTY_HTTP_ADDRESS, "localhost");
	properties.put(HttpConstants.PROPERTY_HTTP_PORT, "8088");
	properties.put(HttpConstants.PROPERTY_HTTP_SECURE, "false");

	Injector i = Guice.createInjector(new URLFetchServiceClientModule() {
	    @Override
	    protected void configure() {
		Names.bindProperties(binder(), properties);
		super.configure();
	    }
	});
	HttpFutureCommandClient client = i
		.getInstance(HttpFutureCommandClient.class);
	assert client instanceof URLFetchServiceClient;
    }
}
