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
package org.jclouds.aws.ec2.demos.createlamp;

import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.asType;
import static org.jclouds.scriptbuilder.domain.Statements.exec;

import java.net.InetSocketAddress;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.EC2ContextFactory;
import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.domain.IpProtocol;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.predicates.InstanceStateRunning;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.RestContext;
import org.jclouds.scriptbuilder.ScriptBuilder;
import org.jclouds.scriptbuilder.domain.OsFamily;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * This the Main class of an Application that demonstrates the use of the EC2Client by creating a
 * small lamp server.
 * 
 * Usage is: java MainApp accesskeyid secretkey command name where command in create destroy
 * 
 * @author Adrian Cole
 */
public class MainApp {

   public static int PARAMETERS = 4;
   public static String INVALID_SYNTAX = "Invalid number of parameters. Syntax is: accesskeyid secretkey command name\nwhere command in create destroy";

   public static void main(String[] args) throws TimeoutException {

      if (args.length < PARAMETERS)
         throw new IllegalArgumentException(INVALID_SYNTAX);

      // Args
      String accesskeyid = args[0];
      String secretkey = args[1];
      String command = args[2];
      String name = args[3];

      // Init
      RestContext<EC2AsyncClient, EC2Client> context = EC2ContextFactory.createContext(accesskeyid,
               secretkey);

      // Get a synchronous client
      EC2Client client = context.getApi();

      try {
         if (command.equals("create")) {

            KeyPair pair = createKeyPair(client, name);

            RunningInstance instance = createSecurityGroupKeyPairAndInstance(client, name);

            System.out.printf("instance %s ready%n", instance.getId());
            System.out.printf("ip address: %s%n", instance.getIpAddress().getHostAddress());
            System.out.printf("dns name: %s%n", instance.getDnsName());
            System.out.printf("login identity:%n%s%n", pair.getKeyMaterial());

         } else if (command.equals("destroy")) {
            destroySecurityGroupKeyPairAndInstance(client, name);
         } else {
            throw new IllegalArgumentException(INVALID_SYNTAX);
         }
      } finally {
         // Close connecton
         context.close();
         System.exit(0);
      }

   }

   private static void destroySecurityGroupKeyPairAndInstance(EC2Client client, String name) {
      try {
         String id = findInstanceByKeyName(client, name).getId();
         System.out.printf("%d: %s terminating instance%n", System.currentTimeMillis(), id);
         client.getInstanceServices().terminateInstancesInRegion(Region.DEFAULT,
                  findInstanceByKeyName(client, name).getId());
      } catch (NoSuchElementException e) {
      } catch (Exception e) {
         e.printStackTrace();
      }

      try {
         System.out.printf("%d: %s deleting keypair%n", System.currentTimeMillis(), name);
         client.getKeyPairServices().deleteKeyPairInRegion(Region.DEFAULT, name);
      } catch (Exception e) {
         e.printStackTrace();
      }

      try {
         System.out.printf("%d: %s deleting group%n", System.currentTimeMillis(), name);
         client.getSecurityGroupServices().deleteSecurityGroupInRegion(Region.DEFAULT, name);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private static RunningInstance createSecurityGroupKeyPairAndInstance(EC2Client client,
            String name) throws TimeoutException {
      // create a new security group
      createSecurityGroupAndAuthorizePorts(client, name);

      // create a new instance
      RunningInstance instance = runInstance(client, name, name);

      // await for the instance to start
      return blockUntilInstanceRunning(client, instance);
   }

   static void createSecurityGroupAndAuthorizePorts(EC2Client client, String name) {
      System.out.printf("%d: creating security group: %s%n", System.currentTimeMillis(), name);
      client.getSecurityGroupServices().createSecurityGroupInRegion(Region.DEFAULT, name, name);
      for (int port : new int[] { 80, 443, 22 }) {
         client.getSecurityGroupServices().authorizeSecurityGroupIngressInRegion(Region.DEFAULT,
                  name, IpProtocol.TCP, port, port, "0.0.0.0/0");
      }
   }

   static KeyPair createKeyPair(EC2Client client, String name) {
      System.out.printf("%d: creating keypair: %s%n", System.currentTimeMillis(), name);
      return client.getKeyPairServices().createKeyPairInRegion(Region.DEFAULT, name);
   }

   static RunningInstance runInstance(EC2Client client, String securityGroupName, String keyPairName) {

      String script = new ScriptBuilder() // lamp install script
               .addStatement(exec("runurl run.alestic.com/apt/upgrade"))//
               .addStatement(exec("runurl run.alestic.com/install/lamp"))//
               .build(OsFamily.UNIX);

      System.out.printf("%d: running instance%n", System.currentTimeMillis());
      Reservation reservation = client.getInstanceServices().runInstancesInRegion(Region.DEFAULT,
               null, // allow ec2 to chose an availability zone
               "ami-ccf615a5", // alestic ami allows auto-invoke of user data scripts
               1, // minimum instances
               1, // maximum instances
               asType(InstanceType.M1_SMALL) // smallest instance size
                        .withKeyName(keyPairName) // key I created above
                        .withSecurityGroup(securityGroupName) // group I created above
                        .withUserData(script.getBytes())); // script to run as root

      return Iterables.getOnlyElement(reservation.getRunningInstances());

   }

   static RunningInstance blockUntilInstanceRunning(EC2Client client, RunningInstance instance)
            throws TimeoutException {
      // create utilities that wait for the instance to finish
      RetryablePredicate<RunningInstance> runningTester = new RetryablePredicate<RunningInstance>(
               new InstanceStateRunning(client.getInstanceServices()), 180, 5, TimeUnit.SECONDS);
      RetryablePredicate<InetSocketAddress> socketTester = new RetryablePredicate<InetSocketAddress>(
               new SocketOpen(), 180, 1, TimeUnit.SECONDS);

      System.out.printf("%d: %s awaiting instance to run %n", System.currentTimeMillis(), instance
               .getId());
      if (!runningTester.apply(instance))
         throw new TimeoutException("timeout waiting for instance to run: " + instance.getId());

      instance = findInstanceById(client, instance.getId());

      System.out.printf("%d: %s awaiting ssh service to start%n", System.currentTimeMillis(),
               instance.getIpAddress());
      if (!socketTester.apply(new InetSocketAddress(instance.getIpAddress(), 22)))
         throw new TimeoutException("timeout waiting for ssh to start: " + instance.getIpAddress());

      System.out.printf("%d: %s ssh service started%n", System.currentTimeMillis(), instance
               .getIpAddress());

      System.out.printf("%d: %s awaiting http service to start%n", System.currentTimeMillis(),
               instance.getIpAddress());
      if (!socketTester.apply(new InetSocketAddress(instance.getIpAddress(), 80)))
         throw new TimeoutException("timeout waiting for http to start: " + instance.getIpAddress());

      System.out.printf("%d: %s http service started%n", System.currentTimeMillis(), instance
               .getIpAddress());
      return instance;
   }

   private static RunningInstance findInstanceById(EC2Client client, String instanceId) {
      // search my account for the instance I just created
      Set<Reservation> reservations = client.getInstanceServices().describeInstancesInRegion(
               Region.DEFAULT, instanceId); // last parameter (ids) narrows the search

      // since we refined by instanceId there should only be one instance
      return Iterables.getOnlyElement(Iterables.getOnlyElement(reservations).getRunningInstances());
   }

   private static RunningInstance findInstanceByKeyName(EC2Client client, final String keyName) {
      // search my account for the instance I just created
      Set<Reservation> reservations = client.getInstanceServices().describeInstancesInRegion(
               Region.DEFAULT);

      // extract all the instances from all reservations
      Set<RunningInstance> allInstances = Sets.newHashSet();
      for (Reservation reservation : reservations) {
         allInstances.addAll(reservation.getRunningInstances());
      }

      // get the first one that has a keyname matching what I just created
      return Iterables.find(allInstances, new Predicate<RunningInstance>() {

         @Override
         public boolean apply(RunningInstance input) {
            return input.getKeyName().equals(keyName)
                     && input.getInstanceState() != InstanceState.TERMINATED;
         }

      });
   }
}
