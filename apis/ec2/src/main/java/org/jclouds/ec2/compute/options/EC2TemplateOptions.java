/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ec2.compute.options;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.emptyToNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.domain.BlockDeviceMapping;
import org.jclouds.ec2.domain.BlockDeviceMapping.MapEBSSnapshotToDevice;
import org.jclouds.ec2.domain.BlockDeviceMapping.MapEphemeralDeviceToDevice;
import org.jclouds.ec2.domain.BlockDeviceMapping.MapNewVolumeToDevice;
import org.jclouds.ec2.domain.BlockDeviceMapping.UnmapDeviceNamed;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Bytes;

/**
 * Contains options supported in the {@code ComputeService#runNode} operation on
 * the "ec2" provider. <h2>
 * Usage</h2> The recommended way to instantiate a EC2TemplateOptions object is
 * to statically import EC2TemplateOptions.* and invoke a static creation method
 * followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.aws.ec2.compute.options.EC2TemplateOptions.Builder.*;
 * <p/>
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set<? extends NodeMetadata> set = client.createNodesInGroup(tag, 2, templateBuilder.build());
 * <code>
 * 
 * @author Adrian Cole
 */
public class EC2TemplateOptions extends TemplateOptions implements Cloneable {
   @Override
   public EC2TemplateOptions clone() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof EC2TemplateOptions) {
         EC2TemplateOptions eTo = EC2TemplateOptions.class.cast(to);
         if (getGroups().size() > 0)
            eTo.securityGroups(getGroups());
         if (getKeyPair() != null)
            eTo.keyPair(getKeyPair());
         if (getBlockDeviceMappings().size() > 0)
            eTo.blockDeviceMappings(getBlockDeviceMappings());
         if (!shouldAutomaticallyCreateKeyPair())
            eTo.noKeyPair();
         if (getUserData() != null)
            eTo.userData(getUserData());
      }
   }

   private Set<String> groupNames = ImmutableSet.of();
   private String keyPair = null;
   private boolean noKeyPair;
   private List<Byte> userData;
   private ImmutableSet.Builder<BlockDeviceMapping> blockDeviceMappings = ImmutableSet.builder();

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      EC2TemplateOptions that = EC2TemplateOptions.class.cast(o);
      return super.equals(that) && equal(this.groupNames, that.groupNames) && equal(this.keyPair, that.keyPair)
               && equal(this.noKeyPair, that.noKeyPair) && equal(this.userData, that.userData)
               && equal(this.blockDeviceMappings, that.blockDeviceMappings);
   }

   @Override
   public int hashCode() {
      return Objects
               .hashCode(super.hashCode(), groupNames, keyPair, noKeyPair, userData, userData, blockDeviceMappings);
   }

   @Override
   public ToStringHelper string() {
      ToStringHelper toString = super.string();
      if (groupNames.size() != 0)
         toString.add("groupNames", groupNames);
      if (noKeyPair)
         toString.add("noKeyPair", noKeyPair);
      toString.add("keyPair", keyPair);
      if (userData != null && userData.size() > 0)
         toString.add("userDataCksum", Hashing.crc32().hashBytes(Bytes.toArray(userData)));
      ImmutableSet<BlockDeviceMapping> mappings = blockDeviceMappings.build();
      if (mappings.size() != 0)
         toString.add("blockDeviceMappings", mappings);
      return toString;
   }

   public static final EC2TemplateOptions NONE = new EC2TemplateOptions();

   /**
    * 
    * @see EC2TemplateOptions#securityGroups(Iterable<String>)
    */
   public EC2TemplateOptions securityGroups(String... groupNames) {
      return securityGroups(ImmutableSet.copyOf(groupNames));
   }

   /**
    * Specifies the security groups to be used for nodes with this template
    */
   public EC2TemplateOptions securityGroups(Iterable<String> groupNames) {
      checkArgument(Iterables.size(groupNames) > 0, "you must specify at least one security group");
      for (String groupId : groupNames)
         checkNotNull(emptyToNull(groupId), "all security groups must be non-empty");
      this.groupNames = ImmutableSet.copyOf(groupNames);
      return this;
   }

   /**
    * Unencoded data
    */
   public EC2TemplateOptions userData(byte[] unencodedData) {
      checkArgument(checkNotNull(unencodedData, "unencodedData").length <= 16 * 1024,
            "userData cannot be larger than 16kb");
      this.userData = Bytes.asList(unencodedData);
      return this;
   }

   /**
    * Specifies the keypair used to run instances with
    */
   public EC2TemplateOptions keyPair(String keyPair) {
      checkState(!noKeyPair, "you cannot specify both options keyPair and noKeyPair");
      this.keyPair = checkNotNull(emptyToNull(keyPair), "use noKeyPair option to request boot without a keypair");
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

   public EC2TemplateOptions mapEBSSnapshotToDeviceName(String deviceName, String snapshotId,
         @Nullable Integer sizeInGib, boolean deleteOnTermination) {
      blockDeviceMappings.add(new MapEBSSnapshotToDevice(deviceName, snapshotId, sizeInGib, deleteOnTermination));
      return this;
   }

   public EC2TemplateOptions mapNewVolumeToDeviceName(String deviceName, int sizeInGib, boolean deleteOnTermination) {
      blockDeviceMappings.add(new MapNewVolumeToDevice(deviceName, sizeInGib, deleteOnTermination));
      return this;
   }

   public EC2TemplateOptions mapEphemeralDeviceToDeviceName(String deviceName, String virtualName) {
      blockDeviceMappings.add(new MapEphemeralDeviceToDevice(deviceName, virtualName));
      return this;
   }

   public EC2TemplateOptions unmapDeviceNamed(String deviceName) {
      blockDeviceMappings.add(new UnmapDeviceNamed(deviceName));
      return this;
   }

   public EC2TemplateOptions blockDeviceMappings(Iterable<? extends BlockDeviceMapping> blockDeviceMappings) {
      this.blockDeviceMappings.addAll(checkNotNull(blockDeviceMappings, "blockDeviceMappings"));
      return this;
   }

   public static class Builder extends TemplateOptions.Builder {
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
      public static EC2TemplateOptions securityGroups(String... groupNames) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.securityGroups(groupNames));
      }

      /**
       * @see EC2TemplateOptions#securityGroups(Iterable<String>)
       */
      public static EC2TemplateOptions securityGroups(Iterable<String> groupNames) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.securityGroups(groupNames));
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
       * @see TemplateOptions#userMetadata(Map)
       */
      public static EC2TemplateOptions userMetadata(Map<String, String> userMetadata) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.userMetadata(userMetadata));
      }
      
      public static EC2TemplateOptions overrideLoginUser(String user) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return options.overrideLoginUser(user);
      }

      public static EC2TemplateOptions overrideLoginPassword(String password) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return options.overrideLoginPassword(password);
      }

      public static EC2TemplateOptions overrideLoginPrivateKey(String privateKey) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return options.overrideLoginPrivateKey(privateKey);
      }

      public static EC2TemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return options.overrideAuthenticateSudo(authenticateSudo);
      }

      public static EC2TemplateOptions overrideLoginCredentials(LoginCredentials credentials) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return options.overrideLoginCredentials(credentials);
      }

      public static EC2TemplateOptions nameTask(String name) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return options.nameTask(name);
      }

      public static EC2TemplateOptions runAsRoot(boolean value) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return options.runAsRoot(value);
      }

      public static EC2TemplateOptions tags(Iterable<String> tags) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return options.tags(tags);
      }

      public static EC2TemplateOptions blockUntilRunning(boolean blockUntilRunning) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return options.blockUntilRunning(blockUntilRunning);
      }

      public static EC2TemplateOptions runScript(Statement script) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return options.runScript(script);
      }
      
      public static EC2TemplateOptions runScript(String script) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return options.runScript(script);
      }

      public static EC2TemplateOptions userMetadata(String key, String value) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return options.userMetadata(key, value);
      }

      public static EC2TemplateOptions blockOnComplete(boolean value) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return options.blockOnComplete(value);
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
   public EC2TemplateOptions installPrivateKey(String privateKey) {
      return EC2TemplateOptions.class.cast(super.installPrivateKey(privateKey));
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
   public EC2TemplateOptions overrideLoginCredentials(LoginCredentials overridingCredentials) {
      return EC2TemplateOptions.class.cast(super.overrideLoginCredentials(overridingCredentials));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions overrideLoginPassword(String password) {
      return EC2TemplateOptions.class.cast(super.overrideLoginPassword(password));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions overrideLoginPrivateKey(String privateKey) {
      return EC2TemplateOptions.class.cast(super.overrideLoginPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions overrideLoginUser(String loginUser) {
      return EC2TemplateOptions.class.cast(super.overrideLoginUser(loginUser));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
      return EC2TemplateOptions.class.cast(super.overrideAuthenticateSudo(authenticateSudo));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions userMetadata(Map<String, String> userMetadata) {
      return EC2TemplateOptions.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions userMetadata(String key, String value) {
      return EC2TemplateOptions.class.cast(super.userMetadata(key, value));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions runScript(String script) {
      return EC2TemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions tags(Iterable<String> tags) {
      return EC2TemplateOptions.class.cast(super.tags(tags));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions wrapInInitScript(boolean wrapInInitScript) {
      return EC2TemplateOptions.class.cast(super.wrapInInitScript(wrapInInitScript));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2TemplateOptions blockOnComplete(boolean blockOnComplete) {
      return EC2TemplateOptions.class.cast(super.blockOnComplete(blockOnComplete));
   }
   
   /**
    * @return groupNames the user specified to run instances with, or zero
    *         length set to create an implicit group
    */
   public Set<String> getGroups() {
      return groupNames;
   }

   /**
    * @return keyPair to use when running the instance or null, to generate a
    *         keypair.
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
      return userData == null ? null : Bytes.toArray(userData);
   }

   /**
    * @return BlockDeviceMapping to use when running the instance or null.
    */
   public Set<BlockDeviceMapping> getBlockDeviceMappings() {
      return blockDeviceMappings.build();
   }

}
