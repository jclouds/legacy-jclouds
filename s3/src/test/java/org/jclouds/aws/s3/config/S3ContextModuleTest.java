/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
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
package org.jclouds.aws.s3.config;

import static org.testng.Assert.assertEquals;

import org.jclouds.aws.s3.S3Constants;
import org.jclouds.aws.s3.filters.ParseS3ErrorFromXmlContent;
import org.jclouds.http.HttpResponseHandler;
import org.jclouds.http.annotation.ClientErrorHandler;
import org.jclouds.http.annotation.RedirectHandler;
import org.jclouds.http.annotation.ServerErrorHandler;
import org.jclouds.http.config.JavaUrlHttpFutureCommandClientModule;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class S3ContextModuleTest {

    Injector injector = null;

    @BeforeMethod
    void setUpInjector() {
	injector = Guice.createInjector(new S3ContextModule() {
	    @Override
	    protected void configure() {
		bindConstant().annotatedWith(
			Names.named(S3Constants.PROPERTY_AWS_ACCESSKEYID)).to(
			"localhost");
		bindConstant().annotatedWith(
			Names.named(S3Constants.PROPERTY_AWS_SECRETACCESSKEY))
			.to("localhost");
		bindConstant().annotatedWith(
			Names.named(S3Constants.PROPERTY_HTTP_ADDRESS)).to(
			"localhost");
		bindConstant().annotatedWith(
			Names.named(S3Constants.PROPERTY_HTTP_PORT)).to("1000");
		bindConstant().annotatedWith(
			Names.named(S3Constants.PROPERTY_HTTP_SECURE)).to(
			"false");
		super.configure();
	    }
	}, new JavaUrlHttpFutureCommandClientModule());
    }

    @AfterMethod
    void tearDownInjector() {
	injector = null;
    }

    private static class ClientErrorHandlerTest {
	@Inject
	@ClientErrorHandler
	HttpResponseHandler errorHandler;
    }

    @Test
    void testClientErrorHandler() {
	ClientErrorHandlerTest error = injector
		.getInstance(ClientErrorHandlerTest.class);
	assertEquals(error.errorHandler.getClass(),
		ParseS3ErrorFromXmlContent.class);
    }

    private static class ServerErrorHandlerTest {
	@Inject
	@ServerErrorHandler
	HttpResponseHandler errorHandler;
    }

    @Test
    void testServerErrorHandler() {
	ServerErrorHandlerTest error = injector
		.getInstance(ServerErrorHandlerTest.class);
	assertEquals(error.errorHandler.getClass(),
		ParseS3ErrorFromXmlContent.class);
    }

    private static class RedirectHandlerTest {
	@Inject
	@RedirectHandler
	HttpResponseHandler errorHandler;
    }

    @Test
    void testRedirectHandler() {
	RedirectHandlerTest error = injector
		.getInstance(RedirectHandlerTest.class);
	assertEquals(error.errorHandler.getClass(),
		ParseS3ErrorFromXmlContent.class);
    }

}