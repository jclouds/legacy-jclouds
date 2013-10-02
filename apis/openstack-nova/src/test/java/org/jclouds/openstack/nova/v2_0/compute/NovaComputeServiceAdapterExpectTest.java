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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.Map;
import java.util.Properties;

import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ServerInZone;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndName;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaComputeServiceContextExpectTest;
import org.testng.annotations.Test;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests the compute service abstraction of the nova api.
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "NovaComputeServiceAdapterExpectTest")
public class NovaComputeServiceAdapterExpectTest extends BaseNovaComputeServiceContextExpectTest<Injector> {
   HttpRequest serverDetail = HttpRequest
         .builder()
         .method("GET")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/71752")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken).build();

   HttpResponse serverDetailResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/server_details.json")).build();

   public void testCreateNodeWithGroupEncodedIntoNameWithDiskConfig() throws Exception {

      HttpRequest createServer = HttpRequest
         .builder()
         .method("POST")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(payloadFromStringWithContentType(
                  "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\",\"OS-DCF:diskConfig\":\"AUTO\"}}","application/json"))
         .build();

      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
         .payload(payloadFromResourceWithContentType("/new_server_disk_config_auto.json","application/json; charset=UTF-8")).build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
               .put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess)
               .put(extensionsOfNovaRequest, extensionsOfNovaResponse)
               .put(listDetail, listDetailResponse)
               .put(listFlavorsDetail, listFlavorsDetailResponse)
               .put(createServer, createServerResponse)
               .put(serverDetail, serverDetailResponse).build();

      Injector forDiskConfig = requestsSendResponses(requestResponseMap);

      Template template = forDiskConfig.getInstance(TemplateBuilder.class).build();
      template.getOptions().as(NovaTemplateOptions.class).diskConfig(Server.DISK_CONFIG_AUTO);
      
      NovaComputeServiceAdapter adapter = forDiskConfig.getInstance(NovaComputeServiceAdapter.class);

      NodeAndInitialCredentials<ServerInZone> server = adapter.createNodeWithGroupEncodedIntoName("test", "test-e92", template);
      assertNotNull(server);
      assertEquals(server.getNode().getServer().getDiskConfig().orNull(), Server.DISK_CONFIG_AUTO);
   }

   public void testCreateNodeWithGroupEncodedIntoNameWhenSecurityGroupsArePresent() throws Exception {

      HttpRequest createServer = HttpRequest
         .builder()
         .method("POST")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(payloadFromStringWithContentType(
                  "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\",\"security_groups\":[{\"name\":\"group1\"}, {\"name\":\"group2\"}]}}","application/json"))
         .build();

      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
         .payload(payloadFromResourceWithContentType("/new_server.json","application/json; charset=UTF-8")).build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
               .put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess)
               .put(extensionsOfNovaRequest, extensionsOfNovaResponse)
               .put(listDetail, listDetailResponse)
               .put(listFlavorsDetail, listFlavorsDetailResponse)
               .put(createServer, createServerResponse)
               .put(serverDetail, serverDetailResponse).build();

      Injector forSecurityGroups = requestsSendResponses(requestResponseMap);

      Template template = forSecurityGroups.getInstance(TemplateBuilder.class).build();
      template.getOptions().as(NovaTemplateOptions.class).securityGroupNames("group1", "group2");
      
      NovaComputeServiceAdapter adapter = forSecurityGroups.getInstance(NovaComputeServiceAdapter.class);

      NodeAndInitialCredentials<ServerInZone> server = adapter.createNodeWithGroupEncodedIntoName("test", "test-e92",
               template);
      assertNotNull(server);
      assertEquals(server.getCredentials(), LoginCredentials.builder().password("ZWuHcmTMQ7eXoHeM").build());
   }

   /**
    * We need to choose the correct credential for attempts to start the server. cloud-init or the
    * like will set the ssh key as the login credential, and not necessarily will password
    * authentication even be available.
    */
   public void testWhenKeyPairPresentWeUsePrivateKeyAsCredentialNotPassword() throws Exception {
      
      HttpRequest createServer = HttpRequest
         .builder()
         .method("POST")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(payloadFromStringWithContentType(
                  "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\",\"key_name\":\"foo\"}}","application/json"))
         .build();

  
      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
         .payload(payloadFromResourceWithContentType("/new_server_no_adminpass.json","application/json; charset=UTF-8")).build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
               .put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess)
               .put(extensionsOfNovaRequest, extensionsOfNovaResponse)
               .put(listDetail, listDetailResponse)
               .put(listFlavorsDetail, listFlavorsDetailResponse)
               .put(createServer, createServerResponse)
               .put(serverDetail, serverDetailResponse).build();

      Injector forSecurityGroups = requestsSendResponses(requestResponseMap);

      Template template = forSecurityGroups.getInstance(TemplateBuilder.class).build();
      template.getOptions().as(NovaTemplateOptions.class).keyPairName("foo");
      
      NovaComputeServiceAdapter adapter = forSecurityGroups.getInstance(NovaComputeServiceAdapter.class);
      
      // we expect to have already an entry in the cache for the key
      LoadingCache<ZoneAndName, KeyPair> keyPairCache = forSecurityGroups.getInstance(Key
               .get(new TypeLiteral<LoadingCache<ZoneAndName, KeyPair>>() {
               }));
      keyPairCache.put(ZoneAndName.fromZoneAndName("az-1.region-a.geo-1", "foo"), KeyPair.builder().name("foo")
               .privateKey("privateKey").build());
      
      NodeAndInitialCredentials<ServerInZone> server = adapter.createNodeWithGroupEncodedIntoName("test", "test-e92",
               template);
      assertNotNull(server);
      assertEquals(server.getCredentials(), LoginCredentials.builder().privateKey("privateKey").build());
   }


   /**
    * When enable_instance_password is false, then no admin pass is generated.
    * However in this case if you don't specify the name of the SSH keypair to
    * inject, then you simply cannot log in to the server.
    */
   public void testNoKeyPairOrAdminPass() throws Exception {
      
      HttpRequest createServer = HttpRequest
         .builder()
         .method("POST")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(payloadFromStringWithContentType(
                  "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\"}}","application/json"))
         .build();
  
      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
         .payload(payloadFromResourceWithContentType("/new_server_no_adminpass.json","application/json; charset=UTF-8")).build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
               .put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess)
               .put(extensionsOfNovaRequest, extensionsOfNovaResponse)
               .put(listDetail, listDetailResponse)
               .put(listFlavorsDetail, listFlavorsDetailResponse)
               .put(createServer, createServerResponse)
               .put(serverDetail, serverDetailResponse).build();

      Injector forSecurityGroups = requestsSendResponses(requestResponseMap);

      Template template = forSecurityGroups.getInstance(TemplateBuilder.class).build();
      
      NovaComputeServiceAdapter adapter = forSecurityGroups.getInstance(NovaComputeServiceAdapter.class);
      
      NodeAndInitialCredentials<ServerInZone> server = adapter.createNodeWithGroupEncodedIntoName("test", "test-e92",
            template);
      assertNotNull(server);
      assertNull(server.getCredentials());
   }
   
   @Override
   public Injector apply(ComputeServiceContext input) {
      return input.utils().injector();
   }
   
   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      // only specify one zone so that we don't have to configure requests for multiple zones
      overrides.setProperty("jclouds.zones", "az-1.region-a.geo-1");
      return overrides;
   }
}
