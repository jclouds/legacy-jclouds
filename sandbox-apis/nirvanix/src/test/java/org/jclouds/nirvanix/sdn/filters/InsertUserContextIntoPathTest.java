/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.nirvanix.sdn.filters;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Properties;

import javax.ws.rs.POST;

import org.jclouds.concurrent.MoreExecutors;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.nirvanix.sdn.SDNPropertiesBuilder;
import org.jclouds.nirvanix.sdn.config.SDNAuthRestClientModule;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.config.RestModule;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

@Test(groups = "unit", sequential = true, testName = "sdn.InsertUserContextIntoPathTest")
// sequential as easymock isn't threadsafe
public class InsertUserContextIntoPathTest {

   private Injector injector;
   private InsertUserContextIntoPath filter;
   private RestAnnotationProcessor<TestService> factory;
   private Method method;

   private static interface TestService {
      @POST
         public void foo(@EndpointParam URI endpoint);
   }

   public void testRequestInvalid() {
      HttpRequest request = factory.createRequest(method, URI.create("https://host/path"));
      request = filter.filter(request);
      request = filter.filter(request);
      assertEquals(request.getEndpoint().getPath(), "/token/appname/username/path");
      assertEquals(request.getEndpoint().getHost(), "host");
   }

   public void testRequestNoSession() {
      HttpRequest request = factory.createRequest(method, URI.create("https://host/path"));
      request = filter.filter(request);
      assertEquals(request.getEndpoint().getPath(), "/token/appname/username/path");
      assertEquals(request.getEndpoint().getHost(), "host");
   }

   public void testRequestAlreadyHasSession() {
      HttpRequest request = factory.createRequest(method, URI.create("https://host/token/appname/username/path"));
      request = filter.filter(request);
      assertEquals(request.getEndpoint().getPath(), "/token/appname/username/path");
      assertEquals(request.getEndpoint().getHost(), "host");
   }

   @BeforeClass
   protected void createFilter() throws SecurityException, NoSuchMethodException {
      injector = Guice.createInjector(new RestModule(), new ExecutorServiceModule(MoreExecutors.sameThreadExecutor(),
            MoreExecutors.sameThreadExecutor()), new JavaUrlHttpCommandExecutorServiceModule(), new AbstractModule() {

         protected void configure() {
            install(new SDNAuthRestClientModule());
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
            AddSessionTokenToRequest sessionManager = createMock(AddSessionTokenToRequest.class);
            expect(sessionManager.getSessionToken()).andReturn("token").anyTimes();
            replay(sessionManager);
            bind(AddSessionTokenToRequest.class).toInstance(sessionManager);
            Names.bindProperties(this.binder(),
                  new SDNPropertiesBuilder(new Properties()).credentials("appkey/appname/username", "password").build());
         }

      });
      filter = injector.getInstance(InsertUserContextIntoPath.class);
      factory = injector.getInstance(Key.get(new TypeLiteral<RestAnnotationProcessor<TestService>>() {
      }));
      method = TestService.class.getMethod("foo", URI.class);
   }

}