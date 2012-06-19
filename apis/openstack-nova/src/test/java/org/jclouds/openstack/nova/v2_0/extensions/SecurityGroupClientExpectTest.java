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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.domain.Ingress;
import org.jclouds.openstack.nova.v2_0.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientExpectTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseSecurityGroupListTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseSecurityGroupTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests annotation parsing of {@code SecurityGroupAsyncClient}
 * 
 * @author Michael Arnold
 */
@Test(groups = "unit", testName = "SecurityGroupClientExpectTest")
public class SecurityGroupClientExpectTest extends BaseNovaClientExpectTest {
   public void testListSecurityGroupsWhenResponseIs2xx() throws Exception {
      HttpRequest listSecurityGroups = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups")).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse listSecurityGroupsResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/securitygroup_list.json")).build();

      NovaClient clientWhenSecurityGroupsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, listSecurityGroups,
               listSecurityGroupsResponse);

      assertEquals(clientWhenSecurityGroupsExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(clientWhenSecurityGroupsExist.getSecurityGroupExtensionForZone("az-1.region-a.geo-1").get()
               .listSecurityGroups().toString(), new ParseSecurityGroupListTest().expected().toString());
   }

   public void testListSecurityGroupsWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest listListSecurityGroups = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups")).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse listListSecurityGroupsResponse = HttpResponse.builder().statusCode(404).build();

      NovaClient clientWhenNoSecurityGroupsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, listListSecurityGroups,
               listListSecurityGroupsResponse);

      assertTrue(clientWhenNoSecurityGroupsExist.getSecurityGroupExtensionForZone("az-1.region-a.geo-1").get()
               .listSecurityGroups().isEmpty());
   }

   public void testGetSecurityGroupWhenResponseIs2xx() throws Exception {

      HttpRequest getSecurityGroup = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups/0")).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse getSecurityGroupResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/securitygroup_details.json")).build();

      NovaClient clientWhenSecurityGroupsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, getSecurityGroup,
               getSecurityGroupResponse);

      assertEquals(clientWhenSecurityGroupsExist.getSecurityGroupExtensionForZone("az-1.region-a.geo-1").get()
               .getSecurityGroup("0").toString(), new ParseSecurityGroupTest().expected().toString());
   }

   public void testGetSecurityGroupWhenResponseIs404() throws Exception {
      HttpRequest getSecurityGroup = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups/0")).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse getSecurityGroupResponse = HttpResponse.builder().statusCode(404).build();

      NovaClient clientWhenNoSecurityGroupsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, getSecurityGroup,
               getSecurityGroupResponse);

      assertNull(clientWhenNoSecurityGroupsExist.getSecurityGroupExtensionForZone("az-1.region-a.geo-1").get()
               .getSecurityGroup("0"));

   }

   public void testCreateSecurityGroupWhenResponseIs2xx() throws Exception {
      HttpRequest createSecurityGroup = HttpRequest.builder().method("POST").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups")).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build())
               .payload(
                        payloadFromStringWithContentType(
                                 "{\"security_group\":{\"name\":\"jclouds-test\",\"description\":\"jclouds-test\"}}",
                                 "application/json")).build();

      HttpResponse createSecurityGroupResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/securitygroup_created.json")).build();

      NovaClient clientWhenSecurityGroupsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, createSecurityGroup,
               createSecurityGroupResponse);

      assertEquals(clientWhenSecurityGroupsExist.getSecurityGroupExtensionForZone("az-1.region-a.geo-1").get()
               .createSecurityGroupWithNameAndDescription("jclouds-test", "jclouds-test").toString(),
               createSecurityGroupExpected().toString());
   }

   public void testDeleteSecurityGroupWhenResponseIs2xx() throws Exception {
      HttpRequest deleteSecurityGroup = HttpRequest.builder().method("DELETE").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-groups/160"))
               .headers(
                        ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                                 .put("X-Auth-Token", authToken).build()).build();

      HttpResponse deleteSecurityGroupResponse = HttpResponse.builder().statusCode(202).build();

      NovaClient clientWhenServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, deleteSecurityGroup,
               deleteSecurityGroupResponse);

      assertTrue(clientWhenServersExist.getSecurityGroupExtensionForZone("az-1.region-a.geo-1").get()
               .deleteSecurityGroup("160"));

   }

   public void testCreateSecurityGroupRuleForCidrBlockWhenResponseIs2xx() throws Exception {
      HttpRequest createSecurityGroupRule = HttpRequest
               .builder()
               .method("POST")
               .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-group-rules"))
               .headers(
                        ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put(
                                 "X-Auth-Token", authToken).build())
               .payload(
                        payloadFromStringWithContentType(
                                 "{\"security_group_rule\":{\"parent_group_id\":\"161\",\"cidr\":\"0.0.0.0/0\",\"ip_protocol\":\"tcp\",\"from_port\":\"80\",\"to_port\":\"8080\"}}",
                                 "application/json")).build();

      HttpResponse createSecurityGroupRuleResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/securitygrouprule_created.json")).build();

      NovaClient clientWhenSecurityGroupsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, createSecurityGroupRule,
               createSecurityGroupRuleResponse);

      assertEquals(clientWhenSecurityGroupsExist.getSecurityGroupExtensionForZone("az-1.region-a.geo-1").get()
               .createSecurityGroupRuleAllowingCidrBlock("161",
                        Ingress.builder().ipProtocol(IpProtocol.TCP).fromPort(80).toPort(8080).build(), "0.0.0.0/0")
               .toString(), createSecurityGroupRuleExpected().toString());
   }
   
   public void testCreateSecurityGroupRuleForSecurityGroupIdWhenResponseIs2xx() throws Exception {
      HttpRequest createSecurityGroupRule = HttpRequest
               .builder()
               .method("POST")
               .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-group-rules"))
               .headers(
                        ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put(
                                 "X-Auth-Token", authToken).build())
               .payload(
                        payloadFromStringWithContentType(
                                 "{\"security_group_rule\":{\"group_id\":\"999\",\"parent_group_id\":\"161\",\"ip_protocol\":\"tcp\",\"from_port\":\"80\",\"to_port\":\"8080\"}}",
                                 "application/json")).build();

      HttpResponse createSecurityGroupRuleResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/securitygrouprule_created.json")).build();

      NovaClient clientWhenSecurityGroupsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, createSecurityGroupRule,
               createSecurityGroupRuleResponse);

      assertEquals(clientWhenSecurityGroupsExist.getSecurityGroupExtensionForZone("az-1.region-a.geo-1").get()
               .createSecurityGroupRuleAllowingSecurityGroupId("161",
                        Ingress.builder().ipProtocol(IpProtocol.TCP).fromPort(80).toPort(8080).build(), "999")
               .toString(), createSecurityGroupRuleExpected().toString());
   }

   public void testDeleteSecurityGroupRuleWhenResponseIs2xx() throws Exception {
      HttpRequest deleteSecurityGroupRule = HttpRequest.builder().method("DELETE").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-security-group-rules/161"))
               .headers(
                        ImmutableMultimap.<String, String> builder().put("Accept", "*/*")
                                 .put("X-Auth-Token", authToken).build()).build();

      HttpResponse deleteSecurityGroupRuleResponse = HttpResponse.builder().statusCode(202).build();

      NovaClient clientWhenSecurityGroupsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, deleteSecurityGroupRule,
               deleteSecurityGroupRuleResponse);

      assertTrue(clientWhenSecurityGroupsExist.getSecurityGroupExtensionForZone("az-1.region-a.geo-1").get()
               .deleteSecurityGroupRule("161"));

   }

   private SecurityGroup createSecurityGroupExpected() {
      return SecurityGroup.builder().description("jclouds-test").id("160").name("jclouds-test").rules(
               ImmutableSet.<SecurityGroupRule> of()).tenantId("dev_16767499955063").build();
   }

   private SecurityGroupRule createSecurityGroupRuleExpected() {
      return SecurityGroupRule.builder().fromPort(80).id("218").ipProtocol(
               IpProtocol.TCP).ipRange("0.0.0.0/0").parentGroupId("161").toPort(8080).build();
   }

}
