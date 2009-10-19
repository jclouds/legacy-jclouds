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
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Singleton;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.blobstore.integration.internal.StubBlobStore;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.ReturnVoidIf2xx;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.endpoints.RootContainer;
import org.jclouds.mezeo.pcs2.endpoints.WebDAV;
import org.jclouds.mezeo.pcs2.functions.AddEntryIntoMap;
import org.jclouds.mezeo.pcs2.functions.AddMetadataAndReturnId;
import org.jclouds.mezeo.pcs2.functions.AssembleBlobFromContentAndMetadataCache;
import org.jclouds.mezeo.pcs2.functions.InvalidateContainerNameCacheAndReturnTrueIf2xx;
import org.jclouds.mezeo.pcs2.functions.InvalidatePCSKeyCacheAndReturnVoidIf2xx;
import org.jclouds.mezeo.pcs2.functions.ReturnFalseIfContainerNotFound;
import org.jclouds.mezeo.pcs2.options.PutBlockOptions;
import org.jclouds.rest.config.RestModule;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.DateService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
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
public class PCSBlobStoreTest {
   public static final class StubPCSConnection implements PCSConnection {
      DateService dateService = new DateService();

      public Future<? extends SortedSet<FileMetadata>> listBlobs(String containerName) {
         return new StubBlobStore.FutureBase<SortedSet<FileMetadata>>() {
            public SortedSet<FileMetadata> get() throws InterruptedException, ExecutionException {

               return ImmutableSortedSet
                        .of(
                                 new FileMetadata(
                                          "more",
                                          URI
                                                   .create("https://pcsbeta.mezeo.net/v2/files/5C81DADC-AAEE-11DE-9D55-B39340AEFF3A"),
                                          dateService.fromSeconds(1254005157), dateService
                                                   .fromSeconds(1254005158), dateService
                                                   .fromSeconds(1254005159), "adrian@jclouds.org",
                                          false, false, 1, 254288,
                                          MediaType.APPLICATION_OCTET_STREAM, true),

                                 new FileMetadata(
                                          "testfile.txt",
                                          URI
                                                   .create("https://pcsbeta.mezeo.net/v2/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3"),
                                          dateService.fromSeconds(1254000180), dateService
                                                   .fromSeconds(1254000181), dateService
                                                   .fromSeconds(1254000182), "adrian@jclouds.org",
                                          false, true, 3, 5, MediaType.TEXT_PLAIN, false));

            }
         };

      }

      public SortedSet<ContainerMetadata> listContainers() {
         return ImmutableSortedSet
                  .of(new ContainerMetadata(
                           "mycontainer",
                           URI
                                    .create("https://pcsbeta.mezeo.net/v2/containers/7F143552-AAF5-11DE-BBB0-0BC388ED913B"),
                           URI
                                    .create("https://pcsbeta.mezeo.net/v2/containers/7F143552-AAF5-11DE-BBB0-0BC388ED913B"),
                           dateService.fromSeconds(1254008225),
                           dateService.fromSeconds(1254008226),
                           dateService.fromSeconds(1254008227), "adrian@jclouds.org", true, false,
                           1, 1024));
      }

      public Future<byte[]> uploadFile(String containerName, PCSFile object) {
         return null;
      }

      public Future<Boolean> removeBlob(String container, String key) {
         return null;
      }

      public Future<PCSFile> getBlob(String container, String key) {
         return null;
      }

      public FileMetadata blobMetadata(String container, String key) {
         return null;
      }

      public Future<PCSFile> getBlob(String container, String key, GetOptions options) {
         return null;
      }

      public boolean containerExists(String containerName) {
         return false;
      }

      public Future<URI> createContainer(String container) {
         return null;
      }

      public Future<Void> deleteContainer(URI container) {
         return null;
      }

      public Future<Void> deleteFile(URI file) {
         return null;
      }

      public Future<InputStream> downloadFile(URI file) {
         return null;
      }

      public Future<? extends SortedSet<FileMetadata>> listFiles(URI container) {
         return null;
      }

      public Future<URI> uploadFile(URI container, PCSFile object) {
         return null;
      }

      public Future<? extends SortedSet<ContainerMetadata>> listContainers(URI container) {
         throw new UnsupportedOperationException();
      }

      public Future<URI> createContainer(URI parent, String container) {
         throw new UnsupportedOperationException();
      }

      public Future<Void> appendFile(URI file, PCSFile object) {
         throw new UnsupportedOperationException();
      }

      public Future<URI> createFile(URI container, final PCSFile object) {
         return new StubBlobStore.FutureBase<URI>() {

            public URI get() throws InterruptedException, ExecutionException {
               return URI.create("http://localhost/" + object.getName());
            }
         };
      }

      public Future<Void> uploadBlock(URI file, PCSFile object, PutBlockOptions... options) {
         throw new UnsupportedOperationException();
      }

   }

   public void testListContainers() throws SecurityException, NoSuchMethodException {
      Method method = PCSBlobStore.class.getMethod("listContainers");

      GeneratedHttpRequest<PCSBlobStore> httpMethod = processor.createRequest(method,
               new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/root/contents");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("X-Cloud-Depth"), Collections.singletonList("2"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(), ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testCreateContainer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSBlobStore.class.getMethod("createContainer", String.class);

      GeneratedHttpRequest<PCSBlobStore> httpMethod = processor.createRequest(method,
               new Object[] { "container" });
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
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
   }

   public void testDeleteContainer() throws SecurityException, NoSuchMethodException {
      Method method = PCSBlobStore.class.getMethod("deleteContainer", String.class);

      GeneratedHttpRequest<PCSBlobStore> httpMethod = processor.createRequest(method,
               new Object[] { "mycontainer" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(),
               "/containers/7F143552-AAF5-11DE-BBB0-0BC388ED913B");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.DELETE);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               InvalidateContainerNameCacheAndReturnTrueIf2xx.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnVoidOnNotFoundOr404.class);
   }

   public void testContainerExists() throws SecurityException, NoSuchMethodException {
      Method method = PCSBlobStore.class.getMethod("containerExists", String.class);

      GeneratedHttpRequest<PCSBlobStore> httpMethod = processor.createRequest(method,
               new Object[] { "mycontainer" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(),
               "/containers/7F143552-AAF5-11DE-BBB0-0BC388ED913B");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnTrueIf2xx.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnFalseIfContainerNotFound.class);
   }

   public void testListBlobs() throws SecurityException, NoSuchMethodException {
      Method method = PCSBlobStore.class.getMethod("listBlobs", String.class);

      GeneratedHttpRequest<PCSBlobStore> httpMethod = processor.createRequest(method,
               new Object[] { "mycontainer" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(),
               "/containers/7F143552-AAF5-11DE-BBB0-0BC388ED913B/contents");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("X-Cloud-Depth"), Collections.singletonList("2"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(), ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testPutBlob() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSBlobStore.class.getMethod("putBlob", String.class, PCSFile.class);
      PCSFile file = new PCSFile("hello");
      file.setData("wonkers");
      GeneratedHttpRequest<PCSBlobStore> httpMethod = processor.createRequest(method, new Object[] {
               "mycontainer", file });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/files/o/content");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(file.getData().toString().getBytes().length + ""));
      assertEquals(httpMethod.getEntity(), file.getData());
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               AddMetadataAndReturnId.class);
   }

   public void testRemoveBlob() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSBlobStore.class.getMethod("removeBlob", String.class, String.class);

      GeneratedHttpRequest<PCSBlobStore> httpMethod = processor.createRequest(method, new Object[] {
               "mycontainer", "testfile.txt" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(),
               "/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.DELETE);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               InvalidatePCSKeyCacheAndReturnVoidIf2xx.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnVoidOnNotFoundOr404.class);
   }

   public void testGetBlob() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSBlobStore.class.getMethod("getBlob", String.class, String.class);

      GeneratedHttpRequest<PCSBlobStore> httpMethod = processor.createRequest(method, new Object[] {
               "mycontainer", "testfile.txt" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/webdav/mycontainer/testfile.txt");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               AssembleBlobFromContentAndMetadataCache.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ThrowKeyNotFoundOn404.class);
   }

   public void testGetBlobOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSBlobStore.class.getMethod("getBlob", String.class, String.class,
               GetOptions.class);

      GeneratedHttpRequest<PCSBlobStore> httpMethod = processor.createRequest(method, new Object[] {
               "mycontainer", "testfile.txt", new GetOptions() });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/webdav/mycontainer/testfile.txt");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               AssembleBlobFromContentAndMetadataCache.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ThrowKeyNotFoundOn404.class);
   }

   public void testGetBlobMetadata() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSBlobStore.class.getMethod("blobMetadata", String.class, String.class);

      GeneratedHttpRequest<PCSBlobStore> httpMethod = processor.createRequest(method, new Object[] {
               "mycontainer", "testfile.txt" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(),
               "/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(), ParseSax.class);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("X-Cloud-Depth"), Collections.singletonList("2"));
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ThrowKeyNotFoundOn404.class);
   }

   public void testPutMetadata() throws SecurityException, NoSuchMethodException {
      Method method = PCSUtil.class.getMethod("putMetadata", String.class, String.class,
               String.class);
      GeneratedHttpRequest<PCSUtil> httpMethod = utilProcessor.createRequest(method, new Object[] {
               "id", "pow", "bar" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/files/id/metadata/pow");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(httpMethod.getEntity().toString().getBytes().length + ""));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("application/unknown"));
      assertEquals("bar", httpMethod.getEntity());
      assertEquals(utilProcessor.createExceptionParserOrNullIfNotFound(method), null);
      assertEquals(utilProcessor.createResponseParser(method, httpMethod).getClass(),
               ReturnVoidIf2xx.class);
   }

   public void testAddEntryToMap() throws SecurityException, NoSuchMethodException {
      Method method = PCSUtil.class.getMethod("addEntryToMap", Map.class, String.class,
               URI.class);

      GeneratedHttpRequest<PCSUtil> httpMethod = utilProcessor
               .createRequest(method, new Object[] { ImmutableMap.of("key", "value"),
                        "newkey", URI.create("http://localhost/pow") });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/pow");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(utilProcessor.createExceptionParserOrNullIfNotFound(method), null);
      assertEquals(utilProcessor.createResponseParser(method, httpMethod).getClass(),
               AddEntryIntoMap.class);
   }

   RestAnnotationProcessor<PCSBlobStore> processor;
   private RestAnnotationProcessor<PCSUtil> utilProcessor;

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
                     bind(URI.class).annotatedWith(WebDAV.class).toInstance(
                              URI.create("http://localhost:8080/webdav"));
                     bind(PCSConnection.class).to(StubPCSConnection.class).asEagerSingleton();
                     bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
                        public Logger getLogger(String category) {
                           return Logger.NULL;
                        }
                     });
                  }

                  @SuppressWarnings("unused")
                  @Provides
                  @Singleton
                  public PCSUtil getPCSUtil() {
                     return new PCSUtil() {

                        public Future<Void> addEntryToMap(Map<String, String> map,
                                 String key, URI value) {
                           return null;
                        }

                        public Future<Void> putMetadata(String resourceId, String key, String value) {
                           return null;
                        }

                     };
                  }

                  @SuppressWarnings("unused")
                  @Provides
                  @Singleton
                  ConcurrentMap<org.jclouds.blobstore.domain.Key, String> giveMap() {
                     ConcurrentHashMap<org.jclouds.blobstore.domain.Key, String> map = new ConcurrentHashMap<org.jclouds.blobstore.domain.Key, String>();
                     map.put(new org.jclouds.blobstore.domain.Key("mycontainer", "testfile.txt"),
                              "9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3");
                     return map;
                  }

                  @SuppressWarnings("unused")
                  @Provides
                  @Singleton
                  ConcurrentMap<org.jclouds.blobstore.domain.Key, FileMetadata> giveMap2() {
                     ConcurrentHashMap<org.jclouds.blobstore.domain.Key, FileMetadata> map = new ConcurrentHashMap<org.jclouds.blobstore.domain.Key, FileMetadata>();
                     map.put(new org.jclouds.blobstore.domain.Key("mycontainer", "testfile.txt"),
                              new FileMetadata("testfile.txt"));
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
               }, new RestModule(), new ExecutorServiceModule(new WithinThreadExecutorService()),
               new JavaUrlHttpCommandExecutorServiceModule());

      processor = injector.getInstance(Key
               .get(new TypeLiteral<RestAnnotationProcessor<PCSBlobStore>>() {
               }));
      utilProcessor = injector.getInstance(Key
               .get(new TypeLiteral<RestAnnotationProcessor<PCSUtil>>() {
               }));
   }
}
