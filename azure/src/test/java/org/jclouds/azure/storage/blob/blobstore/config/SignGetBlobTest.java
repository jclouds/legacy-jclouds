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

package org.jclouds.azure.storage.blob.blobstore.config;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.azure.storage.blob.AzureBlobAsyncClient;
import org.jclouds.azure.storage.blob.blobstore.strategy.SignGetBlob;
import org.jclouds.azure.storage.blob.config.AzureBlobRestClientModule;
import org.jclouds.azure.storage.filters.SharedKeyLiteAuthentication;
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

/**
 * Tests behavior of {@code SignGetBlob}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azureblob.SignGetBlobTest")
public class SignGetBlobTest extends RestClientTest<AzureBlobAsyncClient> {

   public void testSignGetBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HttpRequest request = new SignGetBlob(processor).apply("container", "blob");

      assertRequestLineEquals(request, "GET https://identity.blob.core.windows.net/container/blob HTTP/1.1");
      assertNonPayloadHeadersEqual(
               request,
               "Authorization: SharedKeyLite identity:bX3fHsuXQQEzvLey2TD76FcDDvDIHZpgSX2j5oH4Iy8=\nDate: 2009-11-08T15:54:08.897Z\nx-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertEquals(request.getFilters().size(), 0);
   }

   @RequiresHttp
   @ConfiguresRestClient
   protected static final class TestAzureBlobRestClientModule extends AzureBlobRestClientModule {
      @Override
      protected void configure() {
         super.configure();
      }

      @Override
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return "2009-11-08T15:54:08.897Z";
      }
   }

   @Override
   protected Module createModule() {
      return new TestAzureBlobRestClientModule();
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SharedKeyLiteAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AzureBlobAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AzureBlobAsyncClient>>() {
      };
   }

   @Override
   public ContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory().createContextSpec("azureblob", "identity", "credential", new Properties());
   }

}