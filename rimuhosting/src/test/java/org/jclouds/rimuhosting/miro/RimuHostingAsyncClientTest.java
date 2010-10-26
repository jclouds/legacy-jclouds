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

package org.jclouds.rimuhosting.miro;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.rimuhosting.miro.binder.CreateServerOptions;
import org.jclouds.rimuhosting.miro.filters.RimuHostingAuthentication;
import org.jclouds.rimuhosting.miro.functions.ParseRimuHostingException;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code RimuHostingAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "rimuhosting.RimuHostingAsyncClientTest")
public class RimuHostingAsyncClientTest extends RestClientTest<RimuHostingAsyncClient> {

   public void testCreateServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = RimuHostingAsyncClient.class.getMethod("createServer", String.class, String.class, String.class,
            CreateServerOptions[].class);
      GeneratedHttpRequest<RimuHostingAsyncClient> httpRequest = processor.createRequest(method, "test.ivan.api.com",
            "lenny", "MIRO1B");

      assertRequestLineEquals(httpRequest, "POST https://rimuhosting.com/r/orders/new-vps HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(
            httpRequest,
            "{\"request\":{\"instantiation_options\":{\"distro\":\"lenny\",\"domain_name\":\"test.ivan.api.com\"},\"pricing_plan_code\":\"MIRO1B\",\"meta_data\":[]}}",
            "application/json", false);
      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ParseRimuHostingException.class);

      checkFilters(httpRequest);

   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), RimuHostingAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<RimuHostingAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<RimuHostingAsyncClient>>() {
      };
   }

   @Override
   public RestContextSpec<RimuHostingClient, RimuHostingAsyncClient> createContextSpec() {
      return new RestContextFactory().createContextSpec("rimuhosting", "apikey", "null", new Properties());
   }
}
