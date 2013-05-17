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
package org.jclouds.openstack.nova.v2_0.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions.Builder.blockUntilRunning;
import static org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions.Builder.keyPairName;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Properties;
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
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * Tests the compute service abstraction of the nova api.
 * 
 * @author Matt Stephenson
 */
@Test(groups = "unit", testName = "NovaComputeServiceExpectTest")
public class NovaComputeServiceExpectTest extends BaseNovaComputeServiceExpectTest {
   
   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      // only specify limited zones so that we don't have to configure requests for multiple zones.
      // since we are doing tests with keystone responses from hpcloud and also openstack, we have
      // to whitelist one zone from each
      overrides.setProperty("jclouds.zones", "az-1.region-a.geo-1,RegionOne");
      return overrides;
   }
   
   public void testListLocationsWhenResponseIs2xx() throws Exception {

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
            .put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess)
            .put(extensionsOfNovaRequest, extensionsOfNovaResponse).put(listDetail, listDetailResponse)
            .put(listServers, listServersResponse).put(listFlavorsDetail, listFlavorsDetailResponse).build();

      ComputeService apiWhenServersExist = requestsSendResponses(requestResponseMap);

      Set<? extends Location> locations = apiWhenServersExist.listAssignableLocations();
      assertNotNull(locations);
      assertEquals(locations.size(), 1);
      assertEquals(locations.iterator().next().getId(), "az-1.region-a.geo-1");

      assertNotNull(apiWhenServersExist.listNodes());
      assertEquals(apiWhenServersExist.listNodes().size(), 1);
      assertEquals(apiWhenServersExist.listNodes().iterator().next().getId(),
            "az-1.region-a.geo-1/52415800-8b69-11e0-9b19-734f000004d2");
      assertEquals(apiWhenServersExist.listNodes().iterator().next().getName(), "sample-server");
   }

   Map<HttpRequest, HttpResponse> defaultTemplateOpenStack = ImmutableMap
         .<HttpRequest, HttpResponse> builder()
         .put(keystoneAuthWithUsernameAndPasswordAndTenantName,
               HttpResponse
                     .builder()
                     .statusCode(200)
                     .message("HTTP/1.1 200")
                     .payload(
                           payloadFromResourceWithContentType("/keystoneAuthResponse_openstack.json", "application/json"))
                     .build())
         .put(extensionsOfNovaRequest.toBuilder()
               .endpoint("https://nova-api.openstack.org:9774/v1.1/3456/extensions").build(),
               HttpResponse.builder().statusCode(200).payload(payloadFromResource("/extension_list_openstack.json"))
                     .build())
         .put(listDetail.toBuilder()
               .endpoint("https://nova-api.openstack.org:9774/v1.1/3456/images/detail").build(),
               HttpResponse.builder().statusCode(200).payload(payloadFromResource("/image_list_detail_openstack.json"))
                     .build())
         .put(listServers.toBuilder()
               .endpoint("https://nova-api.openstack.org:9774/v1.1/3456/servers/detail").build(),
               listServersResponse)
         .put(listFlavorsDetail.toBuilder()
               .endpoint("https://nova-api.openstack.org:9774/v1.1/3456/flavors/detail").build(),
               HttpResponse.builder().statusCode(200).payload(payloadFromResource("/flavor_list_detail_openstack.json"))
                     .build()).build();

   public void testDefaultTemplateOpenStack() throws Exception {

      ComputeService apiForOpenStack = requestsSendResponses(defaultTemplateOpenStack);

      Template defaultTemplate = apiForOpenStack.templateBuilder().imageId("RegionOne/15").build();
      checkTemplate(defaultTemplate);
      checkTemplate(apiForOpenStack.templateBuilder().fromTemplate(defaultTemplate).build());

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
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/detail")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listServersResponse = HttpResponse.builder().statusCode(404).build();

      ComputeService apiWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, listServers, listServersResponse);

      assertTrue(apiWhenNoServersExist.listNodes().isEmpty());
   }

   HttpRequest list = HttpRequest
         .builder()
         .method("GET")
         .endpoint("https://nova-api.openstack.org:9774/v1.1/3456/os-security-groups")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken).build();

   HttpResponse notFound = HttpResponse.builder().statusCode(404).build();

   HttpRequest createWithPrefixOnGroup = HttpRequest
         .builder()
         .method("POST")
         .endpoint("https://nova-api.openstack.org:9774/v1.1/3456/os-security-groups")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(
               payloadFromStringWithContentType(
                     "{\"security_group\":{\"name\":\"jclouds-test\",\"description\":\"jclouds-test\"}}",
                     "application/json")).build();

   HttpResponse securityGroupCreated = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/securitygroup_created.json")).build();

   HttpRequest createRuleForDefaultPort22 = HttpRequest
         .builder()
         .method("POST")
         .endpoint("https://nova-api.openstack.org:9774/v1.1/3456/os-security-group-rules")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(
               payloadFromStringWithContentType(
                     "{\"security_group_rule\":{\"parent_group_id\":\"160\",\"cidr\":\"0.0.0.0/0\",\"ip_protocol\":\"tcp\",\"from_port\":\"22\",\"to_port\":\"22\"}}",
                     "application/json")).build();

   HttpResponse securityGroupRuleCreated = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/securitygrouprule_created.json")).build();

   HttpRequest getSecurityGroup = HttpRequest
         .builder()
         .method("GET")
         .endpoint("https://nova-api.openstack.org:9774/v1.1/3456/os-security-groups/160")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken).build();

   HttpResponse securityGroupWithPort22 = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/securitygroup_details_port22.json")).build();
   
   HttpRequest create = HttpRequest
         .builder()
         .method("POST")
         .endpoint("https://nova-api.openstack.org:9774/v1.1/3456/os-keypairs")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(
               payloadFromStringWithContentType(
                     "{\"keypair\":{\"name\":\"jclouds-test-0\"}}",
                     "application/json")).build();

   HttpResponse keyPairWithPrivateKey = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/keypair_created_computeservice.json")).build();

   HttpRequest serverDetail = HttpRequest
         .builder()
         .method("GET")
         .endpoint("https://nova-api.openstack.org:9774/v1.1/3456/servers/71752")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken).build();

   HttpResponse serverDetailResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/server_details.json")).build();

   @Test
   public void testCreateNodeWithGeneratedKeyPair() throws Exception {
      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
            .putAll(defaultTemplateOpenStack);
      requestResponseMap.put(list, notFound);

      requestResponseMap.put(createWithPrefixOnGroup, securityGroupCreated);

      requestResponseMap.put(createRuleForDefaultPort22, securityGroupRuleCreated);

      requestResponseMap.put(getSecurityGroup, securityGroupWithPort22);

      requestResponseMap.put(create, keyPairWithPrivateKey);

      requestResponseMap.put(serverDetail, serverDetailResponse);

      HttpRequest createServerWithGeneratedKeyPair = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://nova-api.openstack.org:9774/v1.1/3456/servers")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(
                  payloadFromStringWithContentType(
                        "{\"server\":{\"name\":\"test-1\",\"imageRef\":\"14\",\"flavorRef\":\"1\",\"key_name\":\"jclouds-test-0\",\"security_groups\":[{\"name\":\"jclouds-test\"}]}}",
                        "application/json")).build();

      HttpResponse createdServer = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
            .payload(payloadFromResourceWithContentType("/new_server.json", "application/json; charset=UTF-8")).build();

      requestResponseMap.put(createServerWithGeneratedKeyPair, createdServer);

      ComputeService apiThatCreatesNode = requestsSendResponses(requestResponseMap.build(), new AbstractModule() {

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

      NodeMetadata node = Iterables.getOnlyElement(apiThatCreatesNode.createNodesInGroup("test", 1,
            blockUntilRunning(false).generateKeyPair(true)));
      assertNotNull(node.getCredentials().getPrivateKey());
   }

   @Test
   public void testCreateNodeWhileUserSpecifiesKeyPair() throws Exception {
      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
            .putAll(defaultTemplateOpenStack);
      requestResponseMap.put(list, notFound);

      requestResponseMap.put(createWithPrefixOnGroup, securityGroupCreated);

      requestResponseMap.put(createRuleForDefaultPort22, securityGroupRuleCreated);

      requestResponseMap.put(getSecurityGroup, securityGroupWithPort22);

      requestResponseMap.put(serverDetail, serverDetailResponse);

      HttpRequest createServerWithSuppliedKeyPair = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://nova-api.openstack.org:9774/v1.1/3456/servers")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(
                  payloadFromStringWithContentType(
                        "{\"server\":{\"name\":\"test-0\",\"imageRef\":\"14\",\"flavorRef\":\"1\",\"key_name\":\"fooPair\",\"security_groups\":[{\"name\":\"jclouds-test\"}]}}",
                        "application/json")).build();

      HttpResponse createdServer = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
            .payload(payloadFromResourceWithContentType("/new_server.json", "application/json; charset=UTF-8")).build();

      requestResponseMap.put(createServerWithSuppliedKeyPair, createdServer);

      ComputeService apiThatCreatesNode = requestsSendResponses(requestResponseMap.build(), new AbstractModule() {

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

      NodeMetadata node = Iterables.getOnlyElement(apiThatCreatesNode.createNodesInGroup("test", 1,
            keyPairName("fooPair").blockUntilRunning(false)));
      // we don't have access to this private key
      assertEquals(node.getCredentials().getPrivateKey(), null);
   }


   @Test
   public void testCreateNodeWhileUserSpecifiesKeyPairAndUserSpecifiedGroups() throws Exception {
      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
            .putAll(defaultTemplateOpenStack);
      requestResponseMap.put(list, notFound);

      requestResponseMap.put(serverDetail, serverDetailResponse);

      HttpRequest createServerWithSuppliedKeyPairAndGroup = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://nova-api.openstack.org:9774/v1.1/3456/servers")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(
                  payloadFromStringWithContentType(
                        "{\"server\":{\"name\":\"test-0\",\"imageRef\":\"14\",\"flavorRef\":\"1\",\"key_name\":\"fooPair\",\"security_groups\":[{\"name\":\"mygroup\"}]}}",
                        "application/json")).build();

      HttpResponse createdServer = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
            .payload(payloadFromResourceWithContentType("/new_server.json", "application/json; charset=UTF-8")).build();

      requestResponseMap.put(createServerWithSuppliedKeyPairAndGroup, createdServer);

      ComputeService apiThatCreatesNode = requestsSendResponses(requestResponseMap.build(), new AbstractModule() {

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

      NodeMetadata node = Iterables.getOnlyElement(apiThatCreatesNode.createNodesInGroup("test", 1,
            keyPairName("fooPair").securityGroupNames("mygroup").blockUntilRunning(false)));
      // we don't have access to this private key
      assertEquals(node.getCredentials().getPrivateKey(), null);
   }

}
