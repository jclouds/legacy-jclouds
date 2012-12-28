/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.softlayer.features;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code ProductPackageAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ProductPackageAsyncClientTest extends BaseSoftLayerAsyncClientTest<ProductPackageAsyncClient> {

   public void testGetProductPackage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ProductPackageAsyncClient.class.getMethod("getProductPackage", long.class);
      HttpRequest httpRequest = processor.createRequest(method, 1234);

      assertRequestLineEquals(
               httpRequest,
               "GET https://api.softlayer.com/rest/v3/SoftLayer_Product_Package/1234.json?objectMask=items.prices%3Bitems.categories%3Blocations.locationAddress%3Blocations.regions HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<ProductPackageAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<ProductPackageAsyncClient>>() {
      };
   }
}
