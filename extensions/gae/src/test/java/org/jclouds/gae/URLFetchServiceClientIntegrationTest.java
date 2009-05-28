/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.gae;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.gae.config.URLFetchServiceClientModule;
import org.jclouds.http.BaseHttpFutureCommandClientTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Module;
import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;

/**
 * 
 * Integration test for the URLFetchService
 * 
 * @author Adrian Cole
 */
@Test
public class URLFetchServiceClientIntegrationTest extends
	BaseHttpFutureCommandClientTest {

    @BeforeMethod
    void setupApiProxy() {
	ApiProxy.setEnvironmentForCurrentThread(new TestEnvironment());
	ApiProxy.setDelegate(new ApiProxyLocalImpl(new File(".")) {
	});
    }

    @Override
    @Test(invocationCount = 50, timeOut = 1500)
    public void testGetAndParseSax() throws MalformedURLException,
	    ExecutionException, InterruptedException, TimeoutException {
	setupApiProxy();
	super.testGetAndParseSax();
    }

    @Override
    @Test(invocationCount = 50, timeOut = 1500)
    public void testGetString() throws MalformedURLException,
	    ExecutionException, InterruptedException, TimeoutException {
	setupApiProxy();
	super.testGetString();
    }

    @Override
    @Test(enabled = false)
    public void testGetStringWithHeader() throws MalformedURLException,
	    ExecutionException, InterruptedException, TimeoutException {
	// GAE does not support sending headers in their test stub as of version
	// 1.2.0
    }

    @Override
    @Test(invocationCount = 50, timeOut = 1500)
    public void testHead() throws MalformedURLException, ExecutionException,
	    InterruptedException, TimeoutException {
	setupApiProxy();
	super.testHead();
    }

    @Test(enabled = false)
    public void testRequestFilter() throws MalformedURLException,
	    ExecutionException, InterruptedException, TimeoutException {
	// GAE does not support sending headers in their test stub as of version
	// 1.2.0
    }

    class TestEnvironment implements ApiProxy.Environment {
	public String getAppId() {
	    return "Unit Tests";
	}

	public String getVersionId() {
	    return "1.0";
	}

	public void setDefaultNamespace(String s) {
	}

	public String getRequestNamespace() {
	    return null;
	}

	public String getDefaultNamespace() {
	    return null;
	}

	public String getAuthDomain() {
	    return null;
	}

	public boolean isLoggedIn() {
	    return false;
	}

	public String getEmail() {
	    return null;
	}

	public boolean isAdmin() {
	    return false;
	}
    }

    protected Module createClientModule() {
	return new URLFetchServiceClientModule();
    }

    @Override
    protected void addConnectionProperties(Properties props) {
    }
}