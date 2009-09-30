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
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.BlobStoreMapsModule;
import org.jclouds.blobstore.functions.ReturnTrueOnNotFoundOr404;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.blobstore.integration.internal.StubBlobStore;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReturnFalseOn404;
import org.jclouds.http.functions.ReturnStringIf200;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.options.GetOptions;
import org.jclouds.mezeo.pcs2.binders.PCSFileAsMultipartFormBinderTest;
import org.jclouds.mezeo.pcs2.config.PCSContextModule;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.endpoints.RootContainer;
import org.jclouds.mezeo.pcs2.functions.AddMetadataAndParseResourceIdIntoBytes;
import org.jclouds.mezeo.pcs2.functions.AssembleBlobFromContentAndMetadataCache;
import org.jclouds.mezeo.pcs2.functions.InvalidateContainerNameCacheAndReturnTrueIf2xx;
import org.jclouds.mezeo.pcs2.functions.InvalidatePCSKeyCacheAndReturnTrueIf2xx;
import org.jclouds.mezeo.pcs2.functions.ReturnFalseIfContainerNotFound;
import org.jclouds.mezeo.pcs2.functions.ReturnTrueIfContainerNotFound;
import org.jclouds.mezeo.pcs2.reference.PCSConstants;
import org.jclouds.rest.JaxrsAnnotationProcessor;
import org.jclouds.rest.config.JaxrsModule;
import org.jclouds.util.DateService;
import org.jclouds.util.Jsr330;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.ImmutableList;

/**
 * Tests behavior of {@code PCSConnection}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.PCSConnectionTest")
public class PCSBlobStoreTest {
   public static final class StubPCSConnection implements PCSBlobStore {
      DateService dateService = new DateService();

      public Future<Boolean> createContainer(String container) {
         return null;
      }

      public Future<Boolean> deleteContainer(String containerName) {
         return null;
      }

      public Future<? extends List<FileMetadata>> listBlobs(String containerName) {
         return new StubBlobStore.FutureBase<List<FileMetadata>>() {
            public List<FileMetadata> get() throws InterruptedException, ExecutionException {

               return ImmutableList
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

      public List<ContainerMetadata> listContainers() {
         return ImmutableList
                  .of(new ContainerMetadata(
                           "mycontainer",
                           URI
                                    .create("https://pcsbeta.mezeo.net/v2/containers/7F143552-AAF5-11DE-BBB0-0BC388ED913B"),
                           dateService.fromSeconds(1254008225),
                           dateService.fromSeconds(1254008226),
                           dateService.fromSeconds(1254008227), "adrian@jclouds.org", true, false,
                           1, 1024));
      }

      public Future<byte[]> putBlob(String containerName, PCSFile object) {
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

   }

   public void testListContainers() throws SecurityException, NoSuchMethodException {
      Method method = PCSBlobStore.class.getMethod("listContainers");

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
      Method method = PCSBlobStore.class.getMethod("createContainer", String.class);

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
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
   }

   public void testDeleteContainer() throws SecurityException, NoSuchMethodException {
      Method method = PCSBlobStore.class.getMethod("deleteContainer", String.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { "mycontainer" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(),
               "/containers/7F143552-AAF5-11DE-BBB0-0BC388ED913B");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.DELETE);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               InvalidateContainerNameCacheAndReturnTrueIf2xx.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnTrueIfContainerNotFound.class);
   }

   public void testContainerExists() throws SecurityException, NoSuchMethodException {
      Method method = PCSBlobStore.class.getMethod("containerExists", String.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { "mycontainer" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(),
               "/containers/7F143552-AAF5-11DE-BBB0-0BC388ED913B");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ReturnTrueIf2xx.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnFalseIfContainerNotFound.class);
   }

   public void testListBlobs() throws SecurityException, NoSuchMethodException {
      Method method = PCSBlobStore.class.getMethod("listBlobs", String.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { "mycontainer" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(),
               "/containers/7F143552-AAF5-11DE-BBB0-0BC388ED913B/contents");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("X-Cloud-Depth"), Collections.singletonList("2"));
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testPutBlob() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSBlobStore.class.getMethod("putBlob", String.class, PCSFile.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { "mycontainer",
               PCSFileAsMultipartFormBinderTest.TEST_BLOB });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(),
               "/containers/7F143552-AAF5-11DE-BBB0-0BC388ED913B/contents");
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
               AddMetadataAndParseResourceIdIntoBytes.class);
   }

   public void testRemoveBlob() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSBlobStore.class.getMethod("removeBlob", String.class, String.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { "mycontainer",
               "testfile.txt" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(),
               "/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.DELETE);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               InvalidatePCSKeyCacheAndReturnTrueIf2xx.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnTrueOnNotFoundOr404.class);
   }

   public void testGetBlob() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSBlobStore.class.getMethod("getBlob", String.class, String.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { "mycontainer",
               "testfile.txt" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(),
               "/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3/content");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               AssembleBlobFromContentAndMetadataCache.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ThrowKeyNotFoundOn404.class);
   }

   public void testGetBlobOptios() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSBlobStore.class.getMethod("getBlob", String.class, String.class,
               GetOptions.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { "mycontainer",
               "testfile.txt", new GetOptions() });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(),
               "/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3/content");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               AssembleBlobFromContentAndMetadataCache.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ThrowKeyNotFoundOn404.class);
   }

   public void testGetBlobMetadata() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSBlobStore.class.getMethod("blobMetadata", String.class, String.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { "mycontainer",
               "testfile.txt" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(),
               "/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ParseSax.class);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("X-Cloud-Depth"), Collections.singletonList("2"));
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ThrowKeyNotFoundOn404.class);
   }

   public void testPutMetadata() throws SecurityException, NoSuchMethodException {
      Method method = PCSUtil.class.getMethod("put", URI.class, String.class);
      HttpRequest httpMethod = utilProcessor.createRequest(method, new Object[] {
               URI.create("http://localhost/pow"), "bar" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/pow");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(httpMethod.getEntity().toString().getBytes().length + ""));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("application/unknown"));
      assertEquals("bar", httpMethod.getEntity());
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnFalseOn404.class);
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ReturnTrueIf2xx.class);
   }

   public void testGetMetadata() throws SecurityException, NoSuchMethodException {
      Method method = PCSUtil.class.getMethod("get", URI.class);

      HttpRequest httpMethod = utilProcessor.createRequest(method, new Object[] {
               URI.create("http://localhost/pow"), "foo" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/pow");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ReturnStringIf200.class);
   }

   JaxrsAnnotationProcessor<PCSBlobStore> processor;
   private JaxrsAnnotationProcessor<PCSUtil> utilProcessor;

   @SuppressWarnings("unchecked")
   @BeforeClass
   void setupFactory() {
      Injector injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(PCS.class)
                     .toInstance(URI.create("http://localhost:8080"));
            bind(URI.class).annotatedWith(RootContainer.class).toInstance(
                     URI.create("http://localhost:8080/root"));
            bindConstant().annotatedWith(Jsr330.named(PCSConstants.PROPERTY_PCS2_USER)).to("user");
            bindConstant().annotatedWith(Jsr330.named(PCSConstants.PROPERTY_PCS2_PASSWORD)).to(
                     "password");
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         public PCSBlobStore getPCSConnection() {
            return new StubPCSConnection();
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         public PCSUtil getPCSUtil() {
            return new PCSUtil() {

               public String get(URI resource) {
                  return null;
               }

               public boolean put(URI resource, String value) {
                  return true;
               }

            };
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         public BasicAuthentication provideBasicAuthentication(
                  @Named(PCSConstants.PROPERTY_PCS2_USER) String user,
                  @Named(PCSConstants.PROPERTY_PCS2_PASSWORD) String password)
                  throws UnsupportedEncodingException {
            return new BasicAuthentication(user, password);
         }
      }, new JaxrsModule(), new BlobStoreMapsModule(new TypeLiteral<PCSBlobStore>() {
      }, new TypeLiteral<ContainerMetadata>() {
      }, new TypeLiteral<FileMetadata>() {
      }, new TypeLiteral<PCSFile>() {
      }), new PCSContextModule(), new ExecutorServiceModule(new WithinThreadExecutorService()),
               new JavaUrlHttpCommandExecutorServiceModule());

      processor = injector.getInstance(Key
               .get(new TypeLiteral<JaxrsAnnotationProcessor<PCSBlobStore>>() {
               }));
      utilProcessor = injector.getInstance(Key
               .get(new TypeLiteral<JaxrsAnnotationProcessor<PCSUtil>>() {
               }));
   }
}
