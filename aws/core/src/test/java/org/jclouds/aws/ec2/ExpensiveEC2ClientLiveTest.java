/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.withKeyName;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.util.Utils;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.Injector;

/**
 * Follows the book Cloud Application Architectures ISBN: 978-0-596-15636-7
 * 
 * Generally disabled, as it incurs higher fees.
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true, testName = "ec2.ExpensiveEC2ClientLiveTest")
public class ExpensiveEC2ClientLiveTest {

   private EC2Client client;
   protected SshClient.Factory sshFactory;
   private String serverPrefix = System.getProperty("user.name") + ".ec2";
   private KeyPair keyPair;
   private String securityGroupName;
   private String serverId;
   private InetAddress address;

   private RetryablePredicate<InetSocketAddress> socketTester;

   @BeforeGroups(groups = { "live" })
   public void setupClient() throws InterruptedException, ExecutionException, TimeoutException {
      String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      Injector injector = new EC2ContextBuilder(new EC2PropertiesBuilder(user, password).build())
               .withModules(new Log4JLoggingModule(), new JschSshClientModule()).buildInjector();
      client = injector.getInstance(EC2Client.class);
      sshFactory = injector.getInstance(SshClient.Factory.class);
      SocketOpen socketOpen = injector.getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<InetSocketAddress>(socketOpen, 60, 1, TimeUnit.SECONDS);
      injector.injectMembers(socketOpen); // add logger
   }

   @Test(enabled = true)
   void testCreateSecurityGroupIngressCidr() throws InterruptedException, ExecutionException,
            TimeoutException {
      securityGroupName = serverPrefix + "ingress";

      try {
         client.getSecurityGroupServices().deleteSecurityGroupInRegion(Region.DEFAULT,
                  securityGroupName);
      } catch (Exception e) {
      }

      client.getSecurityGroupServices().createSecurityGroupInRegion(Region.DEFAULT,
               securityGroupName, securityGroupName);
      client.getSecurityGroupServices().authorizeSecurityGroupIngressInRegion(Region.DEFAULT,
               securityGroupName, IpProtocol.TCP, 80, 80, "0.0.0.0/0");
      client.getSecurityGroupServices().authorizeSecurityGroupIngressInRegion(Region.DEFAULT,
               securityGroupName, IpProtocol.TCP, 443, 443, "0.0.0.0/0");
      client.getSecurityGroupServices().authorizeSecurityGroupIngressInRegion(Region.DEFAULT,
               securityGroupName, IpProtocol.TCP, 22, 22, "0.0.0.0/0");
   }

   @Test(enabled = true)
   void testCreateKeyPair() throws InterruptedException, ExecutionException, TimeoutException {
      String keyName = serverPrefix + "1";
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
      String imageId = "ami-1fd73376";
      RunningInstance server = null;
      while (server == null) {
         try {
            System.out.printf("%d: running instance%n", System.currentTimeMillis());
            server = client.getInstanceServices().runInstancesInRegion(
                     Region.DEFAULT,
                     null,
                     imageId,
                     1,
                     1,
                     withKeyName(keyPair.getKeyName()).asType(InstanceType.M1_SMALL)
                              .withSecurityGroup(securityGroupName)).getRunningInstances()
                     .iterator().next();
         } catch (HttpResponseException htpe) {
            if (htpe.getResponse().getStatusCode() == 400)
               continue;
            throw htpe;
         }
      }
      assertNotNull(server.getInstanceId());
      serverId = server.getInstanceId();
      assertEquals(server.getInstanceState(), InstanceState.PENDING);
      server = blockUntilRunningInstanceActive(serverId);

      sshPing(server);
      System.out.printf("%d: %s ssh connection made%n", System.currentTimeMillis(), serverId);

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
               serverId);

      compare = Iterables.getLast(client.getElasticIPAddressServices().describeAddressesInRegion(
               Region.DEFAULT, address));

      assertEquals(compare.getPublicIp(), address);
      assertEquals(compare.getInstanceId(), serverId);

      Reservation reservation = client.getInstanceServices().describeInstancesInRegion(
               Region.DEFAULT, serverId).last();

      assertNotNull(reservation.getRunningInstances().last().getIpAddress());
      assertFalse(reservation.getRunningInstances().last().getIpAddress().equals(address));

      doCheckKey(address);

      client.getElasticIPAddressServices().disassociateAddressInRegion(Region.DEFAULT, address);

      compare = Iterables.getLast(client.getElasticIPAddressServices().describeAddressesInRegion(
               Region.DEFAULT, address));

      assertEquals(compare.getPublicIp(), address);
      assert compare.getInstanceId() == null;

      reservation = client.getInstanceServices()
               .describeInstancesInRegion(Region.DEFAULT, serverId).last();
      // assert reservation.getRunningInstances().last().getIpAddress() == null; TODO
   }

   /**
    * this tests "personality" as the file looked up was sent during server creation
    */
   private void sshPing(RunningInstance newDetails) throws IOException {
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

   private void doCheckKey(RunningInstance newDetails) throws IOException {
      doCheckKey(InetAddress.getByName(newDetails.getDnsName()));
   }

   private void doCheckKey(InetAddress address) throws IOException {
      SshClient connection = sshFactory.create(new InetSocketAddress(address, 22), "root", keyPair
               .getKeyMaterial().getBytes());
      try {
         connection.connect();
         InputStream etcPasswd = connection.get("/etc/passwd");
         Utils.toStringAndClose(etcPasswd);
      } finally {
         if (connection != null)
            connection.disconnect();
      }
   }

   private RunningInstance blockUntilRunningInstanceActive(String serverId)
            throws InterruptedException, ExecutionException, TimeoutException {
      RunningInstance currentDetails = null;
      for (currentDetails = getRunningInstance(serverId); currentDetails.getInstanceState() != InstanceState.RUNNING; currentDetails = getRunningInstance(serverId)) {
         System.out.printf("%s blocking on status active: currently: %s%n", currentDetails
                  .getInstanceId(), currentDetails.getInstanceState());
         Thread.sleep(5 * 1000);
      }

      System.out.printf("%d: %s awaiting ssh service to start%n", System.currentTimeMillis(),
               currentDetails.getDnsName());
      assert socketTester.apply(new InetSocketAddress(currentDetails.getDnsName(), 22));
      System.out.printf("%d: %s ssh service started%n", System.currentTimeMillis(), currentDetails
               .getDnsName());
      return currentDetails;
   }

   @AfterTest
   void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      if (address != null)
         client.getElasticIPAddressServices().releaseAddressInRegion(Region.DEFAULT, address);
      if (serverId != null)
         client.getInstanceServices().terminateInstancesInRegion(Region.DEFAULT, serverId);
      if (keyPair != null)
         client.getKeyPairServices().deleteKeyPairInRegion(Region.DEFAULT, keyPair.getKeyName());
      if (securityGroupName != null)
         client.getSecurityGroupServices().deleteSecurityGroupInRegion(Region.DEFAULT,
                  securityGroupName);
   }

   private RunningInstance getRunningInstance(String serverId) throws InterruptedException,
            ExecutionException, TimeoutException {
      return client.getInstanceServices().describeInstancesInRegion(Region.DEFAULT, serverId)
               .first().getRunningInstances().first();
   }

}
