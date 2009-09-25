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

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.cloud.CloudContextBuilder;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.rest.JaxrsAnnotationProcessorTest.Localhost;
import org.jclouds.rest.config.JaxrsModule;
import org.jclouds.util.Jsr330;
import org.jclouds.util.Utils;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public abstract class BaseJettyTest {
   protected static final String XML = "<foo><bar>whoppers</bar></foo>";
   protected static final String XML2 = "<foo><bar>chubbs</bar></foo>";

   protected Server server = null;
   protected IntegrationTestClient client;
   protected Injector injector;
   private Closer closer;
   private AtomicInteger cycle = new AtomicInteger(0);
   private Server server2;

   @BeforeTest
   @Parameters( { "test-jetty-port" })
   public void setUpJetty(@Optional("8123") final int testPort) throws Exception {
      Handler server1Handler = new AbstractHandler() {
         public void handle(String target, HttpServletRequest request,
                  HttpServletResponse response, int dispatch) throws IOException, ServletException {
            if (failIfNoContentLength(request, response)) {
               return;
            } else if (target.indexOf("redirect") > 0) {
               response.sendRedirect("http://localhost:" + (testPort + 1));
            } else if (request.getMethod().equals("PUT")) {
               if (request.getContentLength() > 0) {
                  response.setStatus(HttpServletResponse.SC_OK);
                  response.getWriter().println(
                           Utils.toStringAndClose(request.getInputStream()) + "PUT");
               } else {
                  response.sendError(500, "no content");
               }
            } else if (request.getMethod().equals("POST")) {
               if (request.getContentLength() > 0) {
                  response.setStatus(HttpServletResponse.SC_OK);
                  response.getWriter().println(
                           Utils.toStringAndClose(request.getInputStream()) + "POST");
               } else {
                  response.sendError(500, "no content");
               }
            } else if (request.getHeader("Range") != null) {
               response.sendError(404, "no content");
            } else if (request.getHeader("test") != null) {
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
      server.setHandler(server1Handler);
      server.start();

      Handler server2Handler = new AbstractHandler() {
         public void handle(String target, HttpServletRequest request,
                  HttpServletResponse response, int dispatch) throws IOException, ServletException {
            if (request.getMethod().equals("PUT")) {
               if (request.getContentLength() > 0) {
                  response.setStatus(HttpServletResponse.SC_OK);
                  response.getWriter().println(
                           Utils.toStringAndClose(request.getInputStream()) + "PUTREDIRECT");
               } else {
                  response.sendError(500, "no content");
               }
            } else {
               response.setContentType("text/xml");
               response.setStatus(HttpServletResponse.SC_OK);
               response.getWriter().println(XML2);
            }
            ((Request) request).setHandled(true);
         }
      };

      server2 = new Server(testPort + 1);
      server2.setHandler(server2Handler);
      server2.start();

      final Properties properties = new Properties();
      addConnectionProperties(properties);

      List<Module> modules = Lists.newArrayList(new AbstractModule() {
         @Override
         protected void configure() {
            Jsr330.bindProperties(binder(), properties);
            bind(URI.class).annotatedWith(Localhost.class).toInstance(
                     URI.create("http://localhost:" + testPort));
         }
      }, new JDKLoggingModule(), new JaxrsModule(), createClientModule());
      CloudContextBuilder.addExecutorServiceIfNotPresent(modules);
      injector = Guice.createInjector(modules);
      RestClientFactory factory = injector.getInstance(RestClientFactory.class);
      client = factory.create(IntegrationTestClient.class);
      closer = injector.getInstance(Closer.class);
      assert client != null;
   }

   @AfterTest
   public void tearDownJetty() throws Exception {
      closer.close();
      server2.stop();
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
   protected boolean failOnRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
      if (cycle.incrementAndGet() % 10 == 0) {
         response.sendError(500);
         ((Request) request).setHandled(true);
         return true;
      }
      return false;
   }

   protected boolean failIfNoContentLength(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
      if (request.getHeader(HttpHeaders.CONTENT_LENGTH) == null) {
         response.sendError(500);
         ((Request) request).setHandled(true);
         return true;
      }
      return false;
   }

}
