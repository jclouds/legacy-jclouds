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
package org.jclouds.openstack.nova.v2_0.compute.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Properties;
import java.util.Set;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaComputeServiceExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * 
 * @author Andrew Bayer
 */
@Test(groups = "unit", testName = "NovaSecurityGroupExtensionExpectTest")
public class NovaSecurityGroupExtensionExpectTest extends BaseNovaComputeServiceExpectTest {

   protected String zone = "az-1.region-a.geo-1";

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      overrides.setProperty("jclouds.zones", zone);
      return overrides;
   }

   public void testListSecurityGroups() {
      HttpRequest list = HttpRequest.builder().method("GET").endpoint(
              URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups")).headers(
              ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                      authToken).build()).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/securitygroup_list.json")).build();


      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess);
      requestResponseMap.put(extensionsOfNovaRequest, extensionsOfNovaResponse);
      requestResponseMap.put(list, listResponse).build();

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap.build()).getSecurityGroupExtension().get();

      Set<SecurityGroup> groups = extension.listSecurityGroups();
      assertEquals(groups.size(), 1);
   }

   public void testListSecurityGroupsInLocation() {
      HttpRequest list = HttpRequest.builder().method("GET").endpoint(
              URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups")).headers(
              ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                      authToken).build()).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/securitygroup_list.json")).build();


      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess);
      requestResponseMap.put(extensionsOfNovaRequest, extensionsOfNovaResponse);
      requestResponseMap.put(list, listResponse).build();

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap.build()).getSecurityGroupExtension().get();

      Set<SecurityGroup> groups = extension.listSecurityGroupsInLocation(new LocationBuilder()
              .scope(LocationScope.ZONE)
              .id(zone)
              .description("zone")
              .build());
      assertEquals(groups.size(), 1);
   }

   public void testListSecurityGroupsForNode() {
      HttpRequest serverReq = HttpRequest.builder().method("GET").endpoint(
              URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-create-server-ext/8d0a6ca5-8849-4b3d-b86e-f24c92490ebb"))
              .headers(
                      ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                              authToken).build()).build();

      HttpResponse serverResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/server_with_security_groups_extension.json")).build();

      HttpRequest list = HttpRequest.builder().method("GET").endpoint(
              URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups")).headers(
              ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                      authToken).build()).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/securitygroup_list.json")).build();


      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess);
      requestResponseMap.put(extensionsOfNovaRequest, extensionsOfNovaResponse);
      requestResponseMap.put(serverReq, serverResponse);
      requestResponseMap.put(list, listResponse).build();

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap.build()).getSecurityGroupExtension().get();

      Set<SecurityGroup> groups = extension.listSecurityGroupsForNode(zone + "/8d0a6ca5-8849-4b3d-b86e-f24c92490ebb");
      assertEquals(groups.size(), 1);
   }

   public void testGetSecurityGroupById() {
      HttpRequest getSecurityGroup = HttpRequest.builder().method("GET").endpoint(
              URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups/160")).headers(
              ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                      authToken).build()).build();

      HttpResponse getSecurityGroupResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/securitygroup_details_extension.json")).build();

      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess);
      requestResponseMap.put(extensionsOfNovaRequest, extensionsOfNovaResponse);
      requestResponseMap.put(getSecurityGroup, getSecurityGroupResponse).build();

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap.build()).getSecurityGroupExtension().get();

      SecurityGroup group = extension.getSecurityGroupById(zone + "/160");
      assertEquals(group.getId(), "160");
   }

   public void testCreateSecurityGroup() {
      HttpRequest create = HttpRequest.builder().method("POST").endpoint(
              URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups")).headers(
              ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                      authToken).build())
              .payload(
                      payloadFromStringWithContentType(
                              "{\"security_group\":{\"name\":\"jclouds-test\",\"description\":\"jclouds-test\"}}",
                              "application/json")).build();

      HttpResponse createResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/securitygroup_created.json")).build();

      HttpRequest list = HttpRequest.builder().method("GET").endpoint(
              URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups")).headers(
              ImmutableMultimap.<String, String>builder().put("Accept", "application/json").put("X-Auth-Token",
                      authToken).build()).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/securitygroup_list_extension.json")).build();

      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess);
      requestResponseMap.put(extensionsOfNovaRequest, extensionsOfNovaResponse);
      requestResponseMap.put(create, createResponse);
      requestResponseMap.put(list, listResponse).build();

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap.build()).getSecurityGroupExtension().get();

      SecurityGroup group = extension.createSecurityGroup("test", new LocationBuilder()
              .scope(LocationScope.ZONE)
              .id(zone)
              .description("zone")
              .build());
      assertEquals(group.getId(), "160");
   }

   public void testRemoveSecurityGroup() {
      HttpRequest delete = HttpRequest.builder().method("DELETE").endpoint(
              URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups/160"))
              .headers(
                      ImmutableMultimap.<String, String>builder().put("Accept", "application/json")
                              .put("X-Auth-Token", authToken).build()).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(202).build();

      HttpRequest getSecurityGroup = HttpRequest.builder().method("GET").endpoint(
              URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups/160")).headers(
              ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                      authToken).build()).build();

      HttpResponse getSecurityGroupResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/securitygroup_details_extension.json")).build();

      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess);
      requestResponseMap.put(extensionsOfNovaRequest, extensionsOfNovaResponse);
      requestResponseMap.put(getSecurityGroup, getSecurityGroupResponse);
      requestResponseMap.put(delete, deleteResponse).build();

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap.build()).getSecurityGroupExtension().get();

      assertTrue(extension.removeSecurityGroup(zone + "/160"), "Expected removal of securitygroup to be successful");
   }

   public void testAddIpPermissionCidrFromIpPermission() {
      HttpRequest createRule = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-group-rules")
              .addHeader("Accept", "application/json")
              .addHeader("X-Auth-Token", authToken)
              .payload(
                      payloadFromStringWithContentType(
                              "{\"security_group_rule\":{\"parent_group_id\":\"160\",\"cidr\":\"10.2.6.0/24\",\"ip_protocol\":\"tcp\",\"from_port\":\"22\",\"to_port\":\"22\"}}",
                              "application/json")).build();

      HttpResponse createRuleResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/securitygrouprule_created_cidr.json")).build();

      HttpRequest getSecurityGroup = HttpRequest.builder().method("GET").endpoint(
              URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups/160")).headers(
              ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                      authToken).build()).build();

      HttpResponse getSecurityGroupNoRulesResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/securitygroup_details_extension_norules.json")).build();

      HttpResponse getSecurityGroupResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/securitygroup_details_extension.json")).build();


      SecurityGroupExtension extension = orderedRequestsSendResponses(ImmutableList.of(keystoneAuthWithUsernameAndPasswordAndTenantName,
              extensionsOfNovaRequest, getSecurityGroup, createRule, getSecurityGroup),
              ImmutableList.of(responseWithKeystoneAccess, extensionsOfNovaResponse, getSecurityGroupNoRulesResponse,
                      createRuleResponse, getSecurityGroupResponse)).getSecurityGroupExtension().get();

      IpPermission.Builder builder = IpPermission.builder();

      builder.ipProtocol(IpProtocol.TCP);
      builder.fromPort(22);
      builder.toPort(22);
      builder.cidrBlock("10.2.6.0/24");

      IpPermission perm = builder.build();

      SecurityGroup origGroup = extension.getSecurityGroupById(zone + "/160");

      assertNotNull(origGroup);
      SecurityGroup newGroup = extension.addIpPermission(perm, origGroup);

      assertNotNull(newGroup);
   }

   public void testAddIpPermissionCidrFromParams() {
      HttpRequest createRule = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-group-rules")
              .addHeader("Accept", "application/json")
              .addHeader("X-Auth-Token", authToken)
              .payload(
                      payloadFromStringWithContentType(
                              "{\"security_group_rule\":{\"parent_group_id\":\"160\",\"cidr\":\"10.2.6.0/24\",\"ip_protocol\":\"tcp\",\"from_port\":\"22\",\"to_port\":\"22\"}}",
                              "application/json")).build();

      HttpResponse createRuleResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/securitygrouprule_created_cidr.json")).build();

      HttpRequest getSecurityGroup = HttpRequest.builder().method("GET").endpoint(
              URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups/160")).headers(
              ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                      authToken).build()).build();

      HttpResponse getSecurityGroupNoRulesResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/securitygroup_details_extension_norules.json")).build();

      HttpResponse getSecurityGroupResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/securitygroup_details_extension.json")).build();


      SecurityGroupExtension extension = orderedRequestsSendResponses(ImmutableList.of(keystoneAuthWithUsernameAndPasswordAndTenantName,
              extensionsOfNovaRequest, getSecurityGroup, createRule, getSecurityGroup),
              ImmutableList.of(responseWithKeystoneAccess, extensionsOfNovaResponse, getSecurityGroupNoRulesResponse,
                      createRuleResponse, getSecurityGroupResponse)).getSecurityGroupExtension().get();

      SecurityGroup origGroup = extension.getSecurityGroupById(zone + "/160");

      assertNotNull(origGroup);
      SecurityGroup newGroup = extension.addIpPermission(IpProtocol.TCP,
              22,
              22,
              emptyMultimap(),
              ImmutableSet.of("10.2.6.0/24"),
              emptyStringSet(),
              origGroup);

      assertNotNull(newGroup);
   }

   public void testAddIpPermissionGroupFromIpPermission() {
      HttpRequest createRule = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-group-rules")
              .addHeader("Accept", "application/json")
              .addHeader("X-Auth-Token", authToken)
              .payload(
                      payloadFromStringWithContentType(
                              "{\"security_group_rule\":{\"group_id\":\"11111\",\"parent_group_id\":\"160\",\"ip_protocol\":\"tcp\",\"from_port\":\"22\",\"to_port\":\"22\"}}",
                              "application/json")).build();

      HttpResponse createRuleResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/securitygrouprule_created_group.json")).build();

      HttpRequest getSecurityGroup = HttpRequest.builder().method("GET").endpoint(
              URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups/160")).headers(
              ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                      authToken).build()).build();

      HttpResponse getSecurityGroupNoRulesResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/securitygroup_details_extension_norules.json")).build();

      HttpResponse getSecurityGroupResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/securitygroup_details_extension.json")).build();


      SecurityGroupExtension extension = orderedRequestsSendResponses(ImmutableList.of(keystoneAuthWithUsernameAndPasswordAndTenantName,
              extensionsOfNovaRequest, getSecurityGroup, createRule, getSecurityGroup),
              ImmutableList.of(responseWithKeystoneAccess, extensionsOfNovaResponse, getSecurityGroupNoRulesResponse,
                      createRuleResponse, getSecurityGroupResponse)).getSecurityGroupExtension().get();

      IpPermission.Builder builder = IpPermission.builder();

      builder.ipProtocol(IpProtocol.TCP);
      builder.fromPort(22);
      builder.toPort(22);
      builder.groupId("admin/11111");

      IpPermission perm = builder.build();

      SecurityGroup origGroup = extension.getSecurityGroupById(zone + "/160");

      assertNotNull(origGroup);
      SecurityGroup newGroup = extension.addIpPermission(perm, origGroup);

      assertNotNull(newGroup);
   }

   public void testAddIpPermissionGroupFromParams() {
      HttpRequest createRule = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-group-rules")
              .addHeader("Accept", "application/json")
              .addHeader("X-Auth-Token", authToken)
              .payload(
                      payloadFromStringWithContentType(
                              "{\"security_group_rule\":{\"group_id\":\"11111\",\"parent_group_id\":\"160\",\"ip_protocol\":\"tcp\",\"from_port\":\"22\",\"to_port\":\"22\"}}",
                              "application/json")).build();

      HttpResponse createRuleResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/securitygrouprule_created_group.json")).build();

      HttpRequest getSecurityGroup = HttpRequest.builder().method("GET").endpoint(
              URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups/160")).headers(
              ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                      authToken).build()).build();

      HttpResponse getSecurityGroupNoRulesResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/securitygroup_details_extension_norules.json")).build();

      HttpResponse getSecurityGroupResponse = HttpResponse.builder().statusCode(200).payload(
              payloadFromResource("/securitygroup_details_extension.json")).build();


      SecurityGroupExtension extension = orderedRequestsSendResponses(ImmutableList.of(keystoneAuthWithUsernameAndPasswordAndTenantName,
              extensionsOfNovaRequest, getSecurityGroup, createRule, getSecurityGroup),
              ImmutableList.of(responseWithKeystoneAccess, extensionsOfNovaResponse, getSecurityGroupNoRulesResponse,
                      createRuleResponse, getSecurityGroupResponse)).getSecurityGroupExtension().get();

      SecurityGroup origGroup = extension.getSecurityGroupById(zone + "/160");

      assertNotNull(origGroup);
      SecurityGroup newGroup = extension.addIpPermission(IpProtocol.TCP,
              22,
              22,
              emptyMultimap(),
              emptyStringSet(),
              ImmutableSet.of("admin/11111"),
              origGroup);

      assertNotNull(newGroup);
   }

   private Multimap<String, String> emptyMultimap() {
      return LinkedHashMultimap.create();
   }

   private Set<String> emptyStringSet() {
      return Sets.newLinkedHashSet();
   }

}
