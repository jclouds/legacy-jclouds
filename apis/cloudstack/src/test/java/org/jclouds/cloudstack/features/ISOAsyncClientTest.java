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
package org.jclouds.cloudstack.features;

import java.lang.reflect.Method;

import org.jclouds.cloudstack.domain.ExtractMode;
import org.jclouds.cloudstack.domain.PermissionOperation;
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.AccountInDomainOptions;
import org.jclouds.cloudstack.options.DeleteISOOptions;
import org.jclouds.cloudstack.options.ExtractISOOptions;
import org.jclouds.cloudstack.options.RegisterISOOptions;
import org.jclouds.cloudstack.options.UpdateISOOptions;
import org.jclouds.cloudstack.options.UpdateISOPermissionsOptions;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSet;
import com.google.inject.TypeLiteral;
/**
 * Tests the behaviour of ISOAsyncClient.
 * 
 * @see ISOAsyncClient
 * @author Richard Downer
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "ISOAsyncClientTest")
public class ISOAsyncClientTest extends BaseCloudStackAsyncClientTest<ISOAsyncClient> {

   public void testAttachISO() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("attachISO", String.class, String.class);
      HttpRequest httpRequest = processor.createRequest(method, "3", "5");

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=attachIso&id=3&virtualmachineid=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testDetachISO() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("detachISO", String.class);
      HttpRequest httpRequest = processor.createRequest(method, 3);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=detachIso&virtualmachineid=3 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testUpdateISO() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("updateISO", String.class, UpdateISOOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=updateIso&id=3 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testUpdateISOOptions() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("updateISO", String.class, UpdateISOOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3, UpdateISOOptions.Builder.bootable(true).displayText("robert").format("format").name("bob").osTypeId("9").passwordEnabled(true));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=updateIso&id=3&bootable=true&displaytext=robert&format=format&name=bob&ostypeid=9&passwordenabled=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testDeleteISO() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("deleteISO", String.class, DeleteISOOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=deleteIso&id=3 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testDeleteISOOptions() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("deleteISO", String.class, DeleteISOOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3, DeleteISOOptions.Builder.zoneId("5"));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=deleteIso&id=3&zoneid=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testCopyISO() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("copyISO", String.class, String.class, String.class);
      HttpRequest httpRequest = processor.createRequest(method, 3, 5, 7);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=copyIso&id=3&destzoneid=7&sourcezoneid=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testUpdateISOPermissions() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("updateISOPermissions", String.class, UpdateISOPermissionsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=updateIsoPermissions&id=3 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testUpdateISOPermissionsOptions() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("updateISOPermissions", String.class, UpdateISOPermissionsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3, UpdateISOPermissionsOptions.Builder.accounts(ImmutableSet.<String>of("fred", "bob")).isExtractable(true).isFeatured(true).isPublic(true).operation(PermissionOperation.add));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=updateIsoPermissions&id=3&accounts=fred,bob&isextractable=true&isfeatured=true&ispublic=true&op=add HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testListISOPermissions() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("listISOPermissions", String.class, AccountInDomainOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=listIsoPermissions&listAll=true&id=3 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testListISOPermissionsOptions() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("listISOPermissions", String.class, AccountInDomainOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3, AccountInDomainOptions.Builder.accountInDomain("fred", "5"));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=listIsoPermissions&listAll=true&id=3&account=fred&domainid=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testExtractISO() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("extractISO", String.class, ExtractMode.class, String.class, ExtractISOOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3, ExtractMode.HTTP_DOWNLOAD, 5);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=extractIso&id=3&zoneid=5&mode=HTTP_DOWNLOAD HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testExtractISOOptions() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("extractISO", String.class, ExtractMode.class, String.class, ExtractISOOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3, ExtractMode.HTTP_DOWNLOAD, 5, ExtractISOOptions.Builder.url("http://example.com/"));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=extractIso&id=3&zoneid=5&mode=HTTP_DOWNLOAD&url=http%3A//example.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

@Override
   protected TypeLiteral<RestAnnotationProcessor<ISOAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<ISOAsyncClient>>() {
      };
   }

}
