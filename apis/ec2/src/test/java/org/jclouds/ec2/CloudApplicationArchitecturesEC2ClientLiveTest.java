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
package org.jclouds.ec2;

import static org.jclouds.ec2.options.RunInstancesOptions.Builder.asType;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.Image.EbsBlockDevice;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.domain.PublicIpInstanceIdPair;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.domain.Volume.InstanceInitiatedShutdownBehavior;
import org.jclouds.ec2.predicates.InstanceHasIpAddress;
import org.jclouds.ec2.predicates.InstanceStateRunning;
import org.jclouds.http.HttpResponseException;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.scriptbuilder.ScriptBuilder;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.google.inject.Injector;

/**
 * Follows the book Cloud Application Architectures ISBN: 978-0-596-15636-7
 * <p/>
 * adds in functionality to boot a lamp instance: http://alestic.com/2009/06/ec2-user-data-scripts
 * <p/>
 * Generally disabled, as it incurs higher fees.
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = false, singleThreaded = true, testName = "CloudApplicationArchitecturesEC2ClientLiveTest")
public class CloudApplicationArchitecturesEC2ClientLiveTest extends BaseComputeServiceContextLiveTest {
   public CloudApplicationArchitecturesEC2ClientLiveTest() {
      provider = "ec2";
   }

   private EC2Client client;
   protected SshClient.Factory sshFactory;
   private String instancePrefix = System.getProperty("user.name") + ".ec2";
   private KeyPair keyPair;
   private String securityGroupName;
   private String instanceId;
   private String address;

   private RetryablePredicate<HostAndPort> socketTester;
   private RetryablePredicate<RunningInstance> hasIpTester;
   private RetryablePredicate<RunningInstance> runningTester;

   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      Injector injector = view.utils().injector();
      client = injector.getInstance(EC2Client.class);
      sshFactory = injector.getInstance(SshClient.Factory.class);
      runningTester = new RetryablePredicate<RunningInstance>(new InstanceStateRunning(client), 180, 5,
            TimeUnit.SECONDS);
      hasIpTester = new RetryablePredicate<RunningInstance>(new InstanceHasIpAddress(client), 180, 5, TimeUnit.SECONDS);
      SocketOpen socketOpen = injector.getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<HostAndPort>(socketOpen, 180, 1, TimeUnit.SECONDS);
   }

   @Test(enabled = false)
   void testCreateSecurityGroupIngressCidr() throws InterruptedException, ExecutionException, TimeoutException {
      securityGroupName = instancePrefix + "ingress";

      try {
         client.getSecurityGroupServices().deleteSecurityGroupInRegion(null, securityGroupName);
      } catch (Exception e) {
      }

      client.getSecurityGroupServices().createSecurityGroupInRegion(null, securityGroupName, securityGroupName);
      for (int port : new int[] { 80, 443, 22 }) {
         client.getSecurityGroupServices().authorizeSecurityGroupIngressInRegion(null, securityGroupName,
               IpProtocol.TCP, port, port, "0.0.0.0/0");
      }
   }

   @Test(enabled = false)
   void testCreateKeyPair() throws InterruptedException, ExecutionException, TimeoutException {
      String keyName = instancePrefix + "1";
      try {
         client.getKeyPairServices().deleteKeyPairInRegion(null, keyName);
      } catch (Exception e) {

      }
      client.getKeyPairServices().deleteKeyPairInRegion(null, keyName);

      keyPair = client.getKeyPairServices().createKeyPairInRegion(null, keyName);
      assertNotNull(keyPair);
      assertNotNull(keyPair.getKeyMaterial());
      assertNotNull(keyPair.getSha1OfPrivateKey());
      assertEquals(keyPair.getKeyName(), keyName);
   }

   @Test(enabled = false, dependsOnMethods = { "testCreateKeyPair", "testCreateSecurityGroupIngressCidr" })
   public void testCreateRunningInstance() throws Exception {
      String script = new ScriptBuilder() // lamp install script
            .addStatement(exec("runurl run.alestic.com/apt/upgrade"))//
            .addStatement(exec("runurl run.alestic.com/install/lamp"))//
            .render(OsFamily.UNIX);

      RunningInstance instance = null;
      while (instance == null) {
         try {

            System.out.printf("%d: running instance%n", System.currentTimeMillis());
            Reservation<? extends RunningInstance> reservation = client.getInstanceServices().runInstancesInRegion(
                  null, null, // allow
                  // ec2
                  // to
                  // chose
                  // an
                  // availability
                  // zone
                  "ami-ccf615a5", // alestic ami allows auto-invoke of
                  // user data scripts
                  1, // minimum instances
                  1, // maximum instances
                  asType(InstanceType.M1_SMALL) // smallest instance size
                        .withKeyName(keyPair.getKeyName()) // key I
                        // created
                        // above
                        .withSecurityGroup(securityGroupName) // group I
                        // created
                        // above
                        .withUserData(script.getBytes())); // script to
            // run as root

            instance = Iterables.getOnlyElement(reservation);

         } catch (HttpResponseException htpe) {
            if (htpe.getResponse().getStatusCode() == 400)
               continue;
            throw htpe;
         }
      }
      assertNotNull(instance.getId());
      instanceId = instance.getId();
      assertEquals(instance.getInstanceState(), InstanceState.PENDING);
      instance = blockUntilWeCanSshIntoInstance(instance);

      verifyInstanceProperties(script);
      tryToChangeStuff();
      sshPing(instance);
      System.out.printf("%d: %s ssh connection made%n", System.currentTimeMillis(), instanceId);

   }

   private void verifyInstanceProperties(String script) {
      assertEquals(script, client.getInstanceServices().getUserDataForInstanceInRegion(null, instanceId));

      assertEquals(null, client.getInstanceServices().getRootDeviceNameForInstanceInRegion(null, instanceId));

      assert client.getInstanceServices().getRamdiskForInstanceInRegion(null, instanceId).startsWith("ari-");

      assertEquals(false, client.getInstanceServices().isApiTerminationDisabledForInstanceInRegion(null, instanceId));

      assert client.getInstanceServices().getKernelForInstanceInRegion(null, instanceId).startsWith("aki-");

      assertEquals(InstanceType.M1_SMALL,
            client.getInstanceServices().getInstanceTypeForInstanceInRegion(null, instanceId));

      assertEquals(InstanceInitiatedShutdownBehavior.TERMINATE, client.getInstanceServices()
            .getInstanceInitiatedShutdownBehaviorForInstanceInRegion(null, instanceId));

      assertEquals(ImmutableMap.<String, EbsBlockDevice> of(), client.getInstanceServices()
            .getBlockDeviceMappingForInstanceInRegion(null, instanceId));
   }

   private void setApiTerminationDisabledForInstanceInRegion() {
      client.getInstanceServices().setApiTerminationDisabledForInstanceInRegion(null, instanceId, true);
      assertEquals(true, client.getInstanceServices().isApiTerminationDisabledForInstanceInRegion(null, instanceId));
      client.getInstanceServices().setApiTerminationDisabledForInstanceInRegion(null, instanceId, false);
      assertEquals(false, client.getInstanceServices().isApiTerminationDisabledForInstanceInRegion(null, instanceId));
   }

   private void tryToChangeStuff() {
      setApiTerminationDisabledForInstanceInRegion();
      setUserDataForInstanceInRegion();
      setRamdiskForInstanceInRegion();
      setKernelForInstanceInRegion();
      setInstanceTypeForInstanceInRegion();
      setInstanceInitiatedShutdownBehaviorForInstanceInRegion();
      setBlockDeviceMappingForInstanceInRegion();
   }

   private void setUserDataForInstanceInRegion() {
      try {
         client.getInstanceServices().setUserDataForInstanceInRegion(null, instanceId, "test".getBytes());
         assert false : "shouldn't be allowed, as instance needs to be stopped";
      } catch (AWSResponseException e) {
         assertEquals("IncorrectInstanceState", e.getError().getCode());
      }
   }

   private void setRamdiskForInstanceInRegion() {
      try {
         String ramdisk = client.getInstanceServices().getRamdiskForInstanceInRegion(null, instanceId);
         client.getInstanceServices().setRamdiskForInstanceInRegion(null, instanceId, ramdisk);
         assert false : "shouldn't be allowed, as instance needs to be stopped";
      } catch (AWSResponseException e) {
         assertEquals("IncorrectInstanceState", e.getError().getCode());
      }
   }

   private void setKernelForInstanceInRegion() {
      try {
         String oldKernel = client.getInstanceServices().getKernelForInstanceInRegion(null, instanceId);
         client.getInstanceServices().setKernelForInstanceInRegion(null, instanceId, oldKernel);
         assert false : "shouldn't be allowed, as instance needs to be stopped";
      } catch (AWSResponseException e) {
         assertEquals("IncorrectInstanceState", e.getError().getCode());
      }
   }

   private void setInstanceTypeForInstanceInRegion() {
      try {
         client.getInstanceServices().setInstanceTypeForInstanceInRegion(null, instanceId, InstanceType.C1_MEDIUM);
         assert false : "shouldn't be allowed, as instance needs to be stopped";
      } catch (AWSResponseException e) {
         assertEquals("IncorrectInstanceState", e.getError().getCode());
      }
   }

   private void setBlockDeviceMappingForInstanceInRegion() {
      Map<String, BlockDevice> mapping = Maps.newLinkedHashMap();
      try {
         client.getInstanceServices().setBlockDeviceMappingForInstanceInRegion(null, instanceId, mapping);
         assert false : "shouldn't be allowed, as instance needs to be ebs based-ami";
      } catch (AWSResponseException e) {
         assertEquals("InvalidParameterCombination", e.getError().getCode());
      }
   }

   private void setInstanceInitiatedShutdownBehaviorForInstanceInRegion() {
      try {
         client.getInstanceServices().setInstanceInitiatedShutdownBehaviorForInstanceInRegion(null, instanceId,
               InstanceInitiatedShutdownBehavior.STOP);
         assert false : "shouldn't be allowed, as instance needs to be ebs based-ami";
      } catch (AWSResponseException e) {
         assertEquals("UnsupportedInstanceAttribute", e.getError().getCode());
      }
   }

   @Test(enabled = false, dependsOnMethods = "testCreateRunningInstance")
   void testReboot() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      RunningInstance instance = getInstance(instanceId);
      System.out.printf("%d: %s rebooting instance %n", System.currentTimeMillis(), instanceId);
      client.getInstanceServices().rebootInstancesInRegion(null, instanceId);
      Thread.sleep(1000);
      instance = getInstance(instanceId);
      blockUntilWeCanSshIntoInstance(instance);
      SshClient ssh = sshFactory.create(HostAndPort.fromParts(instance.getIpAddress(), 22),
            LoginCredentials.builder().user("root").privateKey(keyPair.getKeyMaterial()).build());
      try {
         ssh.connect();
         ExecResponse uptime = ssh.exec("uptime");
         assert uptime.getOutput().indexOf("0 min") != -1 : "reboot didn't work: " + uptime;
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   @Test(enabled = false, dependsOnMethods = "testReboot")
   void testElasticIpAddress() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      address = client.getElasticIPAddressServices().allocateAddressInRegion(null);
      assertNotNull(address);

      PublicIpInstanceIdPair compare = Iterables.getLast(client.getElasticIPAddressServices()
            .describeAddressesInRegion(null, address));

      assertEquals(compare.getPublicIp(), address);
      assert compare.getInstanceId() == null;

      client.getElasticIPAddressServices().associateAddressInRegion(null, address, instanceId);

      compare = Iterables.getLast(client.getElasticIPAddressServices().describeAddressesInRegion(null, address));

      assertEquals(compare.getPublicIp(), address);
      assertEquals(compare.getInstanceId(), instanceId);

      Reservation<? extends RunningInstance> reservation = Iterables.getOnlyElement(client.getInstanceServices()
            .describeInstancesInRegion(null, instanceId));

      assertNotNull(Iterables.getOnlyElement(reservation).getIpAddress());
      assertFalse(Iterables.getOnlyElement(reservation).getIpAddress().equals(address));

      doCheckKey(address);

      client.getElasticIPAddressServices().disassociateAddressInRegion(null, address);

      compare = Iterables.getLast(client.getElasticIPAddressServices().describeAddressesInRegion(null, address));

      assertEquals(compare.getPublicIp(), address);
      assert compare.getInstanceId() == null;

      reservation = Iterables.getOnlyElement(client.getInstanceServices().describeInstancesInRegion(null, instanceId));
      // assert reservation.getRunningInstances().last().getIpAddress() == null;
      // TODO
   }

   private RunningInstance blockUntilWeCanSshIntoInstance(RunningInstance instance) throws UnknownHostException {
      System.out.printf("%d: %s awaiting instance to run %n", System.currentTimeMillis(), instance.getId());
      assert runningTester.apply(instance);

      instance = getInstance(instance.getId());

      System.out
            .printf("%d: %s awaiting instance to have ip assigned %n", System.currentTimeMillis(), instance.getId());
      assert hasIpTester.apply(instance);

      System.out.printf("%d: %s awaiting ssh service to start%n", System.currentTimeMillis(), instance.getIpAddress());
      assert socketTester.apply(HostAndPort.fromParts(instance.getIpAddress(), 22));

      System.out.printf("%d: %s ssh service started%n", System.currentTimeMillis(), instance.getDnsName());
      sshPing(instance);
      System.out.printf("%d: %s ssh connection made%n", System.currentTimeMillis(), instance.getId());

      System.out.printf("%d: %s awaiting http service to start%n", System.currentTimeMillis(), instance.getIpAddress());
      assert socketTester.apply(HostAndPort.fromParts(instance.getIpAddress(), 80));
      System.out.printf("%d: %s http service started%n", System.currentTimeMillis(), instance.getDnsName());
      return instance;
   }

   private RunningInstance getInstance(String instanceId) {
      // search my identity for the instance I just created
      Set<? extends Reservation<? extends RunningInstance>> reservations = client.getInstanceServices()
            .describeInstancesInRegion(null, instanceId); // last parameter
      // (ids) narrows the
      // search

      return Iterables.getOnlyElement(Iterables.getOnlyElement(reservations));
   }

   /**
    * this tests "personality" as the file looked up was sent during instance creation
    * 
    * @throws UnknownHostException
    */
   private void sshPing(RunningInstance newDetails) throws UnknownHostException {
      try {
         doCheckKey(newDetails);
      } catch (SshException e) {// try twice in case there is a network timeout
         try {
            Thread.sleep(10 * 1000);
         } catch (InterruptedException e1) {
         }
         doCheckKey(newDetails);
      }
   }

   private void doCheckKey(RunningInstance newDetails) throws UnknownHostException {
      doCheckKey(newDetails.getIpAddress());
   }

   private void doCheckKey(String address) {
      SshClient ssh = sshFactory.create(HostAndPort.fromParts(address, 22),
            LoginCredentials.builder().user("root").privateKey(keyPair.getKeyMaterial()).build());
      try {
         ssh.connect();
         ExecResponse hello = ssh.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   @AfterTest
   void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      if (address != null)
         client.getElasticIPAddressServices().releaseAddressInRegion(null, address);
      if (instanceId != null)
         client.getInstanceServices().terminateInstancesInRegion(null, instanceId);
      if (keyPair != null)
         client.getKeyPairServices().deleteKeyPairInRegion(null, keyPair.getKeyName());
      if (securityGroupName != null)
         client.getSecurityGroupServices().deleteSecurityGroupInRegion(null, securityGroupName);
   }

}
