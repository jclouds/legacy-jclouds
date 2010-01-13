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
import static org.jclouds.scriptbuilder.domain.Statements.exec;

import java.net.InetAddress;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.domain.IpProtocol;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.CreateNodeResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.LoginType;
import org.jclouds.compute.domain.NodeIdentity;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Profile;
import org.jclouds.compute.domain.internal.CreateNodeResponseImpl;
import org.jclouds.compute.domain.internal.NodeIdentityImpl;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.ScriptBuilder;
import org.jclouds.scriptbuilder.domain.OsFamily;

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
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final EC2Client ec2Client;
   private final Predicate<RunningInstance> instanceStateRunning;
   private final RunningInstanceToNodeMetadata runningInstanceToNodeMetadata;

   @Inject
   public EC2ComputeService(EC2Client tmClient, Predicate<RunningInstance> instanceStateRunning,
            RunningInstanceToNodeMetadata runningInstanceToNodeMetadata) {
      this.ec2Client = tmClient;
      this.instanceStateRunning = instanceStateRunning;
      this.runningInstanceToNodeMetadata = runningInstanceToNodeMetadata;
   }

   // TODO: handle regions
   private Map<InstanceType, Map<Image, String>> imageAmiIdMap = ImmutableMap
            .<InstanceType, Map<Image, String>> of(InstanceType.M1_SMALL,//
                     ImmutableMap.<Image, String> builder().put(Image.UBUNTU_90, "ami-1515f67c")
                              .put(Image.RHEL_53, "ami-368b685f").build(),//
                     InstanceType.C1_MEDIUM,//
                     ImmutableMap.<Image, String> builder().put(Image.UBUNTU_90, "ami-1515f67c")
                              .put(Image.RHEL_53, "ami-368b685f").build(), //
                     InstanceType.C1_XLARGE,//
                     ImmutableMap.<Image, String> builder().put(Image.UBUNTU_90, "ami-ab15f6c2")
                              .build());// todo ami

   private Map<Profile, InstanceType> profileInstanceTypeMap = ImmutableMap
            .<Profile, InstanceType> builder().put(Profile.SMALLEST, InstanceType.M1_SMALL).put(
                     Profile.MEDIUM, InstanceType.C1_MEDIUM).put(Profile.FASTEST,
                     InstanceType.C1_XLARGE).build();

   private static Map<InstanceState, NodeState> instanceToNodeState = ImmutableMap
            .<InstanceState, NodeState> builder().put(InstanceState.PENDING, NodeState.PENDING)
            .put(InstanceState.RUNNING, NodeState.RUNNING).put(InstanceState.SHUTTING_DOWN,
                     NodeState.PENDING).put(InstanceState.TERMINATED, NodeState.TERMINATED).build();

   @Override
   public CreateNodeResponse createNode(String name, Profile profile, Image image) {
      InstanceType type = checkNotNull(profileInstanceTypeMap.get(profile),
               "profile not supported: " + profile);
      String ami = checkNotNull(imageAmiIdMap.get(type).get(image), "image not supported: " + image);

      KeyPair keyPair = createKeyPair(name);
      String securityGroupName = name;
      createSecurityGroup(securityGroupName, 22, 80, 8080, 443);

      String script = new ScriptBuilder() // update and install jdk
               .addStatement(exec("apt-get update"))//
               .addStatement(exec("apt-get upgrade -y"))//
               .addStatement(exec("apt-get install -y openjdk-6-jdk"))//
               .addStatement(exec("wget -qO/usr/bin/runurl run.alestic.com/runurl"))//
               .addStatement(exec("chmod 755 /usr/bin/runurl"))//
               .build(OsFamily.UNIX);

      logger.debug(">> running instance ami(%s) type(%s) keyPair(%s) securityGroup(%s)", ami, type,
               keyPair.getKeyName(), securityGroupName);

      RunningInstance runningInstance = Iterables.getLast(ec2Client.getInstanceServices()
               .runInstancesInRegion(Region.DEFAULT, null, ami, 1, 1,
                        withKeyName(keyPair.getKeyName())// key I created above
                                 .asType(type)// instance size
                                 .withSecurityGroup(securityGroupName)// group I created above
                                 .withAdditionalInfo(name)// description
                                 .withUserData(script.getBytes()) // script to run as root
               ).getRunningInstances());
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
      return new CreateNodeResponseImpl(runningInstance.getId(), name, instanceToNodeState
               .get(runningInstance.getInstanceState()), publicAddresses, privateAddresses, 22,
               LoginType.SSH, new Credentials("root", keyPair.getKeyMaterial()), ImmutableMap
                        .<String, String> of());
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
   public NodeMetadata getNodeMetadata(String id) {
      RunningInstance runningInstance = getRunningInstance(id);
      return runningInstanceToNodeMetadata.apply(runningInstance);
   }

   @Singleton
   private static class RunningInstanceToNodeMetadata implements
            Function<RunningInstance, NodeMetadata> {

      @Override
      public NodeMetadata apply(RunningInstance from) {
         return new NodeMetadataImpl(from.getId(), from.getKeyName(), instanceToNodeState.get(from
                  .getInstanceState()), nullSafeSet(from.getIpAddress()), nullSafeSet(from
                  .getPrivateIpAddress()), 22, LoginType.SSH, ImmutableMap.<String, String> of());
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
   public Set<NodeIdentity> getNodeByName(final String name) {
      return Sets.newHashSet(Iterables.filter(listNodes(), new Predicate<NodeIdentity>() {
         @Override
         public boolean apply(NodeIdentity input) {
            return input.getName().equalsIgnoreCase(name);
         }
      }));
   }

   /**
    * hack alert. can't find a good place to store the original servername, so we are reusing the
    * keyname. This will break.
    */
   @Override
   public Set<NodeIdentity> listNodes() {
      logger.debug(">> listing servers");
      Set<NodeIdentity> servers = Sets.newHashSet();
      for (Reservation reservation : ec2Client.getInstanceServices().describeInstancesInRegion(
               Region.DEFAULT)) {
         Iterables.addAll(servers, Iterables.transform(reservation.getRunningInstances(),
                  new Function<RunningInstance, NodeIdentity>() {
                     @Override
                     public NodeIdentity apply(RunningInstance from) {
                        return new NodeIdentityImpl(from.getId(), from.getKeyName());
                     }
                  }));
      }
      logger.debug("<< list(%d)", servers.size());
      return servers;
   }

   @Override
   public void destroyNode(String id) {
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