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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jclouds.http.commands.CommandFactory;
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

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

public abstract class BaseJettyTest {
	protected static final String XML = "<foo><bar>whoppers</bar></foo>";
	protected Server server = null;
	protected CommandFactory factory;
	protected HttpFutureCommandClient client;
	protected Injector injector;
	private Closer closer;
	private AtomicInteger cycle = new AtomicInteger(0);

	@BeforeTest
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
					if (failOnRequest(request, response))
						return;
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
	
	@AfterTest
	public void tearDownJetty() throws Exception {
		closer.close();
		server.stop();
	}
	

	protected abstract void addConnectionProperties(Properties props);

	protected abstract Module createClientModule();
	
	/**
	 * Fails every 10 requests.
	 *  
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	protected boolean failOnRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (cycle.incrementAndGet() % 10 == 0) {
			response.sendError(500);
			((Request) request).setHandled(true);
			return true;
		}
		return false;
	}
	
}
