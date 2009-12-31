/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.asType;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.domain.IpProtocol;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.PublicIpInstanceIdPair;
import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.domain.Image.EbsBlockDevice;
import org.jclouds.aws.ec2.domain.Volume.InstanceInitiatedShutdownBehavior;
import org.jclouds.aws.ec2.predicates.InstanceStateRunning;
import org.jclouds.encryption.internal.Base64;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.scriptbuilder.ScriptBuilder;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
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
@Test(groups = "live", enabled = true, sequential = true, testName = "ec2.CloudApplicationArchitecturesEC2ClientLiveTest")
public class CloudApplicationArchitecturesEC2ClientLiveTest {

   private EC2Client client;
   protected SshClient.Factory sshFactory;
   private String instancePrefix = System.getProperty("user.name") + ".ec2";
   private KeyPair keyPair;
   private String securityGroupName;
   private String instanceId;
   private InetAddress address;

   private RetryablePredicate<InetSocketAddress> socketTester;
   private RetryablePredicate<RunningInstance> runningTester;

   @BeforeGroups(groups = { "live" })
   public void setupClient() throws InterruptedException, ExecutionException, TimeoutException {
      String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      Injector injector = new EC2ContextBuilder(new EC2PropertiesBuilder(user, password).build())
               .withModules(new Log4JLoggingModule(), new JschSshClientModule()).buildInjector();
      client = injector.getInstance(EC2Client.class);
      sshFactory = injector.getInstance(SshClient.Factory.class);
      runningTester = new RetryablePredicate<RunningInstance>(new InstanceStateRunning(client
               .getInstanceServices()), 180, 5, TimeUnit.SECONDS);
      socketTester = new RetryablePredicate<InetSocketAddress>(new SocketOpen(), 180, 1,
               TimeUnit.SECONDS);
   }

   @Test(enabled = true)
   void testCreateSecurityGroupIngressCidr() throws InterruptedException, ExecutionException,
            TimeoutException {
      securityGroupName = instancePrefix + "ingress";

      try {
         client.getSecurityGroupServices().deleteSecurityGroupInRegion(Region.DEFAULT,
                  securityGroupName);
      } catch (Exception e) {
      }

      client.getSecurityGroupServices().createSecurityGroupInRegion(Region.DEFAULT,
               securityGroupName, securityGroupName);
      for (int port : new int[] { 80, 443, 22 }) {
         client.getSecurityGroupServices().authorizeSecurityGroupIngressInRegion(Region.DEFAULT,
                  securityGroupName, IpProtocol.TCP, port, port, "0.0.0.0/0");
      }
   }

   @Test(enabled = true)
   void testCreateKeyPair() throws InterruptedException, ExecutionException, TimeoutException {
      String keyName = instancePrefix + "1";
      try {
         client.getKeyPairServices().deleteKeyPairInRegion(Region.DEFAULT, keyName);
      } catch (Exception e) {

      }
      client.getKeyPairServices().deleteKeyPairInRegion(Region.DEFAULT, keyName);

      keyPair = client.getKeyPairServices().createKeyPairInRegion(Region.DEFAULT, keyName);
      assertNotNull(keyPair);
      assertNotNull(keyPair.getKeyMaterial());
      assertNotNull(keyPair.getKeyFingerprint());
      assertEquals(keyPair.getKeyName(), keyName);
   }

   @Test(enabled = true, dependsOnMethods = { "testCreateKeyPair",
            "testCreateSecurityGroupIngressCidr" })
   public void testCreateRunningInstance() throws Exception {
      String script = new ScriptBuilder() // lamp install script
               .addStatement(exec("runurl run.alestic.com/apt/upgrade"))//
               .addStatement(exec("runurl run.alestic.com/install/lamp"))//
               .build(OsFamily.UNIX);

      // userData must be base 64 encoded
      String encodedScript = Base64.encodeBytes(script.getBytes());
      RunningInstance instance = null;
      while (instance == null) {
         try {

            System.out.printf("%d: running instance%n", System.currentTimeMillis());
            Reservation reservation = client.getInstanceServices().runInstancesInRegion(
                     Region.DEFAULT, null, // allow ec2 to chose an availability zone
                     "ami-ccf615a5", // alestic ami allows auto-invoke of user data scripts
                     1, // minimum instances
                     1, // maximum instances
                     asType(InstanceType.M1_SMALL) // smallest instance size
                              .withKeyName(keyPair.getKeyName()) // key I created above
                              .withSecurityGroup(securityGroupName) // group I created above
                              .withUserData(encodedScript)); // script to run as root

            instance = Iterables.getOnlyElement(reservation.getRunningInstances());

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

      sshPing(instance);
      System.out.printf("%d: %s ssh connection made%n", System.currentTimeMillis(), instanceId);

   }

   private void verifyInstanceProperties(String script) {
      assertEquals(script, client.getInstanceServices().getUserDataForInstanceInRegion(
               Region.DEFAULT, instanceId));

      assertEquals(null, client.getInstanceServices().getRootDeviceNameForInstanceInRegion(
               Region.DEFAULT, instanceId));

      assert client.getInstanceServices().getRamdiskForInstanceInRegion(Region.DEFAULT, instanceId)
               .startsWith("ari-");

      assertEquals(false, client.getInstanceServices().getDisableApiTerminationForInstanceInRegion(
               Region.DEFAULT, instanceId));

      assert client.getInstanceServices().getKernelForInstanceInRegion(Region.DEFAULT, instanceId)
               .startsWith("aki-");

      assertEquals(InstanceType.M1_SMALL, client.getInstanceServices()
               .getInstanceTypeForInstanceInRegion(Region.DEFAULT, instanceId));

      assertEquals(InstanceInitiatedShutdownBehavior.TERMINATE, client.getInstanceServices()
               .getInstanceInitiatedShutdownBehaviorForInstanceInRegion(Region.DEFAULT, instanceId));

      assertEquals(ImmutableMap.<String, EbsBlockDevice> of(), client.getInstanceServices()
               .getBlockDeviceMappingForInstanceInRegion(Region.DEFAULT, instanceId));
   }

   @Test(enabled = true, dependsOnMethods = "testCreateRunningInstance")
   void testElasticIpAddress() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      address = client.getElasticIPAddressServices().allocateAddressInRegion(Region.DEFAULT);
      assertNotNull(address);

      PublicIpInstanceIdPair compare = Iterables.getLast(client.getElasticIPAddressServices()
               .describeAddressesInRegion(Region.DEFAULT, address));

      assertEquals(compare.getPublicIp(), address);
      assert compare.getInstanceId() == null;

      client.getElasticIPAddressServices().associateAddressInRegion(Region.DEFAULT, address,
               instanceId);

      compare = Iterables.getLast(client.getElasticIPAddressServices().describeAddressesInRegion(
               Region.DEFAULT, address));

      assertEquals(compare.getPublicIp(), address);
      assertEquals(compare.getInstanceId(), instanceId);

      Reservation reservation = Iterables.getOnlyElement(client.getInstanceServices()
               .describeInstancesInRegion(Region.DEFAULT, instanceId));

      assertNotNull(Iterables.getOnlyElement(reservation.getRunningInstances()).getIpAddress());
      assertFalse(Iterables.getOnlyElement(reservation.getRunningInstances()).getIpAddress()
               .equals(address));

      doCheckKey(address);

      client.getElasticIPAddressServices().disassociateAddressInRegion(Region.DEFAULT, address);

      compare = Iterables.getLast(client.getElasticIPAddressServices().describeAddressesInRegion(
               Region.DEFAULT, address));

      assertEquals(compare.getPublicIp(), address);
      assert compare.getInstanceId() == null;

      reservation = Iterables.getOnlyElement(client.getInstanceServices()
               .describeInstancesInRegion(Region.DEFAULT, instanceId));
      // assert reservation.getRunningInstances().last().getIpAddress() == null; TODO
   }

   private RunningInstance blockUntilWeCanSshIntoInstance(RunningInstance instance)
            throws UnknownHostException {
      System.out.printf("%d: %s awaiting instance to run %n", System.currentTimeMillis(), instance
               .getId());
      assert runningTester.apply(instance);

      // search my account for the instance I just created
      Set<Reservation> reservations = client.getInstanceServices().describeInstancesInRegion(
               instance.getRegion(), instance.getId()); // last parameter (ids) narrows the search

      instance = Iterables.getOnlyElement(Iterables.getOnlyElement(reservations)
               .getRunningInstances());

      System.out.printf("%d: %s awaiting ssh service to start%n", System.currentTimeMillis(),
               instance.getIpAddress());
      assert socketTester.apply(new InetSocketAddress(instance.getIpAddress(), 22));

      System.out.printf("%d: %s ssh service started%n", System.currentTimeMillis(), instance
               .getDnsName());
      sshPing(instance);
      System.out.printf("%d: %s ssh connection made%n", System.currentTimeMillis(), instance
               .getId());

      System.out.printf("%d: %s awaiting http service to start%n", System.currentTimeMillis(),
               instance.getIpAddress());
      assert socketTester.apply(new InetSocketAddress(instance.getIpAddress(), 80));
      System.out.printf("%d: %s http service started%n", System.currentTimeMillis(), instance
               .getDnsName());
      return instance;
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

   private void doCheckKey(InetAddress address) {
      SshClient ssh = sshFactory.create(new InetSocketAddress(address, 22), "root", keyPair
               .getKeyMaterial().getBytes());
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
         client.getElasticIPAddressServices().releaseAddressInRegion(Region.DEFAULT, address);
      if (instanceId != null)
         client.getInstanceServices().terminateInstancesInRegion(Region.DEFAULT, instanceId);
      if (keyPair != null)
         client.getKeyPairServices().deleteKeyPairInRegion(Region.DEFAULT, keyPair.getKeyName());
      if (securityGroupName != null)
         client.getSecurityGroupServices().deleteSecurityGroupInRegion(Region.DEFAULT,
                  securityGroupName);
   }

}
