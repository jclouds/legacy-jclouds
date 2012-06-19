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
package org.jclouds.openstack.nova.v2_0.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions.Builder.blockUntilRunning;
import static org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions.Builder.keyPairName;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Location;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaComputeServiceExpectTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * Tests the compute service abstraction of the nova client.
 * 
 * @author Matt Stephenson
 */
@Test(groups = "unit", testName = "NovaComputeServiceExpectTest")
public class NovaComputeServiceExpectTest extends BaseNovaComputeServiceExpectTest {

   public void testListLocationsWhenResponseIs2xx() throws Exception {

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
            .put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess)
            .put(extensionsOfNovaRequest, extensionsOfNovaResponse).put(listImagesDetail, listImagesDetailResponse)
            .put(listServers, listServersResponse).put(listFlavorsDetail, listFlavorsDetailResponse).build();

      ComputeService clientWhenServersExist = requestsSendResponses(requestResponseMap);

      Set<? extends Location> locations = clientWhenServersExist.listAssignableLocations();
      assertNotNull(locations);
      assertEquals(locations.size(), 1);
      assertEquals(locations.iterator().next().getId(), "az-1.region-a.geo-1");

      assertNotNull(clientWhenServersExist.listNodes());
      assertEquals(clientWhenServersExist.listNodes().size(), 1);
      assertEquals(clientWhenServersExist.listNodes().iterator().next().getId(),
            "az-1.region-a.geo-1/52415800-8b69-11e0-9b19-734f000004d2");
      assertEquals(clientWhenServersExist.listNodes().iterator().next().getName(), "sample-server");
   }

   Map<HttpRequest, HttpResponse> defaultTemplateTryStack = ImmutableMap
         .<HttpRequest, HttpResponse> builder()
         .put(keystoneAuthWithUsernameAndPasswordAndTenantName,
               HttpResponse
                     .builder()
                     .statusCode(200)
                     .message("HTTP/1.1 200")
                     .payload(
                           payloadFromResourceWithContentType("/keystoneAuthResponse_trystack.json", "application/json"))
                     .build())
         .put(extensionsOfNovaRequest.toBuilder()
               .endpoint(URI.create("https://nova-api.trystack.org:9774/v1.1/3456/extensions")).build(),
               HttpResponse.builder().statusCode(200).payload(payloadFromResource("/extension_list_trystack.json"))
                     .build())
         .put(listImagesDetail.toBuilder()
               .endpoint(URI.create("https://nova-api.trystack.org:9774/v1.1/3456/images/detail")).build(),
               HttpResponse.builder().statusCode(200).payload(payloadFromResource("/image_list_detail_trystack.json"))
                     .build())
         .put(listServers.toBuilder()
               .endpoint(URI.create("https://nova-api.trystack.org:9774/v1.1/3456/servers/detail")).build(),
               listServersResponse)
         .put(listFlavorsDetail.toBuilder()
               .endpoint(URI.create("https://nova-api.trystack.org:9774/v1.1/3456/flavors/detail")).build(),
               HttpResponse.builder().statusCode(200).payload(payloadFromResource("/flavor_list_detail_trystack.json"))
                     .build()).build();

   public void testDefaultTemplateTryStack() throws Exception {

      ComputeService clientForTryStack = requestsSendResponses(defaultTemplateTryStack);

      Template defaultTemplate = clientForTryStack.templateBuilder().imageId("RegionOne/15").build();
      checkTemplate(defaultTemplate);
      checkTemplate(clientForTryStack.templateBuilder().fromTemplate(defaultTemplate).build());

   }

   private void checkTemplate(Template defaultTemplate) {
      assertEquals(defaultTemplate.getImage().getId(), "RegionOne/15");
      assertEquals(defaultTemplate.getImage().getProviderId(), "15");
      assertEquals(defaultTemplate.getHardware().getId(), "RegionOne/1");
      assertEquals(defaultTemplate.getHardware().getProviderId(), "1");
      assertEquals(defaultTemplate.getLocation().getId(), "RegionOne");
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
   }

   public void testListServersWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest listServers = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/detail"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listServersResponse = HttpResponse.builder().statusCode(404).build();

      ComputeService clientWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, listServers, listServersResponse);

      assertTrue(clientWhenNoServersExist.listNodes().isEmpty());
   }

   HttpRequest listSecurityGroups = HttpRequest
         .builder()
         .method("GET")
         .endpoint(URI.create("https://nova-api.trystack.org:9774/v1.1/3456/os-security-groups"))
         .headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                     .put("X-Auth-Token", authToken).build()).build();

   HttpResponse notFound = HttpResponse.builder().statusCode(404).build();

   HttpRequest createSecurityGroupWithPrefixOnGroup = HttpRequest
         .builder()
         .method("POST")
         .endpoint(URI.create("https://nova-api.trystack.org:9774/v1.1/3456/os-security-groups"))
         .headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                     .put("X-Auth-Token", authToken).build())
         .payload(
               payloadFromStringWithContentType(
                     "{\"security_group\":{\"name\":\"jclouds-test\",\"description\":\"jclouds-test\"}}",
                     "application/json")).build();

   HttpResponse securityGroupCreated = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/securitygroup_created.json")).build();

   HttpRequest createSecurityGroupRuleForDefaultPort22 = HttpRequest
         .builder()
         .method("POST")
         .endpoint(URI.create("https://nova-api.trystack.org:9774/v1.1/3456/os-security-group-rules"))
         .headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                     .put("X-Auth-Token", authToken).build())
         .payload(
               payloadFromStringWithContentType(
                     "{\"security_group_rule\":{\"parent_group_id\":\"160\",\"cidr\":\"0.0.0.0/0\",\"ip_protocol\":\"tcp\",\"from_port\":\"22\",\"to_port\":\"22\"}}",
                     "application/json")).build();

   HttpResponse securityGroupRuleCreated = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/securitygrouprule_created.json")).build();

   HttpRequest getSecurityGroup = HttpRequest
         .builder()
         .method("GET")
         .endpoint(URI.create("https://nova-api.trystack.org:9774/v1.1/3456/os-security-groups/160"))
         .headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                     .put("X-Auth-Token", authToken).build()).build();

   HttpResponse securityGroupWithPort22 = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/securitygroup_details_port22.json")).build();
   
   HttpRequest createKeyPair = HttpRequest
         .builder()
         .method("POST")
         .endpoint(URI.create("https://nova-api.trystack.org:9774/v1.1/3456/os-keypairs"))
         .headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                     .put("X-Auth-Token", authToken).build())
         .payload(
               payloadFromStringWithContentType(
                     "{\"keypair\":{\"name\":\"jclouds-test-0\"}}",
                     "application/json")).build();

   HttpResponse keyPairWithPrivateKey = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/keypair_created_computeservice.json")).build();

   HttpRequest serverDetail = HttpRequest
         .builder()
         .method("GET")
         .endpoint(URI.create("https://nova-api.trystack.org:9774/v1.1/3456/servers/71752"))
         .headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                     .put("X-Auth-Token", authToken).build()).build();

   HttpResponse serverDetailResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/server_details.json")).build();

   @Test
   public void testCreateNodeWithGeneratedKeyPair() throws Exception {
      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
            .putAll(defaultTemplateTryStack);
      requestResponseMap.put(listSecurityGroups, notFound);

      requestResponseMap.put(createSecurityGroupWithPrefixOnGroup, securityGroupCreated);

      requestResponseMap.put(createSecurityGroupRuleForDefaultPort22, securityGroupRuleCreated);

      requestResponseMap.put(getSecurityGroup, securityGroupWithPort22);

      requestResponseMap.put(createKeyPair, keyPairWithPrivateKey);

      requestResponseMap.put(serverDetail, serverDetailResponse);

      HttpRequest createServerWithGeneratedKeyPair = HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("https://nova-api.trystack.org:9774/v1.1/3456/servers"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build())
            .payload(
                  payloadFromStringWithContentType(
                        "{\"server\":{\"name\":\"test-1\",\"imageRef\":\"14\",\"flavorRef\":\"1\",\"key_name\":\"jclouds-test-0\",\"security_groups\":[{\"name\":\"jclouds-test\"}]}}",
                        "application/json")).build();

      HttpResponse createdServer = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
            .payload(payloadFromResourceWithContentType("/new_server.json", "application/json; charset=UTF-8")).build();

      requestResponseMap.put(createServerWithGeneratedKeyPair, createdServer);

      ComputeService clientThatCreatesNode = requestsSendResponses(requestResponseMap.build(), new AbstractModule() {

         @Override
         protected void configure() {
            // predicatable node names
            final AtomicInteger suffix = new AtomicInteger();
            bind(new TypeLiteral<Supplier<String>>() {
            }).toInstance(new Supplier<String>() {

               @Override
               public String get() {
                  return suffix.getAndIncrement() + "";
               }

            });
         }

      });

      NodeMetadata node = Iterables.getOnlyElement(clientThatCreatesNode.createNodesInGroup("test", 1,
            blockUntilRunning(false).generateKeyPair(true)));
      assertNotNull(node.getCredentials().getPrivateKey());
   }

   @Test
   public void testCreateNodeWhileUserSpecifiesKeyPair() throws Exception {
      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
            .putAll(defaultTemplateTryStack);
      requestResponseMap.put(listSecurityGroups, notFound);

      requestResponseMap.put(createSecurityGroupWithPrefixOnGroup, securityGroupCreated);

      requestResponseMap.put(createSecurityGroupRuleForDefaultPort22, securityGroupRuleCreated);

      requestResponseMap.put(getSecurityGroup, securityGroupWithPort22);

      requestResponseMap.put(serverDetail, serverDetailResponse);

      HttpRequest createServerWithSuppliedKeyPair = HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("https://nova-api.trystack.org:9774/v1.1/3456/servers"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build())
            .payload(
                  payloadFromStringWithContentType(
                        "{\"server\":{\"name\":\"test-0\",\"imageRef\":\"14\",\"flavorRef\":\"1\",\"key_name\":\"fooPair\",\"security_groups\":[{\"name\":\"jclouds-test\"}]}}",
                        "application/json")).build();

      HttpResponse createdServer = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
            .payload(payloadFromResourceWithContentType("/new_server.json", "application/json; charset=UTF-8")).build();

      requestResponseMap.put(createServerWithSuppliedKeyPair, createdServer);

      ComputeService clientThatCreatesNode = requestsSendResponses(requestResponseMap.build(), new AbstractModule() {

         @Override
         protected void configure() {
            // predicatable node names
            final AtomicInteger suffix = new AtomicInteger();
            bind(new TypeLiteral<Supplier<String>>() {
            }).toInstance(new Supplier<String>() {

               @Override
               public String get() {
                  return suffix.getAndIncrement() + "";
               }

            });
         }

      });

      NodeMetadata node = Iterables.getOnlyElement(clientThatCreatesNode.createNodesInGroup("test", 1,
            keyPairName("fooPair").blockUntilRunning(false)));
      // we don't have access to this private key
      assertEquals(node.getCredentials().getPrivateKey(), null);
   }
}
