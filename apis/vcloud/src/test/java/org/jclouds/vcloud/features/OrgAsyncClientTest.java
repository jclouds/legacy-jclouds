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
package org.jclouds.vcloud.features;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.vcloud.internal.BaseVCloudAsyncClientTest;
import org.jclouds.vcloud.xml.OrgHandler;
import org.jclouds.vcloud.xml.OrgListHandler;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code OrgAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "OrgAsyncClientTest")
public class OrgAsyncClientTest extends BaseVCloudAsyncClientTest<OrgAsyncClient> {

   @Override
   protected TypeLiteral<RestAnnotationProcessor<OrgAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<OrgAsyncClient>>() {
      };
   }

   public void testlistOrgs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = OrgAsyncClient.class.getMethod("listOrgs");
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/org HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.orgList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, OrgListHandler.class);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

   public void testOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = OrgAsyncClient.class.getMethod("getOrg", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/org/1"));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/org/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.org+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, OrgHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testFindOrgNamed() throws SecurityException, NoSuchMethodException, IOException {
      Method method = OrgAsyncClient.class.getMethod("findOrgNamed", String.class);
      HttpRequest request = processor.createRequest(method, "org");

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/org/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.org+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, OrgHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

}
