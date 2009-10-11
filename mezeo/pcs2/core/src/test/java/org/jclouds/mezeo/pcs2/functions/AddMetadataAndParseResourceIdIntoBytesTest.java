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
package org.jclouds.mezeo.pcs2.functions;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

import javax.ws.rs.POST;
import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.domain.Key;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.mezeo.pcs2.PCSUtil;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.config.RestModule;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.rest.internal.RuntimeDelegateImpl;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code UseResourceIdAsETag}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.AddMetadataAndParseResourceIdIntoBytesTest")
public class AddMetadataAndParseResourceIdIntoBytesTest {
   static {
      RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
   }
   HttpResponse response = new HttpResponse();
   ConcurrentMap<Key, String> fileCache;
   private RestAnnotationProcessor<org.jclouds.mezeo.pcs2.functions.AddMetadataAndParseResourceIdIntoBytesTest.TestService> factory;
   private Method method;

   private static interface TestService {
      @POST
      public void foo(String container, PCSFile file, @Endpoint URI endpoint);
   }

   @BeforeClass
   void setupMap() {
      fileCache = new ConcurrentHashMap<Key, String>();
      fileCache.put(new Key("container", "key"), "7F143552-AAF5-11DE-BBB0-0BC388ED913B");
   }

   @SuppressWarnings("unchecked")
   PCSUtil createPCSUtil() {
      PCSUtil connection = createMock(PCSUtil.class);
      final Future<Void> voidF = createMock(Future.class);
      expect(
               connection.putMetadata(eq("7F143552-AAF5-11DE-BBB0-0BC388ED913B"), eq("foo"),
                        eq("bar"))).andReturn(voidF);
      expect(
               connection.putMetadata(eq("7F143552-AAF5-11DE-BBB0-0BC388ED913B"), eq("biz"),
                        eq("baz"))).andReturn(voidF);
      replay(connection);
      return connection;
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testNoArgs() {
      AddMetadataAndParseResourceIdIntoBytes function = new AddMetadataAndParseResourceIdIntoBytes(
               fileCache, createPCSUtil());

      function.apply(response);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testNoRequest() {
      AddMetadataAndParseResourceIdIntoBytes function = new AddMetadataAndParseResourceIdIntoBytes(
               fileCache, createPCSUtil());
      function.apply(response);
   }

   public void testGetEtag() {
      PCSUtil connection = createPCSUtil();
      AddMetadataAndParseResourceIdIntoBytes function = new AddMetadataAndParseResourceIdIntoBytes(
               fileCache, connection);
      function.setContext(factory.createRequest(method, "container", new PCSFile("key"), URI
               .create("http://localhost:8080")));
      response.setContent(IOUtils
               .toInputStream("http://localhost/contents/7F143552-AAF5-11DE-BBB0-0BC388ED913B"));
      byte[] eTag = function.apply(response);

      byte[] expected = HttpUtils.fromHexString("7F143552AAF511DEBBB00BC388ED913B");
      assertEquals(eTag, expected);
   }

   public void testMetadataGetEtag() {
      PCSUtil connection = createPCSUtil();
      AddMetadataAndParseResourceIdIntoBytes function = new AddMetadataAndParseResourceIdIntoBytes(
               fileCache, connection);
      PCSFile pcsFile = new PCSFile("key");
      pcsFile.getMetadata().getUserMetadata().put("foo", "bar");
      pcsFile.getMetadata().getUserMetadata().put("biz", "baz");

      function.setContext(factory.createRequest(method, "container", pcsFile, URI
               .create("http://localhost:8080")));
      response.setContent(IOUtils
               .toInputStream("http://localhost/contents/7F143552-AAF5-11DE-BBB0-0BC388ED913B"));
      byte[] eTag = function.apply(response);

      byte[] expected = HttpUtils.fromHexString("7F143552AAF511DEBBB00BC388ED913B");
      assertEquals(eTag, expected);
      verify(connection);
   }

   /**
    * before class, as we need to ensure that the filter is threadsafe.
    * 
    * @throws NoSuchMethodException
    * @throws SecurityException
    * 
    */
   @BeforeClass
   protected void createFilter() throws SecurityException, NoSuchMethodException {
      Injector injector = Guice.createInjector(new RestModule(), new ExecutorServiceModule(
               new WithinThreadExecutorService()), new JavaUrlHttpCommandExecutorServiceModule(),
               new AbstractModule() {

                  protected void configure() {
                     RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
                     bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
                        public Logger getLogger(String category) {
                           return Logger.NULL;
                        }
                     });
                  }

               });
      factory = injector.getInstance(com.google.inject.Key
               .get(new TypeLiteral<RestAnnotationProcessor<TestService>>() {
               }));

      method = TestService.class.getMethod("foo", String.class, PCSFile.class, URI.class);
   }
}
