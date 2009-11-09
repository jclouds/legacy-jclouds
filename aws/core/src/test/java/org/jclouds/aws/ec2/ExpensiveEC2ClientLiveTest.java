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
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.domain.IpProtocol;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.util.Utils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Follows the book Cloud Application Architectures ISBN: 978-0-596-15636-7
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "ec2.ExpensiveEC2ClientLiveTest")
public class ExpensiveEC2ClientLiveTest {

   private EC2Client client;
   protected SshClient.Factory sshFactory;
   private String serverPrefix = System.getProperty("user.name") + ".ec2";
   private KeyPair keyPair;
   private String securityGroupName;
   private String serverId;

   @BeforeGroups(groups = { "live" })
   public void setupClient() throws InterruptedException, ExecutionException, TimeoutException {
      String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

      client = new EC2ContextBuilder(new EC2PropertiesBuilder(user, password).build()).withModules(
               new Log4JLoggingModule()).buildContext().getApi();

      Injector injector = Guice.createInjector(new Log4JLoggingModule(), new JschSshClientModule(),
               new ExecutorServiceModule(new WithinThreadExecutorService()));
      client = EC2ContextFactory.createContext(user, password, new Log4JLoggingModule()).getApi();
      sshFactory = injector.getInstance(SshClient.Factory.class);
   }

   @Test
   void testCreateSecurityGroupIngressCidr() throws InterruptedException, ExecutionException,
            TimeoutException {
      securityGroupName = serverPrefix + "ingress";

      try {
         client.deleteSecurityGroup(securityGroupName).get(30, TimeUnit.SECONDS);
      } catch (Exception e) {
      }

      client.createSecurityGroup(securityGroupName, securityGroupName).get(30, TimeUnit.SECONDS);
      client.authorizeSecurityGroupIngress(securityGroupName, IpProtocol.TCP, 80, 80, "0.0.0.0/0")
               .get(30, TimeUnit.SECONDS);
      client
               .authorizeSecurityGroupIngress(securityGroupName, IpProtocol.TCP, 443, 443,
                        "0.0.0.0/0").get(30, TimeUnit.SECONDS);
      client.authorizeSecurityGroupIngress(securityGroupName, IpProtocol.TCP, 22, 22, "0.0.0.0/0")
               .get(30, TimeUnit.SECONDS);
   }

   @Test
   void testCreateKeyPair() throws InterruptedException, ExecutionException, TimeoutException {
      String keyName = serverPrefix + "1";
      try {
         client.deleteKeyPair(keyName).get(30, TimeUnit.SECONDS);
      } catch (Exception e) {

      }
      client.deleteKeyPair(keyName).get(30, TimeUnit.SECONDS);

      keyPair = client.createKeyPair(keyName).get(30, TimeUnit.SECONDS);
      assertNotNull(keyPair);
      assertNotNull(keyPair.getKeyMaterial());
      assertNotNull(keyPair.getKeyFingerprint());
      assertEquals(keyPair.getKeyName(), keyName);

   }

   @Test(dependsOnMethods = { "testCreateKeyPair", "testCreateSecurityGroupIngressCidr" })
   public void testCreateRunningInstance() throws Exception {
      String imageId = "ami-1fd73376";
      RunningInstance server = null;
      while (server == null) {
         try {
            server = client.runInstances(
                     imageId,
                     1,
                     1,
                     withKeyName(keyPair.getKeyName()).asType(InstanceType.M1_SMALL)
                              .withSecurityGroup(securityGroupName)).get(30, TimeUnit.SECONDS)
                     .getRunningInstances().iterator().next();
         } catch (UndeclaredThrowableException e) {
            HttpResponseException htpe = (HttpResponseException) e.getCause().getCause();
            if (htpe.getResponse().getStatusCode() == 400)
               continue;
            throw e;
         }
      }
      assertNotNull(server.getInstanceId());
      serverId = server.getInstanceId();
      assertEquals(server.getInstanceState(), InstanceState.PENDING);
      server = blockUntilRunningInstanceActive(serverId);

      sshPing(server);
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
      SshClient connection = sshFactory.create(new InetSocketAddress(newDetails.getDnsName(), 22),
               "root", keyPair.getKeyMaterial().getBytes());
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
      System.out.printf("%s awaiting daemons to start%n", currentDetails.getInstanceId());
      Thread.sleep(10 * 1000);
      return currentDetails;
   }

   @AfterClass
   void cleanup() {
      if (serverId != null)
         client.terminateInstances(serverId);
      if (keyPair != null)
         client.deleteKeyPair(keyPair.getKeyName());
      if (securityGroupName != null)
         client.deleteSecurityGroup(securityGroupName);
   }

   private RunningInstance getRunningInstance(String serverId) throws InterruptedException,
            ExecutionException, TimeoutException {
      return client.describeInstances(serverId).get(15, TimeUnit.SECONDS).first()
               .getRunningInstances().first();
   }

}
