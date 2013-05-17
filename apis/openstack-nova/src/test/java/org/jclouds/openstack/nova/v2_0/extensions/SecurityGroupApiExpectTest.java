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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Ingress;
import org.jclouds.openstack.nova.v2_0.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseSecurityGroupListTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseSecurityGroupTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests annotation parsing of {@code SecurityGroupAsyncApi}
 * 
 * @author Michael Arnold
 */
@Test(groups = "unit", testName = "SecurityGroupApiExpectTest")
public class SecurityGroupApiExpectTest extends BaseNovaApiExpectTest {
   public void testListSecurityGroupsWhenResponseIs2xx() throws Exception {
      HttpRequest list = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups")).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/securitygroup_list.json")).build();

      NovaApi apiWhenSecurityGroupsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, list,
               listResponse);

      assertEquals(apiWhenSecurityGroupsExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1", "az-2.region-a.geo-1", "az-3.region-a.geo-1"));

      assertEquals(apiWhenSecurityGroupsExist.getSecurityGroupExtensionForZone("az-1.region-a.geo-1").get()
               .list().toString(), new ParseSecurityGroupListTest().expected().toString());
   }

   public void testListSecurityGroupsWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest listListSecurityGroups = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups")).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse listListSecurityGroupsResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenNoSecurityGroupsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, listListSecurityGroups,
               listListSecurityGroupsResponse);

      assertTrue(apiWhenNoSecurityGroupsExist.getSecurityGroupExtensionForZone("az-1.region-a.geo-1").get()
               .list().isEmpty());
   }

   public void testGetSecurityGroupWhenResponseIs2xx() throws Exception {

      HttpRequest getSecurityGroup = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups/0")).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse getSecurityGroupResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/securitygroup_details.json")).build();

      NovaApi apiWhenSecurityGroupsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, getSecurityGroup,
               getSecurityGroupResponse);

      assertEquals(apiWhenSecurityGroupsExist.getSecurityGroupExtensionForZone("az-1.region-a.geo-1").get()
               .get("0").toString(), new ParseSecurityGroupTest().expected().toString());
   }

   public void testGetSecurityGroupWhenResponseIs404() throws Exception {
      HttpRequest getSecurityGroup = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups/0")).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse getSecurityGroupResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenNoSecurityGroupsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, getSecurityGroup,
               getSecurityGroupResponse);

      assertNull(apiWhenNoSecurityGroupsExist.getSecurityGroupExtensionForZone("az-1.region-a.geo-1").get()
               .get("0"));

   }

   public void testCreateSecurityGroupWhenResponseIs2xx() throws Exception {
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

      NovaApi apiWhenSecurityGroupsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, create,
               createResponse);

      assertEquals(apiWhenSecurityGroupsExist.getSecurityGroupExtensionForZone("az-1.region-a.geo-1").get()
               .createWithDescription("jclouds-test", "jclouds-test").toString(),
               createExpected().toString());
   }

   public void testDeleteSecurityGroupWhenResponseIs2xx() throws Exception {
      HttpRequest delete = HttpRequest.builder().method("DELETE").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups/160"))
               .headers(
                        ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                                 .put("X-Auth-Token", authToken).build()).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(202).build();

      NovaApi apiWhenServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, delete,
               deleteResponse);

      assertTrue(apiWhenServersExist.getSecurityGroupExtensionForZone("az-1.region-a.geo-1").get()
               .delete("160"));

   }

   public void testCreateSecurityGroupRuleForCidrBlockWhenResponseIs2xx() throws Exception {
      HttpRequest createRule = HttpRequest
               .builder()
               .method("POST")
               .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-group-rules")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
               .payload(
                        payloadFromStringWithContentType(
                                 "{\"security_group_rule\":{\"parent_group_id\":\"161\",\"cidr\":\"0.0.0.0/0\",\"ip_protocol\":\"tcp\",\"from_port\":\"80\",\"to_port\":\"8080\"}}",
                                 "application/json")).build();

      HttpResponse createRuleResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/securitygrouprule_created.json")).build();

      NovaApi apiWhenSecurityGroupsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, createRule,
               createRuleResponse);

      assertEquals(apiWhenSecurityGroupsExist.getSecurityGroupExtensionForZone("az-1.region-a.geo-1").get()
               .createRuleAllowingCidrBlock("161",
                        Ingress.builder().ipProtocol(IpProtocol.TCP).fromPort(80).toPort(8080).build(), "0.0.0.0/0")
               .toString(), createRuleExpected().toString());
   }
   
   public void testCreateSecurityGroupRuleForSecurityGroupIdWhenResponseIs2xx() throws Exception {
      HttpRequest createRule = HttpRequest
               .builder()
               .method("POST")
               .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-group-rules")
               .addHeader("Accept", "application/json")
               .addHeader("X-Auth-Token", authToken)
               .payload(
                        payloadFromStringWithContentType(
                                 "{\"security_group_rule\":{\"group_id\":\"999\",\"parent_group_id\":\"161\",\"ip_protocol\":\"tcp\",\"from_port\":\"80\",\"to_port\":\"8080\"}}",
                                 "application/json")).build();

      HttpResponse createRuleResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/securitygrouprule_created.json")).build();

      NovaApi apiWhenSecurityGroupsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, createRule,
               createRuleResponse);

      assertEquals(apiWhenSecurityGroupsExist.getSecurityGroupExtensionForZone("az-1.region-a.geo-1").get()
               .createRuleAllowingSecurityGroupId("161",
                        Ingress.builder().ipProtocol(IpProtocol.TCP).fromPort(80).toPort(8080).build(), "999")
               .toString(), createRuleExpected().toString());
   }

   public void testDeleteSecurityGroupRuleWhenResponseIs2xx() throws Exception {
      HttpRequest deleteRule = HttpRequest.builder().method("DELETE").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-group-rules/161"))
               .headers(
                        ImmutableMultimap.<String, String> builder().put("Accept", "*/*")
                                 .put("X-Auth-Token", authToken).build()).build();

      HttpResponse deleteRuleResponse = HttpResponse.builder().statusCode(202).build();

      NovaApi apiWhenSecurityGroupsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, deleteRule,
               deleteRuleResponse);

      assertTrue(apiWhenSecurityGroupsExist.getSecurityGroupExtensionForZone("az-1.region-a.geo-1").get()
               .deleteRule("161"));

   }

   private SecurityGroup createExpected() {
      return SecurityGroup.builder().description("jclouds-test").id("160").name("jclouds-test").rules(
               ImmutableSet.<SecurityGroupRule> of()).tenantId("dev_16767499955063").build();
   }

   private SecurityGroupRule createRuleExpected() {
      return SecurityGroupRule.builder().fromPort(80).id("218").ipProtocol(
               IpProtocol.TCP).ipRange("0.0.0.0/0").parentGroupId("161").toPort(8080).build();
   }

}
