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
package org.jclouds.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jclouds.http.commands.CommandFactory;
import org.jclouds.http.commands.GetAndParseSax;
import org.jclouds.http.commands.GetString;
import org.jclouds.http.commands.Head;
import org.jclouds.http.commands.callables.xml.ParseSax;
import org.jclouds.http.commands.config.HttpCommandsModule;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
@Test(threadPoolSize = 10)
public abstract class BaseHttpFutureCommandClientTest {
    protected static final String XML = "<foo><bar>whoppers</bar></foo>";
    protected Server server = null;
    protected CommandFactory factory;
    protected HttpFutureCommandClient client;
    protected Injector injector;
    private Closer closer;

    @BeforeTest
    @Parameters( { "test-jetty-port" })
    public void setUpJetty(@Optional("8123") final int testPort)
	    throws Exception {
	Handler handler = new AbstractHandler() {
	    private AtomicInteger cycle = new AtomicInteger(0);

	    public void handle(String target, HttpServletRequest request,
		    HttpServletResponse response, int dispatch)
		    throws IOException, ServletException {
		if (request.getHeader("test") != null) {
		    response.setContentType("text/plain");
		    response.setStatus(HttpServletResponse.SC_OK);
		    response.getWriter().println("test");
		} else {
		    if (failEveryTenRequests(request, response))
			return;
		    response.setContentType("text/xml");
		    response.setStatus(HttpServletResponse.SC_OK);
		    response.getWriter().println(XML);
		}
		((Request) request).setHandled(true);
	    }

	    private boolean failEveryTenRequests(HttpServletRequest request,
		    HttpServletResponse response) throws IOException {
		if (cycle.incrementAndGet() % 10 == 0) {
		    response.sendError(500);
		    ((Request) request).setHandled(true);
		    return true;
		}
		return false;
	    }
	};

	server = new Server(testPort);
	server.setHandler(handler);
	server.start();
	final Properties properties = new Properties();
	properties.put(HttpConstants.PROPERTY_HTTP_ADDRESS, "localhost");
	properties.put(HttpConstants.PROPERTY_HTTP_PORT, testPort + "");
	properties.put(HttpConstants.PROPERTY_HTTP_SECURE, "false");
	addConnectionProperties(properties);
	final List<HttpRequestFilter> filters = new ArrayList<HttpRequestFilter>(
		1);
	filters.add(new HttpRequestFilter() {
	    public void filter(HttpRequest request) throws HttpException {
		if (request.getHeaders().containsKey("filterme")) {
		    request.getHeaders().put("test", "test");
		}
	    }
	});
	injector = Guice.createInjector(new AbstractModule() {
	    @Override
	    protected void configure() {
		Names.bindProperties(binder(), properties);
	    }
	}, new JDKLoggingModule(), new HttpCommandsModule(),
		createClientModule(), new AbstractModule() {
		    @Override
		    protected void configure() {
			bind(new TypeLiteral<List<HttpRequestFilter>>() {
			}).toInstance(filters);
		    }
		});
	factory = injector.getInstance(Key.get(CommandFactory.class));
	client = injector.getInstance(HttpFutureCommandClient.class);
	closer = injector.getInstance(Closer.class);
	assert client != null;
    }

    protected abstract void addConnectionProperties(Properties props);

    protected abstract Module createClientModule();

    @AfterTest
    public void tearDownJetty() throws Exception {
	closer.close();
	server.stop();
    }

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
