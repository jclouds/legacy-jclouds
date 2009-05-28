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
package org.jclouds.http;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.http.commands.GetAndParseSax;
import org.jclouds.http.commands.GetString;
import org.jclouds.http.commands.Head;
import org.jclouds.http.commands.callables.xml.ParseSax;
import org.testng.annotations.Test;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
@Test(threadPoolSize = 10)
public abstract class BaseHttpFutureCommandClientTest extends BaseJettyTest {

    @Test(invocationCount = 50, timeOut = 3000)
    public void testRequestFilter() throws MalformedURLException,
	    ExecutionException, InterruptedException, TimeoutException {
	GetString get = factory.createGetString("/");
	get.getRequest().getHeaders().put("filterme", "filterme");
	client.submit(get);
	assert get.get(10, TimeUnit.SECONDS).trim().equals("test") : String
		.format("expected: [%1$s], but got [%2$s]", "test", get.get(10,
			TimeUnit.SECONDS));
    }

    @Test(invocationCount = 50, timeOut = 3000)
    public void testGetStringWithHeader() throws MalformedURLException,
	    ExecutionException, InterruptedException, TimeoutException {
	GetString get = factory.createGetString("/");
	get.getRequest().getHeaders().put("test", "test");
	client.submit(get);
	assert get.get(10, TimeUnit.SECONDS).trim().equals("test") : String
		.format("expected: [%1$s], but got [%2$s]", "test", get.get(10,
			TimeUnit.SECONDS));
    }

    @Test(invocationCount = 50, timeOut = 3000)
    public void testGetString() throws MalformedURLException,
	    ExecutionException, InterruptedException, TimeoutException {
	GetString get = factory.createGetString("/");
	assert get != null;
	client.submit(get);
	assert get.get(10, TimeUnit.SECONDS).trim().equals(XML) : String
		.format("expected: [%1$s], but got [%2$s]", XML, get.get(10,
			TimeUnit.SECONDS));
    }

    @Test(invocationCount = 50, timeOut = 3000)
    public void testHead() throws MalformedURLException, ExecutionException,
	    InterruptedException, TimeoutException {
	Head head = factory.createHead("/");
	assert head != null;
	client.submit(head);
	assert head.get(10, TimeUnit.SECONDS);
    }

    @Test(invocationCount = 50, timeOut = 3000)
    public void testGetAndParseSax() throws MalformedURLException,
	    ExecutionException, InterruptedException, TimeoutException {
	GetAndParseSax<?> getAndParseSax = factory.createGetAndParseSax("/",
		new ParseSax.HandlerWithResult<String>() {
		    @Override
		    public String getResult() {
			return bar;
		    }

		    private String bar = null;
		    private StringBuilder currentText = new StringBuilder();

		    @Override
		    public void endElement(String uri, String name, String qName) {

			if (qName.equals("bar")) {
			    bar = currentText.toString();
			}
			currentText = new StringBuilder();
		    }

		    @Override
		    public void characters(char ch[], int start, int length) {
			currentText.append(ch, start, length);
		    }
		});
	assert getAndParseSax != null;
	client.submit(getAndParseSax);
	assert getAndParseSax.get(10, TimeUnit.SECONDS).equals("whoppers");
    }
}
