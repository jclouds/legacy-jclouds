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

package org.jclouds.chef;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Set;

import org.jclouds.chef.config.ChefRestClientModule;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.domain.Role;
import org.jclouds.chef.filters.SignedHeaderAuth;
import org.jclouds.chef.filters.SignedHeaderAuthTest;
import org.jclouds.chef.functions.ParseKeySetFromJson;
import org.jclouds.chef.functions.ParseSearchClientsFromJson;
import org.jclouds.chef.functions.ParseSearchDatabagFromJson;
import org.jclouds.chef.functions.ParseSearchNodesFromJson;
import org.jclouds.chef.functions.ParseSearchRolesFromJson;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Bytes;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code ChefAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "chef.ChefAsyncClientTest")
public class ChefAsyncClientTest extends RestClientTest<ChefAsyncClient> {

   public void testCommitSandbox() throws SecurityException, NoSuchMethodException, IOException {

      Method method = ChefAsyncClient.class.getMethod("commitSandbox", String.class, boolean.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method,
            "0189e76ccc476701d6b374e5a1a27347", true);
      assertRequestLineEquals(httpRequest,
            "PUT http://localhost:4000/sandboxes/0189e76ccc476701d6b374e5a1a27347 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, "{\"is_completed\":\"true\"}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testGetUploadSandboxForChecksums() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("getUploadSandboxForChecksums", Set.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, ImmutableSet.of(Bytes
            .asList(CryptoStreams.hex("0189e76ccc476701d6b374e5a1a27347")), Bytes.asList(CryptoStreams
            .hex("0c5ecd7788cf4f6c7de2a57193897a6c")), Bytes.asList(CryptoStreams
            .hex("1dda05ed139664f1f89b9dec482b77c0"))));
      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/sandboxes HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(
            httpRequest,
            "{\"checksums\":{\"0189e76ccc476701d6b374e5a1a27347\":null,\"0c5ecd7788cf4f6c7de2a57193897a6c\":null,\"1dda05ed139664f1f89b9dec482b77c0\":null}}",
            "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testGetCookbook() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("getCookbook", String.class, String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "cookbook", "1.0.0");
      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/cookbooks/cookbook/1.0.0 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteCookbook() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("deleteCookbook", String.class, String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "cookbook", "1.0.0");
      assertRequestLineEquals(httpRequest, "DELETE http://localhost:4000/cookbooks/cookbook/1.0.0 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testUpdateCookbook() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("updateCookbook", String.class, String.class,
            CookbookVersion.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "cookbook", "1.0.1",
            new CookbookVersion("cookbook", "1.0.1"));

      assertRequestLineEquals(httpRequest, "PUT http://localhost:4000/cookbooks/cookbook/1.0.1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(
            httpRequest,
            "{\"name\":\"cookbook-1.0.1\",\"definitions\":[],\"attributes\":[],\"files\":[],\"metadata\":{\"suggestions\":{},\"dependencies\":{},\"conflicting\":{},\"providing\":{},\"platforms\":{},\"recipes\":{},\"replacing\":{},\"groupings\":{},\"attributes\":{},\"recommendations\":{}},\"providers\":[],\"cookbook_name\":\"cookbook\",\"resources\":[],\"templates\":[],\"libraries\":[],\"version\":\"1.0.1\",\"recipes\":[],\"root_files\":[],\"json_class\":\"Chef::CookbookVersion\",\"chef_type\":\"cookbook_version\"}",
            "application/json", false);
      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListCookbooks() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("listCookbooks");
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/cookbooks HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseKeySetFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testClientExists() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("clientExists", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "client");
      assertRequestLineEquals(httpRequest, "HEAD http://localhost:4000/clients/client HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnFalseOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteClient() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("deleteClient", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "client");
      assertRequestLineEquals(httpRequest, "DELETE http://localhost:4000/clients/client HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateClient() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("createClient", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "client");

      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/clients HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, "{\"clientname\":\"client\"}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListClients() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("listClients");
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/clients HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseKeySetFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGenerateKeyForClient() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("generateKeyForClient", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "client");
      assertRequestLineEquals(httpRequest, "PUT http://localhost:4000/clients/client HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, "{\"clientname\":\"client\", \"private_key\": true}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testNodeExists() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("nodeExists", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "node");
      assertRequestLineEquals(httpRequest, "HEAD http://localhost:4000/nodes/node HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnFalseOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteNode() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("deleteNode", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "node");
      assertRequestLineEquals(httpRequest, "DELETE http://localhost:4000/nodes/node HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateNode() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("createNode", Node.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, new Node("testnode",
            ImmutableSet.of("recipe[java]")));

      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/nodes HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(
            httpRequest,
            "{\"name\":\"testnode\",\"normal\":{},\"override\":{},\"default\":{},\"automatic\":{},\"run_list\":[\"recipe[java]\"],\"json_class\":\"Chef::Node\",\"chef_type\":\"node\"}",
            "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testUpdateNode() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("updateNode", Node.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, new Node("testnode",
            ImmutableSet.of("recipe[java]")));

      assertRequestLineEquals(httpRequest, "PUT http://localhost:4000/nodes/testnode HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(
            httpRequest,
            "{\"name\":\"testnode\",\"normal\":{},\"override\":{},\"default\":{},\"automatic\":{},\"run_list\":[\"recipe[java]\"],\"json_class\":\"Chef::Node\",\"chef_type\":\"node\"}",
            "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListNodes() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("listNodes");
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/nodes HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseKeySetFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testRoleExists() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("roleExists", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "role");
      assertRequestLineEquals(httpRequest, "HEAD http://localhost:4000/roles/role HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnFalseOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteRole() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("deleteRole", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "role");
      assertRequestLineEquals(httpRequest, "DELETE http://localhost:4000/roles/role HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateRole() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("createRole", Role.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, new Role("testrole",
            ImmutableSet.of("recipe[java]")));

      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/roles HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(
            httpRequest,
            "{\"name\":\"testrole\",\"override_attributes\":{},\"default_attributes\":{},\"run_list\":[\"recipe[java]\"],\"json_class\":\"Chef::Role\",\"chef_type\":\"role\"}",
            "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testUpdateRole() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("updateRole", Role.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, new Role("testrole",
            ImmutableSet.of("recipe[java]")));

      assertRequestLineEquals(httpRequest, "PUT http://localhost:4000/roles/testrole HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(
            httpRequest,
            "{\"name\":\"testrole\",\"override_attributes\":{},\"default_attributes\":{},\"run_list\":[\"recipe[java]\"],\"json_class\":\"Chef::Role\",\"chef_type\":\"role\"}",
            "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListRoles() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("listRoles");
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/roles HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseKeySetFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDatabagExists() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("databagExists", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "databag");
      assertRequestLineEquals(httpRequest, "HEAD http://localhost:4000/data/databag HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnFalseOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteDatabag() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("deleteDatabag", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "databag");
      assertRequestLineEquals(httpRequest, "DELETE http://localhost:4000/data/databag HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateDatabag() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("createDatabag", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "name");

      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/data HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, "{\"name\":\"name\"}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListDatabags() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("listDatabags");
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/data HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseKeySetFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDatabagItemExists() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("databagItemExists", String.class, String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "name", "databagItem");
      assertRequestLineEquals(httpRequest, "HEAD http://localhost:4000/data/name/databagItem HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnFalseOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteDatabagItem() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("deleteDatabagItem", String.class, String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "name", "databagItem");
      assertRequestLineEquals(httpRequest, "DELETE http://localhost:4000/data/name/databagItem HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testCreateDatabagItemThrowsIllegalArgumentOnPrimitive() throws SecurityException, NoSuchMethodException,
         IOException {
      Method method = ChefAsyncClient.class.getMethod("createDatabagItem", String.class, DatabagItem.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "name", new DatabagItem("id",
            "100"));

      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/data/name HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(
            httpRequest,
            "{\"name\":\"testdatabagItem\",\"override_attributes\":{},\"default_attributes\":{},\"run_list\":[\"recipe[java]\"],\"json_class\":\"Chef::DatabagItem\",\"chef_type\":\"databagItem\"}",
            "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testCreateDatabagItemThrowsIllegalArgumentOnWrongId() throws SecurityException, NoSuchMethodException,
         IOException {
      Method method = ChefAsyncClient.class.getMethod("createDatabagItem", String.class, DatabagItem.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "name", new DatabagItem("id",
            "{\"id\": \"item1\",\"my_key\": \"my_data\"}"));

      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/data/name HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(
            httpRequest,
            "{\"name\":\"testdatabagItem\",\"override_attributes\":{},\"default_attributes\":{},\"run_list\":[\"recipe[java]\"],\"json_class\":\"Chef::DatabagItem\",\"chef_type\":\"databagItem\"}",
            "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testCreateDatabagItem() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("createDatabagItem", String.class, DatabagItem.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "name", new DatabagItem("id",
            "{\"id\": \"id\",\"my_key\": \"my_data\"}"));

      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/data/name HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, "{\"id\": \"id\",\"my_key\": \"my_data\"}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testCreateDatabagItemEvenWhenUserForgotId() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("createDatabagItem", String.class, DatabagItem.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "name", new DatabagItem("id",
            "{\"my_key\": \"my_data\"}"));

      assertRequestLineEquals(httpRequest, "POST http://localhost:4000/data/name HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, "{\"id\":\"id\",\"my_key\": \"my_data\"}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testUpdateDatabagItem() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("updateDatabagItem", String.class, DatabagItem.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "name", new DatabagItem("id",
            "{\"my_key\": \"my_data\"}"));

      assertRequestLineEquals(httpRequest, "PUT http://localhost:4000/data/name/id HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");

      assertPayloadEquals(httpRequest, "{\"id\":\"id\",\"my_key\": \"my_data\"}", "application/json", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListDatabagItems() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("listDatabagItems", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "name");

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/data/name HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseKeySetFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListSearchIndexes() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("listSearchIndexes");
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/search HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseKeySetFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testSearchRoles() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("searchRoles");
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/search/role HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseSearchRolesFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testSearchClients() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("searchClients");
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/search/client HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseSearchClientsFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testSearchNodes() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("searchNodes");
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/search/node HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseSearchNodesFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testSearchDatabag() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("searchDatabag", String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor.createRequest(method, "foo");

      assertRequestLineEquals(httpRequest, "GET http://localhost:4000/search/foo HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nX-Chef-Version: 0.9.8\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseSearchDatabagFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SignedHeaderAuth.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<ChefAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<ChefAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new TestChefRestClientModule();
   }

   @RequiresHttp
   @ConfiguresRestClient
   static class TestChefRestClientModule extends ChefRestClientModule {
      @Override
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return "timestamp";
      }

   }

   @Override
   public ContextSpec<ChefClient, ChefAsyncClient> createContextSpec() {
      return new RestContextFactory().createContextSpec("chef", "user", SignedHeaderAuthTest.PRIVATE_KEY,
            new Properties());
   }
}
