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

package org.jclouds.mezeo.pcs2.functions;

import org.testng.annotations.Test;

/**
 * Tests behavior of {@code UseResourceIdAsETag}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.AddMetadataAndReturnIdTest")
public class AddMetadataAndReturnIdTest {
//   static {
//      RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
//   }
//   HttpResponse response = new HttpResponse();
//   ConcurrentMap<Key, String> fileCache;
//   private RestAnnotationProcessor<TestService> factory;
//   private Method method;
//
//   private static interface TestService {
//      @POST
//      public void foo(String container, PCSFile file, @Endpoint URI endpoint);
//   }
//
//   @BeforeClass
//   void setupMap() {
//      fileCache = new ConcurrentHashMap<Key, String>();
//      fileCache.put(new Key("container", "key"), "7F143552-AAF5-11DE-BBB0-0BC388ED913B");
//   }
//
//   @SuppressWarnings("unchecked")
//   PCSUtil createPCSUtil() {
//      PCSUtil connection = createMock(PCSUtil.class);
//      final Future<Void> voidF = createMock(Future.class);
//      expect(
//               connection.putMetadata(eq("7F143552-AAF5-11DE-BBB0-0BC388ED913B"), eq("foo"),
//                        eq("bar"))).andReturn(voidF);
//      expect(
//               connection.putMetadata(eq("7F143552-AAF5-11DE-BBB0-0BC388ED913B"), eq("biz"),
//                        eq("baz"))).andReturn(voidF);
//      replay(connection);
//      return connection;
//   }
//
//   @Test(expectedExceptions = IllegalStateException.class)
//   public void testNoArgs() {
//      AddMetadataAndReturnId function = new AddMetadataAndReturnId(fileCache, createPCSUtil());
//
//      function.apply(response);
//   }
//
//   @Test(expectedExceptions = IllegalStateException.class)
//   public void testNoRequest() {
//      AddMetadataAndReturnId function = new AddMetadataAndReturnId(fileCache, createPCSUtil());
//      function.apply(response);
//   }
//
//   public void testGetEtag() {
//      PCSUtil connection = createPCSUtil();
//      AddMetadataAndReturnId function = new AddMetadataAndReturnId(fileCache, connection);
//      function.setContext(factory.createRequest(method, "container", new PCSFile("key"), URI
//               .create("http://localhost:8080")));
//      response.setContent(Utils
//               .toInputStream("http://localhost/contents/7F143552-AAF5-11DE-BBB0-0BC388ED913B"));
//      String eTag = function.apply(response);
//      assertEquals(eTag, "7F143552-AAF5-11DE-BBB0-0BC388ED913B");
//   }
//
//   public void testMetadataGetEtag() {
//      PCSUtil connection = createPCSUtil();
//      AddMetadataAndReturnId function = new AddMetadataAndReturnId(fileCache, connection);
//      PCSFile pcsFile = new PCSFile("key");
//      pcsFile.getMetadata().getUserMetadata().put("foo", "bar");
//      pcsFile.getMetadata().getUserMetadata().put("biz", "baz");
//
//      function.setContext(factory.createRequest(method, "container", pcsFile, URI
//               .create("http://localhost:8080")));
//      response.setContent(Utils
//               .toInputStream("http://localhost/contents/7F143552-AAF5-11DE-BBB0-0BC388ED913B"));
//      String eTag = function.apply(response);
//      assertEquals(eTag, "7F143552-AAF5-11DE-BBB0-0BC388ED913B");
//      verify(connection);
//   }
//
//   /**
//    * before class, as we need to ensure that the filter is threadsafe.
//    * 
//    * @throws NoSuchMethodException
//    * @throws SecurityException
//    * 
//    */
//   @BeforeClass
//   protected void createFilter() throws SecurityException, NoSuchMethodException {
//      Injector injector = Guice.createInjector(new RestModule(), new ExecutorServiceModule(
//               Executors.sameThreadExecutor()), new JavaUrlHttpCommandExecutorServiceModule(),
//               new AbstractModule() {
//
//                  protected void configure() {
//                     RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
//                     bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
//                        public Logger getLogger(String category) {
//                           return Logger.NULL;
//                        }
//                     });
//                  }
//
//               });
//      factory = injector.getInstance(com.google.inject.Key
//               .get(new TypeLiteral<RestAnnotationProcessor<TestService>>() {
//               }));
//
//      method = TestService.class.getMethod("foo", String.class, PCSFile.class, URI.class);
//   }
}
