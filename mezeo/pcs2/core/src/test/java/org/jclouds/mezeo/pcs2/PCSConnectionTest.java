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
package org.jclouds.mezeo.pcs2;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

import javax.inject.Singleton;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseURIList;
import org.jclouds.http.functions.ReturnInputStream;
import org.jclouds.http.functions.ReturnVoidIf2xx;
import org.jclouds.mezeo.pcs2.binders.PCSFileAsMultipartFormBinderTest;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.endpoints.RootContainer;
import org.jclouds.rest.JaxrsAnnotationProcessor;
import org.jclouds.rest.config.JaxrsModule;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Multimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code PCSConnection}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.PCSConnectionTest")
public class PCSConnectionTest {

   public void testListContainers() throws SecurityException, NoSuchMethodException {
      Method method = PCSConnection.class.getMethod("listContainers");

      HttpRequest httpMethod = processor.createRequest(method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/root/contents");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("X-Cloud-Depth"), Collections.singletonList("2"));
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testCreateContainer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSConnection.class.getMethod("createContainer", String.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { "container" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/root/contents");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.POST);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList("45"));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("application/vnd.csp.container-info+xml"));
      assertEquals(httpMethod.getEntity(), "<container><name>container</name></container>");
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ParseURIList.class);
      // TODO check generic type of response parser
   }

   public void testDeleteContainer() throws SecurityException, NoSuchMethodException {
      Method method = PCSConnection.class.getMethod("deleteContainer", URI.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { URI
               .create("http://localhost/container/1234") });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/container/1234");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.DELETE);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ReturnVoidIf2xx.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnVoidOnNotFoundOr404.class);
   }

   public void testListFiles() throws SecurityException, NoSuchMethodException {
      Method method = PCSConnection.class.getMethod("listFiles", URI.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { URI
               .create("http://localhost/mycontainer") });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/mycontainer/contents");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("X-Cloud-Depth"), Collections.singletonList("2"));
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testListContainersURI() throws SecurityException, NoSuchMethodException {
      Method method = PCSConnection.class.getMethod("listContainers", URI.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { URI
               .create("http://localhost/mycontainer") });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/mycontainer/contents");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("X-Cloud-Depth"), Collections.singletonList("2"));
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testUploadFile() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSConnection.class.getMethod("uploadFile", URI.class, PCSFile.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] {
               URI.create("http://localhost/mycontainer"),
               PCSFileAsMultipartFormBinderTest.TEST_BLOB });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/mycontainer/contents");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.POST);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(PCSFileAsMultipartFormBinderTest.EXPECTS.length() + ""));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("multipart/form-data; boundary="
                        + PCSFileAsMultipartFormBinderTest.BOUNDRY));
      assertEquals(Utils.toStringAndClose((InputStream) httpMethod.getEntity()),
               PCSFileAsMultipartFormBinderTest.EXPECTS);
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ParseURIList.class);
   }

   public void testDownloadFile() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSConnection.class.getMethod("downloadFile", URI.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { URI
               .create("http://localhost/container") });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/container/content");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ReturnInputStream.class);
   }

   public void testDeleteFile() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSConnection.class.getMethod("deleteFile", URI.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { URI
               .create("http://localhost/contents/file") });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/contents/file");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.DELETE);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ReturnVoidIf2xx.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnVoidOnNotFoundOr404.class);
   }

   JaxrsAnnotationProcessor<PCSConnection> processor;

   @BeforeClass
   void setupFactory() {

      Injector injector = Guice.createInjector(
               new AbstractModule() {
                  @Override
                  protected void configure() {
                     bind(URI.class).annotatedWith(PCS.class).toInstance(
                              URI.create("http://localhost:8080"));
                     bind(URI.class).annotatedWith(RootContainer.class).toInstance(
                              URI.create("http://localhost:8080/root"));
                  }

                  @SuppressWarnings("unused")
                  @Provides
                  @Singleton
                  public PCSUtil getPCSUtil() {
                     return new PCSUtil() {

                        public Future<Void> put(URI resource, String value) {
                           return null;
                        }

                        public Future<Void> addEntryToMultiMap(Multimap<String, String> map,
                                 String key, URI value) {
                           return null;
                        }

                     };
                  }

                  @SuppressWarnings("unused")
                  @Provides
                  @Singleton
                  ConcurrentMap<org.jclouds.mezeo.pcs2.functions.Key, String> giveMap() {
                     ConcurrentHashMap<org.jclouds.mezeo.pcs2.functions.Key, String> map = new ConcurrentHashMap<org.jclouds.mezeo.pcs2.functions.Key, String>();
                     map.put(
                              new org.jclouds.mezeo.pcs2.functions.Key("mycontainer",
                                       "testfile.txt"), "9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3");
                     return map;
                  }

                  @SuppressWarnings("unused")
                  @Provides
                  @Singleton
                  ConcurrentMap<org.jclouds.mezeo.pcs2.functions.Key, FileMetadata> giveMap2() {
                     ConcurrentHashMap<org.jclouds.mezeo.pcs2.functions.Key, FileMetadata> map = new ConcurrentHashMap<org.jclouds.mezeo.pcs2.functions.Key, FileMetadata>();
                     map.put(
                              new org.jclouds.mezeo.pcs2.functions.Key("mycontainer",
                                       "testfile.txt"), new FileMetadata("testfile.txt"));
                     return map;
                  }

                  @SuppressWarnings("unused")
                  @Provides
                  @Singleton
                  ConcurrentMap<String, String> giveMap3() {
                     ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
                     map.put("mycontainer", "7F143552-AAF5-11DE-BBB0-0BC388ED913B");
                     return map;
                  }

                  @SuppressWarnings("unused")
                  @Provides
                  @Singleton
                  public BasicAuthentication provideBasicAuthentication()
                           throws UnsupportedEncodingException {
                     return new BasicAuthentication("foo", "bar");
                  }
               }, new JaxrsModule(), new ExecutorServiceModule(new WithinThreadExecutorService()),
               new JavaUrlHttpCommandExecutorServiceModule());

      processor = injector.getInstance(Key
               .get(new TypeLiteral<JaxrsAnnotationProcessor<PCSConnection>>() {
               }));
   }
}
