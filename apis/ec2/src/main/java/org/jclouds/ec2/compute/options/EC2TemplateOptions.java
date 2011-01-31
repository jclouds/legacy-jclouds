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

package org.jclouds.ec2.compute.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Arrays;
import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.ec2.domain.BlockDeviceMapping;
import org.jclouds.ec2.domain.BlockDeviceMapping.MapEBSSnapshotToDevice;
import org.jclouds.ec2.domain.BlockDeviceMapping.MapEphemeralDeviceToDevice;
import org.jclouds.ec2.domain.BlockDeviceMapping.MapNewVolumeToDevice;
import org.jclouds.ec2.domain.BlockDeviceMapping.UnmapDeviceNamed;
import org.jclouds.io.Payload;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.util.Preconditions2;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Contains options supported in the {@code ComputeService#runNode} operation on the "ec2" provider.
 * <h2>
 * Usage</h2> The recommended way to instantiate a EC2TemplateOptions object is to statically import
 * EC2TemplateOptions.* and invoke a static creation method followed by an instance mutator (if
 * needed):
 * <p/>
 * <code>
 * import static org.jclouds.aws.ec2.compute.options.EC2TemplateOptions.Builder.*;
 * <p/>
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set<? extends NodeMetadata> set = client.runNodesWithTag(tag, 2, templateBuilder.build());
 * <code>
 * 
 * @author Adrian Cole
 */
public class EC2TemplateOptions extends TemplateOptions {

   private Set<String> groupIds = ImmutableSet.of();
   private String keyPair = null;
   private boolean noKeyPair;
   private byte[] userData;
   private Set<BlockDeviceMapping> blockDeviceMappings = ImmutableSet.of();

   public static final EC2TemplateOptions NONE = new EC2TemplateOptions();

   /**
    * 
    * @see EC2TemplateOptions#securityGroups(Iterable<String>)
    */
   public EC2TemplateOptions securityGroups(String... groupIds) {
      return securityGroups(ImmutableSet.copyOf(groupIds));
   }

   /**
    * Specifies the security groups to be used for nodes with this template
    */
   public EC2TemplateOptions securityGroups(Iterable<String> groupIds) {
      checkArgument(Iterables.size(groupIds) > 0, "you must specify at least one security group");
      for (String groupId : groupIds)
         Preconditions2.checkNotEmpty(groupId, "all security groups must be non-empty");
      this.groupIds = ImmutableSet.copyOf(groupIds);
      return this;
   }

   /**
    * Unencoded data
    */
   public EC2TemplateOptions userData(byte[] unencodedData) {
      checkArgument(checkNotNull(unencodedData, "unencodedData").length <= 16 * 1024,
               "userData cannot be larger than 16kb");
      this.userData = unencodedData;
      return this;
   }

   /**
    * Specifies the keypair used to run instances with
    */
   public EC2TemplateOptions keyPair(String keyPair) {
      checkNotNull(keyPair, "use noKeyPair option to request boot without a keypair");
      checkState(!noKeyPair, "you cannot specify both options keyPair and noKeyPair");
      Preconditions2.checkNotEmpty(keyPair, "keypair must be non-empty");
      this.keyPair = keyPair;
      return this;
   }

   /**
    * Do not use a keypair on instances
    */
   public EC2TemplateOptions noKeyPair() {
      checkState(keyPair == null, "you cannot specify both options keyPair and noKeyPair");
      this.noKeyPair = true;
      return this;
   }

   /**
    * Specifies the block device mappings to be used to run the instance
    */
   public EC2TemplateOptions mapEBSSnapshotToDeviceName(String deviceName, String snapshotId,
            @Nullable Integer sizeInGib, boolean deleteOnTermination) {
      checkNotNull(deviceName, "deviceName cannot be null");
      Preconditions2.checkNotEmpty(deviceName, "deviceName must be non-empty");
      checkNotNull(snapshotId, "snapshotId cannot be null");
      Preconditions2.checkNotEmpty(snapshotId, "snapshotId must be non-empty");
      com.google.common.collect.ImmutableSet.Builder<BlockDeviceMapping> mappings = ImmutableSet
               .<BlockDeviceMapping> builder();
      mappings.addAll(blockDeviceMappings);
      MapEBSSnapshotToDevice mapping = new MapEBSSnapshotToDevice(deviceName, snapshotId, sizeInGib,
               deleteOnTermination);
      mappings.add(mapping);
      blockDeviceMappings = mappings.build();
      return this;
   }

   /**
    * Specifies the block device mappings to be used to run the instance
    */
   public EC2TemplateOptions mapNewVolumeToDeviceName(String deviceName, int sizeInGib, boolean deleteOnTermination) {
      checkNotNull(deviceName, "deviceName cannot be null");
      Preconditions2.checkNotEmpty(deviceName, "deviceName must be non-empty");

      com.google.common.collect.ImmutableSet.Builder<BlockDeviceMapping> mappings = ImmutableSet
               .<BlockDeviceMapping> builder();
      mappings.addAll(blockDeviceMappings);
      MapNewVolumeToDevice mapping = new MapNewVolumeToDevice(deviceName, sizeInGib, deleteOnTermination);
      mappings.add(mapping);
      blockDeviceMappings = mappings.build();
      return this;
   }

   /**
    * Specifies the block device mappings to be used to run the instance
    */
   public EC2TemplateOptions mapEphemeralDeviceToDeviceName(String deviceName, String virtualName) {
      checkNotNull(deviceName, "deviceName cannot be null");
      Preconditions2.checkNotEmpty(deviceName, "deviceName must be non-empty");
      checkNotNull(virtualName, "virtualName cannot be null");
      Preconditions2.checkNotEmpty(virtualName, "virtualName must be non-empty");

      com.google.common.collect.ImmutableSet.Builder<BlockDeviceMapping> mappings = ImmutableSet
               .<BlockDeviceMapping> builder();
      mappings.addAll(blockDeviceMappings);
      MapEphemeralDeviceToDevice mapping = new MapEphemeralDeviceToDevice(deviceName, virtualName);
      mappings.add(mapping);
      blockDeviceMappings = mappings.build();
      return this;
   }

   /**
    * Specifies the block device mappings to be used to run the instance
    */
   public EC2TemplateOptions unmapDeviceNamed(String deviceName) {
      checkNotNull(deviceName, "deviceName cannot be null");
      Preconditions2.checkNotEmpty(deviceName, "deviceName must be non-empty");

      com.google.common.collect.ImmutableSet.Builder<BlockDeviceMapping> mappings = ImmutableSet
               .<BlockDeviceMapping> builder();
      mappings.addAll(blockDeviceMappings);
      UnmapDeviceNamed mapping = new UnmapDeviceNamed(deviceName);
      mappings.add(mapping);
      blockDeviceMappings = mappings.build();
      return this;
   }

   /**
    * Specifies the block device mappings to be used to run the instance
    */
   public EC2TemplateOptions blockDeviceMappings(Set<? extends BlockDeviceMapping> blockDeviceMappings) {
      this.blockDeviceMappings = ImmutableSet.copyOf(checkNotNull(blockDeviceMappings, "blockDeviceMappings"));
      return this;
   }

   public static class Builder {
      /**
       * @see EC2TemplateOptions#blockDeviceMappings
       */
      public static EC2TemplateOptions blockDeviceMappings(Set<? extends BlockDeviceMapping> blockDeviceMappings) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return options.blockDeviceMappings(blockDeviceMappings);
      }

      /**
       * @see EC2TemplateOptions#mapEBSSnapshotToDeviceName
       */
      public static EC2TemplateOptions mapEBSSnapshotToDeviceName(String deviceName, String snapshotId,
               @Nullable Integer sizeInGib, boolean deleteOnTermination) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return options.mapEBSSnapshotToDeviceName(deviceName, snapshotId, sizeInGib, deleteOnTermination);
      }

      /**
       * @see EC2TemplateOptions#mapNewVolumeToDeviceName
       */
      public static EC2TemplateOptions mapNewVolumeToDeviceName(String deviceName, int sizeInGib,
               boolean deleteOnTermination) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return options.mapNewVolumeToDeviceName(deviceName, sizeInGib, deleteOnTermination);
      }

      /**
       * @see EC2TemplateOptions#mapEphemeralDeviceToDeviceName
       */
      public static EC2TemplateOptions mapEphemeralDeviceToDeviceName(String deviceName, String virtualName) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return options.mapEphemeralDeviceToDeviceName(deviceName, virtualName);
      }

      /**
       * @see EC2TemplateOptions#unmapDeviceNamed
       */
      public static EC2TemplateOptions unmapDeviceNamed(String deviceName) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return options.unmapDeviceNamed(deviceName);
      }

      /**
       * @see EC2TemplateOptions#securityGroups(Iterable<String>)
       */
      public static EC2TemplateOptions securityGroups(String... groupIds) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.securityGroups(groupIds));
      }

      /**
       * @see EC2TemplateOptions#securityGroups(Iterable<String>)
       */
      public static EC2TemplateOptions securityGroups(Iterable<String> groupIds) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.securityGroups(groupIds));
      }

      /**
       * @see EC2TemplateOptions#keyPair
       */
      public static EC2TemplateOptions keyPair(String keyPair) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.keyPair(keyPair));
      }

      /**
       * @see EC2TemplateOptions#userData
       */
      public static EC2TemplateOptions userData(byte[] unencodedData) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.userData(unencodedData));
      }

      /**
       * @see EC2TemplateOptions#noKeyPair
       */
      public static EC2TemplateOptions noKeyPair() {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.noKeyPair());
      }

      // methods that only facilitate returning the correct object type
      /**
       * @see TemplateOptions#inboundPorts
       */
      public static EC2TemplateOptions inboundPorts(int... ports) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.inboundPorts(ports));
      }

      /**
       * @see TemplateOptions#port
       */
      public static EC2TemplateOptions blockOnPort(int port, int seconds) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.blockOnPort(port, seconds));
      }

      /**
       * @see TemplateOptions#runScript
       */
      public static EC2TemplateOptions runScript(byte[] script) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.runScript(script));
      }

      /**
       * @see TemplateOptions#installPrivateKey
       */
      public static EC2TemplateOptions installPrivateKey(String rsaKey) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.installPrivateKey(rsaKey));
      }

      /**
       * @see TemplateOptions#authorizePublicKey
       */
      public static EC2TemplateOptions authorizePublicKey(String rsaKey) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.authorizePublicKey(rsaKey));
      }

      /**
       * @see TemplateOptions#withDetails
       */
      public static EC2TemplateOptions withDetails() {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.withMetadata());
      }

   }

   // methods that only facilitate returning the correct object type

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions blockOnPort(int port, int seconds) {
      return EC2TemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions inboundPorts(int... ports) {
      return EC2TemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions authorizePublicKey(String publicKey) {
      return EC2TemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @Deprecated
   public EC2TemplateOptions authorizePublicKey(Payload publicKey) {
      return EC2TemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions installPrivateKey(String privateKey) {
      return EC2TemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @Deprecated
   public EC2TemplateOptions installPrivateKey(Payload privateKey) {
      return EC2TemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions runScript(Payload script) {
      return EC2TemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @Deprecated
   public EC2TemplateOptions runScript(byte[] script) {
      return EC2TemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions withMetadata() {
      return EC2TemplateOptions.class.cast(super.withMetadata());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions blockUntilRunning(boolean blockUntilRunning) {
      return EC2TemplateOptions.class.cast(super.blockUntilRunning(blockUntilRunning));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions dontAuthorizePublicKey() {
      return EC2TemplateOptions.class.cast(super.dontAuthorizePublicKey());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions nameTask(String name) {
      return EC2TemplateOptions.class.cast(super.nameTask(name));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions runAsRoot(boolean runAsRoot) {
      return EC2TemplateOptions.class.cast(super.runAsRoot(runAsRoot));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions runScript(Statement script) {
      return EC2TemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions withOverridingCredentials(Credentials overridingCredentials) {
      return EC2TemplateOptions.class.cast(super.withOverridingCredentials(overridingCredentials));
   }

   /**
    * @return groupIds the user specified to run instances with, or zero length set to create an
    *         implicit group
    */
   public Set<String> getGroupIds() {
      return groupIds;
   }

   /**
    * @return keyPair to use when running the instance or null, to generate a keypair.
    */
   public String getKeyPair() {
      return keyPair;
   }

   /**
    * @return true (default) if we are supposed to use a keypair
    */
   public boolean shouldAutomaticallyCreateKeyPair() {
      return !noKeyPair;
   }

   /**
    * @return unencoded user data.
    */
   public byte[] getUserData() {
      return userData;
   }

   /**
    * @return BlockDeviceMapping to use when running the instance or null.
    */
   public Set<BlockDeviceMapping> getBlockDeviceMappings() {
      return blockDeviceMappings;
   }

   @Override
   public int hashCode() {

      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((blockDeviceMappings == null) ? 0 : blockDeviceMappings.hashCode());
      result = prime * result + ((groupIds == null) ? 0 : groupIds.hashCode());
      result = prime * result + ((keyPair == null) ? 0 : keyPair.hashCode());
      result = prime * result + (noKeyPair ? 1231 : 1237);
      result = prime * result + Arrays.hashCode(userData);
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      EC2TemplateOptions other = (EC2TemplateOptions) obj;
      if (blockDeviceMappings == null) {
         if (other.blockDeviceMappings != null)
            return false;
      } else if (!blockDeviceMappings.equals(other.blockDeviceMappings))
         return false;
      if (groupIds == null) {
         if (other.groupIds != null)
            return false;
      } else if (!groupIds.equals(other.groupIds))
         return false;
      if (keyPair == null) {
         if (other.keyPair != null)
            return false;
      } else if (!keyPair.equals(other.keyPair))
         return false;

      if (!Arrays.equals(userData, other.userData))
         return false;

      return true;
   }

   @Override
   public String toString() {
      return "[groupIds=" + groupIds + ", keyPair=" + keyPair + ", noKeyPair=" + noKeyPair + ", userData="
               + Arrays.toString(userData) + ", blockDeviceMappings=" + blockDeviceMappings + "]";
   }
}
