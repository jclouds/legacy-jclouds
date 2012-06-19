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
package org.jclouds.openstack.nova.v2_0.functions;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.compute.functions.CreateSecurityGroupIfNeeded;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.SecurityGroupInZone;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneSecurityGroupNameAndPorts;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientExpectTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseComputeServiceTypicalSecurityGroupTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "CreateSecurityGroupIfNeededTest")
public class CreateSecurityGroupIfNeededTest extends BaseNovaClientExpectTest {
   HttpRequest createSecurityGroup = HttpRequest.builder().method("POST").endpoint(
            URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups")).headers(
            ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                     authToken).build())
            .payload(
                     payloadFromStringWithContentType(
                              "{\"security_group\":{\"name\":\"jclouds_mygroup\",\"description\":\"jclouds_mygroup\"}}",
                              "application/json")).build();

   public void testCreateNewGroup() throws Exception {

      Builder<HttpRequest, HttpResponse> builder = ImmutableMap.builder();
      
      builder.put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess);
      builder.put(extensionsOfNovaRequest, extensionsOfNovaResponse);
      int groupId = 2769;

      HttpResponse createSecurityGroupResponse = HttpResponse.builder().statusCode(200)
               .payload(
                        payloadFromStringWithContentType(
                                 String.format("{\"security_group\": {\"rules\": [], \"tenant_id\": \"37936628937291\", \"id\": %s, \"name\": \"jclouds_mygroup\", \"description\": \"jclouds_mygroup\"}}", groupId),
                                 "application/json; charset=UTF-8")).build();

      builder.put(createSecurityGroup, createSecurityGroupResponse);
      
      int ruleId = 10331;
      
      for (int port : ImmutableList.of(22,8080)){
         
         HttpRequest createCidrRule = HttpRequest.builder().method("POST").endpoint(
                  URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-group-rules")).headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                           authToken).build())
                  .payload(
                           payloadFromStringWithContentType(
                                    String.format("{\"security_group_rule\":{\"parent_group_id\":\"%s\",\"cidr\":\"0.0.0.0/0\",\"ip_protocol\":\"tcp\",\"from_port\":\"%d\",\"to_port\":\"%d\"}}",
                                                      groupId, port, port), "application/json")).build();
         
         HttpResponse createCidrRuleResponse = HttpResponse.builder().statusCode(200)
                  .payload(
                           payloadFromStringWithContentType(
                                    String.format("{\"security_group_rule\": {\"from_port\": %d, \"group\": {}, \"ip_protocol\": \"tcp\", \"to_port\": %d, \"parent_group_id\": %d, \"ip_range\": {\"cidr\": \"0.0.0.0/0\"}, \"id\": %d}}",
                                             port, port, groupId, ruleId++), "application/json; charset=UTF-8")).build();
         
         builder.put(createCidrRule, createCidrRuleResponse);

         HttpRequest createSelfRule = HttpRequest.builder().method("POST").endpoint(
                  URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-group-rules")).headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                           authToken).build())
                  .payload(
                           payloadFromStringWithContentType(
                                    String.format("{\"security_group_rule\":{\"group_id\":\"%d\",\"parent_group_id\":\"%d\",\"ip_protocol\":\"tcp\",\"from_port\":\"%d\",\"to_port\":\"%d\"}}",
                                                      groupId, groupId, port, port), "application/json")).build();

         // note server responds with group name in the rule!!
         HttpResponse createSelfRuleResponse = HttpResponse.builder().statusCode(200)
                  .payload(
                           payloadFromStringWithContentType(
                                    String.format("{\"security_group_rule\": {\"from_port\": %d, \"group\": {\"tenant_id\": \"37936628937291\", \"name\": \"jclouds_mygroup\"}, \"ip_protocol\": \"tcp\", \"to_port\": %d, \"parent_group_id\": %d, \"ip_range\": {}, \"id\": %d}}",
                                             port, port, groupId, ruleId++), "application/json; charset=UTF-8")).build();
         
         builder.put(createSelfRule, createSelfRuleResponse);
      }
      
      HttpRequest getSecurityGroup = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups/" + groupId)).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse getSecurityGroupResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/securitygroup_details_computeservice_typical.json")).build();
      
      builder.put(getSecurityGroup, getSecurityGroupResponse);

      NovaClient clientCanCreateSecurityGroup = requestsSendResponses(builder.build());

      CreateSecurityGroupIfNeeded fn = new CreateSecurityGroupIfNeeded(clientCanCreateSecurityGroup);

      // we can find it
      assertEquals(fn.apply(
               new ZoneSecurityGroupNameAndPorts("az-1.region-a.geo-1", "jclouds_mygroup", ImmutableSet.of(22, 8080)))
               .toString(), new SecurityGroupInZone(new ParseComputeServiceTypicalSecurityGroupTest().expected(),
               "az-1.region-a.geo-1").toString());

   }

   public void testReturnExistingGroupOnAlreadyExists() throws Exception {

      Builder<HttpRequest, HttpResponse> builder = ImmutableMap.builder();
      
      builder.put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess);
      builder.put(extensionsOfNovaRequest, extensionsOfNovaResponse);

      HttpResponse createSecurityGroupResponse = HttpResponse.builder().statusCode(400)
               .payload(
                        payloadFromStringWithContentType(
                                 "{\"badRequest\": {\"message\": \"Security group test already exists\", \"code\": 400}}",
                                 "application/json; charset=UTF-8")).build();

      builder.put(createSecurityGroup, createSecurityGroupResponse);
          
      HttpRequest listSecurityGroups = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups")).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse listSecurityGroupsResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/securitygroup_list_details_computeservice_typical.json")).build();

      builder.put(listSecurityGroups, listSecurityGroupsResponse);

      NovaClient clientWhenSecurityGroupsExist = requestsSendResponses(builder.build());

      CreateSecurityGroupIfNeeded fn = new CreateSecurityGroupIfNeeded(clientWhenSecurityGroupsExist);

      // we can find it
      assertEquals(fn.apply(
               new ZoneSecurityGroupNameAndPorts("az-1.region-a.geo-1", "jclouds_mygroup", ImmutableSet.of(22, 8080)))
               .toString(), new SecurityGroupInZone(new ParseComputeServiceTypicalSecurityGroupTest().expected(),
               "az-1.region-a.geo-1").toString());

   }
}
