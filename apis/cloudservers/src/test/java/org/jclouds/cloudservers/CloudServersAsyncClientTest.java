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
package org.jclouds.cloudservers;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.cloudservers.options.CreateServerOptions.Builder.withFile;
import static org.jclouds.cloudservers.options.CreateServerOptions.Builder.withMetadata;
import static org.jclouds.cloudservers.options.CreateServerOptions.Builder.withSharedIpGroup;
import static org.jclouds.cloudservers.options.CreateSharedIpGroupOptions.Builder.withServer;
import static org.jclouds.cloudservers.options.ListOptions.Builder.changesSince;
import static org.jclouds.cloudservers.options.ListOptions.Builder.withDetails;
import static org.jclouds.cloudservers.options.RebuildServerOptions.Builder.withImage;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Properties;

import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.cloudservers.config.CloudServersRestClientModule;
import org.jclouds.cloudservers.domain.BackupSchedule;
import org.jclouds.cloudservers.domain.DailyBackup;
import org.jclouds.cloudservers.domain.RebootType;
import org.jclouds.cloudservers.domain.WeeklyBackup;
import org.jclouds.cloudservers.options.CreateServerOptions;
import org.jclouds.cloudservers.options.CreateSharedIpGroupOptions;
import org.jclouds.cloudservers.options.ListOptions;
import org.jclouds.cloudservers.options.RebuildServerOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.openstack.filters.AddTimestampQuery;
import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v1_1.config.AuthenticationServiceModule.GetAuth;
import org.jclouds.openstack.keystone.v1_1.domain.Auth;
import org.jclouds.openstack.keystone.v1_1.parse.ParseAuthTest;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Module;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code CloudServersAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", singleThreaded = true, testName = "CloudServersAsyncClientTest")
public class CloudServersAsyncClientTest extends BaseAsyncClientTest<CloudServersAsyncClient> {
   private static final Class<? extends ListOptions[]> listOptionsVarargsClass = new ListOptions[] {}.getClass();
   private static final Class<? extends CreateServerOptions[]> createServerOptionsVarargsClass = new CreateServerOptions[] {}
         .getClass();

   public void testCreateServer() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("createServer", String.class, int.class, int.class,
            createServerOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, "ralphie", 2, 1);

      assertRequestLineEquals(request, "POST https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, "{\"server\":{\"name\":\"ralphie\",\"imageId\":2,\"flavorId\":1}}",
            "application/json", false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);

   }

   public void testCreateServerWithIpGroup() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("createServer", String.class, int.class, int.class,
            createServerOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, "ralphie", 2, 1, withSharedIpGroup(2));

      assertRequestLineEquals(request, "POST https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request,
            "{\"server\":{\"name\":\"ralphie\",\"imageId\":2,\"flavorId\":1,\"sharedIpGroupId\":2}}",
            "application/json", false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreateServerWithFile() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("createServer", String.class, int.class, int.class,
            createServerOptionsVarargsClass);
      HttpRequest request = processor
            .createRequest(method, "ralphie", 2, 1, withFile("/etc/jclouds", "foo".getBytes()));

      assertRequestLineEquals(request, "POST https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(
            request,
            "{\"server\":{\"name\":\"ralphie\",\"imageId\":2,\"flavorId\":1,\"personality\":[{\"path\":\"/etc/jclouds\",\"contents\":\"Zm9v\"}]}}",
            "application/json", false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);

   }

   public void testCreateServerWithMetadata() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("createServer", String.class, int.class, int.class,
            createServerOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, "ralphie", 2, 1,
            withMetadata(ImmutableMap.of("foo", "bar")));

      assertRequestLineEquals(request, "POST https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request,
            "{\"server\":{\"name\":\"ralphie\",\"imageId\":2,\"flavorId\":1,\"metadata\":{\"foo\":\"bar\"}}}",
            "application/json", false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);

   }

   public void testCreateServerWithIpGroupAndSharedIp() throws IOException, SecurityException, NoSuchMethodException,
         UnknownHostException {
      Method method = CloudServersAsyncClient.class.getMethod("createServer", String.class, int.class, int.class,
            createServerOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, "ralphie", 2, 1,
            withSharedIpGroup(2).withSharedIp("127.0.0.1"));

      assertRequestLineEquals(request, "POST https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(
            request,
            "{\"server\":{\"name\":\"ralphie\",\"imageId\":2,\"flavorId\":1,\"sharedIpGroupId\":2,\"addresses\":{\"public\":[\"127.0.0.1\"]}}}",
            "application/json", false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteImage() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("deleteImage", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "DELETE https://lon.servers.api.rackspacecloud.com/v1.0/10001786/images/2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testLimits() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("getLimits");
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/limits?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListServers() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listServers", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   Date now = new Date(10000000l);

   public void testListServersOptions() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listServers", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, changesSince(now).maxResults(1).startAt(2));

      assertRequestLineEquals(request,
            "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers?format=json&changes-since=10000&limit=1&offset=2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListServersDetail() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listServers", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, withDetails());

      assertRequestLineEquals(request, "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/detail?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetServer() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("getServer", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/2?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListFlavors() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listFlavors", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/flavors?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListFlavorsOptions() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listFlavors", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, changesSince(now).maxResults(1).startAt(2));

      assertRequestLineEquals(request,
            "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/flavors?format=json&changes-since=10000&limit=1&offset=2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListFlavorsDetail() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listFlavors", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, withDetails());

      assertRequestLineEquals(request, "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/flavors/detail?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListFlavorsDetailOptions() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listFlavors", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, withDetails().changesSince(now).maxResults(1).startAt(2));

      assertRequestLineEquals(request,
            "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/flavors/detail?format=json&changes-since=10000&limit=1&offset=2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetFlavor() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("getFlavor", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/flavors/2?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListImages() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listImages", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/images?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListImagesDetail() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listImages", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, withDetails());

      assertRequestLineEquals(request, "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/images/detail?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListImagesOptions() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listImages", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, changesSince(now).maxResults(1).startAt(2));

      assertRequestLineEquals(request,
            "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/images?format=json&changes-since=10000&limit=1&offset=2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListImagesDetailOptions() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listImages", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, withDetails().changesSince(now).maxResults(1).startAt(2));

      assertRequestLineEquals(request,
            "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/images/detail?format=json&changes-since=10000&limit=1&offset=2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetImage() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("getImage", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/images/2?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDeleteServer() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("deleteServer", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "DELETE https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testShareIpNoConfig() throws IOException, SecurityException, NoSuchMethodException, UnknownHostException {
      Method method = CloudServersAsyncClient.class.getMethod("shareIp", String.class, int.class, int.class,
            boolean.class);
      HttpRequest request = processor.createRequest(method, "127.0.0.1", 2, 3, false);

      assertRequestLineEquals(request, "PUT https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/2/ips/public/127.0.0.1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "{\"shareIp\":{\"sharedIpGroupId\":3,\"configureServer\":false}}",
            MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);

   }

   public void testShareIpConfig() throws IOException, SecurityException, NoSuchMethodException, UnknownHostException {
      Method method = CloudServersAsyncClient.class.getMethod("shareIp", String.class, int.class, int.class,
            boolean.class);
      HttpRequest request = processor.createRequest(method, "127.0.0.1", 2, 3, true);

      assertRequestLineEquals(request, "PUT https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/2/ips/public/127.0.0.1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "{\"shareIp\":{\"sharedIpGroupId\":3,\"configureServer\":true}}",
            MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);

   }

   public void testUnshareIpNoConfig() throws IOException, SecurityException, NoSuchMethodException,
         UnknownHostException {
      Method method = CloudServersAsyncClient.class.getMethod("unshareIp", String.class, int.class);
      HttpRequest request = processor.createRequest(method, "127.0.0.1", 2, 3, false);

      assertRequestLineEquals(request, "DELETE https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/2/ips/public/127.0.0.1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(request);

   }

   public void testReplaceBackupSchedule() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("replaceBackupSchedule", int.class, BackupSchedule.class);
      HttpRequest request = processor.createRequest(method, 2, BackupSchedule.builder().weekly(WeeklyBackup.MONDAY)
            .daily(DailyBackup.H_0800_1000).enabled(true).build());

      assertRequestLineEquals(request, "POST https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/2/backup_schedule HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request,
            "{\"backupSchedule\":{\"daily\":\"H_0800_1000\",\"enabled\":true,\"weekly\":\"MONDAY\"}}",
            MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);

   }

   public void testDeleteBackupSchedule() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("deleteBackupSchedule", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "DELETE https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/2/backup_schedule HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseOnNotFoundOr404.class);

      checkFilters(request);

   }

   public void testChangeAdminPass() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("changeAdminPass", int.class, String.class);
      HttpRequest request = processor.createRequest(method, 2, "foo");

      assertRequestLineEquals(request, "PUT https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "{\"server\":{\"adminPass\":\"foo\"}}", MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);

   }

   public void testChangeServerName() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("renameServer", int.class, String.class);
      HttpRequest request = processor.createRequest(method, 2, "foo");

      assertRequestLineEquals(request, "PUT https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "{\"server\":{\"name\":\"foo\"}}", MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);

   }

   public void testListSharedIpGroups() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listSharedIpGroups", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/shared_ip_groups?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListSharedIpGroupsOptions() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listSharedIpGroups", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, changesSince(now).maxResults(1).startAt(2));

      assertRequestLineEquals(request,
            "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/shared_ip_groups?format=json&changes-since=10000&limit=1&offset=2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListSharedIpGroupsDetail() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listSharedIpGroups", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, withDetails());

      assertRequestLineEquals(request, "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/shared_ip_groups/detail?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListSharedIpGroupsDetailOptions() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listSharedIpGroups", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, withDetails().changesSince(now).maxResults(1).startAt(2));

      assertRequestLineEquals(request,
            "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/shared_ip_groups/detail?format=json&changes-since=10000&limit=1&offset=2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetSharedIpGroup() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("getSharedIpGroup", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/shared_ip_groups/2?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   private static final Class<? extends CreateSharedIpGroupOptions[]> createSharedIpGroupOptionsVarargsClass = new CreateSharedIpGroupOptions[] {}
         .getClass();

   public void testCreateSharedIpGroup() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("createSharedIpGroup", String.class,
            createSharedIpGroupOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, "ralphie");

      assertRequestLineEquals(request, "POST https://lon.servers.api.rackspacecloud.com/v1.0/10001786/shared_ip_groups?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, "{\"sharedIpGroup\":{\"name\":\"ralphie\"}}", MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);

   }

   public void testCreateSharedIpGroupWithIpGroup() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("createSharedIpGroup", String.class,
            createSharedIpGroupOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, "ralphie", withServer(2));

      assertRequestLineEquals(request, "POST https://lon.servers.api.rackspacecloud.com/v1.0/10001786/shared_ip_groups?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, "{\"sharedIpGroup\":{\"name\":\"ralphie\",\"server\":2}}",
            MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteSharedIpGroup() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("deleteSharedIpGroup", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "DELETE https://lon.servers.api.rackspacecloud.com/v1.0/10001786/shared_ip_groups/2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListAddresses() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("getAddresses", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/2/ips?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testListPublicAddresses() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listPublicAddresses", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/2/ips/public?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListPrivateAddresses() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listPrivateAddresses", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/2/ips/private?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListBackupSchedule() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("getBackupSchedule", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "GET https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/2/backup_schedule?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

   public void testCreateImageWithIpGroup() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("createImageFromServer", String.class, int.class);
      HttpRequest request = processor.createRequest(method, "ralphie", 2);

      assertRequestLineEquals(request, "POST https://lon.servers.api.rackspacecloud.com/v1.0/10001786/images?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, "{\"image\":{\"serverId\":2,\"name\":\"ralphie\"}}", MediaType.APPLICATION_JSON,
            false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);

   }

   private static final Class<? extends RebuildServerOptions[]> rebuildServerOptionsVarargsClass = new RebuildServerOptions[] {}
         .getClass();

   public void testRebuildServer() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("rebuildServer", int.class,
            rebuildServerOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, 3);

      assertRequestLineEquals(request, "POST https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/3/action?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "{\"rebuild\":{}}", MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testRebuildServerWithImage() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("rebuildServer", int.class,
            rebuildServerOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, 3, withImage(2));

      assertRequestLineEquals(request, "POST https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/3/action?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "{\"rebuild\":{\"imageId\":2}}", MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testReboot() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("rebootServer", int.class, RebootType.class);
      HttpRequest request = processor.createRequest(method, 2, RebootType.HARD);

      assertRequestLineEquals(request, "POST https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/2/action?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "{\"reboot\":{\"type\":\"HARD\"}}", MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testResize() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("resizeServer", int.class, int.class);
      HttpRequest request = processor.createRequest(method, 2, 3);

      assertRequestLineEquals(request, "POST https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/2/action?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "{\"resize\":{\"flavorId\":3}}", MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);

   }

   public void testConfirmResize() throws IOException, IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("confirmResizeServer", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "POST https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/2/action?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "{\"confirmResize\":null}", MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testRevertResize() throws IOException, SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("revertResizeServer", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "POST https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/2/action?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "{\"revertResize\":null}", MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 2);
      assertEquals(request.getFilters().get(0).getClass(), AuthenticateRequest.class);
      assertEquals(request.getFilters().get(1).getClass(), AddTimestampQuery.class);

   }

   @Override
   protected Module createModule() {
      return new TestCloudServersRestClientModule();
   }

   @ConfiguresRestClient
      protected static class TestCloudServersRestClientModule extends CloudServersRestClientModule {

      @Provides
      @Singleton
      GetAuth provideGetAuth() {
         return new GetAuth(null) {
            @Override
            public Auth load(Credentials in) {
               return new ParseAuthTest().expected();
            }
         };
      }

   }

   protected String provider = "cloudservers";

   @Override
   protected ApiMetadata createApiMetadata() {
      return new CloudServersApiMetadata();
   }

   @Override
   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(PROPERTY_REGIONS, "US");
      overrides.setProperty(PROPERTY_API_VERSION, "1");
      overrides.setProperty(provider + ".endpoint", "https://auth");
      return overrides;
   }
}
