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
package org.jclouds.nirvanix.sdn;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.AsyncClientFactory;
import org.jclouds.rest.config.RestModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code SDNAuthentication}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "sdn.SDNAuthenticationLiveTest")
public class SDNAuthenticationLiveTest {
   String appname = checkNotNull(System.getProperty("jclouds.test.appname"), "jclouds.test.appname");
   String appid = checkNotNull(System.getProperty("jclouds.test.appid"), "jclouds.test.appid");
   String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
   String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

   private Injector injector;

   @Test
   public void testAuthentication() throws Exception {
      SDNAuthentication authentication = injector.getInstance(SDNAuthentication.class);
      String response = authentication.authenticate(appid, user, password)
               .get(10, TimeUnit.SECONDS);
      assertNotNull(response);
   }

   @BeforeClass
   void setupFactory() {
      injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(SDN.class).toInstance(
                     URI.create("http://services.nirvanix.com"));
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         protected SDNAuthentication provideCloud(AsyncClientFactory factory) {
            return factory.create(SDNAuthentication.class);
         }
      }, new RestModule(), new Log4JLoggingModule(), new ExecutorServiceModule(
               sameThreadExecutor(), sameThreadExecutor()),
               new JavaUrlHttpCommandExecutorServiceModule());
   }
}
