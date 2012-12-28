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
package org.jclouds.openstack.nova;

import static org.jclouds.openstack.nova.options.CreateServerOptions.Builder.withFile;
import static org.jclouds.openstack.nova.options.CreateServerOptions.Builder.withMetadata;
import static org.jclouds.openstack.nova.options.ListOptions.Builder.changesSince;
import static org.jclouds.openstack.nova.options.ListOptions.Builder.withDetails;
import static org.jclouds.openstack.nova.options.RebuildServerOptions.Builder.withImage;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;

import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.openstack.filters.AddTimestampQuery;
import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.internal.TestOpenStackAuthenticationModule;
import org.jclouds.openstack.nova.config.NovaRestClientModule;
import org.jclouds.openstack.nova.domain.RebootType;
import org.jclouds.openstack.nova.options.CreateServerOptions;
import org.jclouds.openstack.nova.options.ListOptions;
import org.jclouds.openstack.nova.options.RebuildServerOptions;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code NovaAsyncClient}
 *
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", singleThreaded = true, testName = "NovaAsyncClientTest")
public class NovaAsyncClientTest extends BaseAsyncClientTest<NovaAsyncClient> {
   private static final Class<? extends ListOptions[]> listOptionsVarargsClass = new ListOptions[]{}.getClass();
   private static final Class<? extends CreateServerOptions[]> createServerOptionsVarargsClass = new CreateServerOptions[]{}
         .getClass();

   @Test
   public void testCreateServer() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("createServer", String.class, String.class, String.class,
            createServerOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, "ralphie", 2, 1);

      assertRequestLineEquals(request, "POST http://endpoint/vapi-version/servers?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, "{\"server\":{\"name\":\"ralphie\",\"imageRef\":\"2\",\"flavorRef\":\"1\"}}",
            "application/json", false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);

   }

   @Test
   public void testCreateServerWithFile() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("createServer", String.class, String.class, String.class,
            createServerOptionsVarargsClass);
      HttpRequest request = processor
            .createRequest(method, "ralphie", 2, 1, withFile("/etc/jclouds", "foo".getBytes()));

      assertRequestLineEquals(request, "POST http://endpoint/vapi-version/servers?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(
            request,
            "{\"server\":{\"name\":\"ralphie\",\"imageRef\":\"2\",\"flavorRef\":\"1\",\"personality\":[{\"path\":\"/etc/jclouds\",\"contents\":\"Zm9v\"}]}}",
            "application/json", false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);

   }

   @Test
   public void testCreateServerWithMetadata() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("createServer", String.class, String.class, String.class,
            createServerOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, "ralphie", 2, 1,
            withMetadata(ImmutableMap.of("foo", "bar")));

      assertRequestLineEquals(request, "POST http://endpoint/vapi-version/servers?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request,
            "{\"server\":{\"name\":\"ralphie\",\"imageRef\":\"2\",\"flavorRef\":\"1\",\"metadata\":{\"foo\":\"bar\"}}}",
            "application/json", false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);

   }
   
   @Test
   public void testCreateServerWithKeyName() throws Exception {
	   Method method = NovaAsyncClient.class.getMethod("createServer", String.class, String.class, String.class,
	            createServerOptionsVarargsClass);
	      HttpRequest request = processor.createRequest(method, "ralphie", 2, 1,
	            withMetadata(ImmutableMap.of("foo", "bar")).withKeyName("mykey"));

	      assertRequestLineEquals(request, "POST http://endpoint/vapi-version/servers?format=json HTTP/1.1");
	      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
	      assertPayloadEquals(request,
	            "{\"server\":{\"name\":\"ralphie\",\"imageRef\":\"2\",\"flavorRef\":\"1\",\"metadata\":{\"foo\":\"bar\"},\"key_name\":\"mykey\"}}",
	            "application/json", false);

	      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
	      assertSaxResponseParserClassEquals(method, null);
	      assertFallbackClassEquals(method, null);

	      checkFilters(request);
   }
   
   @Test
   public void testCreateServerWithAdminPass() throws Exception {
	   Method method = NovaAsyncClient.class.getMethod("createServer", String.class, String.class, String.class,
	            createServerOptionsVarargsClass);
	      HttpRequest request = processor.createRequest(method, "ralphie", 2, 1,
	            withMetadata(ImmutableMap.of("foo", "bar")).withAdminPass("mypass"));

	      assertRequestLineEquals(request, "POST http://endpoint/vapi-version/servers?format=json HTTP/1.1");
	      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
	      assertPayloadEquals(request,
	            "{\"server\":{\"name\":\"ralphie\",\"imageRef\":\"2\",\"flavorRef\":\"1\",\"adminPass\":\"mypass\",\"metadata\":{\"foo\":\"bar\"}}}",
	            "application/json", false);

	      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
	      assertSaxResponseParserClassEquals(method, null);
	      assertFallbackClassEquals(method, null);

	      checkFilters(request);
   }
   
   public void testDeleteImage() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("deleteImage", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "DELETE http://endpoint/vapi-version/images/2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: */*\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListServers() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("listServers", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET http://endpoint/vapi-version/servers?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   Date now = new Date(10000000l);

   public void testListServersOptions() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("listServers", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, changesSince(now).maxResults(1).startAt(2));

      assertRequestLineEquals(request,
            "GET http://endpoint/vapi-version/servers?format=json&changes-since=10000&limit=1&offset=2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListServersDetail() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("listServers", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, withDetails());

      assertRequestLineEquals(request, "GET http://endpoint/vapi-version/servers/detail?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetServer() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("getServer", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "GET http://endpoint/vapi-version/servers/2?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetServerByUUID() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("getServer", String.class);
      HttpRequest request = processor.createRequest(method, "dfdcd0a6-0a2f-11e1-8505-2837371c69ae");

      assertRequestLineEquals(request, "GET http://endpoint/vapi-version/servers/dfdcd0a6-0a2f-11e1-8505-2837371c69ae?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListFlavors() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("listFlavors", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET http://endpoint/vapi-version/flavors?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListFlavorsOptions() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("listFlavors", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, changesSince(now).maxResults(1).startAt(2));

      assertRequestLineEquals(request,
            "GET http://endpoint/vapi-version/flavors?format=json&changes-since=10000&limit=1&offset=2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListFlavorsDetail() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("listFlavors", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, withDetails());

      assertRequestLineEquals(request, "GET http://endpoint/vapi-version/flavors/detail?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListFlavorsDetailOptions() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("listFlavors", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, withDetails().changesSince(now).maxResults(1).startAt(2));

      assertRequestLineEquals(request,
            "GET http://endpoint/vapi-version/flavors/detail?format=json&changes-since=10000&limit=1&offset=2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetFlavor() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("getFlavor", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "GET http://endpoint/vapi-version/flavors/2?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetFlavorByUUID() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("getFlavor", String.class);
      HttpRequest request = processor.createRequest(method, "209904b6-0a30-11e1-a0f0-2837371c69ae");

      assertRequestLineEquals(request, "GET http://endpoint/vapi-version/flavors/209904b6-0a30-11e1-a0f0-2837371c69ae?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListImages() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("listImages", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET http://endpoint/vapi-version/images?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListImagesDetail() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("listImages", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, withDetails());

      assertRequestLineEquals(request, "GET http://endpoint/vapi-version/images/detail?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListImagesOptions() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("listImages", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, changesSince(now).maxResults(1).startAt(2));

      assertRequestLineEquals(request,
            "GET http://endpoint/vapi-version/images?format=json&changes-since=10000&limit=1&offset=2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListImagesDetailOptions() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("listImages", listOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, withDetails().changesSince(now).maxResults(1).startAt(2));

      assertRequestLineEquals(request,
            "GET http://endpoint/vapi-version/images/detail?format=json&changes-since=10000&limit=1&offset=2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetImage() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("getImage", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "GET http://endpoint/vapi-version/images/2?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetImageByUUID() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("getImage", int.class);
      HttpRequest request = processor.createRequest(method, "3bd52d90-0a30-11e1-83f5-2837371c69ae");

      assertRequestLineEquals(request, "GET http://endpoint/vapi-version/images/3bd52d90-0a30-11e1-83f5-2837371c69ae?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDeleteServer() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("deleteServer", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "DELETE http://endpoint/vapi-version/servers/2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: */*\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDeleteServerByUUID() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("deleteServer", String.class);
      HttpRequest request = processor.createRequest(method, "db8a1ac6-0a35-11e1-a42f-2837371c69ae");

      assertRequestLineEquals(request, "DELETE http://endpoint/vapi-version/servers/db8a1ac6-0a35-11e1-a42f-2837371c69ae HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: */*\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testChangeAdminPass() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("changeAdminPass", int.class, String.class);
      HttpRequest request = processor.createRequest(method, 2, "foo");

      assertRequestLineEquals(request, "POST http://endpoint/vapi-version/servers/2/action HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: */*\n");
      assertPayloadEquals(request, "{\"changePassword\":{\"adminPass\":\"foo\"}}", MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);

   }

   public void testChangeServerName() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("renameServer", int.class, String.class);
      HttpRequest request = processor.createRequest(method, 2, "foo");

      assertRequestLineEquals(request, "PUT http://endpoint/vapi-version/servers/2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: */*\n");
      assertPayloadEquals(request, "{\"server\":{\"name\":\"foo\"}}", MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);

   }

   public void testListAddresses() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("getAddresses", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "GET http://endpoint/vapi-version/servers/2/ips?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testListPublicAddresses() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("listPublicAddresses", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "GET http://endpoint/vapi-version/servers/2/ips/public?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   @Test
   public void testListPrivateAddresses() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("listPrivateAddresses", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "GET http://endpoint/vapi-version/servers/2/ips/private?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   @Test
   public void testCreateImageWithIpGroup() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("createImageFromServer", String.class, int.class);
      HttpRequest request = processor.createRequest(method, "ralphie", 2);

      assertRequestLineEquals(request, "POST http://endpoint/vapi-version/images?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, "{\"image\":{\"serverId\":2,\"name\":\"ralphie\"}}", MediaType.APPLICATION_JSON,
            false);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);

   }

   private static final Class<? extends RebuildServerOptions[]> rebuildServerOptionsVarargsClass = new RebuildServerOptions[]{}
         .getClass();

   @Test
   public void testRebuildServer() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("rebuildServer", int.class,
            rebuildServerOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, 3);

      assertRequestLineEquals(request, "POST http://endpoint/vapi-version/servers/3/action?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: */*\n");
      assertPayloadEquals(request, "{\"rebuild\":{}}", MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   @Test
   public void testRebuildServerWithImage() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("rebuildServer", int.class,
            rebuildServerOptionsVarargsClass);
      HttpRequest request = processor.createRequest(method, 3, withImage("2"));

      assertRequestLineEquals(request, "POST http://endpoint/vapi-version/servers/3/action?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: */*\n");
      assertPayloadEquals(request, "{\"rebuild\":{\"imageRef\":\"2\"}}", MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   @Test
   public void testReboot() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("rebootServer", int.class, RebootType.class);
      HttpRequest request = processor.createRequest(method, 2, RebootType.HARD);

      assertRequestLineEquals(request, "POST http://endpoint/vapi-version/servers/2/action?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: */*\n");
      assertPayloadEquals(request, "{\"reboot\":{\"type\":\"HARD\"}}", MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   @Test
   public void testResize() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("resizeServer", int.class, int.class);
      HttpRequest request = processor.createRequest(method, 2, 3);

      assertRequestLineEquals(request, "POST http://endpoint/vapi-version/servers/2/action?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: */*\n");
      assertPayloadEquals(request, "{\"resize\":{\"flavorId\":3}}", MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);

   }

   public void testConfirmResize() throws IOException, IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("confirmResizeServer", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "POST http://endpoint/vapi-version/servers/2/action?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: */*\n");
      assertPayloadEquals(request, "{\"confirmResize\":null}", MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testRevertResize() throws IOException, SecurityException, NoSuchMethodException {
      Method method = NovaAsyncClient.class.getMethod("revertResizeServer", int.class);
      HttpRequest request = processor.createRequest(method, 2);

      assertRequestLineEquals(request, "POST http://endpoint/vapi-version/servers/2/action?format=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: */*\n");
      assertPayloadEquals(request, "{\"revertResize\":null}", MediaType.APPLICATION_JSON, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }
   
	public void testGetFloatingIP() throws SecurityException,
			NoSuchMethodException {
		Method method = NovaAsyncClient.class.getMethod("getFloatingIP",
				int.class);
		HttpRequest request = processor.createRequest(method, 2);

		assertRequestLineEquals(request,
				"GET http://endpoint/vapi-version/os-floating-ips/2?format=json HTTP/1.1");
		assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
		assertPayloadEquals(request, null, null, false);

		assertResponseParserClassEquals(method, request,
				UnwrapOnlyJsonValue.class);
		assertSaxResponseParserClassEquals(method, null);
		assertFallbackClassEquals(method,
				NullOnNotFoundOr404.class);

		checkFilters(request);
	}
	
	public void testListFloatingIPs() throws SecurityException, NoSuchMethodException {
	      Method method = NovaAsyncClient.class.getMethod("listFloatingIPs");
	      HttpRequest request = processor.createRequest(method);

	      assertRequestLineEquals(request,
	            "GET http://endpoint/vapi-version/os-floating-ips?format=json HTTP/1.1");
	      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
	      assertPayloadEquals(request, null, null, false);

	      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
	      assertSaxResponseParserClassEquals(method, null);
	      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

	      checkFilters(request);	
	}
	
	public void testGetSecurityGroup() throws SecurityException,
			NoSuchMethodException {
		Method method = NovaAsyncClient.class.getMethod("getSecurityGroup",
				int.class);
		HttpRequest request = processor.createRequest(method, 2);

		assertRequestLineEquals(request,
				"GET http://endpoint/vapi-version/os-security-groups/2?format=json HTTP/1.1");
		assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
		assertPayloadEquals(request, null, null, false);

		assertResponseParserClassEquals(method, request,
				UnwrapOnlyJsonValue.class);
		assertSaxResponseParserClassEquals(method, null);
		assertFallbackClassEquals(method,
				NullOnNotFoundOr404.class);

		checkFilters(request);
	}
	
	public void testListSecurityGroups() throws SecurityException, NoSuchMethodException {
	      Method method = NovaAsyncClient.class.getMethod("listSecurityGroups");
	      HttpRequest request = processor.createRequest(method);

	      assertRequestLineEquals(request,
	            "GET http://endpoint/vapi-version/os-security-groups?format=json HTTP/1.1");
	      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
	      assertPayloadEquals(request, null, null, false);

	      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
	      assertSaxResponseParserClassEquals(method, null);
	      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

	      checkFilters(request);	
	}
   
   @Override
   protected TypeLiteral<RestAnnotationProcessor<NovaAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<NovaAsyncClient>>() {
      };
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 2);
      assertEquals(request.getFilters().get(0).getClass(), AuthenticateRequest.class);
      assertEquals(request.getFilters().get(1).getClass(), AddTimestampQuery.class);

   }

   @Override
   protected Module createModule() {
      return new TestNovaRestClientModule();
   }

   @ConfiguresRestClient
      protected static class TestNovaRestClientModule extends NovaRestClientModule {
      private TestNovaRestClientModule() {
         super(new TestOpenStackAuthenticationModule());
      }

   }

   protected String provider = "nova";

   @Override
   protected ProviderMetadata createProviderMetadata() {
      return AnonymousProviderMetadata.forApiWithEndpoint(new NovaApiMetadata(), "http://endpoint");
   }
}
