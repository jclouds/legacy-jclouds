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
package org.jclouds.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jclouds.http.commands.CommandFactory;
import org.jclouds.http.commands.GetAndParseSax;
import org.jclouds.http.commands.GetString;
import org.jclouds.http.commands.Head;
import org.jclouds.http.commands.callables.xml.ParseSax;
import org.jclouds.http.commands.config.HttpCommandsModule;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
@Test(threadPoolSize = 100)
public abstract class BaseHttpFutureCommandClientTest {
    protected static final String XML = "<foo><bar>whoppers</bar></foo>";
    protected Server server = null;
    protected CommandFactory factory;
    protected HttpFutureCommandClient client;
    protected Injector injector;

    @BeforeClass
    @Parameters( { "test-jetty-port" })
    public void setUpJetty(@Optional("8123") final int testPort)
	    throws Exception {
	Handler handler = new AbstractHandler() {
	    public void handle(String target, HttpServletRequest request,
		    HttpServletResponse response, int dispatch)
		    throws IOException, ServletException {
		if (request.getHeader("test") != null) {
		    response.setContentType("text/plain");
		    response.setStatus(HttpServletResponse.SC_OK);
		    response.getWriter().println("test");
		} else {
		    response.setContentType("text/xml");
		    response.setStatus(HttpServletResponse.SC_OK);
		    response.getWriter().println(XML);
		}
		((Request) request).setHandled(true);
	    }
	};

	server = new Server(testPort);
	server.setHandler(handler);
	server.start();
	final Properties properties = new Properties();
	properties.put("jclouds.http.address", "localhost");
	properties.put("jclouds.http.port", testPort + "");
	properties.put("jclouds.http.secure", "false");
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
	}, new HttpCommandsModule(), createClientModule(),
		new AbstractModule() {
		    @Override
		    protected void configure() {
			bind(new TypeLiteral<List<HttpRequestFilter>>() {
			}).toInstance(filters);
		    }
		});
	factory = injector.getInstance(CommandFactory.class);
	client = injector.getInstance(HttpFutureCommandClient.class);
	assert client != null;
    }

    protected abstract void addConnectionProperties(Properties props);

    protected abstract Module createClientModule();

    @AfterClass
    public void tearDownJetty() throws Exception {
	client.close();
	server.stop();
    }

    @Test(invocationCount = 500, timeOut = 1000)
    void testRequestFilter() throws MalformedURLException, ExecutionException,
	    InterruptedException {
	GetString get = factory.createGetString("/");
	get.getRequest().getHeaders().put("filterme", "filterme");
	client.submit(get);
	assert get.get().trim().equals("test") : String.format(
		"expected: [%1s], but got [%2s]", "test", get.get());
    }

    @Test(invocationCount = 500, timeOut = 1000)
    void testGetStringWithHeader() throws MalformedURLException,
	    ExecutionException, InterruptedException {
	GetString get = factory.createGetString("/");
	get.getRequest().getHeaders().put("test", "test");
	client.submit(get);
	assert get.get().trim().equals("test") : String.format(
		"expected: [%1s], but got [%2s]", "test", get.get());
    }

    @Test(invocationCount = 500, timeOut = 1000)
    void testGetString() throws MalformedURLException, ExecutionException,
	    InterruptedException {
	GetString get = factory.createGetString("/");
	assert get != null;
	client.submit(get);
	assert get.get().trim().equals(XML) : String.format(
		"expected: [%1s], but got [%2s]", XML, get.get());
    }

    @Test(invocationCount = 500, timeOut = 1000)
    void testHead() throws MalformedURLException, ExecutionException,
	    InterruptedException {
	Head head = factory.createHead("/");
	assert head != null;
	client.submit(head);
	assert head.get();
    }

    @Test(invocationCount = 500, timeOut = 1000)
    void testGetAndParseSax() throws MalformedURLException, ExecutionException,
	    InterruptedException {
	GetAndParseSax getAndParseSax = factory.createGetAndParseSax("/",
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
	assert getAndParseSax.get().equals("whoppers");
    }
}
