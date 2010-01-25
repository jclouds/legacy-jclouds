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
package org.jclouds.gae.config;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.jclouds.Constants;
import org.jclouds.PropertiesBuilder;
import org.jclouds.gae.GaeHttpCommandExecutorService;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * Tests the ability to configure a {@link GoogleAppEngineConfigurationModule}
 * 
 * @author Adrian Cole
 */
@Test
public class GoogleAppEngineConfigurationModuleTest {

   public void testConfigureBindsClient() {
      final Properties properties = new PropertiesBuilder() {
         public PropertiesBuilder withEndpoint(URI endpoint) {
            return null;
         }

         public PropertiesBuilder withCredentials(String account, String key) {
            return null;
         }
      }.build();

      Injector i = Guice.createInjector(new GoogleAppEngineConfigurationModule() {
         @Override
         protected void configure() {
            Jsr330.bindProperties(binder(), properties);
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
            super.configure();
         }
      });
      HttpCommandExecutorService client = i.getInstance(HttpCommandExecutorService.class);
      i.getInstance(Key.get(ExecutorService.class, Jsr330.named(Constants.PROPERTY_USER_THREADS)));
      // TODO check single threaded;
      assert client instanceof GaeHttpCommandExecutorService;
   }
}
