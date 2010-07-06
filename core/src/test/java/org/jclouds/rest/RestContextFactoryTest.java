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
package org.jclouds.rest;

import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContextBuilder;
import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.Properties;

import org.jclouds.PropertiesBuilder;
import org.jclouds.http.IntegrationTestAsyncClient;
import org.jclouds.http.IntegrationTestClient;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.config.RestClientModule;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "rest.RestContextFactoryTest")
public class RestContextFactoryTest {

   public void testBuilder() {
      ContextSpec<IntegrationTestClient, IntegrationTestAsyncClient> contextSpec = contextSpec(
               "test", "http://localhost", "1", "dummy", null, IntegrationTestClient.class,
               IntegrationTestAsyncClient.class);

      createContextBuilder(contextSpec);
   }

   public void testBuilderProperties() {
      ContextSpec<IntegrationTestClient, IntegrationTestAsyncClient> contextSpec = contextSpec(
               "test", "http://localhost", "1", "dummy", null, IntegrationTestClient.class,
               IntegrationTestAsyncClient.class);

      Properties props = RestContextFactory.toProperties(contextSpec);
      assertEquals(props.getProperty("test.endpoint"), "http://localhost");
      assertEquals(props.getProperty("test.apiversion"), "1");
      assertEquals(props.getProperty("test.identity"), "dummy");
      assertEquals(props.getProperty("test.credential"), null);
      assertEquals(props.getProperty("test.sync"), IntegrationTestClient.class.getName());
      assertEquals(props.getProperty("test.async"), IntegrationTestAsyncClient.class.getName());
      assertEquals(props.getProperty("test.propertiesbuilder"), null);
      assertEquals(props.getProperty("test.contextbuilder"), null);

      new RestContextFactory().createContext("test", props);
   }

   public void testBuilderPropertiesWithCredential() {
      ContextSpec<IntegrationTestClient, IntegrationTestAsyncClient> contextSpec = contextSpec(
               "test", "http://localhost", "1", "dummy", "credential", IntegrationTestClient.class,
               IntegrationTestAsyncClient.class);

      Properties props = RestContextFactory.toProperties(contextSpec);
      assertEquals(props.getProperty("test.endpoint"), "http://localhost");
      assertEquals(props.getProperty("test.apiversion"), "1");
      assertEquals(props.getProperty("test.identity"), "dummy");
      assertEquals(props.getProperty("test.credential"), "credential");
      assertEquals(props.getProperty("test.sync"), IntegrationTestClient.class.getName());
      assertEquals(props.getProperty("test.async"), IntegrationTestAsyncClient.class.getName());
      assertEquals(props.getProperty("test.propertiesbuilder"), null);
      assertEquals(props.getProperty("test.contextbuilder"), null);

      new RestContextFactory().createContext("test", props);
   }

   @SuppressWarnings("unchecked")
   public void testBuilderPropertiesWithContextBuilder() {
      ContextSpec<IntegrationTestClient, IntegrationTestAsyncClient> contextSpec = contextSpec(
               "test", "http://localhost", "1", "dummy", null, (Class) null, (Class) null,
               PropertiesBuilder.class, (Class) IntegrationTestContextBuilder.class);

      Properties props = RestContextFactory.toProperties(contextSpec);
      assertEquals(props.getProperty("test.endpoint"), "http://localhost");
      assertEquals(props.getProperty("test.apiversion"), "1");
      assertEquals(props.getProperty("test.identity"), "dummy");
      assertEquals(props.getProperty("test.credential"), null);
      assertEquals(props.getProperty("test.sync"), null);
      assertEquals(props.getProperty("test.async"), null);
      assertEquals(props.getProperty("test.propertiesbuilder"), PropertiesBuilder.class.getName());
      assertEquals(props.getProperty("test.contextbuilder"), IntegrationTestContextBuilder.class
               .getName());

      new RestContextFactory().createContext("test", props);
   }

   @SuppressWarnings("unchecked")
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testBuilderPropertiesWithWrongConfig() {
      @SuppressWarnings("unused")
      ContextSpec<IntegrationTestClient, IntegrationTestAsyncClient> contextSpec = contextSpec(
               "test", "http://localhost", "1", "dummy", null, (Class) null, (Class) null,
               (Class) IntegrationTestContextBuilder.class, (Class) PropertiesBuilder.class);
   }

   @RequiresHttp
   @ConfiguresRestClient
   public static class IntegrationTestRestClientModule extends
            RestClientModule<IntegrationTestClient, IntegrationTestAsyncClient> {

      public IntegrationTestRestClientModule() {
         super(IntegrationTestClient.class, IntegrationTestAsyncClient.class);
      }

   }

   public static class IntegrationTestContextBuilder extends
            RestContextBuilder<IntegrationTestClient, IntegrationTestAsyncClient> {

      public IntegrationTestContextBuilder(Properties props) {
         super(IntegrationTestClient.class, IntegrationTestAsyncClient.class, props);
      }

      protected void addClientModule(List<Module> modules) {
         modules.add(new IntegrationTestRestClientModule());
      }

   }
}
