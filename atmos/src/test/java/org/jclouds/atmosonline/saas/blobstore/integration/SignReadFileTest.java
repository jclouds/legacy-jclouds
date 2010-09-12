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

package org.jclouds.atmosonline.saas.blobstore.integration;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.blobstore.strategy.SignReadFile;
import org.jclouds.atmosonline.saas.config.AtmosStorageRestClientModule;
import org.jclouds.atmosonline.saas.filters.SignRequest;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

@Test(groups = "unit", testName = "emcsaas.SignReadFileTest")
public class SignReadFileTest extends RestClientTest<AtmosStorageAsyncClient> {

   public void testSignReadFile() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HttpRequest request = new SignReadFile(processor).apply("container", "name");

      assertRequestLineEquals(request, "GET https://accesspoint.atmosonline.com/rest/namespace/container/name HTTP/1.1");
      assertNonPayloadHeadersEqual(
               request,
               "Accept: */*\nDate: Thu, 05 Jun 2008 16:38:19 GMT\nx-emc-signature: Mhe5tqaKv04BlMvEjreNKkHHxzk=\nx-emc-uid: identity\n");
      assertPayloadEquals(request, null, null, false);

      assertEquals(request.getFilters().size(), 0);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SignRequest.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AtmosStorageAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AtmosStorageAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new TestAtmosStorageRestClientModule();
   }

   @RequiresHttp
   @ConfiguresRestClient
   private static final class TestAtmosStorageRestClientModule extends AtmosStorageRestClientModule {
      @Override
      protected void configure() {
         super.configure();
      }

      @Override
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return "Thu, 05 Jun 2008 16:38:19 GMT";
      }
   }

   @Override
   public ContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory().createContextSpec("atmosonline", "identity", "credential", new Properties());
   }

}