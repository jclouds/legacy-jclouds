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
package org.jclouds.ec2.compute.strategy;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import javax.inject.Provider;

import org.jclouds.aws.domain.Region;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.ec2.compute.domain.EC2HardwareBuilder;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.domain.BlockDeviceMapping;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.encryption.internal.Base64;
import org.jclouds.scriptbuilder.domain.Statements;
import org.testng.annotations.Test;

import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptionsTest")
public class CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptionsTest {

   public static final Credentials CREDENTIALS = new Credentials(null, "-----BEGIN RSA PRIVATE KEY-----\n"
            + "MIIEowIBAAKCAQEA0CbFlhSdbMdad2ux2BVqk6Ut5fLKb0CdbqubGcEBfwsSz9Rp4Ile76P90MpV\n"
            + "W1BGKL5V4MO+flG6dZnRWPVmgrNVyDTmEsALiMGjfEwbACEZ1A8C6mPa36wWO7MlxuyMjg8OczTB\n"
            + "EXnHNDpxE5a6KowJtzFlmgjHk2Y+Q42UIqPx47lQUv5bdMDCnfNNomSzTVRjOZLUkDja+ybCKdux\n"
            + "gqTsuInhuBRMx+wxff8Z43ECdJV6UPoXK3der1dlZunxGCFkCeYq0kCX7FZ7PV35X744jqhD8P+7\n"
            + "y5prO4W+M3DWgChUx0OlbDbSHtDVlcfdbj/+4AKYKU6rQOqh+4DPDQIDAQABAoIBAHjQuEiXKJSV\n"
            + "1U2RZcVtENInws9AL/2I/Jfa5Qh6vTqXG9EjklywfzkK72x7tDVvD3ngmAoAs5WwLFDL+fXvYhOk\n"
            + "sbql8ZCahVdYRWME7XsSu2IZYHDZipXe1XzLS7b9X8uos5Ns4E8bZuNKtI1RJDdD1vPMqRNR2z0T\n"
            + "0Dn3eC7t+t+t7PWaK5AXu2ot7DoOeG1QhqJbwd5pMkIn2ydBILytgmDk/2P3EtJGePIJIeQBicmw\n"
            + "Z0KrJFa/K2cC8AtmMJUoZMo+mh1yemDbDLCZW30PjFHbZtcszS2cydAgq/HDFkZynvZG0zhbx/To\n"
            + "jzcNza1AyypYwOwb2/9/ulXZp0UCgYEA+QFgWDfYLH2zwjU5b6e0UbIyd/X/yRZ+L8lOEBd0Bbu8\n"
            + "qO3txaDbwi7o2mG7pJENHJ3u62CHjgTGDNW9V9Q8eNoGtj3uHvMvi7FdDEK8B6izdZyR7hmZmQ/5\n"
            + "MIldelyiGZlz1KBSoy4FsCpA7hV7cI6H6x+Im24NxG90/wd/EgMCgYEA1f+cUyUisIO3yKOCf0hQ\n"
            + "aL289q2//F2cbvBxtki6I8JzTg1H3oTO2WVrXQeCA3a/yiuRUatgGH4mxrpCF6byVJyqrEWAj4kU\n"
            + "uTbhMgIYhLGoaF1e+vMirCRXUXox0i5X976ASzHn64V9JSd1B+UbKfpcFTYYnChmrRDzmhKN1a8C\n"
            + "gYBTvIHAyO7ab18/BRUOllAOVSWhr8lXv0eqHEEzKh/rOaoFCRY3qpOcZpgJsGogumK1Z+sLnoeX\n"
            + "W8WaVVp6KbY4UeGF8aedItyvVnLbB6ohzTqkZ4Wvk05S6cs75kXYO0SL5U3NiCiiFXz2NA9nwTOk\n"
            + "s1nD2PPgiQ76Kx0mEkhKLwKBgFhHEJqv+AZu37Kx2NRe5WS/2KK9/DPD/hM5tv7mM3sq7Nvm2J3v\n"
            + "lVDS6J5AyZ5aLzXcER9qncKcz6wtC7SsFs1Wr4VPSoBroRPikrVJbgnXK8yZr+O/xq7Scv7WdJTq\n"
            + "rzkw6cWbObvLnltkUn/GQBVqBPBvF2nbtLdyBbuqKb5bAoGBAI1+aoJnvXEXxT4UHrMkQcY0eXRz\n"
            + "3UdbzJmtjMW9CR6l9s11mV6PcZP4qnODp3nd6a+lPeL3wVYQ47DsTJ/Bx5dI17zA5mU57n6mV0a3\n"
            + "DbSoPKSdaKTQdo2THnVE9P9sPKZWueAcsE4Yw/qcTjoxrtUnAH/AXN250v0tkKIOvMhu\n"
            + "-----END RSA PRIVATE KEY-----");

   public static final KeyPair KEYPAIR = KeyPair.builder().region(Region.AP_SOUTHEAST_1).keyName("myKeyPair")
            .sha1OfPrivateKey("13:36:74:b9:56:bb:07:96:c0:19:ab:00:7f:9f:06:d2:16:a0:45:32").fingerprint(
                     "60:15:d1:f1:d9:e2:3c:e2:ee:a9:64:6a:42:a7:34:0c").keyMaterial(CREDENTIALS.credential).build();

   private static final Provider<RunInstancesOptions> OPTIONS_PROVIDER = new javax.inject.Provider<RunInstancesOptions>() {

      @Override
      public RunInstancesOptions get() {
         return new RunInstancesOptions();
      }

   };

   public void testExecuteWithDefaultOptionsEC2() throws SecurityException, NoSuchMethodException {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String group = "group";
      Hardware size = EC2HardwareBuilder.m1_small32().build();
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
      expect(strategy.createNewKeyPairUnlessUserSpecifiedOtherwise(region, group, options)).andReturn(
               systemGeneratedKeyPairName);
      expect(strategy.getSecurityGroupsForTagAndOptions(region, group, options)).andReturn(generatedGroups);
      expect(options.getUserData()).andReturn(null);

      // replay mocks
      replay(options);
      replay(template);
      replay(strategy);

      // run
      RunInstancesOptions customize = strategy.execute(region, group, template);
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
      String group = "group";
      Hardware size = EC2HardwareBuilder.m1_small32().build();
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
      expect(strategy.createNewKeyPairUnlessUserSpecifiedOtherwise(region, group, options)).andReturn(
               systemGeneratedKeyPairName);
      expect(strategy.getSecurityGroupsForTagAndOptions(region, group, options)).andReturn(generatedGroups);
      expect(options.getUserData()).andReturn("hello".getBytes());

      // replay mocks
      replay(options);
      replay(template);
      replay(strategy);

      // run
      RunInstancesOptions customize = strategy.execute(region, group, template);
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
      String group = "group";
      String userSuppliedKeyPair = "myKeyPair";

      // create mocks
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      EC2TemplateOptions options = createMock(EC2TemplateOptions.class);
      KeyPair keyPair = createMock(KeyPair.class);

      // setup expectations
      expect(options.getKeyPair()).andReturn(userSuppliedKeyPair);
      expect(options.getOverridingCredentials()).andReturn(null);
      expect(options.getRunScript()).andReturn(null);

      // replay mocks
      replay(options);
      replay(keyPair);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.createNewKeyPairUnlessUserSpecifiedOtherwise(region, group, options), userSuppliedKeyPair);

      // verify mocks
      verify(options);
      verify(keyPair);
      verifyStrategy(strategy);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testCreateNewKeyPairUnlessUserSpecifiedOtherwise_reusesKeyWhenToldToWithRunScriptButNoCredentials() {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String group = "group";
      String userSuppliedKeyPair = "myKeyPair";

      // create mocks
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      EC2TemplateOptions options = createMock(EC2TemplateOptions.class);
      KeyPair keyPair = createMock(KeyPair.class);

      // setup expectations
      expect(options.getKeyPair()).andReturn(userSuppliedKeyPair);
      expect(options.getOverridingCredentials()).andReturn(null);
      expect(options.getRunScript()).andReturn(Statements.exec("echo foo"));

      expect(strategy.credentialsMap.getUnchecked(new RegionAndName(region, userSuppliedKeyPair))).andThrow(
               new NullPointerException());

      // replay mocks
      replay(options);
      replay(keyPair);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.createNewKeyPairUnlessUserSpecifiedOtherwise(region, group, options), userSuppliedKeyPair);

      // verify mocks
      verify(options);
      verify(keyPair);
      verifyStrategy(strategy);
   }

   public void testCreateNewKeyPairUnlessUserSpecifiedOtherwise_reusesKeyWhenToldToWithRunScriptAndCredentialsAlreadyInMap() {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String group = "group";
      String userSuppliedKeyPair = "myKeyPair";

      // create mocks
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      EC2TemplateOptions options = createMock(EC2TemplateOptions.class);
      KeyPair keyPair = createMock(KeyPair.class);

      // setup expectations
      expect(options.getKeyPair()).andReturn(userSuppliedKeyPair);
      expect(options.getOverridingCredentials()).andReturn(null);
      expect(options.getRunScript()).andReturn(Statements.exec("echo foo"));

      expect(strategy.credentialsMap.getUnchecked(new RegionAndName(region, userSuppliedKeyPair))).andReturn(keyPair);

      // replay mocks
      replay(options);
      replay(keyPair);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.createNewKeyPairUnlessUserSpecifiedOtherwise(region, group, options), userSuppliedKeyPair);

      // verify mocks
      verify(options);
      verify(keyPair);
      verifyStrategy(strategy);
   }

   @SuppressWarnings("unchecked")
   public void testCreateNewKeyPairUnlessUserSpecifiedOtherwise_reusesKeyWhenToldToWithRunScriptAndCredentialsSpecified() {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String group = "group";
      String userSuppliedKeyPair = "myKeyPair";

      // create mocks
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      EC2TemplateOptions options = createMock(EC2TemplateOptions.class);
      KeyPair keyPair = createMock(KeyPair.class);
      ConcurrentMap<RegionAndName, KeyPair> backing = createMock(ConcurrentMap.class);

      // setup expectations
      expect(options.getKeyPair()).andReturn(userSuppliedKeyPair);
      expect(options.getOverridingCredentials()).andReturn(CREDENTIALS).atLeastOnce();
      expect(strategy.credentialsMap.asMap()).andReturn(backing);

      // Notice that the fingerprint and sha1 generated
      expect(backing.put(new RegionAndName(region, userSuppliedKeyPair), KEYPAIR)).andReturn(null);
      expect(options.getRunScript()).andReturn(Statements.exec("echo foo"));
      expect(strategy.credentialsMap.getUnchecked(new RegionAndName(region, userSuppliedKeyPair))).andReturn(keyPair);

      // replay mocks
      replay(options);
      replay(keyPair);
      replay(backing);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.createNewKeyPairUnlessUserSpecifiedOtherwise(region, group, options), userSuppliedKeyPair);

      // verify mocks
      verify(options);
      verify(keyPair);
      verify(backing);
      verifyStrategy(strategy);
   }

   public void testCreateNewKeyPairUnlessUserSpecifiedOtherwise_createsNewKeyPairAndReturnsItsNameByDefault()
            throws ExecutionException {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String group = "group";
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
      expect(keyPair.getKeyName()).andReturn(systemGeneratedKeyPairName).atLeastOnce();
      expect(strategy.credentialsMap.getUnchecked(new RegionAndName(region, group))).andReturn(keyPair);
      expect(options.getRunScript()).andReturn(null);

      // replay mocks
      replay(options);
      replay(keyPair);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.createNewKeyPairUnlessUserSpecifiedOtherwise(region, group, options),
               systemGeneratedKeyPairName);

      // verify mocks
      verify(options);
      verify(keyPair);
      verifyStrategy(strategy);
   }

   public void testCreateNewKeyPairUnlessUserSpecifiedOtherwise_doesntCreateAKeyPairAndReturnsNullWhenToldNotTo() {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String group = "group";
      String userSuppliedKeyPair = null;
      boolean shouldAutomaticallyCreateKeyPair = false; // here's the important
      // part!

      // create mocks
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      EC2TemplateOptions options = createMock(EC2TemplateOptions.class);
      KeyPair keyPair = createMock(KeyPair.class);

      // setup expectations
      expect(options.getKeyPair()).andReturn(userSuppliedKeyPair);
      expect(options.getRunScript()).andReturn(null);
      expect(options.shouldAutomaticallyCreateKeyPair()).andReturn(shouldAutomaticallyCreateKeyPair);

      // replay mocks
      replay(options);
      replay(keyPair);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.createNewKeyPairUnlessUserSpecifiedOtherwise(region, group, options), null);

      // verify mocks
      verify(options);
      verify(keyPair);
      verifyStrategy(strategy);
   }

   public void testGetSecurityGroupsForTagAndOptions_createsNewGroupByDefaultWhenNoPortsAreSpecifiedWhenDoesntExist()
            throws ExecutionException {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String group = "group";
      String generatedMarkerGroup = "jclouds#group#" + Region.AP_SOUTHEAST_1;
      Set<String> groupIds = ImmutableSet.<String> of();
      int[] ports = new int[] {};
      boolean shouldAuthorizeSelf = true;
      Set<String> returnVal = ImmutableSet.<String> of(generatedMarkerGroup);

      // create mocks
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      EC2TemplateOptions options = createMock(EC2TemplateOptions.class);

      // setup expectations
      expect(options.getGroups()).andReturn(groupIds).atLeastOnce();
      expect(options.getInboundPorts()).andReturn(ports).atLeastOnce();
      RegionNameAndIngressRules regionNameAndIngressRules = new RegionNameAndIngressRules(region, generatedMarkerGroup,
               ports, shouldAuthorizeSelf);
      expect(strategy.securityGroupMap.getUnchecked(regionNameAndIngressRules)).andReturn(group);

      // replay mocks
      replay(options);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.getSecurityGroupsForTagAndOptions(region, group, options), returnVal);

      // verify mocks
      verify(options);
      verifyStrategy(strategy);
   }

   public void testGetSecurityGroupsForTagAndOptions_createsNewGroupByDefaultWhenPortsAreSpecifiedWhenDoesntExist()
            throws ExecutionException {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String group = "group";
      String generatedMarkerGroup = "jclouds#group#" + Region.AP_SOUTHEAST_1;
      Set<String> groupIds = ImmutableSet.<String> of();
      int[] ports = new int[] { 22, 80 };
      boolean shouldAuthorizeSelf = true;
      Set<String> returnVal = ImmutableSet.<String> of(generatedMarkerGroup);

      // create mocks
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      EC2TemplateOptions options = createMock(EC2TemplateOptions.class);

      // setup expectations
      expect(options.getGroups()).andReturn(groupIds).atLeastOnce();
      expect(options.getInboundPorts()).andReturn(ports).atLeastOnce();
      RegionNameAndIngressRules regionNameAndIngressRules = new RegionNameAndIngressRules(region, generatedMarkerGroup,
               ports, shouldAuthorizeSelf);
      expect(strategy.securityGroupMap.getUnchecked(regionNameAndIngressRules)).andReturn(generatedMarkerGroup);

      // replay mocks
      replay(options);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.getSecurityGroupsForTagAndOptions(region, group, options), returnVal);

      // verify mocks
      verify(options);
      verifyStrategy(strategy);
   }

   public void testGetSecurityGroupsForTagAndOptions_reusesGroupByDefaultWhenNoPortsAreSpecifiedWhenDoesExist()
            throws ExecutionException {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String group = "group";
      String generatedMarkerGroup = "jclouds#group#" + Region.AP_SOUTHEAST_1;
      Set<String> groupIds = ImmutableSet.<String> of();
      int[] ports = new int[] {};
      boolean shouldAuthorizeSelf = true;
      Set<String> returnVal = ImmutableSet.<String> of(generatedMarkerGroup);

      // create mocks
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      EC2TemplateOptions options = createMock(EC2TemplateOptions.class);

      // setup expectations
      expect(options.getGroups()).andReturn(groupIds).atLeastOnce();
      expect(options.getInboundPorts()).andReturn(ports).atLeastOnce();
      RegionNameAndIngressRules regionNameAndIngressRules = new RegionNameAndIngressRules(region, generatedMarkerGroup,
               ports, shouldAuthorizeSelf);
      expect(strategy.securityGroupMap.getUnchecked(regionNameAndIngressRules)).andReturn(generatedMarkerGroup);

      // replay mocks
      replay(options);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.getSecurityGroupsForTagAndOptions(region, group, options), returnVal);

      // verify mocks
      verify(options);
      verifyStrategy(strategy);
   }

   public void testGetSecurityGroupsForTagAndOptions_reusesGroupByDefaultWhenNoPortsAreSpecifiedWhenDoesExistAndAcceptsUserSuppliedGroups() {
      // setup constants
      String region = Region.AP_SOUTHEAST_1;
      String group = "group";
      String generatedMarkerGroup = "jclouds#group#" + Region.AP_SOUTHEAST_1;
      Set<String> groupIds = ImmutableSet.<String> of("group1", "group2");
      int[] ports = new int[] {};
      boolean shouldAuthorizeSelf = true;
      boolean groupExisted = true;
      Set<String> returnVal = ImmutableSet.<String> of(generatedMarkerGroup, "group1", "group2");

      // create mocks
      CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy = setupStrategy();
      EC2TemplateOptions options = createMock(EC2TemplateOptions.class);

      // setup expectations
      expect(options.getGroups()).andReturn(groupIds).atLeastOnce();
      RegionNameAndIngressRules regionNameAndIngressRules = new RegionNameAndIngressRules(region, generatedMarkerGroup,
               ports, shouldAuthorizeSelf);

      expect(strategy.securityGroupMap.getUnchecked(regionNameAndIngressRules))
               .andReturn(groupExisted ? "group" : null);

      // replay mocks
      replay(options);
      replayStrategy(strategy);

      // run
      assertEquals(strategy.getSecurityGroupsForTagAndOptions(region, group, options), returnVal);

      // verify mocks
      verify(options);
      verifyStrategy(strategy);
   }

   private void verifyStrategy(CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy) {
      verify(strategy.credentialsMap);
      verify(strategy.securityGroupMap);
   }

   @SuppressWarnings("unchecked")
   private CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions setupStrategy() {
      Cache<RegionAndName, KeyPair> credentialsMap = createMock(Cache.class);
      Cache<RegionAndName, String> securityGroupMap = createMock(Cache.class);
      return new CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions(credentialsMap, securityGroupMap,
               OPTIONS_PROVIDER);
   }

   private void replayStrategy(CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions strategy) {
      replay(strategy.credentialsMap);
      replay(strategy.securityGroupMap);
   }

}
