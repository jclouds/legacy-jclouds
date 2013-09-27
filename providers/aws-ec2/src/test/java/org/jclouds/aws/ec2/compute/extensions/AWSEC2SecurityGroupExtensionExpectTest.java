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
package org.jclouds.aws.ec2.compute.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.aws.ec2.compute.internal.BaseAWSEC2ComputeServiceExpectTest;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.SecurityGroupBuilder;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
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
 * @author Andrew Bayer
 */
@Test(groups = "unit", testName = "AWSEC2SecurityGroupExtensionExpectTest")
public class AWSEC2SecurityGroupExtensionExpectTest extends BaseAWSEC2ComputeServiceExpectTest {

   public void testAddIpPermissionCidrFromIpPermission() {
      HttpRequest describeSecurityGroupsSingleRequest = 
         formSigner.filter(HttpRequest.builder()
                           .method("POST")
                           .endpoint("https://ec2." + region + ".amazonaws.com/")
                           .addHeader("Host", "ec2." + region + ".amazonaws.com")
                           .addFormParam("Action", "DescribeSecurityGroups")
                           .addFormParam("GroupId.1", "sg-3c6ef654").build());
      
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
                                 .addFormParam("GroupId", "sg-3c6ef654")
                                 .addFormParam("IpPermissions.0.FromPort", "22")
                                 .addFormParam("IpPermissions.0.IpProtocol", "tcp")
                                 .addFormParam("IpPermissions.0.IpRanges.0.CidrIp", "0.0.0.0/0")
                                 .addFormParam("IpPermissions.0.ToPort", "40")
                                 .build());
                                 
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
      groupBuilder.id("us-east-1/sg-3c6ef654");
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
                           .addFormParam("GroupId.1", "sg-3c6ef654").build());
      
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
                                 .addFormParam("GroupId", "sg-3c6ef654")
                                 .addFormParam("IpPermissions.0.FromPort", "22")
                                 .addFormParam("IpPermissions.0.IpProtocol", "tcp")
                                 .addFormParam("IpPermissions.0.IpRanges.0.CidrIp", "0.0.0.0/0")
                                 .addFormParam("IpPermissions.0.ToPort", "40")
                                 .build());
      
      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeSecurityGroupsSingleRequest, describeSecurityGroupsSingleResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);

      requestResponseMap.put(authorizeSecurityGroupIngressRequestRange, authorizeSecurityGroupIngressResponse);

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap.build()).getSecurityGroupExtension().get();

      SecurityGroupBuilder groupBuilder = new SecurityGroupBuilder();
      groupBuilder.id("us-east-1/sg-3c6ef654");
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
                           .addFormParam("GroupId.1", "sg-3c6ef654").build());
      
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
                                 .addFormParam("GroupId", "sg-3c6ef654")
                                 .addFormParam("IpPermissions.0.FromPort", "22")
                                 .addFormParam("IpPermissions.0.Groups.0.GroupId", "sg-3c6ef654")
                                 .addFormParam("IpPermissions.0.Groups.0.UserId", "993194456877")
                                 .addFormParam("IpPermissions.0.IpProtocol", "tcp")
                                 .addFormParam("IpPermissions.0.ToPort", "40")
                                 .build());

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
      builder.tenantIdGroupNamePair("993194456877", "sg-3c6ef654");

      IpPermission perm = builder.build();

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap.build()).getSecurityGroupExtension().get();

      SecurityGroupBuilder groupBuilder = new SecurityGroupBuilder();
      groupBuilder.id("us-east-1/sg-3c6ef654");
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
      assertTrue(newPerm.getTenantIdGroupNamePairs().values().contains(origGroup.getProviderId()));
   }


   public void testAddIpPermissionGroupFromParams() {
      HttpRequest describeSecurityGroupsSingleRequest = 
         formSigner.filter(HttpRequest.builder()
                           .method("POST")
                           .endpoint("https://ec2." + region + ".amazonaws.com/")
                           .addHeader("Host", "ec2." + region + ".amazonaws.com")
                           .addFormParam("Action", "DescribeSecurityGroups")
                           .addFormParam("GroupId.1", "sg-3c6ef654").build());
      
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
                                 .addFormParam("GroupId", "sg-3c6ef654")
                                 .addFormParam("IpPermissions.0.FromPort", "22")
                                 .addFormParam("IpPermissions.0.Groups.0.GroupId", "sg-3c6ef654")
                                 .addFormParam("IpPermissions.0.Groups.0.UserId", "993194456877")
                                 .addFormParam("IpPermissions.0.IpProtocol", "tcp")
                                 .addFormParam("IpPermissions.0.ToPort", "40")
                                 .build());

      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeSecurityGroupsSingleRequest, describeSecurityGroupsSingleResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);

      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroupTenant, authorizeSecurityGroupIngressResponse);

      SecurityGroupExtension extension = requestsSendResponses(requestResponseMap.build()).getSecurityGroupExtension().get();

      SecurityGroupBuilder groupBuilder = new SecurityGroupBuilder();
      groupBuilder.id("us-east-1/sg-3c6ef654");
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
      permBuilder.put(origGroup.getOwnerId(), origGroup.getId());
      
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
      assertTrue(newPerm.getTenantIdGroupNamePairs().values().contains(origGroup.getProviderId()));
   }

   private Multimap<String, String> emptyMultimap() {
      return LinkedHashMultimap.create();
   }

   private Set<String> emptyStringSet() {
      return Sets.newLinkedHashSet();
   }
}
