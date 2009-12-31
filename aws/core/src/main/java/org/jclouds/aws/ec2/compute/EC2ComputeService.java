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
package org.jclouds.aws.ec2.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.withKeyName;

import java.net.InetAddress;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.domain.IpProtocol;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.CreateServerResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.LoginType;
import org.jclouds.compute.domain.Profile;
import org.jclouds.compute.domain.ServerIdentity;
import org.jclouds.compute.domain.ServerMetadata;
import org.jclouds.compute.domain.ServerState;
import org.jclouds.compute.domain.internal.CreateServerResponseImpl;
import org.jclouds.compute.domain.internal.ServerIdentityImpl;
import org.jclouds.compute.domain.internal.ServerMetadataImpl;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.internal.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Singleton
public class EC2ComputeService implements ComputeService {
   @Resource
   protected Logger logger = Logger.NULL;
   private final EC2Client ec2Client;
   private final Predicate<RunningInstance> instanceStateRunning;
   private final RunningInstanceToServerMetadata runningInstanceToServerMetadata;

   @Inject
   public EC2ComputeService(EC2Client tmClient, Predicate<RunningInstance> instanceStateRunning,
            RunningInstanceToServerMetadata runningInstanceToServerMetadata) {
      this.ec2Client = tmClient;
      this.instanceStateRunning = instanceStateRunning;
      this.runningInstanceToServerMetadata = runningInstanceToServerMetadata;
   }

   private Map<Image, String> imageAmiIdMap = ImmutableMap.<Image, String> builder().put(
            Image.CENTOS_53, "ami-b0a84ad9").put(Image.RHEL_53, "ami-368b685f").build();// todo ami
   // matrix of
   // region
   // 32/64 bit

   private Map<Profile, InstanceType> profileInstanceTypeMap = ImmutableMap
            .<Profile, InstanceType> builder().put(Profile.SMALLEST, InstanceType.M1_SMALL).build();

   private static Map<InstanceState, ServerState> instanceToServerState = ImmutableMap
            .<InstanceState, ServerState> builder().put(InstanceState.PENDING, ServerState.PENDING)
            .put(InstanceState.RUNNING, ServerState.RUNNING).put(InstanceState.SHUTTING_DOWN,
                     ServerState.PENDING).put(InstanceState.TERMINATED, ServerState.TERMINATED)
            .build();

   @Override
   public CreateServerResponse createServer(String name, Profile profile, Image image) {
      String ami = checkNotNull(imageAmiIdMap.get(image), "image not supported: " + image);
      InstanceType type = checkNotNull(profileInstanceTypeMap.get(profile),
               "profile not supported: " + profile);
      KeyPair keyPair = createKeyPair(name);
      String securityGroupName = name;
      createSecurityGroup(securityGroupName, 22, 80, 443);

      logger.debug(">> running instance ami(%s) type(%s) keyPair(%s) securityGroup(%s)", ami, type,
               keyPair.getKeyName(), securityGroupName);

      RunningInstance runningInstance = Iterables
               .getLast(ec2Client.getInstanceServices().runInstancesInRegion(
                        Region.DEFAULT,
                        null,
                        ami,
                        1,
                        1,
                        withKeyName(keyPair.getKeyName()).asType(type).withSecurityGroup(
                                 securityGroupName).withAdditionalInfo(name)).getRunningInstances());
      logger.debug("<< started instance(%s)", runningInstance.getId());
      instanceStateRunning.apply(runningInstance);
      logger.debug("<< running instance(%s)", runningInstance.getId());

      // refresh to get IP address
      runningInstance = getRunningInstance(runningInstance.getId());

      Set<InetAddress> publicAddresses = runningInstance.getIpAddress() == null ? ImmutableSet
               .<InetAddress> of() : ImmutableSet.<InetAddress> of(runningInstance.getIpAddress());
      Set<InetAddress> privateAddresses = runningInstance.getPrivateIpAddress() == null ? ImmutableSet
               .<InetAddress> of()
               : ImmutableSet.<InetAddress> of(runningInstance.getPrivateIpAddress());
      return new CreateServerResponseImpl(runningInstance.getId(), name,
               instanceToServerState.get(runningInstance.getInstanceState()), publicAddresses,
               privateAddresses, 22, LoginType.SSH, new Credentials("root", keyPair
                        .getKeyMaterial()));
   }

   private KeyPair createKeyPair(String name) {
      logger.debug(">> creating keyPair name(%s)", name);
      KeyPair keyPair;
      try {
         keyPair = ec2Client.getKeyPairServices().createKeyPairInRegion(Region.DEFAULT, name);
         logger.debug("<< created keyPair(%s)", keyPair.getKeyName());

      } catch (AWSResponseException e) {
         if (e.getError().getCode().equals("InvalidKeyPair.Duplicate")) {
            keyPair = Iterables.getLast(ec2Client.getKeyPairServices().describeKeyPairsInRegion(
                     Region.DEFAULT, name));
            logger.debug("<< reused keyPair(%s)", keyPair.getKeyName());

         } else {
            throw e;
         }
      }
      return keyPair;
   }

   private void createSecurityGroup(String name, int... ports) {
      logger.debug(">> creating securityGroup name(%s)", name);

      try {
         ec2Client.getSecurityGroupServices().createSecurityGroupInRegion(Region.DEFAULT, name,
                  name);
         logger.debug("<< created securityGroup(%s)", name);
         logger.debug(">> authorizing securityGroup name(%s) ports(%s)", name, ImmutableSet
                  .of(ports));
         for (int port : ports) {
            ec2Client.getSecurityGroupServices().authorizeSecurityGroupIngressInRegion(
                     Region.DEFAULT, name, IpProtocol.TCP, port, port, "0.0.0.0/0");
         }
         logger.debug("<< authorized securityGroup(%s)", name);
      } catch (AWSResponseException e) {
         if (e.getError().getCode().equals("InvalidGroup.Duplicate")) {
            logger.debug("<< reused securityGroup(%s)", name);
         } else {
            throw e;
         }
      }

   }

   @Override
   public ServerMetadata getServerMetadata(String id) {
      RunningInstance runningInstance = getRunningInstance(id);
      return runningInstanceToServerMetadata.apply(runningInstance);
   }

   @Singleton
   private static class RunningInstanceToServerMetadata implements
            Function<RunningInstance, ServerMetadata> {

      @Override
      public ServerMetadata apply(RunningInstance from) {
         return new ServerMetadataImpl(from.getId(), from.getKeyName(),
                  instanceToServerState.get(from.getInstanceState()), nullSafeSet(from
                           .getIpAddress()), nullSafeSet(from.getPrivateIpAddress()), 22,
                  LoginType.SSH);
      }

      Set<InetAddress> nullSafeSet(InetAddress in) {
         if (in == null) {
            return ImmutableSet.<InetAddress> of();
         }
         return ImmutableSet.<InetAddress> of(in);
      }
   }

   private RunningInstance getRunningInstance(String id) {
      RunningInstance runningInstance = Iterables.getLast(Iterables.getLast(
               ec2Client.getInstanceServices().describeInstancesInRegion(Region.DEFAULT, id))
               .getRunningInstances());
      return runningInstance;
   }

   @Override
   public SortedSet<ServerIdentity> getServerByName(final String name) {
      return Sets.newTreeSet(Iterables.filter(listServers(), new Predicate<ServerIdentity>() {
         @Override
         public boolean apply(ServerIdentity input) {
            return input.getName().equalsIgnoreCase(name);
         }
      }));
   }

   /**
    * hack alert. can't find a good place to store the original servername, so we are reusing the
    * keyname. This will break.
    */
   @Override
   public SortedSet<ServerIdentity> listServers() {
      logger.debug(">> listing servers");
      SortedSet<ServerIdentity> servers = Sets.newTreeSet();
      for (Reservation reservation : ec2Client.getInstanceServices().describeInstancesInRegion(
               Region.DEFAULT)) {
         Iterables.addAll(servers, Iterables.transform(reservation.getRunningInstances(),
                  new Function<RunningInstance, ServerIdentity>() {
                     @Override
                     public ServerIdentity apply(RunningInstance from) {
                        return new ServerIdentityImpl(from.getId(), from.getKeyName());
                     }
                  }));
      }
      logger.debug("<< list(%d)", servers.size());
      return servers;
   }

   @Override
   public void destroyServer(String id) {
      RunningInstance runningInstance = getRunningInstance(id);
      // grab the old keyname
      String name = runningInstance.getKeyName();
      logger.debug(">> terminating instance(%s)", runningInstance.getId());
      ec2Client.getInstanceServices().terminateInstancesInRegion(Region.DEFAULT, id);
      logger.debug("<< terminated instance(%s)", runningInstance.getId());
      logger.debug(">> deleting keyPair(%s)", name);
      ec2Client.getKeyPairServices().deleteKeyPairInRegion(Region.DEFAULT, name);
      logger.debug("<< deleted keyPair(%s)", name);
      logger.debug(">> deleting securityGroup(%s)", name);
      ec2Client.getSecurityGroupServices().deleteSecurityGroupInRegion(Region.DEFAULT, name);
      logger.debug("<< deleted securityGroup(%s)", name);
   }
}