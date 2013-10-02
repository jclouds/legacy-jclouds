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
package org.jclouds.ec2.compute.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.internal.BaseEC2ComputeServiceExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "EC2SecurityGroupExtensionExpectTest")
public class EC2SecurityGroupExtensionExpectTest extends BaseEC2ComputeServiceExpectTest {
   
   public void testListSecurityGroups() {
      HttpRequest describeSecurityGroupsAllRequest = 
         formSigner.filter(HttpRequest.builder()
                           .method("POST")
                           .endpoint("https://ec2." + region + ".amazonaws.com/")
                           .addHeader("Host", "ec2." + region + ".amazonaws.com")
                           .addFormParam("Action", "DescribeSecurityGroups").build());
      
      HttpResponse describeSecurityGroupsAllResponse = 
         HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType(
                                                     "/describe_securitygroups_extension_new.xml", MediaType.APPLICATION_XML)).build();
      
      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeSecurityGroupsAllRequest, describeSecurityGroupsAllResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);

      requestResponseMap.put(authorizeSecurityGroupIngressRequest22, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroup, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(describeInstanceRequest, describeInstanceResponse);
      

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap.build()).getSecurityGroupExtension().get();
      
      Set<SecurityGroup> groups = extension.listSecurityGroups();
      assertEquals(2, groups.size());
   }

   public void testListSecurityGroupsInLocation() {
      HttpRequest describeSecurityGroupsAllRequest = 
         formSigner.filter(HttpRequest.builder()
                           .method("POST")
                           .endpoint("https://ec2." + region + ".amazonaws.com/")
                           .addHeader("Host", "ec2." + region + ".amazonaws.com")
                           .addFormParam("Action", "DescribeSecurityGroups").build());
      
      HttpResponse describeSecurityGroupsAllResponse = 
         HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType(
                                                     "/describe_securitygroups_extension_new.xml", MediaType.APPLICATION_XML)).build();
      
      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeSecurityGroupsAllRequest, describeSecurityGroupsAllResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);

      requestResponseMap.put(authorizeSecurityGroupIngressRequest22, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroup, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(describeInstanceRequest, describeInstanceResponse);
      

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap.build()).getSecurityGroupExtension().get();
      
      Set<SecurityGroup> groups = extension.listSecurityGroupsInLocation(new LocationBuilder()
                                                                         .scope(LocationScope.REGION)
                                                                         .id(region)
                                                                         .description("region")
                                                                         .build());
      assertEquals(2, groups.size());
   }

   
   public void testListSecurityGroupsForNode() {
      HttpRequest describeSecurityGroupsSingleRequest = 
         formSigner.filter(HttpRequest.builder()
                           .method("POST")
                           .endpoint("https://ec2." + region + ".amazonaws.com/")
                           .addHeader("Host", "ec2." + region + ".amazonaws.com")
                           .addFormParam("Action", "DescribeSecurityGroups")
                           .addFormParam("GroupName.1", "sg-3c6ef654").build());
      
      HttpResponse describeSecurityGroupsSingleResponse = 
         HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType(
                                                     "/describe_securitygroups_extension_single.xml", MediaType.APPLICATION_XML)).build();
      
      HttpResponse describeInstanceWithSGResponse = 
            HttpResponse.builder().statusCode(200)
                           .payload(payloadFromResourceWithContentType(
                                 "/describe_instances_running_securitygroups.xml", MediaType.APPLICATION_XML)).build();

      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeSecurityGroupsSingleRequest, describeSecurityGroupsSingleResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);

      requestResponseMap.put(authorizeSecurityGroupIngressRequest22, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroup, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(describeInstanceRequest, describeInstanceWithSGResponse);
      

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap.build()).getSecurityGroupExtension().get();
      
      Set<SecurityGroup> groups = extension.listSecurityGroupsForNode(new RegionAndName(region, "i-2baa5550").slashEncode());
      assertEquals(1, groups.size());
   }

   public void testGetSecurityGroupById() {
      HttpRequest describeSecurityGroupsSingleRequest = 
         formSigner.filter(HttpRequest.builder()
                           .method("POST")
                           .endpoint("https://ec2." + region + ".amazonaws.com/")
                           .addHeader("Host", "ec2." + region + ".amazonaws.com")
                           .addFormParam("Action", "DescribeSecurityGroups")
                           .addFormParam("GroupName.1", "jclouds#some-group").build());
      
      HttpResponse describeSecurityGroupsSingleResponse = 
         HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType(
                                                     "/describe_securitygroups_extension_single.xml", MediaType.APPLICATION_XML)).build();
      

      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeSecurityGroupsSingleRequest, describeSecurityGroupsSingleResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);

      requestResponseMap.put(authorizeSecurityGroupIngressRequest22, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroup, authorizeSecurityGroupIngressResponse);
      

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap.build()).getSecurityGroupExtension().get();
      
      SecurityGroup group = extension.getSecurityGroupById(new RegionAndName(region, "jclouds#some-group").slashEncode());
      assertEquals("sg-3c6ef654", group.getProviderId());
      assertEquals(region + "/jclouds#some-group", group.getId());
   }

   public void testCreateSecurityGroup() {
      HttpRequest createSecurityGroupExtRequest = 
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "CreateSecurityGroup")
                          .addFormParam("GroupDescription", "jclouds#some-group")
                          .addFormParam("GroupName", "jclouds#some-group").build());

      HttpRequest describeSecurityGroupsSingleRequest = 
         formSigner.filter(HttpRequest.builder()
                           .method("POST")
                           .endpoint("https://ec2." + region + ".amazonaws.com/")
                           .addHeader("Host", "ec2." + region + ".amazonaws.com")
                           .addFormParam("Action", "DescribeSecurityGroups")
                           .addFormParam("GroupName.1", "jclouds#some-group").build());

      HttpResponse describeSecurityGroupsSingleResponse = 
         HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType(
                                                     "/describe_securitygroups_extension_single.xml", MediaType.APPLICATION_XML)).build();
      

      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeSecurityGroupsSingleRequest, describeSecurityGroupsSingleResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupExtRequest, createSecurityGroupResponse);

      requestResponseMap.put(authorizeSecurityGroupIngressRequest22, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroup, authorizeSecurityGroupIngressResponse);
      

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap.build()).getSecurityGroupExtension().get();
      
      SecurityGroup group = extension.createSecurityGroup("some-group", new LocationBuilder()
                                                          .scope(LocationScope.REGION)
                                                          .id(region)
                                                          .description("region")
                                                          .build());
      
      assertEquals("sg-3c6ef654", group.getProviderId());
      assertEquals(region + "/jclouds#some-group", group.getId());
   }

   public void testRemoveSecurityGroup() {
      HttpRequest describeSecurityGroupsSingleRequest = 
         formSigner.filter(HttpRequest.builder()
                           .method("POST")
                           .endpoint("https://ec2." + region + ".amazonaws.com/")
                           .addHeader("Host", "ec2." + region + ".amazonaws.com")
                           .addFormParam("Action", "DescribeSecurityGroups")
                           .addFormParam("GroupName.1", "jclouds#some-group").build());
      
      HttpResponse describeSecurityGroupsSingleResponse = 
         HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType(
                                                     "/describe_securitygroups_extension_single.xml", MediaType.APPLICATION_XML)).build();
      
      HttpRequest deleteSecurityGroupRequest = 
         formSigner.filter(HttpRequest.builder()
                           .method("POST")
                           .endpoint("https://ec2." + region + ".amazonaws.com/")
                           .addHeader("Host", "ec2." + region + ".amazonaws.com")
                           .addFormParam("Action", "DeleteSecurityGroup")
                           .addFormParam("GroupName", "jclouds#some-group").build());
      
      HttpResponse deleteSecurityGroupResponse = 
         HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType(
                                                     "/delete_securitygroup.xml", MediaType.APPLICATION_XML)).build();

      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeSecurityGroupsSingleRequest, describeSecurityGroupsSingleResponse);
      requestResponseMap.put(deleteSecurityGroupRequest, deleteSecurityGroupResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);

      requestResponseMap.put(authorizeSecurityGroupIngressRequest22, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroup, authorizeSecurityGroupIngressResponse);
      

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap.build()).getSecurityGroupExtension().get();
      
      assertTrue(extension.removeSecurityGroup(new RegionAndName(region, "jclouds#some-group").slashEncode()));
   }

   public void testAddIpPermissionCidrFromIpPermission() {
      HttpRequest describeSecurityGroupsSingleRequest = 
         formSigner.filter(HttpRequest.builder()
                           .method("POST")
                           .endpoint("https://ec2." + region + ".amazonaws.com/")
                           .addHeader("Host", "ec2." + region + ".amazonaws.com")
                           .addFormParam("Action", "DescribeSecurityGroups")
                           .addFormParam("GroupName.1", "jclouds#some-group").build());
      
      HttpResponse describeSecurityGroupsSingleResponse = 
         HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType(
                                                     "/describe_securitygroups_extension_cidr.xml", MediaType.APPLICATION_XML)).build();
      

      HttpRequest authorizeSecurityGroupIngressRequestRange = 
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "AuthorizeSecurityGroupIngress")
                          .addFormParam("CidrIp", "0.0.0.0/0")
                          .addFormParam("FromPort", "22")
                          .addFormParam("ToPort", "40")
                          .addFormParam("GroupName", "jclouds#some-group")
                          .addFormParam("IpProtocol", "tcp").build());

      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeSecurityGroupsSingleRequest, describeSecurityGroupsSingleResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);

      requestResponseMap.put(authorizeSecurityGroupIngressRequestRange, authorizeSecurityGroupIngressResponse);

      IpPermission.Builder builder = IpPermission.builder();

      builder.ipProtocol(IpProtocol.TCP);
      builder.fromPort(22);
      builder.toPort(40);
      builder.cidrBlock("0.0.0.0/0");

      IpPermission perm = builder.build();

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap.build()).getSecurityGroupExtension().get();

      SecurityGroupBuilder groupBuilder = new SecurityGroupBuilder();
      groupBuilder.id("jclouds#some-group");
      groupBuilder.providerId("sg-3c6ef654");
      groupBuilder.name("jclouds#some-group");
      groupBuilder.location(new LocationBuilder()
                            .scope(LocationScope.REGION)
                            .id(region)
                            .description("region")
                            .build());
      
      SecurityGroup origGroup = groupBuilder.build();

      SecurityGroup newGroup = extension.addIpPermission(perm, origGroup);

      assertEquals(1, newGroup.getIpPermissions().size());

      IpPermission newPerm = Iterables.getOnlyElement(newGroup.getIpPermissions());

      assertNotNull(newPerm);
      assertEquals(IpProtocol.TCP, newPerm.getIpProtocol());
      assertEquals(22, newPerm.getFromPort());
      assertEquals(40, newPerm.getToPort());
      assertEquals(1, newPerm.getCidrBlocks().size());
      assertTrue(newPerm.getCidrBlocks().contains("0.0.0.0/0"));
   }

   public void testAddIpPermissionCidrFromParams() {
      HttpRequest describeSecurityGroupsSingleRequest = 
         formSigner.filter(HttpRequest.builder()
                           .method("POST")
                           .endpoint("https://ec2." + region + ".amazonaws.com/")
                           .addHeader("Host", "ec2." + region + ".amazonaws.com")
                           .addFormParam("Action", "DescribeSecurityGroups")
                           .addFormParam("GroupName.1", "jclouds#some-group").build());
      
      HttpResponse describeSecurityGroupsSingleResponse = 
         HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType(
                                                     "/describe_securitygroups_extension_cidr.xml", MediaType.APPLICATION_XML)).build();
      

      HttpRequest authorizeSecurityGroupIngressRequestRange = 
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "AuthorizeSecurityGroupIngress")
                          .addFormParam("CidrIp", "0.0.0.0/0")
                          .addFormParam("FromPort", "22")
                          .addFormParam("ToPort", "40")
                          .addFormParam("GroupName", "jclouds#some-group")
                          .addFormParam("IpProtocol", "tcp").build());

      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeSecurityGroupsSingleRequest, describeSecurityGroupsSingleResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);

      requestResponseMap.put(authorizeSecurityGroupIngressRequestRange, authorizeSecurityGroupIngressResponse);

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap.build()).getSecurityGroupExtension().get();

      SecurityGroupBuilder groupBuilder = new SecurityGroupBuilder();
      groupBuilder.id("jclouds#some-group");
      groupBuilder.providerId("sg-3c6ef654");
      groupBuilder.name("jclouds#some-group");
      groupBuilder.location(new LocationBuilder()
                            .scope(LocationScope.REGION)
                            .id(region)
                            .description("region")
                            .build());
      
      SecurityGroup origGroup = groupBuilder.build();

      SecurityGroup newGroup = extension.addIpPermission(IpProtocol.TCP,
                                                         22,
                                                         40,
                                                         emptyMultimap(),
                                                         ImmutableSet.of("0.0.0.0/0"),
                                                         emptyStringSet(),
                                                         origGroup);

      assertEquals(1, newGroup.getIpPermissions().size());

      IpPermission newPerm = Iterables.getOnlyElement(newGroup.getIpPermissions());

      assertNotNull(newPerm);
      assertEquals(IpProtocol.TCP, newPerm.getIpProtocol());
      assertEquals(22, newPerm.getFromPort());
      assertEquals(40, newPerm.getToPort());
      assertEquals(1, newPerm.getCidrBlocks().size());
      assertTrue(newPerm.getCidrBlocks().contains("0.0.0.0/0"));
   }


   public void testAddIpPermissionGroupFromIpPermission() {
      HttpRequest describeSecurityGroupsSingleRequest = 
         formSigner.filter(HttpRequest.builder()
                           .method("POST")
                           .endpoint("https://ec2." + region + ".amazonaws.com/")
                           .addHeader("Host", "ec2." + region + ".amazonaws.com")
                           .addFormParam("Action", "DescribeSecurityGroups")
                           .addFormParam("GroupName.1", "jclouds#some-group").build());
      
      HttpResponse describeSecurityGroupsSingleResponse = 
         HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType(
                                                     "/describe_securitygroups_extension_group.xml", MediaType.APPLICATION_XML)).build();
      

      HttpRequest authorizeSecurityGroupIngressRequestGroupTenant = 
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "AuthorizeSecurityGroupIngress")
                          .addFormParam("SourceSecurityGroupName", "jclouds#some-group")
                          .addFormParam("SourceSecurityGroupOwnerId", "993194456877")
                          .addFormParam("GroupName", "jclouds#some-group").build());

      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeSecurityGroupsSingleRequest, describeSecurityGroupsSingleResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);

      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroupTenant, authorizeSecurityGroupIngressResponse);

      IpPermission.Builder builder = IpPermission.builder();

      builder.ipProtocol(IpProtocol.TCP);
      builder.fromPort(22);
      builder.toPort(40);
      builder.tenantIdGroupNamePair("993194456877", "jclouds#some-group");

      IpPermission perm = builder.build();

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap.build()).getSecurityGroupExtension().get();

      SecurityGroupBuilder groupBuilder = new SecurityGroupBuilder();
      groupBuilder.id("jclouds#some-group");
      groupBuilder.providerId("sg-3c6ef654");
      groupBuilder.name("jclouds#some-group");
      groupBuilder.location(new LocationBuilder()
                            .scope(LocationScope.REGION)
                            .id(region)
                            .description("region")
                            .build());
      groupBuilder.ownerId("993194456877");
      
      SecurityGroup origGroup = groupBuilder.build();

      SecurityGroup newGroup = extension.addIpPermission(perm, origGroup);

      assertEquals(1, newGroup.getIpPermissions().size());

      IpPermission newPerm = Iterables.getOnlyElement(newGroup.getIpPermissions());

      assertNotNull(newPerm);
      assertEquals(IpProtocol.TCP, newPerm.getIpProtocol());
      assertEquals(22, newPerm.getFromPort());
      assertEquals(40, newPerm.getToPort());
      assertEquals(0, newPerm.getCidrBlocks().size());
      assertEquals(1, newPerm.getTenantIdGroupNamePairs().size());
      assertTrue(newPerm.getTenantIdGroupNamePairs().keySet().contains(origGroup.getOwnerId()));
      assertTrue(newPerm.getTenantIdGroupNamePairs().values().contains(origGroup.getName()));
   }

   public void testAddIpPermissionGroupFromParams() {
      HttpRequest describeSecurityGroupsSingleRequest = 
         formSigner.filter(HttpRequest.builder()
                           .method("POST")
                           .endpoint("https://ec2." + region + ".amazonaws.com/")
                           .addHeader("Host", "ec2." + region + ".amazonaws.com")
                           .addFormParam("Action", "DescribeSecurityGroups")
                           .addFormParam("GroupName.1", "jclouds#some-group").build());
      
      HttpResponse describeSecurityGroupsSingleResponse = 
         HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType(
                                                     "/describe_securitygroups_extension_group.xml", MediaType.APPLICATION_XML)).build();
      

      HttpRequest authorizeSecurityGroupIngressRequestGroupTenant = 
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "AuthorizeSecurityGroupIngress")
                          .addFormParam("SourceSecurityGroupName", "jclouds#some-group")
                          .addFormParam("SourceSecurityGroupOwnerId", "993194456877")
                          .addFormParam("GroupName", "jclouds#some-group").build());

      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeSecurityGroupsSingleRequest, describeSecurityGroupsSingleResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);

      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroupTenant, authorizeSecurityGroupIngressResponse);

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap.build()).getSecurityGroupExtension().get();

      SecurityGroupBuilder groupBuilder = new SecurityGroupBuilder();
      groupBuilder.id("jclouds#some-group");
      groupBuilder.providerId("sg-3c6ef654");
      groupBuilder.name("jclouds#some-group");
      groupBuilder.ownerId("993194456877");
      groupBuilder.location(new LocationBuilder()
                            .scope(LocationScope.REGION)
                            .id(region)
                            .description("region")
                            .build());
      
      SecurityGroup origGroup = groupBuilder.build();

      ImmutableMultimap.Builder<String, String> permBuilder = ImmutableMultimap.builder();
      permBuilder.put(origGroup.getOwnerId(), origGroup.getName());
      
      SecurityGroup newGroup = extension.addIpPermission(IpProtocol.TCP,
                                                         22,
                                                         40,
                                                         permBuilder.build(),
                                                         emptyStringSet(),
                                                         emptyStringSet(),
                                                         origGroup);

      assertEquals(1, newGroup.getIpPermissions().size());

      IpPermission newPerm = Iterables.getOnlyElement(newGroup.getIpPermissions());

      assertNotNull(newPerm);
      assertEquals(IpProtocol.TCP, newPerm.getIpProtocol());
      assertEquals(22, newPerm.getFromPort());
      assertEquals(40, newPerm.getToPort());
      assertEquals(0, newPerm.getCidrBlocks().size());
      assertEquals(1, newPerm.getTenantIdGroupNamePairs().size());
      assertTrue(newPerm.getTenantIdGroupNamePairs().keySet().contains(origGroup.getOwnerId()));
      assertTrue(newPerm.getTenantIdGroupNamePairs().values().contains(origGroup.getName()));
   }

   private Multimap<String, String> emptyMultimap() {
      return LinkedHashMultimap.create();
   }

   private Set<String> emptyStringSet() {
      return Sets.newLinkedHashSet();
   }
}
