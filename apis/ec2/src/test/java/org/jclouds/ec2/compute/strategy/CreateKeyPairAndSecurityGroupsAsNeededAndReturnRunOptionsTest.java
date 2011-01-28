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

package org.jclouds.ec2.compute.strategy;

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
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.ec2.compute.domain.EC2HardwareBuilder;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.ec2.compute.functions.CreateSecurityGroupIfNeeded;
import org.jclouds.ec2.compute.functions.CreateUniqueKeyPair;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
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
public class CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptionsTest {

   private static final Provider<RunInstancesOptions> OPTIONS_PROVIDER = new javax.inject.Provider<RunInstancesOptions>() {

      @Override
      public RunInstancesOptions get() {
         return new RunInstancesOptions();
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
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy = createMock(
               CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class, new Method[] {
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class
                                 .getDeclaredMethod("getOptionsProvider"),
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class.getDeclaredMethod(
                                 "createNewKeyPairUnlessUserSpecifiedOtherwise", String.class, String.class,
                                 TemplateOptions.class),
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class
                                 .getDeclaredMethod("getSecurityGroupsForTagAndOptions", String.class, String.class,
                                          TemplateOptions.class) });

      EC2TemplateOptions options = createMock(EC2TemplateOptions.class);
      Template template = createMock(Template.class);

      // setup expectations
      expect(strategy.getOptionsProvider()).andReturn(OPTIONS_PROVIDER);
      expect(template.getHardware()).andReturn(size).atLeastOnce();
      expect(template.getOptions()).andReturn(options).atLeastOnce();
      expect(options.getBlockDeviceMappings()).andReturn(ImmutableSet.<BlockDeviceMapping> of()).atLeastOnce();
      expect(strategy.createNewKeyPairUnlessUserSpecifiedOtherwise(region, tag, options)).andReturn(
               systemGeneratedKeyPairName);
      expect(strategy.getSecurityGroupsForTagAndOptions(region, tag, options)).andReturn(generatedGroups);
      expect(options.getUserData()).andReturn(null);

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

   public void testExecuteWithUserData() throws SecurityException, NoSuchMethodException {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String tag = "tag";
      Hardware size = EC2HardwareBuilder.m1_small().build();
      String systemGeneratedKeyPairName = "systemGeneratedKeyPair";
      String generatedGroup = "group";
      Set<String> generatedGroups = ImmutableSet.of(generatedGroup);

      // create mocks
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy = createMock(
               CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class, new Method[] {
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class
                                 .getDeclaredMethod("getOptionsProvider"),
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class.getDeclaredMethod(
                                 "createNewKeyPairUnlessUserSpecifiedOtherwise", String.class, String.class,
                                 TemplateOptions.class),
                        CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class
                                 .getDeclaredMethod("getSecurityGroupsForTagAndOptions", String.class, String.class,
                                          TemplateOptions.class) });

      EC2TemplateOptions options = createMock(EC2TemplateOptions.class);
      Template template = createMock(Template.class);

      // setup expectations
      expect(strategy.getOptionsProvider()).andReturn(OPTIONS_PROVIDER);
      expect(template.getHardware()).andReturn(size).atLeastOnce();
      expect(template.getOptions()).andReturn(options).atLeastOnce();
      expect(options.getBlockDeviceMappings()).andReturn(ImmutableSet.<BlockDeviceMapping> of()).atLeastOnce();
      expect(strategy.createNewKeyPairUnlessUserSpecifiedOtherwise(region, tag, options)).andReturn(
               systemGeneratedKeyPairName);
      expect(strategy.getSecurityGroupsForTagAndOptions(region, tag, options)).andReturn(generatedGroups);
      expect(options.getUserData()).andReturn("hello".getBytes());

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
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      EC2TemplateOptions options = createMock(EC2TemplateOptions.class);
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
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      EC2TemplateOptions options = createMock(EC2TemplateOptions.class);
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
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      EC2TemplateOptions options = createMock(EC2TemplateOptions.class);
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
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      EC2TemplateOptions options = createMock(EC2TemplateOptions.class);

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
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      EC2TemplateOptions options = createMock(EC2TemplateOptions.class);

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
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      EC2TemplateOptions options = createMock(EC2TemplateOptions.class);

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
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      EC2TemplateOptions options = createMock(EC2TemplateOptions.class);

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

   private void verifyStrategy(CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy) {
      verify(strategy.credentialsMap);
      verify(strategy.securityGroupMap);
      verify(strategy.createUniqueKeyPair);
      verify(strategy.createSecurityGroupIfNeeded);
   }

   @SuppressWarnings("unchecked")
   private CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions setupStrategy() {
      Map<RegionAndName, KeyPair> credentialsMap = createMock(Map.class);
      Map<RegionAndName, String> securityGroupMap = createMock(Map.class);
      CreateUniqueKeyPair createUniqueKeyPair = createMock(CreateUniqueKeyPair.class);
      CreateSecurityGroupIfNeeded createSecurityGroupIfNeeded = createMock(CreateSecurityGroupIfNeeded.class);

      return new CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions(credentialsMap, securityGroupMap,
               createUniqueKeyPair, createSecurityGroupIfNeeded, OPTIONS_PROVIDER);
   }

   private void replayStrategy(CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy) {
      replay(strategy.credentialsMap);
      replay(strategy.securityGroupMap);
      replay(strategy.createUniqueKeyPair);
      replay(strategy.createSecurityGroupIfNeeded);
   }

}
