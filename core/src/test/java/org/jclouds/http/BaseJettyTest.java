/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.http;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.PropertiesBuilder;
import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextBuilder;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.rest.internal.RestAnnotationProcessorTest.Localhost;
import org.jclouds.util.Utils;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

public abstract class BaseJettyTest {

   public static final class IntegrationContextBuilder extends
            RestContextBuilder<IntegrationTestAsyncClient, IntegrationTestClient> {
      private final int testPort;

      public IntegrationContextBuilder(Properties properties, int testPort) {
         super(new TypeLiteral<IntegrationTestAsyncClient>() {
         }, new TypeLiteral<IntegrationTestClient>() {
         }, properties);
         this.testPort = testPort;
      }

      @Override
      protected void addContextModule(List<Module> modules) {
         modules.add(new JettyContextModule(testPort));
      }

      @Override
      protected void addClientModule(List<Module> modules) {
         modules.add(new RestIntegrationTestClientModule());
      }
   }

   @ConfiguresRestClient
   @RequiresHttp
   public static class RestIntegrationTestClientModule extends AbstractModule {
      @Override
      protected void configure() {

      }

      @Provides
      @Singleton
      public IntegrationTestAsyncClient provideAsyncClient(RestClientFactory factory) {
         return factory.create(IntegrationTestAsyncClient.class);
      }

      @Provides
      @Singleton
      public IntegrationTestClient provideClient(IntegrationTestAsyncClient client)
               throws IllegalArgumentException, SecurityException, NoSuchMethodException {
         return SyncProxy.create(IntegrationTestClient.class, client);
      }
   }

   public static class JettyContextModule extends AbstractModule {
      private final int testPort;

      private JettyContextModule(int testPort) {
         this.testPort = testPort;
      }

      @Override
      protected void configure() {
         bind(URI.class).annotatedWith(Localhost.class).toInstance(
                  URI.create("http://localhost:" + testPort));
      }

      @SuppressWarnings( { "unchecked" })
      @Provides
      @Singleton
      RestContext<IntegrationTestAsyncClient, IntegrationTestClient> provideContext(Closer closer,
               IntegrationTestAsyncClient aclient, IntegrationTestClient client,
               @Localhost URI endPoint) {
         return new RestContextImpl(closer, aclient, client, endPoint, System
                  .getProperty("user.name"));
      }
   }

   protected static final String XML = "<foo><bar>whoppers</bar></foo>";
   protected static final String XML2 = "<foo><bar>chubbs</bar></foo>";

   protected Server server = null;
   protected IntegrationTestClient client;
   protected Injector injector;
   private AtomicInteger cycle = new AtomicInteger(0);
   private Server server2;
   protected RestContext<IntegrationTestAsyncClient, IntegrationTestClient> context;
   private int testPort;
   static final Pattern actionPattern = Pattern.compile("/objects/(.*)/action/([a-z]*);?(.*)");

   @BeforeTest
   @Parameters( { "test-jetty-port" })
   public void setUpJetty(@Optional("8123") final int testPort) throws Exception {
      this.testPort = testPort;
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
               if (redirectEveryTwentyRequests(request, response))
                  return;
               if (failEveryTenRequests(request, response))
                  return;
               if (request.getContentLength() > 0) {
                  response.setStatus(HttpServletResponse.SC_OK);
                  response.getWriter().println(
                           Utils.toStringAndClose(request.getInputStream()) + "POST");
               } else {
                  handleAction(request, response);
               }
            } else if (request.getHeader("range") != null) {
               response.sendError(404, "no content");
            } else if (request.getHeader("test") != null) {
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
               }
            } else if (request.getMethod().equals("POST")) {
               if (request.getContentLength() > 0) {
                  response.setStatus(HttpServletResponse.SC_OK);
                  response.getWriter().println(
                           Utils.toStringAndClose(request.getInputStream()) + "POST");
               } else {
                  handleAction(request, response);
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

      final Properties properties = new PropertiesBuilder() {

         @Override
         public PropertiesBuilder withCredentials(String account, String key) {
            return null;
         }

         @Override
         public PropertiesBuilder withEndpoint(URI endpoint) {
            return null;
         }

      }.build();
      addConnectionProperties(properties);
      context = newBuilder(testPort, properties, createConnectionModule()).buildContext();
      client = context.getApi();
      assert client != null;

      assert client.newStringBuffer() != null;
   }

   public static RestContextBuilder<IntegrationTestAsyncClient, IntegrationTestClient> newBuilder(
            final int testPort, final Properties properties, Module connectionModule) {
      return new IntegrationContextBuilder(properties, testPort).withModules(connectionModule);
   }

   @AfterTest
   public void tearDownJetty() throws Exception {
      context.close();
      server2.stop();
      server.stop();
   }

   protected abstract void addConnectionProperties(Properties props);

   protected abstract Module createConnectionModule();

   /**
    * Fails every 10 requests.
    * 
    * @param request
    * @param response
    * @return
    * @throws IOException
    */
   protected boolean failEveryTenRequests(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
      if (cycle.incrementAndGet() % 10 == 0) {
         response.sendError(500);
         ((Request) request).setHandled(true);
         return true;
      }
      return false;
   }

   protected boolean redirectEveryTwentyRequests(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
      if (cycle.incrementAndGet() % 20 == 0) {
         response.sendRedirect("http://localhost:" + (testPort + 1));
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

   private void handleAction(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
      final Matcher matcher = actionPattern.matcher(request.getRequestURI());
      boolean matchFound = matcher.find();
      if (matchFound) {
         String objectId = matcher.group(1);
         String action = matcher.group(2);
         Map<String, String> options = Maps.newHashMap();
         if (matcher.groupCount() == 3) {
            String optionsGroup = matcher.group(3);
            for (String entry : optionsGroup.split(";")) {
               if (entry.indexOf('=') >= 0) {
                  String[] keyValue = entry.split("=");
                  options.put(keyValue[0], keyValue[1]);
               }
            }
         }
         response.setStatus(HttpServletResponse.SC_OK);
         response.getWriter().println(objectId + "->" + action + ":" + options);
      } else {
         response.sendError(500, "no content");
      }
   }
}
