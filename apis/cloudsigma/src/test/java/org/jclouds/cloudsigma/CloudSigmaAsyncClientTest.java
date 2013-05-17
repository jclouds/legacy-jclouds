/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudsigma;

import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.apis.ApiMetadata;
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
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code CloudSigmaAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "CloudSigmaAsyncClientTest")
public class CloudSigmaAsyncClientTest extends BaseAsyncClientTest<CloudSigmaAsyncClient> {

   public void testGetProfileInfo() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "getProfileInfo");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/profile/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToProfileInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListStandardDrives() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "listStandardDrives");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/standard/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, SplitNewlines.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testListStandardCds() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "listStandardCds");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/standard/cd/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, SplitNewlines.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testListStandardImages() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "listStandardImages");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/standard/img/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, SplitNewlines.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testListDriveInfo() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "listDriveInfo");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testGetDriveInfo() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "getDriveInfo", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/uuid/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToDriveInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateDrive() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "createDrive", Drive.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(
            new CreateDriveRequest.Builder().name("foo").use(ImmutableList.of("production", "candy")).size(10000l)
                  .build()));

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/drives/create HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "name foo\nsize 10000\nuse production candy", "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToDriveInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCloneDrive() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "cloneDrive", String.class, String.class,
            CloneDriveOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("sourceid", "newname"));

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/drives/sourceid/clone HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "name newname", "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToDriveInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testCloneDriveOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "cloneDrive", String.class, String.class,
            CloneDriveOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("sourceid", "newname",
            new CloneDriveOptions().size(1024l)));

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/drives/sourceid/clone HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "name newname\nsize 1024", "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToDriveInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testSetDriveData() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "setDriveData", String.class, DriveData.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("100", new DriveData.Builder().name("foo").size(10000l)
            .use(ImmutableList.of("production", "candy")).build()));

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/drives/100/set HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "name foo\nsize 10000\nuse production candy", "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToDriveInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testListServers() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "listServers");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/servers/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/servers/list HTTP/1.1");
      // for example, using basic authentication, we should get "only one"
      // header
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\nAuthorization: Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // TODO: insert expected response class, which probably extends ParseJson
      assertResponseParserClassEquals(method, httpRequest, SplitNewlines.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListServerInfo() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "listServerInfo");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/servers/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ListOfKeyValuesDelimitedByBlankLinesToServerInfoSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testGetServerInfo() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "getServerInfo", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/servers/uuid/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToServerInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateServer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "createServer", Server.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(BindServerToPlainTextStringTest.SERVER));

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/servers/create HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, BindServerToPlainTextStringTest.CREATED_SERVER, "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToServerInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testSetServerConfiguration() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "setServerConfiguration", String.class, Server.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("100", BindServerToPlainTextStringTest.SERVER));

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/servers/100/set HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, BindServerToPlainTextStringTest.CREATED_SERVER, "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToServerInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDestroyServer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "destroyServer", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/servers/uuid/destroy HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testStartServer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "startServer", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/servers/uuid/start HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testStopServer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "stopServer", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/servers/uuid/stop HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testShutdownServer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "shutdownServer", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/servers/uuid/shutdown HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testResetServer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "resetServer", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/servers/uuid/reset HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testListDrives() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "listDrives");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/list HTTP/1.1");
      // for example, using basic authentication, we should get "only one"
      // header
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\nAuthorization: Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // TODO: insert expected response class, which probably extends ParseJson
      assertResponseParserClassEquals(method, httpRequest, SplitNewlines.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testDestroyDrive() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "destroyDrive", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/drives/uuid/destroy HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListVLANs() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "listVLANs");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/resources/vlan/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/resources/vlan/list HTTP/1.1");
      // for example, using basic authentication, we should get "only one"
      // header
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\nAuthorization: Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // TODO: insert expected response class, which probably extends ParseJson
      assertResponseParserClassEquals(method, httpRequest, SplitNewlinesAndReturnSecondField.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListVLANInfo() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "listVLANInfo");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/resources/vlan/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ListOfKeyValuesDelimitedByBlankLinesToVLANInfoSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testGetVLANInfo() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "getVLANInfo", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/resources/vlan/uuid/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToVLANInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateVLAN() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "createVLAN", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("poohbear"));

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/resources/vlan/create HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "name poohbear\n", "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToVLANInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testRenameVLAN() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "renameVLAN", String.class, String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("100", "poohbear"));

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/resources/vlan/100/set HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "name poohbear\n", "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToVLANInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testDestroyVLAN() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "destroyVLAN", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/resources/vlan/uuid/destroy HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListStaticIPs() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "listStaticIPs");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/resources/ip/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/resources/ip/list HTTP/1.1");
      // for example, using basic authentication, we should get "only one"
      // header
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\nAuthorization: Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // TODO: insert expected response class, which probably extends ParseJson
      assertResponseParserClassEquals(method, httpRequest, SplitNewlinesAndReturnSecondField.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListStaticIPInfo() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "listStaticIPInfo");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/resources/ip/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ListOfKeyValuesDelimitedByBlankLinesToStaticIPInfoSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testGetStaticIPInfo() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "getStaticIPInfo", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/resources/ip/uuid/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToStaticIPInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateStaticIP() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "createStaticIP");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest, "POST https://api.cloudsigma.com/resources/ip/create HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToStaticIPInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDestroyStaticIP() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CloudSigmaAsyncClient.class, "destroyStaticIP", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "GET https://api.cloudsigma.com/resources/ip/uuid/destroy HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   protected ApiMetadata createApiMetadata() {
      return new CloudSigmaApiMetadata();
   }

}
