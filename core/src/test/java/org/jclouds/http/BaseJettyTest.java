/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.http;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static com.google.common.hash.Hashing.md5;
import static com.google.common.io.BaseEncoding.base64;
import static com.google.common.io.ByteStreams.copy;
import static com.google.common.io.ByteStreams.join;
import static com.google.common.io.ByteStreams.newInputStreamSupplier;
import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.io.Closeables.closeQuietly;
import static com.google.common.net.HttpHeaders.CONTENT_DISPOSITION;
import static com.google.common.net.HttpHeaders.CONTENT_ENCODING;
import static com.google.common.net.HttpHeaders.CONTENT_LANGUAGE;
import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.jclouds.Constants.PROPERTY_RELAX_HOSTNAME;
import static org.jclouds.Constants.PROPERTY_TRUST_ALL_CERTS;
import static org.jclouds.io.ByteSources.asByteSource;
import static org.jclouds.util.Strings2.toStringAndClose;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.jclouds.ContextBuilder;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
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
   protected int testPort;
   protected String md5;
   static final Pattern actionPattern = Pattern.compile("/objects/(.*)/action/([a-z]*);?(.*)");

   @BeforeClass
   @Parameters({ "test-jetty-port" })
   public void setUpJetty(@Optional("8123") final int testPort) throws Exception {
      this.testPort = testPort;

      final InputSupplier<InputStream> oneHundredOneConstitutions = getTestDataSupplier();
      md5 = base64().encode(asByteSource(oneHundredOneConstitutions.getInput()).hash(md5()).asBytes());

      Handler server1Handler = new AbstractHandler() {
         public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
               throws IOException, ServletException {
            if (failIfNoContentLength(request, response)) {
               return;
            } else if (target.indexOf("sleep") > 0) {
               sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
               response.setContentType("text/xml");
               response.setStatus(SC_OK);
            } else if (target.indexOf("redirect") > 0) {
               // in OpenJDK 7.0, expect continue handling is enforced, so we
               // have to consume the stream.
               // http://hg.openjdk.java.net/jdk7/tl/jdk/rev/045aeb76b0ff
               // getInputStream address the expect-continue, per jetty docs
               // http://wiki.eclipse.org/Jetty/Feature/1xx_Responses#100_Continue
               toStringAndClose(request.getInputStream());
               response.sendRedirect("https://localhost:" + (testPort + 1) + "/");
            } else if (target.indexOf("101constitutions") > 0) {
               response.setContentType("text/plain");
               response.setHeader("Content-MD5", md5);
               response.setStatus(SC_OK);
               copy(oneHundredOneConstitutions, response.getOutputStream());
            } else if (request.getMethod().equals("PUT")) {
               if (request.getContentLength() > 0) {
                  response.setStatus(SC_OK);
                  response.getWriter().println(toStringAndClose(request.getInputStream()) + "PUT");
               } else {
                  response.setStatus(SC_OK);
               }
            } else if (request.getMethod().equals("POST")) {
               // don't redirect large objects
               if (request.getContentLength() < 10240 && redirectEveryTwentyRequests(request, response))
                  return;
               if (failEveryTenRequests(request, response))
                  return;
               if (request.getContentLength() > 0) {
                  handlePost(request, response);
               } else {
                  handleAction(request, response);
               }
            } else if (request.getHeader("range") != null) {
               response.sendError(404, "no content");
            } else if (request.getHeader("test") != null) {
               response.setContentType("text/plain");
               response.setStatus(SC_OK);
               response.getWriter().println("test");
            } else if (request.getMethod().equals("HEAD")) {
               // by HTML specification, HEAD response MUST NOT include a body
               response.setContentType("text/xml");
               response.setStatus(SC_OK);
            } else {
               if (failEveryTenRequests(request, response))
                  return;
               response.setContentType("text/xml");
               response.setStatus(SC_OK);
               response.getWriter().println(XML);
            }
            Request.class.cast(request).setHandled(true);
         }

      };

      server = new Server(testPort);
      server.setHandler(server1Handler);
      server.start();

      setupAndStartSSLServer(testPort);

      Properties properties = new Properties();
      addConnectionProperties(properties);
      client = newBuilder(testPort, properties, createConnectionModule()).buildApi(IntegrationTestClient.class);
      assert client != null;

      assert client.newStringBuilder() != null;
   }

   private static void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      InputStream body = request.getInputStream();
      try {
         if (request.getHeader("Content-MD5") != null) {
            String expectedMd5 = request.getHeader("Content-MD5");
            String realMd5FromRequest;
            realMd5FromRequest = base64().encode(asByteSource(body).hash(md5()).asBytes());
            boolean matched = expectedMd5.equals(realMd5FromRequest);
            if (matched) {
               response.setStatus(SC_OK);
               response.addHeader("x-Content-MD5", realMd5FromRequest);
            } else {
               response.sendError(500, "didn't match");
            }
         } else {
            String responseString = (request.getContentLength() < 10240) ? toStringAndClose(body) + "POST" : "POST";
            body = null;
            for (String header : new String[] { CONTENT_DISPOSITION, CONTENT_LANGUAGE, CONTENT_ENCODING })
               if (request.getHeader(header) != null) {
                  response.addHeader("x-" + header, request.getHeader(header));
               }
            response.setStatus(SC_OK);
            response.getWriter().println(responseString);
         }
         Request.class.cast(request).setHandled(true);
      } catch (IOException e) {
         closeQuietly(body);
         response.sendError(500, getStackTraceAsString(e));
      }
   }

   protected void setupAndStartSSLServer(final int testPort) throws Exception {
      Handler server2Handler = new AbstractHandler() {
         public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
               throws IOException, ServletException {
            if (request.getMethod().equals("PUT")) {
               if (request.getContentLength() > 0) {
                  response.setStatus(SC_OK);
                  String text = toStringAndClose(request.getInputStream());
                  response.getWriter().println(text + "PUTREDIRECT");
               }
            } else if (request.getMethod().equals("POST")) {
               if (request.getContentLength() > 0) {
                  handlePost(request, response);
               } else {
                  handleAction(request, response);
               }
            } else if (request.getMethod().equals("HEAD")) {
               // by HTML specification, HEAD response MUST NOT include a body
               response.setContentType("text/xml");
               response.setStatus(SC_OK);
            } else {
               response.setContentType("text/xml");
               response.setStatus(SC_OK);
               response.getWriter().println(XML2);
            }
            Request.class.cast(request).setHandled(true);
         }
      };

      server2 = new Server();
      server2.setHandler(server2Handler);

      SslSelectChannelConnector ssl_connector = new SslSelectChannelConnector();
      ssl_connector.setPort(testPort + 1);
      ssl_connector.setMaxIdleTime(30000);
      SslContextFactory ssl = ssl_connector.getSslContextFactory();
      ssl.setKeyStorePath("src/test/resources/test.jks");
      ssl.setKeyStorePassword("jclouds");
      ssl.setTrustStore("src/test/resources/test.jks");
      ssl.setTrustStorePassword("jclouds");

      server2.setConnectors(new Connector[] { ssl_connector });

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

   public static ContextBuilder newBuilder(int testPort, Properties properties, Module... connectionModules) {
      properties.setProperty(PROPERTY_TRUST_ALL_CERTS, "true");
      properties.setProperty(PROPERTY_RELAX_HOSTNAME, "true");
      return ContextBuilder
            .newBuilder(
                  AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint(IntegrationTestClient.class,
                        IntegrationTestAsyncClient.class, "http://localhost:" + testPort))
            .modules(ImmutableSet.<Module> copyOf(connectionModules)).overrides(properties);
   }

   @AfterClass
   public void tearDownJetty() throws Exception {
      closeQuietly(client);
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
         response.sendError(500, "unlucky 10");
         Request.class.cast(request).setHandled(true);
         return true;
      }
      return false;
   }

   protected boolean redirectEveryTwentyRequests(HttpServletRequest request, HttpServletResponse response)
         throws IOException {
      if (cycle.incrementAndGet() % 20 == 0) {
         response.sendRedirect("http://localhost:" + (testPort + 1) + "/");
         Request.class.cast(request).setHandled(true);
         return true;
      }
      return false;
   }

   protected boolean failIfNoContentLength(HttpServletRequest request, HttpServletResponse response) throws IOException {
      Multimap<String, String> realHeaders = LinkedHashMultimap.create();
      Enumeration<String> headers = request.getHeaderNames();
      while (headers.hasMoreElements()) {
         String header = headers.nextElement().toString();
         Enumeration<String> values = request.getHeaders(header);
         while (values.hasMoreElements()) {
            realHeaders.put(header, values.nextElement().toString());
         }
      }
      if (realHeaders.get(CONTENT_LENGTH) == null) {
         response.getWriter().println("no content length!");
         response.getWriter().println(realHeaders.toString());
         response.sendError(500, "no content length!");
         Request.class.cast(request).setHandled(true);
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
         Builder<String, String> options = ImmutableMap.<String, String> builder();
         if (matcher.groupCount() == 3) {
            options.putAll(Splitter.on(';').withKeyValueSeparator("=").split(matcher.group(3)));
         }
         response.setStatus(SC_OK);
         response.getWriter().println(objectId + "->" + action + ":" + options.build());
      } else {
         response.sendError(500, "no content");
      }
   }

}
