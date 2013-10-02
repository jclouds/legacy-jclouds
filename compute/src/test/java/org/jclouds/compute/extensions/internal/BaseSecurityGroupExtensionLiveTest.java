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
package org.jclouds.compute.extensions.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;


/**
 * Base test for {@link SecurityGroupExtension} implementations.
 * 
 * @author Andrew Bayer
 * 
 */
public abstract class BaseSecurityGroupExtensionLiveTest extends BaseComputeServiceContextLiveTest {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final String secGroupName = "test-create-security-group";
   protected final String nodeGroup = "test-create-node-with-group";

   protected String groupId;

   /**
    * Returns the template for the base node, override to test different templates.
    *
    * @return
    */
   public Template getNodeTemplate() {
      return view.getComputeService().templateBuilder().build();
   }

   @Test(groups = { "integration", "live" }, singleThreaded = true)
   public void testCreateSecurityGroup() throws RunNodesException, InterruptedException, ExecutionException {

      ComputeService computeService = view.getComputeService();

      Location location = getNodeTemplate().getLocation();
      
      Optional<SecurityGroupExtension> securityGroupExtension = computeService.getSecurityGroupExtension();

      assertTrue(securityGroupExtension.isPresent(), "security extension was not present");

      SecurityGroup group = securityGroupExtension.get().createSecurityGroup(secGroupName, location);

      logger.info("Group created: %s", group);

      assertTrue(group.getName().contains(secGroupName));

      groupId = group.getId();
   }

   @Test(groups = { "integration", "live" }, singleThreaded = true, dependsOnMethods = "testCreateSecurityGroup")
   public void testGetSecurityGroupById() throws RunNodesException, InterruptedException, ExecutionException {

      ComputeService computeService = view.getComputeService();

      Location location = getNodeTemplate().getLocation();
      
      Optional<SecurityGroupExtension> securityGroupExtension = computeService.getSecurityGroupExtension();

      assertTrue(securityGroupExtension.isPresent(), "security extension was not present");

      SecurityGroup group = securityGroupExtension.get().getSecurityGroupById(groupId);

      logger.info("Group found: %s", group);

      assertTrue(group.getName().contains(secGroupName));
   }

   @Test(groups = { "integration", "live" }, singleThreaded = true, dependsOnMethods = "testGetSecurityGroupById")
   public void testAddIpPermission() {

      ComputeService computeService = view.getComputeService();

      Optional<SecurityGroupExtension> securityGroupExtension = computeService.getSecurityGroupExtension();
      assertTrue(securityGroupExtension.isPresent(), "security group extension was not present");

      Optional<SecurityGroup> optGroup = getGroup(securityGroupExtension.get());

      assertTrue(optGroup.isPresent());

      SecurityGroup group = optGroup.get();

      IpPermission.Builder builder = IpPermission.builder();

      builder.ipProtocol(IpProtocol.TCP);
      builder.fromPort(10);
      builder.toPort(20);
      builder.cidrBlock("0.0.0.0/0");

      IpPermission perm = builder.build();

      SecurityGroup newGroup = securityGroupExtension.get().addIpPermission(perm, group);

      assertEquals(Iterables.getOnlyElement(newGroup.getIpPermissions()), perm,
              "Expecting IpPermission " + perm + " but group was " + newGroup);
   }
   
   @Test(groups = { "integration", "live" }, singleThreaded = true, dependsOnMethods = "testAddIpPermission")
   public void testRemoveIpPermission() {

      ComputeService computeService = view.getComputeService();

      Optional<SecurityGroupExtension> securityGroupExtension = computeService.getSecurityGroupExtension();
      assertTrue(securityGroupExtension.isPresent(), "security group extension was not present");

      Optional<SecurityGroup> optGroup = getGroup(securityGroupExtension.get());

      assertTrue(optGroup.isPresent());

      SecurityGroup group = optGroup.get();

      IpPermission.Builder builder = IpPermission.builder();

      builder.ipProtocol(IpProtocol.TCP);
      builder.fromPort(10);
      builder.toPort(20);
      builder.cidrBlock("0.0.0.0/0");

      IpPermission perm = builder.build();

      SecurityGroup newGroup = securityGroupExtension.get().removeIpPermission(perm, group);

      assertEquals(Iterables.size(newGroup.getIpPermissions()), 0,
              "Group should have no permissions, but has " + Iterables.size(newGroup.getIpPermissions()));
   }

   @Test(groups = { "integration", "live" }, singleThreaded = true, dependsOnMethods = "testRemoveIpPermission")
   public void testAddIpPermissionsFromSpec() {

      ComputeService computeService = view.getComputeService();

      Optional<SecurityGroupExtension> securityGroupExtension = computeService.getSecurityGroupExtension();
      assertTrue(securityGroupExtension.isPresent(), "security group extension was not present");

      Optional<SecurityGroup> optGroup = getGroup(securityGroupExtension.get());

      assertTrue(optGroup.isPresent());

      SecurityGroup group = optGroup.get();

      IpPermission.Builder builder = IpPermission.builder();

      builder.ipProtocol(IpProtocol.TCP);
      builder.fromPort(50);
      builder.toPort(60);
      builder.cidrBlock("0.0.0.0/0");

      IpPermission perm = builder.build();

      SecurityGroup newGroup = securityGroupExtension.get().addIpPermission(IpProtocol.TCP,
                                                                            50,
                                                                            60,
                                                                            emptyMultimap(),
                                                                            ImmutableSet.of("0.0.0.0/0"),
                                                                            emptyStringSet(),
                                                                            group);

      assertTrue(newGroup.getIpPermissions().contains(perm)); 

      if (securityGroupExtension.get().supportsGroupIds()) {
         IpPermission.Builder secondBuilder = IpPermission.builder();
         
         int fromPort;
         int toPort;

         if (securityGroupExtension.get().supportsPortRangesForGroups()) {
            fromPort = 70;
            toPort = 80;
         } else {
            fromPort = 1;
            toPort = 65535;
         }
         secondBuilder.ipProtocol(IpProtocol.TCP);
         secondBuilder.fromPort(fromPort);
         secondBuilder.toPort(toPort);
         secondBuilder.groupId(group.getId());
         
         IpPermission secondPerm = secondBuilder.build();
         
         SecurityGroup secondNewGroup = securityGroupExtension.get().addIpPermission(IpProtocol.TCP,
                                                                                     fromPort,
                                                                                     toPort,
                                                                                     emptyMultimap(),
                                                                                     emptyStringSet(),
                                                                                     ImmutableSet.of(group.getId()),
                                                                                     newGroup);
         
         assertTrue(secondNewGroup.getIpPermissions().contains(secondPerm)); 
      }

      if (securityGroupExtension.get().supportsTenantIdGroupNamePairs()
              || securityGroupExtension.get().supportsTenantIdGroupIdPairs()) {
         IpPermission.Builder thirdBuilder = IpPermission.builder();

         int fromPort;
         int toPort;

         if (securityGroupExtension.get().supportsPortRangesForGroups()) {
            fromPort = 90;
            toPort = 100;
         } else {
            fromPort = 1;
            toPort = 65535;
         }
         thirdBuilder.ipProtocol(IpProtocol.TCP);
         thirdBuilder.fromPort(fromPort);
         thirdBuilder.toPort(toPort);
         if (securityGroupExtension.get().supportsTenantIdGroupIdPairs()) {
            thirdBuilder.tenantIdGroupNamePair(group.getOwnerId(), group.getProviderId());
         } else if (securityGroupExtension.get().supportsTenantIdGroupNamePairs()) {
            thirdBuilder.tenantIdGroupNamePair(group.getOwnerId(), group.getName());
         }

         IpPermission thirdPerm = thirdBuilder.build();

         SecurityGroup thirdNewGroup = securityGroupExtension.get().addIpPermission(IpProtocol.TCP,
                                                                                    fromPort,
                                                                                    toPort,
                                                                                    thirdPerm.getTenantIdGroupNamePairs(),
                                                                                    emptyStringSet(),
                                                                                    emptyStringSet(),
                                                                                    newGroup);

         assertTrue(thirdNewGroup.getIpPermissions().contains(thirdPerm));
      }
   }

   /*
   @Test(groups = { "integration", "live" }, singleThreaded = true, dependsOnMethods = "testAddIpPermissionsFromSpec")
   public void testCreateNodeWithSecurityGroup() throws RunNodesException, InterruptedException, ExecutionException {

      ComputeService computeService = view.getComputeService();

      Optional<SecurityGroupExtension> securityGroupExtension = computeService.getSecurityGroupExtension();

      assertTrue(securityGroupExtension.isPresent(), "security group extension was not present");

      Template template = view.getComputeService().templateBuilder()
         .options(TemplateOptions.Builder.securityGroups(groupId))
         .build();
      
      NodeMetadata node = Iterables.getOnlyElement(computeService.createNodesInGroup(nodeGroup, 1, template));

      Set<SecurityGroup> groups = securityGroupExtension.get().listSecurityGroupsForNode(node.getId());

      assertTrue(groups.size() > 0, "node has no groups");
      
      Optional<SecurityGroup> secGroup = Iterables.tryFind(securityGroupExtension.get().listSecurityGroupsForNode(node.getId()),
                                                           new Predicate<SecurityGroup>() {
                                                              @Override
                                                              public boolean apply(SecurityGroup input) {
                                                                 return input.getId().equals(groupId);
                                                              }
                                                           });

      assertTrue(secGroup.isPresent());

      computeService.destroyNodesMatching(inGroup(node.getGroup()));

      
   }
*/
   // testDeleteSecurityGroup currently disabled until I can find a way to get it to delete the security group while a terminated
   // instance is still floating around in EC2. - abayer, 6/14/13
   @Test(groups = { "integration", "live" }, singleThreaded = true, dependsOnMethods = "testAddIpPermissionsFromSpec")
   public void testDeleteSecurityGroup() {

      ComputeService computeService = view.getComputeService();

      Optional<SecurityGroupExtension> securityGroupExtension = computeService.getSecurityGroupExtension();
      assertTrue(securityGroupExtension.isPresent(), "security group extension was not present");

      Optional<SecurityGroup> optGroup = getGroup(securityGroupExtension.get());

      assertTrue(optGroup.isPresent());

      SecurityGroup group = optGroup.get();
      assertTrue(securityGroupExtension.get().removeSecurityGroup(group.getId()));
   }
   
   private Multimap<String, String> emptyMultimap() {
      return LinkedHashMultimap.create();
   }

   private Set<String> emptyStringSet() {
      return Sets.newLinkedHashSet();
   }
   
   private Optional<SecurityGroup> getGroup(SecurityGroupExtension ext) {
      return Iterables.tryFind(ext.listSecurityGroups(), new Predicate<SecurityGroup>() {
         @Override
         public boolean apply(SecurityGroup input) {
            return input.getId().equals(groupId);
         }
      });
   }


   private void cleanup() {
      ComputeService computeService = view.getComputeService();

      Location location = getNodeTemplate().getLocation();

      Optional<SecurityGroupExtension> securityGroupExtension = computeService.getSecurityGroupExtension();

      if (securityGroupExtension.isPresent()) {
         Optional<SecurityGroup> group = getGroup(securityGroupExtension.get());

         if (group.isPresent()) {
            securityGroupExtension.get().removeSecurityGroup(group.get().getId());
         }
      }
   }


   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDownContext() {
      try {
         cleanup();
      } catch (Exception e) {

      }
      super.tearDownContext();
   }

}
