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
import org.jclouds.cloudstack.domain.ISO;
import org.jclouds.cloudstack.domain.PermissionOperation;
import org.jclouds.cloudstack.options.AccountInDomainOptions;
import org.jclouds.cloudstack.options.DeleteISOOptions;
import org.jclouds.cloudstack.options.ExtractISOOptions;
import org.jclouds.cloudstack.options.ListISOsOptions;
import org.jclouds.cloudstack.options.RegisterISOOptions;
import org.jclouds.cloudstack.options.UpdateISOOptions;
import org.jclouds.cloudstack.options.UpdateISOPermissionsOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
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
      Method method = ISOAsyncClient.class.getMethod("attachISO", long.class, long.class);
      HttpRequest httpRequest = processor.createRequest(method, 3, 5);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=attachISO&id=3&virtualmachineid=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testDetachISO() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("detachISO", long.class);
      HttpRequest httpRequest = processor.createRequest(method, 3);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=detachISO&virtualmachineid=3 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testGetISO() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("getISO", long.class);
      HttpRequest httpRequest = processor.createRequest(method, 3);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=listISOs&id=3 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testListISOs() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("listISOs", ListISOsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=listISOs HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testListISOsOptions() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("listISOs", ListISOsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, ListISOsOptions.Builder.accountInDomain("fred", 5).bootable(true).hypervisor("xen").id(3).isoFilter(ISO.ISOFilter.featured).isPublic(true).isReady(true).keyword("bob").name("bob's iso").zoneId(7));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=listISOs&account=fred&domainid=5&bootable=true&hypervisor=xen&id=3&isofilter=featured&ispublic=true&isready=true&keyword=bob&name=bob%27s%20iso&zoneid=7 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testRegisterISO() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("registerISO", String.class, String.class, String.class, long.class, RegisterISOOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, "bob's iso", "bob's copy of linux", "http://example.com/", 9);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=registerISO&name=bob%27s%20iso&url=http%3A//example.com/&displaytext=bob%27s%20copy%20of%20linux&zoneid=9 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testRegisterISOOptions() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("registerISO", String.class, String.class, String.class, long.class, RegisterISOOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, "bob's iso", "bob's copy of linux", "http://example.com/", 9, RegisterISOOptions.Builder.accountInDomain("fred", 5).bootable(true).isExtractable(true).isFeatured(true).isPublic(true).osTypeId(7));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=registerISO&name=bob%27s%20iso&url=http%3A//example.com/&displaytext=bob%27s%20copy%20of%20linux&zoneid=9&account=fred&domainid=5&bootable=true&isextractable=true&isfeatured=true&ispublic=true&ostypeid=7 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testUpdateISO() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("updateISO", long.class, UpdateISOOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=updateISO&id=3 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testUpdateISOOptions() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("updateISO", long.class, UpdateISOOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3, UpdateISOOptions.Builder.bootable(true).displayText("robert").format("format").name("bob").osTypeId(9).passwordEnabled(true));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=updateISO&id=3&bootable=true&displaytext=robert&format=format&name=bob&ostypeid=9&passwordenabled=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testDeleteISO() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("deleteISO", long.class, DeleteISOOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=deleteISO&id=3 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testDeleteISOOptions() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("deleteISO", long.class, DeleteISOOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3, DeleteISOOptions.Builder.zoneId(5));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=deleteISO&id=3&zoneid=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testCopyISO() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("copyISO", long.class, long.class, long.class);
      HttpRequest httpRequest = processor.createRequest(method, 3, 5, 7);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=copyISO&id=3&destzoneid=7&sourcezoneid=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testUpdateISOPermissions() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("updateISOPermissions", long.class, UpdateISOPermissionsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=updateISOPermissions&id=3 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testUpdateISOPermissionsOptions() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("updateISOPermissions", long.class, UpdateISOPermissionsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3, UpdateISOPermissionsOptions.Builder.accounts(ImmutableSet.<String>of("fred", "bob")).isExtractable(true).isFeatured(true).isPublic(true).operation(PermissionOperation.add));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=updateISOPermissions&id=3&accounts=fred,bob&isextractable=true&isfeatured=true&ispublic=true&op=add HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testListISOPermissions() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("listISOPermissions", long.class, AccountInDomainOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=listISOPermissions&id=3 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testListISOPermissionsOptions() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("listISOPermissions", long.class, AccountInDomainOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3, AccountInDomainOptions.Builder.accountInDomain("fred", 5));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=listISOPermissions&id=3&account=fred&domainid=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testExtractISO() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("extractISO", long.class, ExtractMode.class, long.class, ExtractISOOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3, ExtractMode.HTTP_DOWNLOAD, 5);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=extractISO&id=3&zoneid=5&mode=HTTP_DOWNLOAD HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testExtractISOOptions() throws NoSuchMethodException {
      Method method = ISOAsyncClient.class.getMethod("extractISO", long.class, ExtractMode.class, long.class, ExtractISOOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 3, ExtractMode.HTTP_DOWNLOAD, 5, ExtractISOOptions.Builder.url("http://example.com/"));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=extractISO&id=3&zoneid=5&mode=HTTP_DOWNLOAD&url=http%3A//example.com/ HTTP/1.1");
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
