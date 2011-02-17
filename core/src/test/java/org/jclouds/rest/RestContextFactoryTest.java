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

package org.jclouds.rest;

import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContextBuilder;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.jclouds.PropertiesBuilder;
import org.jclouds.http.IntegrationTestAsyncClient;
import org.jclouds.http.IntegrationTestClient;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.config.RestClientModule;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class RestContextFactoryTest {

   private static final String provider = "test";

   public void testBuilder() {
      RestContextSpec<IntegrationTestClient, IntegrationTestAsyncClient> contextSpec = contextSpec(provider,
               "http://localhost", "1", "", "dummy", null, IntegrationTestClient.class,
               IntegrationTestAsyncClient.class);

      createContextBuilder(contextSpec);
   }

   public void testBuilderPropertiesWithIso3166() {
      RestContextSpec<IntegrationTestClient, IntegrationTestAsyncClient> contextSpec = contextSpec(provider,
               "http://localhost", "1", "US-CA", "dummy", null, IntegrationTestClient.class,
               IntegrationTestAsyncClient.class);

      Properties props = RestContextFactory.toProperties(contextSpec);
      assertEquals(props.getProperty("test.endpoint"), "http://localhost");
      assertEquals(props.getProperty("test.apiversion"), "1");
      assertEquals(props.getProperty("test.identity"), "dummy");
      assertEquals(props.getProperty("test.iso3166-codes"), "US-CA");
      assertEquals(props.getProperty("test.credential"), null);
      assertEquals(props.getProperty("test.sync"), IntegrationTestClient.class.getName());
      assertEquals(props.getProperty("test.async"), IntegrationTestAsyncClient.class.getName());
      assertEquals(props.getProperty("test.propertiesbuilder"), null);
      assertEquals(props.getProperty("test.contextbuilder"), null);
      assertEquals(props.getProperty("test.modules"), null);

      new RestContextFactory().createContext(provider, props);
   }

   public void testBuilderPropertiesWithCredential() {
      RestContextSpec<IntegrationTestClient, IntegrationTestAsyncClient> contextSpec = contextSpec(provider,
               "http://localhost", "1", "", "dummy", "credential", IntegrationTestClient.class,
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
      assertEquals(props.getProperty("test.modules"), null);

      new RestContextFactory().createContext(provider, props);
   }

   @SuppressWarnings("unchecked")
   public void testBuilderPropertiesWithContextBuilder() {
      @SuppressWarnings("rawtypes")
      RestContextSpec<IntegrationTestClient, IntegrationTestAsyncClient> contextSpec = contextSpec(provider,
               "http://localhost", "1", "", "dummy", null, (Class) null, (Class) null, PropertiesBuilder.class,
               (Class) IntegrationTestContextBuilder.class, Collections.EMPTY_LIST);

      Properties props = RestContextFactory.toProperties(contextSpec);
      assertEquals(props.getProperty("test.endpoint"), "http://localhost");
      assertEquals(props.getProperty("test.apiversion"), "1");
      assertEquals(props.getProperty("test.identity"), "dummy");
      assertEquals(props.getProperty("test.credential"), null);
      assertEquals(props.getProperty("test.sync"), null);
      assertEquals(props.getProperty("test.async"), null);
      assertEquals(props.getProperty("test.propertiesbuilder"), PropertiesBuilder.class.getName());
      assertEquals(props.getProperty("test.contextbuilder"), IntegrationTestContextBuilder.class.getName());
      assertEquals(props.getProperty("test.modules"), null);

      new RestContextFactory().createContext(provider, props);
   }

   @SuppressWarnings("unchecked")
   public void testBuilderPropertiesWithModule() {
      @SuppressWarnings("rawtypes")
      RestContextSpec<IntegrationTestClient, IntegrationTestAsyncClient> contextSpec = contextSpec(provider,
               "http://localhost", "1", "", "dummy", null, (Class) null, (Class) null, PropertiesBuilder.class,
               (Class) IntegrationTestContextBuilder.class, Collections.<Module> singleton(new A()));

      Properties props = RestContextFactory.toProperties(contextSpec);
      assertEquals(props.getProperty("test.endpoint"), "http://localhost");
      assertEquals(props.getProperty("test.apiversion"), "1");
      assertEquals(props.getProperty("test.identity"), "dummy");
      assertEquals(props.getProperty("test.credential"), null);
      assertEquals(props.getProperty("test.sync"), null);
      assertEquals(props.getProperty("test.async"), null);
      assertEquals(props.getProperty("test.propertiesbuilder"), PropertiesBuilder.class.getName());
      assertEquals(props.getProperty("test.contextbuilder"), IntegrationTestContextBuilder.class.getName());
      assertEquals(props.getProperty("test.modules"), "org.jclouds.rest.RestContextFactoryTest$A");

      new RestContextFactory().createContext(provider, props);
   }

   @SuppressWarnings("unchecked")
   public void testBuilderPropertiesWithModules() {
      @SuppressWarnings("rawtypes")
      RestContextSpec<IntegrationTestClient, IntegrationTestAsyncClient> contextSpec = contextSpec(provider,
               "http://localhost", "1", "", "dummy", null, (Class) null, (Class) null, PropertiesBuilder.class,
               (Class) IntegrationTestContextBuilder.class, Arrays.<Module> asList(new A(), new B()));

      Properties props = RestContextFactory.toProperties(contextSpec);
      assertEquals(props.getProperty("test.endpoint"), "http://localhost");
      assertEquals(props.getProperty("test.apiversion"), "1");
      assertEquals(props.getProperty("test.identity"), "dummy");
      assertEquals(props.getProperty("test.credential"), null);
      assertEquals(props.getProperty("test.sync"), null);
      assertEquals(props.getProperty("test.async"), null);
      assertEquals(props.getProperty("test.propertiesbuilder"), PropertiesBuilder.class.getName());
      assertEquals(props.getProperty("test.contextbuilder"), IntegrationTestContextBuilder.class.getName());
      assertEquals(props.getProperty("test.modules"),
               "org.jclouds.rest.RestContextFactoryTest$A,org.jclouds.rest.RestContextFactoryTest$B");

      new RestContextFactory().createContext(provider, props);
   }

   public void testBuilderPropertiesJCloudsScope() {
      Properties props = new Properties();
      props.setProperty("test.endpoint", "http://localhost");
      props.setProperty("test.apiversion", "1");
      props.setProperty("test.iso3166-codes", "US");
      props.setProperty("jclouds.identity", "foo");
      props.setProperty("jclouds.credential", "bar");

      props.setProperty("test.propertiesbuilder", PropertiesBuilder.class.getName());
      props.setProperty("test.contextbuilder", IntegrationTestContextBuilder.class.getName());
      props.setProperty("jclouds.modules",
               "org.jclouds.rest.RestContextFactoryTest$A,org.jclouds.rest.RestContextFactoryTest$B");

      new RestContextFactory() {

         @SuppressWarnings("hiding")
         @Override
         public <S, A> RestContextSpec<S, A> createContextSpec(String providerName, String identity, String credential,
                  Iterable<? extends Module> wiring, Properties _overrides) {
            RestContextSpec<S, A> spec = super
                     .createContextSpec(providerName, identity, credential, wiring, _overrides);
            assertEquals(spec.iso3166Codes, "US");
            assertEquals(spec.identity, "foo");
            assertEquals(spec.credential, "bar");
            assertEquals(Iterables.size(spec.modules), 2);
            return spec;
         }

      }.createContext(provider, props);
   }

   public void testBuilderPropertiesJCloudsScopeWithProviderIdentityAndFileCredential() throws IOException {

      File file = File.createTempFile("foo", "bar");
      file.deleteOnExit();
      Files.write("bar", file, Charsets.UTF_8);
      Properties props = new Properties();
      props.setProperty("test.endpoint", "http://localhost");
      props.setProperty("test.apiversion", "1");
      props.setProperty("test.iso3166-codes", "US");
      props.setProperty("test.identity", "foo");
      props.setProperty("test.credential.file", file.getAbsolutePath());

      props.setProperty("test.propertiesbuilder", PropertiesBuilder.class.getName());
      props.setProperty("test.contextbuilder", IntegrationTestContextBuilder.class.getName());
      props.setProperty("jclouds.modules",
               "org.jclouds.rest.RestContextFactoryTest$A,org.jclouds.rest.RestContextFactoryTest$B");

      new RestContextFactory() {

         @SuppressWarnings("hiding")
         @Override
         public <S, A> RestContextSpec<S, A> createContextSpec(String providerName, String identity, String credential,
                  Iterable<? extends Module> wiring, Properties _overrides) {
            RestContextSpec<S, A> spec = super
                     .createContextSpec(providerName, identity, credential, wiring, _overrides);
            assertEquals(spec.iso3166Codes, "US");
            assertEquals(spec.identity, "foo");
            assertEquals(spec.credential, "bar");
            assertEquals(Iterables.size(spec.modules), 2);
            return spec;
         }

      }.createContext(provider, props);
   }

   public static class A extends AbstractModule {

      @Override
      protected void configure() {

      }

   }

   public static class B extends AbstractModule {

      @Override
      protected void configure() {

      }

   }

   @SuppressWarnings("unchecked")
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testBuilderPropertiesWithWrongConfig() {
      @SuppressWarnings( { "unused", "rawtypes" })
      RestContextSpec<IntegrationTestClient, IntegrationTestAsyncClient> contextSpec = contextSpec(provider,
               "http://localhost", "1", "", "dummy", null, (Class) null, (Class) null,
               (Class) IntegrationTestContextBuilder.class, (Class) PropertiesBuilder.class, Collections.EMPTY_LIST);
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
