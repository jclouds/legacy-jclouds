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

import com.google.common.collect.ImmutableSet;
import com.google.inject.TypeLiteral;
import org.jclouds.cloudstack.domain.ExtractMode;
import org.jclouds.cloudstack.domain.Iso;
import org.jclouds.cloudstack.domain.PermissionOperation;
import org.jclouds.cloudstack.options.AccountInDomainOptions;
import org.jclouds.cloudstack.options.DeleteIsoOptions;
import org.jclouds.cloudstack.options.ExtractIsoOptions;
import org.jclouds.cloudstack.options.ListIsosOptions;
import org.jclouds.cloudstack.options.RegisterIsoOptions;
import org.jclouds.cloudstack.options.UpdateIsoOptions;
import org.jclouds.cloudstack.options.UpdateIsoPermissionsOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
/**
 * Tests the behaviour of IsoAsyncClient.
 * 
 * @see IsoAsyncClient
 * @author Richard Downer
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "IsoAsyncClientTest")
public class IsoAsyncClientTest extends BaseCloudStackAsyncClientTest<IsoAsyncClient> {

   public void testAttachIso() throws NoSuchMethodException {
      Method method = IsoAsyncClient.class.getMethod("attachIso", long.class, long.class);
      HttpRequest httpRequest = processor.createRequest(method, 3, 5);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=attachIso&id=3&virtualmachineid=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testDetachIso() throws NoSuchMethodException {
      Method method = IsoAsyncClient.class.getMethod("detachIso", long.class);
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

   public void testGetIso() throws NoSuchMethodException {
      Method method = IsoAsyncClient.class.getMethod("getIso", long.class);
      HttpRequest httpRequest = processor.createRequest(method, 3);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=listIsos&id=3 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testListIsos() throws NoSuchMethodException {
      Method method = IsoAsyncClient.class.getMethod("listIsos", ListIsosOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=listIsos HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testListIsosOptions() throws NoSuchMethodException {
      Method method = IsoAsyncClient.class.getMethod("listIsos", ListIsosOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, ListIsosOptions.Builder.accountInDomain("fred", 5).bootable(true).hypervisor("xen").id(3).isoFilter(Iso.IsoFilter.featured).isPublic(true).isReady(true).keyword("bob").name("bob's iso").zoneId(7));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=listIsos&account=fred&domainid=5&bootable=true&hypervisor=xen&id=3&isofilter=featured&ispublic=true&isready=true&keyword=bob&name=bob%27s%20iso&zoneid=7 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testRegisterIso() throws NoSuchMethodException {
      Method method = IsoAsyncClient.class.getMethod("registerIso", String.class, String.class, String.class, long.class, RegisterIsoOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, "bob's iso", "bob's copy of linux", "http://example.com/", 9);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=registerIso&name=bob%27s%20iso&url=http%3A//example.com/&displaytext=bob%27s%20copy%20of%20linux&zoneid=9 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testRegisterIsoOptions() throws NoSuchMethodException {
      Method method = IsoAsyncClient.class.getMethod("registerIso", String.class, String.class, String.class, long.class, RegisterIsoOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, "bob's iso", "bob's copy of linux", "http://example.com/", 9, RegisterIsoOptions.Builder.accountInDomain("fred", 5).bootable(true).isExtractable(true).isFeatured(true).isPublic(true).osTypeId(7));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=registerIso&name=bob%27s%20iso&url=http%3A//example.com/&displaytext=bob%27s%20copy%20of%20linux&zoneid=9&account=fred&domainid=5&bootable=true&isextractable=true&isfeatured=true&ispublic=true&ostypeid=7 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testUpdateIso() throws NoSuchMethodException {
      Method method = IsoAsyncClient.class.getMethod("updateIso", long.class, UpdateIsoOptions[].class);
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

   public void testUpdateIsoOptions() throws NoSuchMethodException {
      Method method = IsoAsyncClient.class.getMethod("updateIso", long.class, UpdateIsoOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3, UpdateIsoOptions.Builder.bootable(true).displayText("robert").format("format").name("bob").osTypeId(9).passwordEnabled(true));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=updateIso&id=3&bootable=true&displaytext=robert&format=format&name=bob&ostypeid=9&passwordenabled=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testDeleteIso() throws NoSuchMethodException {
      Method method = IsoAsyncClient.class.getMethod("deleteIso", long.class, DeleteIsoOptions[].class);
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

   public void testDeleteIsoOptions() throws NoSuchMethodException {
      Method method = IsoAsyncClient.class.getMethod("deleteIso", long.class, DeleteIsoOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3, DeleteIsoOptions.Builder.zoneId(5));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=deleteIso&id=3&zoneid=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testCopyIso() throws NoSuchMethodException {
      Method method = IsoAsyncClient.class.getMethod("copyIso", long.class, long.class, long.class);
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

   public void testUpdateIsoPermissions() throws NoSuchMethodException {
      Method method = IsoAsyncClient.class.getMethod("updateIsoPermissions", long.class, UpdateIsoPermissionsOptions[].class);
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

   public void testUpdateIsoPermissionsOptions() throws NoSuchMethodException {
      Method method = IsoAsyncClient.class.getMethod("updateIsoPermissions", long.class, UpdateIsoPermissionsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3, UpdateIsoPermissionsOptions.Builder.accounts(ImmutableSet.<String>of("fred", "bob")).isExtractable(true).isFeatured(true).isPublic(true).operation(PermissionOperation.add));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=updateIsoPermissions&id=3&accounts=fred,bob&isextractable=true&isfeatured=true&ispublic=true&op=add HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testListIsoPermissions() throws NoSuchMethodException {
      Method method = IsoAsyncClient.class.getMethod("listIsoPermissions", long.class, AccountInDomainOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=listIsoPermissions&id=3 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testListIsoPermissionsOptions() throws NoSuchMethodException {
      Method method = IsoAsyncClient.class.getMethod("listIsoPermissions", long.class, AccountInDomainOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3, AccountInDomainOptions.Builder.accountInDomain("fred", 5));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=listIsoPermissions&id=3&account=fred&domainid=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testExtractIso() throws NoSuchMethodException {
      Method method = IsoAsyncClient.class.getMethod("extractIso", long.class, ExtractMode.class, long.class, ExtractIsoOptions[].class);
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

   public void testExtractIsoOptions() throws NoSuchMethodException {
      Method method = IsoAsyncClient.class.getMethod("extractIso", long.class, ExtractMode.class, long.class, ExtractIsoOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3, ExtractMode.HTTP_DOWNLOAD, 5, ExtractIsoOptions.Builder.url("http://example.com/"));

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
   protected TypeLiteral<RestAnnotationProcessor<IsoAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<IsoAsyncClient>>() {
      };
   }

}
