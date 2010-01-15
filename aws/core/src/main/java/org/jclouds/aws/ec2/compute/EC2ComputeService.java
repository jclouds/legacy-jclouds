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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.withKeyName;
import static org.jclouds.scriptbuilder.domain.Statements.exec;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
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
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.CreateNodeResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.LoginType;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Profile;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.internal.ComputeMetadataImpl;
import org.jclouds.compute.domain.internal.CreateNodeResponseImpl;
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
   public CreateNodeResponse startNodeInLocation(String location, String name, Profile profile,
            Image image) {
      Region region = Region.fromValue(location);

      InstanceType type = checkNotNull(profileInstanceTypeMap.get(profile),
               "profile not supported: " + profile);
      String ami = checkNotNull(imageAmiIdMap.get(type).get(image), "image not supported: " + image);

      KeyPair keyPair = createKeyPairInRegion(region, name);
      String securityGroupName = name;
      createSecurityGroupInRegion(region, securityGroupName, 22, 80, 8080, 443);

      String script = new ScriptBuilder() // update and install jdk
               .addStatement(exec("apt-get update"))//
               .addStatement(exec("apt-get upgrade -y"))//
               .addStatement(exec("apt-get install -y openjdk-6-jdk"))//
               .addStatement(exec("wget -qO/usr/bin/runurl run.alestic.com/runurl"))//
               .addStatement(exec("chmod 755 /usr/bin/runurl"))//
               .build(OsFamily.UNIX);

      logger.debug(">> running instance ami(%s) type(%s) keyPair(%s) securityGroup(%s)", ami, type,
               keyPair.getKeyName(), securityGroupName);

      RunningInstance runningInstance = Iterables.getOnlyElement(ec2Client.getInstanceServices()
               .runInstancesInRegion(region, null, ami, 1, 1, withKeyName(keyPair.getKeyName())// key
                        // I
                        // created
                        // above
                        .asType(type)// instance size
                        .withSecurityGroup(securityGroupName)// group I created above
                        .withAdditionalInfo(name)// description
                        .withUserData(script.getBytes()) // script to run as root
               ));
      logger.debug("<< started instance(%s)", runningInstance.getId());
      instanceStateRunning.apply(runningInstance);
      logger.debug("<< running instance(%s)", runningInstance.getId());

      // refresh to get IP address
      runningInstance = getOnlyRunningInstanceInRegion(region, runningInstance.getId());

      Set<InetAddress> publicAddresses = runningInstance.getIpAddress() == null ? ImmutableSet
               .<InetAddress> of() : ImmutableSet.<InetAddress> of(runningInstance.getIpAddress());
      Set<InetAddress> privateAddresses = runningInstance.getPrivateIpAddress() == null ? ImmutableSet
               .<InetAddress> of()
               : ImmutableSet.<InetAddress> of(runningInstance.getPrivateIpAddress());
      return new CreateNodeResponseImpl(runningInstance.getId(), name, runningInstance.getRegion()
               .toString(), null, ImmutableMap.<String, String> of(), instanceToNodeState
               .get(runningInstance.getInstanceState()), publicAddresses, privateAddresses, 22,
               LoginType.SSH, new Credentials(image == Image.UBUNTU_90 ? "ubuntu" : "root", keyPair
                        .getKeyMaterial()), ImmutableMap.<String, String> of());
   }

   private KeyPair createKeyPairInRegion(Region region, String name) {
      logger.debug(">> creating keyPair name(%s)", name);
      KeyPair keyPair;
      try {
         keyPair = ec2Client.getKeyPairServices().createKeyPairInRegion(region, name);
         logger.debug("<< created keyPair(%s)", keyPair.getKeyName());

      } catch (AWSResponseException e) {
         if (e.getError().getCode().equals("InvalidKeyPair.Duplicate")) {
            keyPair = Iterables.getLast(ec2Client.getKeyPairServices().describeKeyPairsInRegion(
                     region, name));
            logger.debug("<< reused keyPair(%s)", keyPair.getKeyName());

         } else {
            throw e;
         }
      }
      return keyPair;
   }

   private void createSecurityGroupInRegion(Region region, String name, int... ports) {
      logger.debug(">> creating securityGroup name(%s)", name);
      try {
         ec2Client.getSecurityGroupServices().createSecurityGroupInRegion(region, name, name);
         logger.debug("<< created securityGroup(%s)", name);
         logger
                  .debug(">> authorizing securityGroup name(%s) ports(%s)", name, Arrays
                           .asList(ports));
         for (int port : ports) {
            ec2Client.getSecurityGroupServices().authorizeSecurityGroupIngressInRegion(region,
                     name, IpProtocol.TCP, port, port, "0.0.0.0/0");
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
   public NodeMetadata getNodeMetadata(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      checkNotNull(node.getId(), "node.id");
      Region region = getRegionFromNodeOrDefault(node);
      RunningInstance runningInstance = Iterables.getOnlyElement(getAllRunningInstancesInRegion(
               region, node.getId()));
      return runningInstanceToNodeMetadata.apply(runningInstance);
   }

   @Singleton
   private static class RunningInstanceToNodeMetadata implements
            Function<RunningInstance, NodeMetadata> {

      @Override
      public NodeMetadata apply(RunningInstance from) {
         return new NodeMetadataImpl(from.getId(), from.getKeyName(), from.getRegion().toString(),
                  null, ImmutableMap.<String, String> of(), instanceToNodeState.get(from
                           .getInstanceState()), nullSafeSet(from.getIpAddress()), nullSafeSet(from
                           .getPrivateIpAddress()), 22, LoginType.SSH, ImmutableMap
                           .<String, String> of("availabilityZone", from.getAvailabilityZone()
                                    .toString()));
      }

      Set<InetAddress> nullSafeSet(InetAddress in) {
         if (in == null) {
            return ImmutableSet.<InetAddress> of();
         }
         return ImmutableSet.<InetAddress> of(in);
      }
   }

   private RunningInstance getOnlyRunningInstanceInRegion(Region region, String id) {
      Iterable<RunningInstance> instances = Iterables.filter(getAllRunningInstancesInRegion(region,
               id), new Predicate<RunningInstance>() {

         @Override
         public boolean apply(RunningInstance instance) {
            return instance.getInstanceState() == InstanceState.PENDING
                     || instance.getInstanceState() == InstanceState.RUNNING;
         }

      });
      int size = Iterables.size(instances);
      if (size == 0)
         throw new NoSuchElementException(String.format(
                  "%d instances in region %s have an instance with id %s running.", size, region,
                  id));
      if (size > 1)
         throw new IllegalStateException(String.format(
                  "%d instances in region %s have an instance with id %s running.  Expected 1",
                  size, region, id));
      return Iterables.getOnlyElement(instances);
   }

   private Iterable<RunningInstance> getAllRunningInstancesInRegion(Region region, String id) {
      return Iterables
               .concat(ec2Client.getInstanceServices().describeInstancesInRegion(region, id));
   }

   /**
    * hack alert. can't find a good place to store the original servername, so we are reusing the
    * keyname. This will break.
    */
   @Override
   public Set<ComputeMetadata> listNodes() {
      logger.debug(">> listing servers");
      Set<ComputeMetadata> servers = Sets.newHashSet();
      for (Region region : ImmutableSet.of(Region.US_EAST_1, Region.US_WEST_1, Region.EU_WEST_1)) {
         Iterables.addAll(servers, Iterables.transform(Iterables.concat(ec2Client
                  .getInstanceServices().describeInstancesInRegion(region)),
                  new Function<RunningInstance, ComputeMetadata>() {
                     @Override
                     public ComputeMetadata apply(RunningInstance from) {
                        return new ComputeMetadataImpl(ComputeType.NODE, from.getId(), from
                                 .getKeyName(), from.getRegion().toString(), null, ImmutableMap
                                 .<String, String> of());
                     }
                  }));
      }
      logger.debug("<< list(%d)", servers.size());
      return servers;
   }

   @Override
   public void destroyNode(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      checkNotNull(node.getId(), "node.id");
      Region region = getRegionFromNodeOrDefault(node);
      for (RunningInstance runningInstance : getAllRunningInstancesInRegion(region, node.getId())) {
         // grab the old keyname
         String name = runningInstance.getKeyName();
         logger.debug(">> terminating instance(%s)", node.getId());
         ec2Client.getInstanceServices().terminateInstancesInRegion(region, node.getId());
         logger.debug("<< terminated instance(%s)", node.getId());
         logger.debug(">> deleting keyPair(%s)", name);
         ec2Client.getKeyPairServices().deleteKeyPairInRegion(region, name);
         logger.debug("<< deleted keyPair(%s)", name);
         logger.debug(">> deleting securityGroup(%s)", name);
         ec2Client.getSecurityGroupServices().deleteSecurityGroupInRegion(region, name);
         logger.debug("<< deleted securityGroup(%s)", name);
      }
   }

   private Region getRegionFromNodeOrDefault(ComputeMetadata node) {
      Region region = node.getLocation() != null ? Region.fromValue(node.getLocation())
               : Region.DEFAULT;
      return region;
   }

   @Override
   public Map<String, Size> getSizes() {
      throw new UnsupportedOperationException();
   }
}