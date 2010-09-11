/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.io.ByteStreams.copy;
import static com.google.common.io.ByteStreams.join;
import static com.google.common.io.ByteStreams.newInputStreamSupplier;
import static com.google.common.io.ByteStreams.toByteArray;
import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContextBuilder;
import static org.jclouds.util.Utils.toStringAndClose;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jclouds.Constants;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.io.InputSuppliers;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextBuilder;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.jetty.ssl.SslSocketConnector;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.InputSupplier;
import com.google.inject.Injector;
import com.google.inject.Module;

public abstract class BaseJettyTest {

   protected static final String XML = "<foo><bar>whoppers</bar></foo>";
   protected static final String XML2 = "<foo><bar>chubbs</bar></foo>";

   protected Server server = null;
   protected IntegrationTestClient client;
   protected Injector injector;
   private AtomicInteger cycle = new AtomicInteger(0);
   private Server server2;
   protected RestContext<IntegrationTestClient, IntegrationTestAsyncClient> context;
   protected int testPort;
   protected String md5;
   static final Pattern actionPattern = Pattern.compile("/objects/(.*)/action/([a-z]*);?(.*)");

   @BeforeTest
   @Parameters( { "test-jetty-port" })
   public void setUpJetty(@Optional("8123") final int testPort) throws Exception {
      this.testPort = testPort;

      final InputSupplier<InputStream> oneHundredOneConstitutions = getTestDataSupplier();

      md5 = CryptoStreams.md5Base64(oneHundredOneConstitutions);

      Handler server1Handler = new AbstractHandler() {
         public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch)
                  throws IOException, ServletException {
            if (failIfNoContentLength(request, response)) {
               return;
            } else if (target.indexOf("sleep") > 0) {
               try {
                  Thread.sleep(100);
               } catch (InterruptedException e) {
                  propagate(e);
               }
               response.setContentType("text/xml");
               response.setStatus(HttpServletResponse.SC_OK);
            } else if (target.indexOf("redirect") > 0) {
               response.sendRedirect("https://localhost:" + (testPort + 1) + "/");
            } else if (target.indexOf("101constitutions") > 0) {
               response.setContentType("text/plain");
               response.setHeader("Content-MD5", md5);
               response.setStatus(HttpServletResponse.SC_OK);
               copy(oneHundredOneConstitutions.getInput(), response.getOutputStream());
            } else if (request.getMethod().equals("PUT")) {
               if (request.getContentLength() > 0) {
                  response.setStatus(HttpServletResponse.SC_OK);
                  response.getWriter().println(toStringAndClose(request.getInputStream()) + "PUT");
               } else {
                  response.sendError(500, "no content");
               }
            } else if (request.getMethod().equals("POST")) {
               if (redirectEveryTwentyRequests(request, response))
                  return;
               if (failEveryTenRequests(request, response))
                  return;
               if (request.getContentLength() > 0) {
                  if (request.getHeader("Content-MD5") != null) {
                     String expectedMd5 = request.getHeader("Content-MD5");
                     String realMd5FromRequest = CryptoStreams.md5Base64(InputSuppliers.of(request.getInputStream()));
                     boolean matched = expectedMd5.equals(realMd5FromRequest);
                     if (matched) {
                        response.setContentType("text/xml");
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().println("created");
                     }
                  } else {
                     response.setStatus(HttpServletResponse.SC_OK);
                     response.getWriter().println(toStringAndClose(request.getInputStream()) + "POST");
                  }
               } else {
                  handleAction(request, response);
               }
            } else if (request.getHeader("range") != null) {
               response.sendError(404, "no content");
            } else if (request.getHeader("test") != null) {
               response.setContentType("text/plain");
               response.setStatus(HttpServletResponse.SC_OK);
               response.getWriter().println("test");
            } else if (request.getMethod().equals("HEAD")) {
               /*
                * NOTE: by HTML specification, HEAD response MUST NOT include a body
                */
               response.setContentType("text/xml");
               response.setStatus(HttpServletResponse.SC_OK);
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

      setupAndStartSSLServer(testPort);

      Properties properties = new Properties();
      addConnectionProperties(properties);
      context = newBuilder(testPort, properties, createConnectionModule()).buildContext();
      client = context.getApi();
      assert client != null;

      assert client.newStringBuffer() != null;
   }

   protected void setupAndStartSSLServer(final int testPort) throws Exception {
      Handler server2Handler = new AbstractHandler() {
         public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch)
                  throws IOException, ServletException {
            if (request.getMethod().equals("PUT")) {
               if (request.getContentLength() > 0) {
                  response.setStatus(HttpServletResponse.SC_OK);
                  response.getWriter().println(toStringAndClose(request.getInputStream()) + "PUTREDIRECT");
               }
            } else if (request.getMethod().equals("POST")) {
               if (request.getContentLength() > 0) {
                  if (request.getHeader("Content-MD5") != null) {
                     String expectedMd5 = request.getHeader("Content-MD5");
                     String realMd5FromRequest;
                     realMd5FromRequest = CryptoStreams.md5Base64(InputSuppliers.of(request.getInputStream()));
                     boolean matched = expectedMd5.equals(realMd5FromRequest);
                     if (matched) {
                        response.setContentType("text/xml");
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().println("created");
                     }
                  } else {
                     response.setStatus(HttpServletResponse.SC_OK);
                     response.getWriter().println(toStringAndClose(request.getInputStream()) + "POST");
                  }
               } else {
                  handleAction(request, response);
               }
            } else if (request.getMethod().equals("HEAD")) {
               /*
                * NOTE: by HTML specification, HEAD response MUST NOT include a body
                */
               response.setContentType("text/xml");
               response.setStatus(HttpServletResponse.SC_OK);
            } else {
               response.setContentType("text/xml");
               response.setStatus(HttpServletResponse.SC_OK);
               response.getWriter().println(XML2);
            }
            ((Request) request).setHandled(true);
         }
      };

      server2 = new Server();
      server2.setHandler(server2Handler);
      SslSocketConnector ssl = new SslSocketConnector();
      ssl.setPort(testPort + 1);
      ssl.setMaxIdleTime(30000);
      ssl.setKeystore("src/test/resources/test.jks");
      ssl.setKeyPassword("jclouds");
      ssl.setTruststore("src/test/resources/test.jks");
      ssl.setTrustPassword("jclouds");
      server2.setConnectors(new Connector[] { ssl });
      server2.start();
   }

   @SuppressWarnings("unchecked")
   public static InputSupplier<InputStream> getTestDataSupplier() throws IOException {
      byte[] oneConstitution = toByteArray(new GZIPInputStream(BaseJettyTest.class.getResourceAsStream("/const.txt.gz")));
      InputSupplier<ByteArrayInputStream> constitutionSupplier = newInputStreamSupplier(oneConstitution);

      InputSupplier<InputStream> temp = join(constitutionSupplier);

      for (int i = 0; i < 100; i++) {
         temp = join(temp, constitutionSupplier);
      }
      return temp;
   }

   public static RestContextBuilder<IntegrationTestClient, IntegrationTestAsyncClient> newBuilder(int testPort,
            Properties properties, Module... connectionModules) {
      properties.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      properties.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      ContextSpec<IntegrationTestClient, IntegrationTestAsyncClient> contextSpec = contextSpec("test",
               "http://localhost:" + testPort, "1", "identity", null, IntegrationTestClient.class,
               IntegrationTestAsyncClient.class, ImmutableSet.<Module> copyOf(connectionModules));
      return createContextBuilder(contextSpec, properties);
   }

   @AfterTest
   public void tearDownJetty() throws Exception {
      context.close();
      if (server2 != null)
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
   protected boolean failEveryTenRequests(HttpServletRequest request, HttpServletResponse response) throws IOException {
      if (cycle.incrementAndGet() % 10 == 0) {
         response.sendError(500);
         ((Request) request).setHandled(true);
         return true;
      }
      return false;
   }

   protected boolean redirectEveryTwentyRequests(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
      if (cycle.incrementAndGet() % 20 == 0) {
         response.sendRedirect("http://localhost:" + (testPort + 1) + "/");
         ((Request) request).setHandled(true);
         return true;
      }
      return false;
   }

   protected boolean failIfNoContentLength(HttpServletRequest request, HttpServletResponse response) throws IOException {
      if (request.getHeader(CONTENT_LENGTH) == null) {
         response.sendError(500);
         ((Request) request).setHandled(true);
         return true;
      }
      return false;
   }

   private void handleAction(HttpServletRequest request, HttpServletResponse response) throws IOException {
      final Matcher matcher = actionPattern.matcher(request.getRequestURI());
      boolean matchFound = matcher.find();
      if (matchFound) {
         String objectId = matcher.group(1);
         String action = matcher.group(2);
         Map<String, String> options = newHashMap();
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
