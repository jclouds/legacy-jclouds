/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.aws.ec2.compute.strategy;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import javax.inject.Provider;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.aws.ec2.domain.PlacementGroup;
import org.jclouds.aws.ec2.functions.CreatePlacementGroupIfNeeded;
import org.jclouds.aws.ec2.options.AWSRunInstancesOptions;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.ec2.compute.EC2TemplateBuilderTest;
import org.jclouds.ec2.compute.domain.EC2HardwareBuilder;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.ec2.compute.functions.CreateSecurityGroupIfNeeded;
import org.jclouds.ec2.compute.functions.CreateUniqueKeyPair;
import org.jclouds.ec2.compute.strategy.CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions;
import org.jclouds.ec2.domain.BlockDeviceMapping;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.encryption.internal.Base64;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptionsTest {
   private static final Provider<RunInstancesOptions> OPTIONS_PROVIDER = new javax.inject.Provider<RunInstancesOptions>() {

      @Override
      public RunInstancesOptions get() {
         return new AWSRunInstancesOptions();
      }

   };

   public void testExecuteWithDefaultOptionsEC2() throws SecurityException, NoSuchMethodException {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String tag = "tag";
      Hardware size = EC2HardwareBuilder.m1_small().build();
      String systemGeneratedKeyPairName = "systemGeneratedKeyPair";
      String generatedGroup = "group";
      Set<String> generatedGroups = ImmutableSet.of(generatedGroup);

      // create mocks
      CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions strategy = createMock(
               CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions.class, new Method[] {
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class
                                 .getDeclaredMethod("getOptionsProvider"),
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class.getDeclaredMethod(
                                 "createNewKeyPairUnlessUserSpecifiedOtherwise", String.class, String.class,
                                 TemplateOptions.class),
                        CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions.class.getDeclaredMethod(
                                 "createNewPlacementGroupUnlessUserSpecifiedOtherwise", String.class, String.class,
                                 TemplateOptions.class),
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class
                                 .getDeclaredMethod("getSecurityGroupsForTagAndOptions", String.class, String.class,
                                          TemplateOptions.class) });

      AWSEC2TemplateOptions options = createMock(AWSEC2TemplateOptions.class);
      Template template = createMock(Template.class);

      // setup expectations
      expect(strategy.getOptionsProvider()).andReturn(OPTIONS_PROVIDER);
      expect(template.getHardware()).andReturn(size).atLeastOnce();
      expect(template.getOptions()).andReturn(options).atLeastOnce();
      expect(options.getBlockDeviceMappings()).andReturn(ImmutableSet.<BlockDeviceMapping> of()).atLeastOnce();
      expect(strategy.createNewKeyPairUnlessUserSpecifiedOtherwise(region, tag, options)).andReturn(
               systemGeneratedKeyPairName);
      expect(strategy.getSecurityGroupsForTagAndOptions(region, tag, options)).andReturn(generatedGroups);
      expect(options.getSubnetId()).andReturn(null);
      expect(options.getUserData()).andReturn(null);
      expect(options.isMonitoringEnabled()).andReturn(false);

      // replay mocks
      replay(options);
      replay(template);
      replay(strategy);

      // run
      RunInstancesOptions customize = strategy.execute(region, tag, template);
      assertEquals(customize.buildQueryParameters(), ImmutableMultimap.<String, String> of());
      assertEquals(customize.buildFormParameters().entries(), ImmutableMultimap.<String, String> of("InstanceType",
               size.getProviderId(), "SecurityGroup.1", generatedGroup, "KeyName", systemGeneratedKeyPairName)
               .entries());
      assertEquals(customize.buildMatrixParameters(), ImmutableMultimap.<String, String> of());
      assertEquals(customize.buildRequestHeaders(), ImmutableMultimap.<String, String> of());
      assertEquals(customize.buildStringPayload(), null);

      // verify mocks
      verify(options);
      verify(template);
      verify(strategy);
   }

   public void testExecuteForCCAutomatic() throws SecurityException, NoSuchMethodException {
      // setup constants
      String region = Region.US_EAST_1;
      String tag = "tag";
      Hardware size = EC2TemplateBuilderTest.CC1_4XLARGE;
      String systemGeneratedKeyPairName = "systemGeneratedKeyPair";
      String generatedGroup = "group";
      Set<String> generatedGroups = ImmutableSet.of(generatedGroup);

      // create mocks
      CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions strategy = createMock(
               CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions.class, new Method[] {
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class
                                 .getDeclaredMethod("getOptionsProvider"),
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class.getDeclaredMethod(
                                 "createNewKeyPairUnlessUserSpecifiedOtherwise", String.class, String.class,
                                 TemplateOptions.class),
                        CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions.class.getDeclaredMethod(
                                 "createNewPlacementGroupUnlessUserSpecifiedOtherwise", String.class, String.class,
                                 TemplateOptions.class),
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class
                                 .getDeclaredMethod("getSecurityGroupsForTagAndOptions", String.class, String.class,
                                          TemplateOptions.class) });

      AWSEC2TemplateOptions options = createMock(AWSEC2TemplateOptions.class);
      Template template = createMock(Template.class);

      // setup expectations
      expect(strategy.getOptionsProvider()).andReturn(OPTIONS_PROVIDER);
      expect(template.getHardware()).andReturn(size).atLeastOnce();
      expect(template.getOptions()).andReturn(options).atLeastOnce();
      expect(options.getBlockDeviceMappings()).andReturn(ImmutableSet.<BlockDeviceMapping> of()).atLeastOnce();
      expect(strategy.createNewKeyPairUnlessUserSpecifiedOtherwise(region, tag, options)).andReturn(
               systemGeneratedKeyPairName);
      expect(strategy.createNewPlacementGroupUnlessUserSpecifiedOtherwise(region, tag, options)).andReturn(
               generatedGroup);
      expect(strategy.getSecurityGroupsForTagAndOptions(region, tag, options)).andReturn(generatedGroups);
      expect(options.getSubnetId()).andReturn(null);
      expect(options.getUserData()).andReturn(null);
      expect(options.isMonitoringEnabled()).andReturn(false);

      // replay mocks
      replay(options);
      replay(template);
      replay(strategy);

      // run
      RunInstancesOptions customize = strategy.execute(region, tag, template);
      assertEquals(customize.buildQueryParameters(), ImmutableMultimap.<String, String> of());
      assertEquals(customize.buildFormParameters().entries(), ImmutableMultimap.<String, String> of("InstanceType",
               size.getProviderId(), "SecurityGroup.1", generatedGroup, "KeyName", systemGeneratedKeyPairName,
               "Placement.GroupName", generatedGroup).entries());
      assertEquals(customize.buildMatrixParameters(), ImmutableMultimap.<String, String> of());
      assertEquals(customize.buildRequestHeaders(), ImmutableMultimap.<String, String> of());
      assertEquals(customize.buildStringPayload(), null);

      // verify mocks
      verify(options);
      verify(template);
      verify(strategy);
   }

   public void testExecuteForCCUserSpecified() throws SecurityException, NoSuchMethodException {
      // setup constants
      String region = Region.US_EAST_1;
      String tag = "tag";
      Hardware size = EC2TemplateBuilderTest.CC1_4XLARGE;
      String systemGeneratedKeyPairName = "systemGeneratedKeyPair";
      String generatedGroup = "group";
      Set<String> generatedGroups = ImmutableSet.of(generatedGroup);

      // create mocks
      CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions strategy = createMock(
               CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions.class, new Method[] {
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class
                                 .getDeclaredMethod("getOptionsProvider"),
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class.getDeclaredMethod(
                                 "createNewKeyPairUnlessUserSpecifiedOtherwise", String.class, String.class,
                                 TemplateOptions.class),
                        CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions.class.getDeclaredMethod(
                                 "createNewPlacementGroupUnlessUserSpecifiedOtherwise", String.class, String.class,
                                 TemplateOptions.class),
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class
                                 .getDeclaredMethod("getSecurityGroupsForTagAndOptions", String.class, String.class,
                                          TemplateOptions.class) });

      AWSEC2TemplateOptions options = createMock(AWSEC2TemplateOptions.class);
      Template template = createMock(Template.class);

      // setup expectations
      expect(strategy.getOptionsProvider()).andReturn(OPTIONS_PROVIDER);
      expect(template.getHardware()).andReturn(size).atLeastOnce();
      expect(template.getOptions()).andReturn(options).atLeastOnce();
      expect(options.getBlockDeviceMappings()).andReturn(ImmutableSet.<BlockDeviceMapping> of()).atLeastOnce();
      expect(strategy.createNewKeyPairUnlessUserSpecifiedOtherwise(region, tag, options)).andReturn(
               systemGeneratedKeyPairName);
      expect(strategy.createNewPlacementGroupUnlessUserSpecifiedOtherwise(region, tag, options)).andReturn(
               generatedGroup);
      expect(strategy.getSecurityGroupsForTagAndOptions(region, tag, options)).andReturn(generatedGroups);
      expect(options.getSubnetId()).andReturn(null);
      expect(options.getUserData()).andReturn(null);
      expect(options.isMonitoringEnabled()).andReturn(false);

      // replay mocks
      replay(options);
      replay(template);
      replay(strategy);

      // run
      RunInstancesOptions customize = strategy.execute(region, tag, template);
      assertEquals(customize.buildQueryParameters(), ImmutableMultimap.<String, String> of());
      assertEquals(customize.buildFormParameters().entries(), ImmutableMultimap.<String, String> of("InstanceType",
               size.getProviderId(), "SecurityGroup.1", generatedGroup, "KeyName", systemGeneratedKeyPairName,
               "Placement.GroupName", generatedGroup).entries());
      assertEquals(customize.buildMatrixParameters(), ImmutableMultimap.<String, String> of());
      assertEquals(customize.buildRequestHeaders(), ImmutableMultimap.<String, String> of());
      assertEquals(customize.buildStringPayload(), null);

      // verify mocks
      verify(options);
      verify(template);
      verify(strategy);
   }

   public void testExecuteWithSubnet() throws SecurityException, NoSuchMethodException {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String tag = "tag";
      Hardware size = EC2HardwareBuilder.m1_small().build();
      String systemGeneratedKeyPairName = "systemGeneratedKeyPair";

      // create mocks
      CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions strategy = createMock(
               CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions.class, new Method[] {
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class
                                 .getDeclaredMethod("getOptionsProvider"),
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class.getDeclaredMethod(
                                 "createNewKeyPairUnlessUserSpecifiedOtherwise", String.class, String.class,
                                 TemplateOptions.class),
                        CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions.class.getDeclaredMethod(
                                 "createNewPlacementGroupUnlessUserSpecifiedOtherwise", String.class, String.class,
                                 TemplateOptions.class),
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class
                                 .getDeclaredMethod("getSecurityGroupsForTagAndOptions", String.class, String.class,
                                          TemplateOptions.class) });

      AWSEC2TemplateOptions options = createMock(AWSEC2TemplateOptions.class);
      Template template = createMock(Template.class);

      // setup expectations
      expect(strategy.getOptionsProvider()).andReturn(OPTIONS_PROVIDER);
      expect(template.getHardware()).andReturn(size).atLeastOnce();
      expect(template.getOptions()).andReturn(options).atLeastOnce();
      expect(options.getBlockDeviceMappings()).andReturn(ImmutableSet.<BlockDeviceMapping> of()).atLeastOnce();
      expect(strategy.createNewKeyPairUnlessUserSpecifiedOtherwise(region, tag, options)).andReturn(
               systemGeneratedKeyPairName);
      expect(options.getSubnetId()).andReturn("1");
      expect(options.getUserData()).andReturn(null);
      expect(options.isMonitoringEnabled()).andReturn(false);

      // replay mocks
      replay(options);
      replay(template);
      replay(strategy);

      // run
      RunInstancesOptions customize = strategy.execute(region, tag, template);
      assertEquals(customize.buildQueryParameters(), ImmutableMultimap.<String, String> of());
      assertEquals(customize.buildFormParameters().entries(), ImmutableMultimap.<String, String> of("InstanceType",
               size.getProviderId(), "SubnetId", "1", "KeyName", systemGeneratedKeyPairName).entries());
      assertEquals(customize.buildMatrixParameters(), ImmutableMultimap.<String, String> of());
      assertEquals(customize.buildRequestHeaders(), ImmutableMultimap.<String, String> of());
      assertEquals(customize.buildStringPayload(), null);

      // verify mocks
      verify(options);
      verify(template);
      verify(strategy);
   }

   public void testExecuteWithUserData() throws SecurityException, NoSuchMethodException {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String tag = "tag";
      Hardware size = EC2HardwareBuilder.m1_small().build();
      String systemGeneratedKeyPairName = "systemGeneratedKeyPair";
      String generatedGroup = "group";
      Set<String> generatedGroups = ImmutableSet.of(generatedGroup);

      // create mocks
      CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions strategy = createMock(
               CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions.class, new Method[] {
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class
                                 .getDeclaredMethod("getOptionsProvider"),
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class.getDeclaredMethod(
                                 "createNewKeyPairUnlessUserSpecifiedOtherwise", String.class, String.class,
                                 TemplateOptions.class),
                        CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions.class.getDeclaredMethod(
                                 "createNewPlacementGroupUnlessUserSpecifiedOtherwise", String.class, String.class,
                                 TemplateOptions.class),
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class
                                 .getDeclaredMethod("getSecurityGroupsForTagAndOptions", String.class, String.class,
                                          TemplateOptions.class) });

      AWSEC2TemplateOptions options = createMock(AWSEC2TemplateOptions.class);
      Template template = createMock(Template.class);

      // setup expectations
      expect(strategy.getOptionsProvider()).andReturn(OPTIONS_PROVIDER);
      expect(template.getHardware()).andReturn(size).atLeastOnce();
      expect(template.getOptions()).andReturn(options).atLeastOnce();
      expect(options.getBlockDeviceMappings()).andReturn(ImmutableSet.<BlockDeviceMapping> of()).atLeastOnce();
      expect(strategy.createNewKeyPairUnlessUserSpecifiedOtherwise(region, tag, options)).andReturn(
               systemGeneratedKeyPairName);
      expect(strategy.getSecurityGroupsForTagAndOptions(region, tag, options)).andReturn(generatedGroups);
      expect(options.getSubnetId()).andReturn(null);
      expect(options.getUserData()).andReturn("hello".getBytes());
      expect(options.isMonitoringEnabled()).andReturn(false);

      // replay mocks
      replay(options);
      replay(template);
      replay(strategy);

      // run
      RunInstancesOptions customize = strategy.execute(region, tag, template);
      assertEquals(customize.buildQueryParameters(), ImmutableMultimap.<String, String> of());
      assertEquals(customize.buildFormParameters().entries(), ImmutableMultimap.<String, String> of("InstanceType",
               size.getProviderId(), "SecurityGroup.1", "group", "KeyName", systemGeneratedKeyPairName, "UserData",
               Base64.encodeBytes("hello".getBytes())).entries());
      assertEquals(customize.buildMatrixParameters(), ImmutableMultimap.<String, String> of());
      assertEquals(customize.buildRequestHeaders(), ImmutableMultimap.<String, String> of());
      assertEquals(customize.buildStringPayload(), null);

      // verify mocks
      verify(options);
      verify(template);
      verify(strategy);
   }

   public void testCreateNewKeyPairUnlessUserSpecifiedOtherwise_reusesKeyWhenToldTo() {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String tag = "tag";
      String userSuppliedKeyPair = "myKeyPair";

      // create mocks
      CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      AWSEC2TemplateOptions options = createMock(AWSEC2TemplateOptions.class);
      KeyPair keyPair = createMock(KeyPair.class);

      // setup expectations
      expect(options.getKeyPair()).andReturn(userSuppliedKeyPair);

      // replay mocks
      replay(options);
      replay(keyPair);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.createNewKeyPairUnlessUserSpecifiedOtherwise(region, tag, options), userSuppliedKeyPair);

      // verify mocks
      verify(options);
      verify(keyPair);
      verifyStrategy(strategy);
   }

   public void testCreateNewKeyPairUnlessUserSpecifiedOtherwise_createsNewKeyPairAndReturnsItsNameByDefault() {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String tag = "tag";
      String userSuppliedKeyPair = null;
      boolean shouldAutomaticallyCreateKeyPair = true;
      String systemGeneratedKeyPairName = "systemGeneratedKeyPair";

      // create mocks
      CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      AWSEC2TemplateOptions options = createMock(AWSEC2TemplateOptions.class);
      KeyPair keyPair = createMock(KeyPair.class);

      // setup expectations
      expect(options.getKeyPair()).andReturn(userSuppliedKeyPair);
      expect(options.shouldAutomaticallyCreateKeyPair()).andReturn(shouldAutomaticallyCreateKeyPair);
      expect(strategy.createUniqueKeyPair.apply(new RegionAndName(region, tag))).andReturn(keyPair);
      expect(keyPair.getKeyName()).andReturn(systemGeneratedKeyPairName).atLeastOnce();
      expect(strategy.credentialsMap.put(new RegionAndName(region, systemGeneratedKeyPairName), keyPair)).andReturn(
               null);

      // replay mocks
      replay(options);
      replay(keyPair);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.createNewKeyPairUnlessUserSpecifiedOtherwise(region, tag, options),
               systemGeneratedKeyPairName);

      // verify mocks
      verify(options);
      verify(keyPair);
      verifyStrategy(strategy);
   }

   public void testCreateNewKeyPairUnlessUserSpecifiedOtherwise_doesntCreateAKeyPairAndReturnsNullWhenToldNotTo() {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String tag = "tag";
      String userSuppliedKeyPair = null;
      boolean shouldAutomaticallyCreateKeyPair = false; // here's the important
      // part!

      // create mocks
      CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      AWSEC2TemplateOptions options = createMock(AWSEC2TemplateOptions.class);
      KeyPair keyPair = createMock(KeyPair.class);

      // setup expectations
      expect(options.getKeyPair()).andReturn(userSuppliedKeyPair);
      expect(options.shouldAutomaticallyCreateKeyPair()).andReturn(shouldAutomaticallyCreateKeyPair);

      // replay mocks
      replay(options);
      replay(keyPair);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.createNewKeyPairUnlessUserSpecifiedOtherwise(region, tag, options), null);

      // verify mocks
      verify(options);
      verify(keyPair);
      verifyStrategy(strategy);
   }

   public void testGetSecurityGroupsForTagAndOptions_createsNewGroupByDefaultWhenNoPortsAreSpecifiedWhenDoesntExist() {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String tag = "tag";
      String generatedMarkerGroup = "jclouds#tag#" + Region.AP_SOUTHEAST_1;
      Set<String> groupIds = ImmutableSet.<String> of();
      int[] ports = new int[] {};
      boolean shouldAuthorizeSelf = true;
      boolean groupExisted = false;
      Set<String> returnVal = ImmutableSet.<String> of(generatedMarkerGroup);

      // create mocks
      CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      AWSEC2TemplateOptions options = createMock(AWSEC2TemplateOptions.class);

      // setup expectations
      expect(options.getGroupIds()).andReturn(groupIds).atLeastOnce();
      expect(options.getInboundPorts()).andReturn(ports).atLeastOnce();
      RegionNameAndIngressRules regionNameAndIngressRules = new RegionNameAndIngressRules(region, generatedMarkerGroup,
               ports, shouldAuthorizeSelf);
      expect(strategy.securityGroupMap.containsKey(regionNameAndIngressRules)).andReturn(groupExisted);
      expect(strategy.createSecurityGroupIfNeeded.apply(regionNameAndIngressRules)).andReturn(generatedMarkerGroup);
      expect(strategy.securityGroupMap.put(regionNameAndIngressRules, generatedMarkerGroup)).andReturn(null);

      // replay mocks
      replay(options);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.getSecurityGroupsForTagAndOptions(region, tag, options), returnVal);

      // verify mocks
      verify(options);
      verifyStrategy(strategy);
   }

   public void testGetSecurityGroupsForTagAndOptions_createsNewGroupByDefaultWhenPortsAreSpecifiedWhenDoesntExist() {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String tag = "tag";
      String generatedMarkerGroup = "jclouds#tag#" + Region.AP_SOUTHEAST_1;
      Set<String> groupIds = ImmutableSet.<String> of();
      int[] ports = new int[] { 22, 80 };
      boolean shouldAuthorizeSelf = true;
      boolean groupExisted = false;
      Set<String> returnVal = ImmutableSet.<String> of(generatedMarkerGroup);

      // create mocks
      CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      AWSEC2TemplateOptions options = createMock(AWSEC2TemplateOptions.class);

      // setup expectations
      expect(options.getGroupIds()).andReturn(groupIds).atLeastOnce();
      expect(options.getInboundPorts()).andReturn(ports).atLeastOnce();
      RegionNameAndIngressRules regionNameAndIngressRules = new RegionNameAndIngressRules(region, generatedMarkerGroup,
               ports, shouldAuthorizeSelf);
      expect(strategy.securityGroupMap.containsKey(regionNameAndIngressRules)).andReturn(groupExisted);
      expect(strategy.createSecurityGroupIfNeeded.apply(regionNameAndIngressRules)).andReturn(generatedMarkerGroup);
      expect(strategy.securityGroupMap.put(regionNameAndIngressRules, generatedMarkerGroup)).andReturn(null);

      // replay mocks
      replay(options);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.getSecurityGroupsForTagAndOptions(region, tag, options), returnVal);

      // verify mocks
      verify(options);
      verifyStrategy(strategy);
   }

   public void testGetSecurityGroupsForTagAndOptions_reusesGroupByDefaultWhenNoPortsAreSpecifiedWhenDoesExist() {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String tag = "tag";
      String generatedMarkerGroup = "jclouds#tag#" + Region.AP_SOUTHEAST_1;
      Set<String> groupIds = ImmutableSet.<String> of();
      int[] ports = new int[] {};
      boolean shouldAuthorizeSelf = true;
      boolean groupExisted = true;
      Set<String> returnVal = ImmutableSet.<String> of(generatedMarkerGroup);

      // create mocks
      CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      AWSEC2TemplateOptions options = createMock(AWSEC2TemplateOptions.class);

      // setup expectations
      expect(options.getGroupIds()).andReturn(groupIds).atLeastOnce();
      expect(options.getInboundPorts()).andReturn(ports).atLeastOnce();
      RegionNameAndIngressRules regionNameAndIngressRules = new RegionNameAndIngressRules(region, generatedMarkerGroup,
               ports, shouldAuthorizeSelf);
      expect(strategy.securityGroupMap.containsKey(regionNameAndIngressRules)).andReturn(groupExisted);

      // replay mocks
      replay(options);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.getSecurityGroupsForTagAndOptions(region, tag, options), returnVal);

      // verify mocks
      verify(options);
      verifyStrategy(strategy);
   }

   public void testGetSecurityGroupsForTagAndOptions_reusesGroupByDefaultWhenNoPortsAreSpecifiedWhenDoesExistAndAcceptsUserSuppliedGroups() {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String tag = "tag";
      String generatedMarkerGroup = "jclouds#tag#" + Region.AP_SOUTHEAST_1;
      Set<String> groupIds = ImmutableSet.<String> of("group1", "group2");
      int[] ports = new int[] {};
      boolean shouldAuthorizeSelf = true;
      boolean groupExisted = true;
      Set<String> returnVal = ImmutableSet.<String> of(generatedMarkerGroup, "group1", "group2");

      // create mocks
      CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      AWSEC2TemplateOptions options = createMock(AWSEC2TemplateOptions.class);

      // setup expectations
      expect(options.getGroupIds()).andReturn(groupIds).atLeastOnce();
      RegionNameAndIngressRules regionNameAndIngressRules = new RegionNameAndIngressRules(region, generatedMarkerGroup,
               ports, shouldAuthorizeSelf); // note
      // this
      // works
      // since
      // there's
      // no equals on portsq
      expect(strategy.securityGroupMap.containsKey(regionNameAndIngressRules)).andReturn(groupExisted);

      // replay mocks
      replay(options);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.getSecurityGroupsForTagAndOptions(region, tag, options), returnVal);

      // verify mocks
      verify(options);
      verifyStrategy(strategy);
   }

   public void testCreateNewPlacementGroupUnlessUserSpecifiedOtherwise_reusesKeyWhenToldTo() {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String tag = "tag";
      String userSuppliedPlacementGroup = "myPlacementGroup";

      // create mocks
      CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      AWSEC2TemplateOptions options = createMock(AWSEC2TemplateOptions.class);
      PlacementGroup placementGroup = createMock(PlacementGroup.class);

      // setup expectations
      expect(options.getPlacementGroup()).andReturn(userSuppliedPlacementGroup);

      // replay mocks
      replay(options);
      replay(placementGroup);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.createNewPlacementGroupUnlessUserSpecifiedOtherwise(region, tag, options),
               userSuppliedPlacementGroup);

      // verify mocks
      verify(options);
      verify(placementGroup);
      verifyStrategy(strategy);
   }

   public void testCreateNewPlacementGroupUnlessUserSpecifiedOtherwise_createsNewPlacementGroupAndReturnsItsNameByDefault() {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String tag = "tag";
      String userSuppliedPlacementGroup = null;
      boolean shouldAutomaticallyCreatePlacementGroup = true;
      String generatedMarkerGroup = "jclouds#tag#" + Region.AP_SOUTHEAST_1;

      // create mocks
      CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      AWSEC2TemplateOptions options = createMock(AWSEC2TemplateOptions.class);

      // setup expectations
      expect(options.getPlacementGroup()).andReturn(userSuppliedPlacementGroup);
      expect(options.shouldAutomaticallyCreatePlacementGroup()).andReturn(shouldAutomaticallyCreatePlacementGroup);
      expect(strategy.placementGroupMap.containsKey(new RegionAndName(region, generatedMarkerGroup))).andReturn(false);
      expect(strategy.createPlacementGroupIfNeeded.apply(new RegionAndName(region, generatedMarkerGroup))).andReturn(
               generatedMarkerGroup);
      expect(strategy.placementGroupMap.put(new RegionAndName(region, generatedMarkerGroup), generatedMarkerGroup))
               .andReturn(null);

      // replay mocks
      replay(options);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.createNewPlacementGroupUnlessUserSpecifiedOtherwise(region, tag, options),
               generatedMarkerGroup);

      // verify mocks
      verify(options);
      verifyStrategy(strategy);
   }

   public void testCreateNewPlacementGroupUnlessUserSpecifiedOtherwise_doesntCreateAPlacementGroupAndReturnsNullWhenToldNotTo() {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String tag = "tag";
      String userSuppliedPlacementGroup = null;
      boolean shouldAutomaticallyCreatePlacementGroup = false; // here's the important
      // part!

      // create mocks
      CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      AWSEC2TemplateOptions options = createMock(AWSEC2TemplateOptions.class);
      PlacementGroup placementGroup = createMock(PlacementGroup.class);

      // setup expectations
      expect(options.getPlacementGroup()).andReturn(userSuppliedPlacementGroup);
      expect(options.shouldAutomaticallyCreatePlacementGroup()).andReturn(shouldAutomaticallyCreatePlacementGroup);

      // replay mocks
      replay(options);
      replay(placementGroup);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.createNewPlacementGroupUnlessUserSpecifiedOtherwise(region, tag, options), null);

      // verify mocks
      verify(options);
      verify(placementGroup);
      verifyStrategy(strategy);
   }

   private void verifyStrategy(CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions strategy) {
      verify(strategy.credentialsMap);
      verify(strategy.securityGroupMap);
      verify(strategy.placementGroupMap);
      verify(strategy.createUniqueKeyPair);
      verify(strategy.createSecurityGroupIfNeeded);
      verify(strategy.createPlacementGroupIfNeeded);
   }

   @SuppressWarnings("unchecked")
   private CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions setupStrategy() {
      Map<RegionAndName, KeyPair> credentialsMap = createMock(Map.class);
      Map<RegionAndName, String> securityGroupMap = createMock(Map.class);
      Map<RegionAndName, String> placementGroupMap = createMock(Map.class);
      CreateUniqueKeyPair createUniqueKeyPair = createMock(CreateUniqueKeyPair.class);
      CreateSecurityGroupIfNeeded createSecurityGroupIfNeeded = createMock(CreateSecurityGroupIfNeeded.class);
      CreatePlacementGroupIfNeeded createPlacementGroupIfNeeded = createMock(CreatePlacementGroupIfNeeded.class);

      return new CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions(credentialsMap, securityGroupMap,
               placementGroupMap, createUniqueKeyPair, createSecurityGroupIfNeeded, OPTIONS_PROVIDER,
               createPlacementGroupIfNeeded);
   }

   private void replayStrategy(CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions strategy) {
      replay(strategy.credentialsMap);
      replay(strategy.securityGroupMap);
      replay(strategy.placementGroupMap);
      replay(strategy.createUniqueKeyPair);
      replay(strategy.createSecurityGroupIfNeeded);
      replay(strategy.createPlacementGroupIfNeeded);
   }

}
