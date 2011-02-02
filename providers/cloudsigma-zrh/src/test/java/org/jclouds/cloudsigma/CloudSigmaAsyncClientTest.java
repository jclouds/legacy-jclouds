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

package org.jclouds.cloudsigma;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.jclouds.cloudsigma.binders.BindServerToPlainTextStringTest;
import org.jclouds.cloudsigma.domain.CreateDriveRequest;
import org.jclouds.cloudsigma.domain.Drive;
import org.jclouds.cloudsigma.domain.DriveData;
import org.jclouds.cloudsigma.domain.Server;
import org.jclouds.cloudsigma.functions.KeyValuesDelimitedByBlankLinesToDriveInfo;
import org.jclouds.cloudsigma.functions.KeyValuesDelimitedByBlankLinesToProfileInfo;
import org.jclouds.cloudsigma.functions.KeyValuesDelimitedByBlankLinesToServerInfo;
import org.jclouds.cloudsigma.functions.KeyValuesDelimitedByBlankLinesToStaticIPInfo;
import org.jclouds.cloudsigma.functions.KeyValuesDelimitedByBlankLinesToVLANInfo;
import org.jclouds.cloudsigma.functions.ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet;
import org.jclouds.cloudsigma.functions.ListOfKeyValuesDelimitedByBlankLinesToServerInfoSet;
import org.jclouds.cloudsigma.functions.ListOfKeyValuesDelimitedByBlankLinesToStaticIPInfoSet;
import org.jclouds.cloudsigma.functions.ListOfKeyValuesDelimitedByBlankLinesToVLANInfoSet;
import org.jclouds.cloudsigma.functions.SplitNewlines;
import org.jclouds.cloudsigma.functions.SplitNewlinesAndReturnSecondField;
import org.jclouds.cloudsigma.options.CloneDriveOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code CloudSigmaAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "CloudSigmaAsyncClientTest")
public class CloudSigmaAsyncClientTest extends RestClientTest<CloudSigmaAsyncClient> {

   public void testGetProfileInfo() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("getProfileInfo");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/profile/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToProfileInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListStandardDrives() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("listStandardDrives");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/standard/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, SplitNewlines.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testListStandardCds() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("listStandardCds");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/standard/cd/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, SplitNewlines.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testListStandardImages() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("listStandardImages");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/standard/img/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, SplitNewlines.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testListDriveInfo() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("listDriveInfo");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testGetDriveInfo() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("getDriveInfo", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/uuid/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToDriveInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateDrive() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("createDrive", Drive.class);
      HttpRequest httpRequest = processor.createRequest(method,
            new CreateDriveRequest.Builder().name("foo").use(ImmutableList.of("production", "candy")).size(10000l)
                  .build());

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/drives/create HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "name foo\nsize 10000\nuse production candy", "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToDriveInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCloneDrive() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("cloneDrive", String.class, String.class,
            CloneDriveOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, "sourceid", "newname");

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/drives/sourceid/clone HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "name newname", "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToDriveInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testCloneDriveOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("cloneDrive", String.class, String.class,
            CloneDriveOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, "sourceid", "newname",
            new CloneDriveOptions().size(1024l));

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/drives/sourceid/clone HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "name newname\nsize 1024", "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToDriveInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testSetDriveData() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("setDriveData", String.class, DriveData.class);
      HttpRequest httpRequest = processor.createRequest(method, "100", new DriveData.Builder().name("foo").size(10000l)
            .use(ImmutableList.of("production", "candy")).build());

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/drives/100/set HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "name foo\nsize 10000\nuse production candy", "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToDriveInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testListServers() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("listServers");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/servers/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/servers/list HTTP/1.1");
      // for example, using basic authentication, we should get "only one"
      // header
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\nAuthorization: Basic Zm9vOmJhcg==\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // TODO: insert expected response class, which probably extends ParseJson
      assertResponseParserClassEquals(method, httpRequest, SplitNewlines.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListServerInfo() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("listServerInfo");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/servers/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ListOfKeyValuesDelimitedByBlankLinesToServerInfoSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testGetServerInfo() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("getServerInfo", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/servers/uuid/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToServerInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("createServer", Server.class);
      HttpRequest httpRequest = processor.createRequest(method, BindServerToPlainTextStringTest.SERVER);

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/servers/create HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, BindServerToPlainTextStringTest.CREATED_SERVER, "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToServerInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testSetServerConfiguration() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("setServerConfiguration", String.class, Server.class);
      HttpRequest httpRequest = processor.createRequest(method, "100", BindServerToPlainTextStringTest.SERVER);

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/servers/100/set HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, BindServerToPlainTextStringTest.CREATED_SERVER, "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToServerInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDestroyServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("destroyServer", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/servers/uuid/destroy HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testStartServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("startServer", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/servers/uuid/start HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testStopServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("stopServer", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/servers/uuid/stop HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testShutdownServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("shutdownServer", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/servers/uuid/shutdown HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testResetServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("resetServer", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/servers/uuid/reset HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testListDrives() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("listDrives");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/list HTTP/1.1");
      // for example, using basic authentication, we should get "only one"
      // header
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\nAuthorization: Basic Zm9vOmJhcg==\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // TODO: insert expected response class, which probably extends ParseJson
      assertResponseParserClassEquals(method, httpRequest, SplitNewlines.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testDestroyDrive() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("destroyDrive", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/drives/uuid/destroy HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListVLANs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("listVLANs");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/resources/vlan/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/resources/vlan/list HTTP/1.1");
      // for example, using basic authentication, we should get "only one"
      // header
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\nAuthorization: Basic Zm9vOmJhcg==\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // TODO: insert expected response class, which probably extends ParseJson
      assertResponseParserClassEquals(method, httpRequest, SplitNewlinesAndReturnSecondField.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListVLANInfo() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("listVLANInfo");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/resources/vlan/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ListOfKeyValuesDelimitedByBlankLinesToVLANInfoSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testGetVLANInfo() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("getVLANInfo", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/resources/vlan/uuid/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToVLANInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateVLAN() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("createVLAN", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "poohbear");

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/resources/vlan/create HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "name poohbear\n", "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToVLANInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testRenameVLAN() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("renameVLAN", String.class, String.class);
      HttpRequest httpRequest = processor.createRequest(method, "100", "poohbear");

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/resources/vlan/100/set HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "name poohbear\n", "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToVLANInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testDestroyVLAN() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("destroyVLAN", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/resources/vlan/uuid/destroy HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListStaticIPs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("listStaticIPs");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/resources/ip/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/resources/ip/list HTTP/1.1");
      // for example, using basic authentication, we should get "only one"
      // header
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\nAuthorization: Basic Zm9vOmJhcg==\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // TODO: insert expected response class, which probably extends ParseJson
      assertResponseParserClassEquals(method, httpRequest, SplitNewlinesAndReturnSecondField.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListStaticIPInfo() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("listStaticIPInfo");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/resources/ip/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ListOfKeyValuesDelimitedByBlankLinesToStaticIPInfoSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testGetStaticIPInfo() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("getStaticIPInfo", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/resources/ip/uuid/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToStaticIPInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateStaticIP() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("createStaticIP");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/resources/ip/create HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToStaticIPInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDestroyStaticIP() throws SecurityException, NoSuchMethodException, IOException {
      Method method = CloudSigmaAsyncClient.class.getMethod("destroyStaticIP", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/resources/ip/uuid/destroy HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<CloudSigmaAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<CloudSigmaAsyncClient>>() {
      };
   }

   @Override
   public RestContextSpec<CloudSigmaClient, CloudSigmaAsyncClient> createContextSpec() {
      return new RestContextFactory().createContextSpec("cloudsigma-zrh", "foo", "bar", new Properties());
   }
}
