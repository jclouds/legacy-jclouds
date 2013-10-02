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
package org.jclouds.aws.ec2.compute;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.emptyToNull;

import java.util.Map;
import java.util.Set;

import org.jclouds.aws.ec2.options.RequestSpotInstancesOptions;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.domain.BlockDeviceMapping;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.SinceApiVersion;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Contains options supported in the {@code ComputeService#runNode} operation on
 * the "ec2" provider. <h2>
 * Usage</h2> The recommended way to instantiate a AWSEC2TemplateOptions object
 * is to statically import AWSEC2TemplateOptions.* and invoke a static creation
 * method followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.aws.ec2.compute.options.AWSEC2TemplateOptions.Builder.*;
 * <p/>
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set<? extends NodeMetadata> set = client.createNodesInGroup(tag, 2, templateBuilder.build());
 * <code>
 * 
 * @author Adrian Cole
 */
public class AWSEC2TemplateOptions extends EC2TemplateOptions implements Cloneable {
   @Override
   public AWSEC2TemplateOptions clone() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof AWSEC2TemplateOptions) {
         AWSEC2TemplateOptions eTo = AWSEC2TemplateOptions.class.cast(to);
         if (getSubnetId() != null)
            eTo.subnetId(getSubnetId());
         if (getIAMInstanceProfileArn() != null)
            eTo.iamInstanceProfileArn(getIAMInstanceProfileArn());
         if (getIAMInstanceProfileName() != null)
            eTo.iamInstanceProfileName(getIAMInstanceProfileName());
         if (isMonitoringEnabled())
            eTo.enableMonitoring();
         if (!shouldAutomaticallyCreatePlacementGroup())
            eTo.noPlacementGroup();
         if (getPlacementGroup() != null)
            eTo.placementGroup(getPlacementGroup());
         if (getGroupIds().size() > 0)
            eTo.securityGroupIds(getGroupIds());
         if (getSpotPrice() != null)
            eTo.spotPrice(getSpotPrice());
         if (getSpotOptions() != null)
            eTo.spotOptions(getSpotOptions());
      }
   }

   private boolean monitoringEnabled;
   private String placementGroup = null;
   private boolean noPlacementGroup;
   private String subnetId;
   private Float spotPrice;
   private RequestSpotInstancesOptions spotOptions = RequestSpotInstancesOptions.NONE;
   private Set<String> groupIds = ImmutableSet.of();
   private String iamInstanceProfileArn;
   private String iamInstanceProfileName;

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      AWSEC2TemplateOptions that = AWSEC2TemplateOptions.class.cast(o);
      return super.equals(that) && equal(this.monitoringEnabled, that.monitoringEnabled)
               && equal(this.placementGroup, that.placementGroup)
               && equal(this.noPlacementGroup, that.noPlacementGroup) && equal(this.subnetId, that.subnetId)
               && equal(this.spotPrice, that.spotPrice) && equal(this.spotOptions, that.spotOptions)
               && equal(this.groupIds, that.groupIds) && equal(this.iamInstanceProfileArn, that.iamInstanceProfileArn)
               && equal(this.iamInstanceProfileName, that.iamInstanceProfileName);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), monitoringEnabled, placementGroup, noPlacementGroup, subnetId,
               spotPrice, spotOptions, groupIds, iamInstanceProfileArn, iamInstanceProfileName);
   }

   @Override
   public ToStringHelper string() {
      ToStringHelper toString = super.string();
      if (monitoringEnabled)
         toString.add("monitoringEnabled", monitoringEnabled);
      toString.add("placementGroup", placementGroup);
      if (noPlacementGroup)
         toString.add("noPlacementGroup", noPlacementGroup);
      toString.add("subnetId", subnetId);
      toString.add("spotPrice", spotPrice);
      if (spotOptions != RequestSpotInstancesOptions.NONE)
         toString.add("spotOptions", spotOptions);
      if (groupIds.size() != 0)
         toString.add("groupIds", groupIds);
      toString.add("iamInstanceProfileArn", iamInstanceProfileArn);
      toString.add("iamInstanceProfileName", iamInstanceProfileName);
      return toString;
   }

   public static final AWSEC2TemplateOptions NONE = new AWSEC2TemplateOptions();

   /**
    * Enable Cloudwatch monitoring
    * 
    * @see CloudWatchClient
    */
   public AWSEC2TemplateOptions enableMonitoring() {
      this.monitoringEnabled = true;
      return this;
   }

   /**
    * Specifies the keypair used to run instances with
    */
   public AWSEC2TemplateOptions placementGroup(String placementGroup) {
      checkNotNull(emptyToNull(placementGroup), "use noPlacementGroup option instead of passing null");
      checkState(!noPlacementGroup, "you cannot specify both options placementGroup and noPlacementGroup");
      this.placementGroup = placementGroup;
      return this;
   }

   /**
    * Do not use a keypair on instances
    */
   public AWSEC2TemplateOptions noPlacementGroup() {
      checkState(placementGroup == null, "you cannot specify both options placementGroup and noPlacementGroup");
      this.noPlacementGroup = true;
      return this;
   }

   /**
    * Specifies the subnetId used to run instances in
    */
   public AWSEC2TemplateOptions subnetId(String subnetId) {
      this.subnetId = checkNotNull(emptyToNull(subnetId), "subnetId must be defined");
      return this;
   }

   /**
    * @see org.jclouds.aws.ec2.options.AWSRunInstancesOptions#withIAMInstanceProfileArn(String)
    */
   @SinceApiVersion("2012-06-01")
   public AWSEC2TemplateOptions iamInstanceProfileArn(String arn) {
      this.iamInstanceProfileArn = checkNotNull(emptyToNull(arn), "arn must be defined");
      return this;
   }

   /**
    * @see org.jclouds.aws.ec2.options.AWSRunInstancesOptions#withIAMInstanceProfileName(String)
    */
   @SinceApiVersion("2012-06-01")
   public AWSEC2TemplateOptions iamInstanceProfileName(String name) {
      this.iamInstanceProfileName = checkNotNull(emptyToNull(name), "name must be defined");
      return this;
   }

   /**
    * Specifies the maximum spot price to use
    */
   public AWSEC2TemplateOptions spotPrice(Float spotPrice) {
      this.spotPrice = spotPrice;
      return this;
   }

   /**
    * Options for starting spot instances
    */
   public AWSEC2TemplateOptions spotOptions(RequestSpotInstancesOptions spotOptions) {
      this.spotOptions = spotOptions != null ? spotOptions : RequestSpotInstancesOptions.NONE;
      return this;
   }

   /**
    * 
    * @see AWSEC2TemplateOptions#securityGroupIds(Iterable<String>)
    */
   public AWSEC2TemplateOptions securityGroupIds(String... groupIds) {
      return securityGroupIds(ImmutableSet.copyOf(groupIds));
   }

   /**
    * Specifies the security group ids to be used for nodes with this template
    */
   public AWSEC2TemplateOptions securityGroupIds(Iterable<String> groupIds) {
      checkArgument(Iterables.size(groupIds) > 0, "you must specify at least one security group");
      for (String groupId : groupIds)
         checkNotNull(emptyToNull(groupId), "all security groups must be non-empty");
      this.groupIds = ImmutableSet.copyOf(groupIds);
      return this;
   }

   public Set<String> getGroupIds() {
      return groupIds;
   }

   public static class Builder {

      public static AWSEC2TemplateOptions overrideLoginUser(String user) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.overrideLoginUser(user);
      }

      public static AWSEC2TemplateOptions overrideLoginPassword(String password) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.overrideLoginPassword(password);
      }

      public static AWSEC2TemplateOptions overrideLoginPrivateKey(String privateKey) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.overrideLoginPrivateKey(privateKey);
      }

      public static AWSEC2TemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.overrideAuthenticateSudo(authenticateSudo);
      }

      public static AWSEC2TemplateOptions overrideLoginCredentials(LoginCredentials credentials) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.overrideLoginCredentials(credentials);
      }

      /**
       * @see AWSEC2TemplateOptions#securityGroupIds(Iterable<String>)
       */
      public static AWSEC2TemplateOptions securityGroupIds(String... groupNames) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return AWSEC2TemplateOptions.class.cast(options.securityGroupIds(groupNames));
      }

      /**
       * @see AWSEC2TemplateOptions#securityGroupIds(Iterable<String>)
       */
      public static AWSEC2TemplateOptions securityGroupIds(Iterable<String> groupNames) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return AWSEC2TemplateOptions.class.cast(options.securityGroupIds(groupNames));
      }

      /**
       * @see EC2TemplateOptions#blockDeviceMappings
       */
      public static AWSEC2TemplateOptions blockDeviceMappings(Set<? extends BlockDeviceMapping> blockDeviceMappings) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.blockDeviceMappings(blockDeviceMappings);
      }

      /**
       * @see EC2TemplateOptions#mapEBSSnapshotToDeviceName
       */
      public static AWSEC2TemplateOptions mapEBSSnapshotToDeviceName(String deviceName, String snapshotId,
            @Nullable Integer sizeInGib, boolean deleteOnTermination) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.mapEBSSnapshotToDeviceName(deviceName, snapshotId, sizeInGib, deleteOnTermination);
      }

      /**
       * @see EC2TemplateOptions#mapNewVolumeToDeviceName
       */
      public static AWSEC2TemplateOptions mapNewVolumeToDeviceName(String deviceName, int sizeInGib,
            boolean deleteOnTermination) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.mapNewVolumeToDeviceName(deviceName, sizeInGib, deleteOnTermination);
      }

      /**
       * @see EC2TemplateOptions#mapEphemeralDeviceToDeviceName
       */
      public static AWSEC2TemplateOptions mapEphemeralDeviceToDeviceName(String deviceName, String virtualName) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.mapEphemeralDeviceToDeviceName(deviceName, virtualName);
      }

      /**
       * @see EC2TemplateOptions#unmapDeviceNamed
       */
      public static AWSEC2TemplateOptions unmapDeviceNamed(String deviceName) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.unmapDeviceNamed(deviceName);
      }

      /**
       * @see AWSEC2TemplateOptions#securityGroups(Iterable<String>)
       */
      public static AWSEC2TemplateOptions securityGroups(String... groupIds) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.securityGroups(groupIds);
      }

      /**
       * @see AWSEC2TemplateOptions#securityGroups(Iterable<String>)
       */
      public static AWSEC2TemplateOptions securityGroups(Iterable<String> groupIds) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.securityGroups(groupIds);
      }

      /**
       * @see AWSEC2TemplateOptions#keyPair
       */
      public static AWSEC2TemplateOptions keyPair(String keyPair) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.keyPair(keyPair);
      }

      /**
       * @see AWSEC2TemplateOptions#userData
       */
      public static AWSEC2TemplateOptions userData(byte[] unencodedData) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.userData(unencodedData);
      }

      /**
       * @see AWSEC2TemplateOptions#noKeyPair
       */
      public static AWSEC2TemplateOptions noKeyPair() {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.noKeyPair();
      }

      /**
       * @see AWSEC2TemplateOptions#placementGroup
       */
      public static AWSEC2TemplateOptions placementGroup(String placementGroup) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.placementGroup(placementGroup);
      }

      /**
       * @see AWSEC2TemplateOptions#noPlacementGroup
       */
      public static AWSEC2TemplateOptions noPlacementGroup() {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.noPlacementGroup();
      }

      /**
       * @see AWSEC2TemplateOptions#enableMonitoring
       */
      public static AWSEC2TemplateOptions enableMonitoring() {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.enableMonitoring();
      }

      // methods that only facilitate returning the correct object type
      /**
       * @see TemplateOptions#inboundPorts
       */
      public static AWSEC2TemplateOptions inboundPorts(int... ports) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.inboundPorts(ports);
      }

      /**
       * @see TemplateOptions#port
       */
      public static AWSEC2TemplateOptions blockOnPort(int port, int seconds) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.blockOnPort(port, seconds);
      }

      /**
       * @see TemplateOptions#installPrivateKey
       */
      public static AWSEC2TemplateOptions installPrivateKey(String rsaKey) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.installPrivateKey(rsaKey);
      }

      /**
       * @see TemplateOptions#authorizePublicKey
       */
      public static AWSEC2TemplateOptions authorizePublicKey(String rsaKey) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.authorizePublicKey(rsaKey);
      }

      /**
       * @see AWSEC2TemplateOptions#subnetId
       */
      public static AWSEC2TemplateOptions subnetId(String subnetId) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.subnetId(subnetId);
      }

      /**
       * @see AWSEC2TemplateOptions#iamInstanceProfileArn
       */
      @SinceApiVersion("2012-06-01")
      public static AWSEC2TemplateOptions iamInstanceProfileArn(String arn) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.iamInstanceProfileArn(arn);
      }

      /**
       * @see AWSEC2TemplateOptions#iamInstanceProfileName
       */
      @SinceApiVersion("2012-06-01")
      public static AWSEC2TemplateOptions iamInstanceProfileName(String name) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.iamInstanceProfileName(name);
      }

      /**
       * @see AWSEC2TemplateOptions#spotPrice
       */
      public static AWSEC2TemplateOptions spotPrice(Float spotPrice) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.spotPrice(spotPrice);
      }

      /**
       * @see AWSEC2TemplateOptions#spotOptions
       */
      public static AWSEC2TemplateOptions spotOptions(RequestSpotInstancesOptions spotOptions) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.spotOptions(spotOptions);
      }

      /**
       * @see TemplateOptions#userMetadata(Map)
       */
      public static AWSEC2TemplateOptions userMetadata(Map<String, String> userMetadata) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return AWSEC2TemplateOptions.class.cast(options.userMetadata(userMetadata));
      }

      /**
       * @see TemplateOptions#userMetadata(String, String)
       */
      public static AWSEC2TemplateOptions userMetadata(String key, String value) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return AWSEC2TemplateOptions.class.cast(options.userMetadata(key, value));
      }

      public static AWSEC2TemplateOptions blockUntilRunning(boolean blockUntilRunning) {
         AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
         return options.blockUntilRunning(blockUntilRunning);
      }
   }

   // methods that only facilitate returning the correct object type

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions blockDeviceMappings(Iterable<? extends BlockDeviceMapping> blockDeviceMappings) {
      return AWSEC2TemplateOptions.class.cast(super.blockDeviceMappings(blockDeviceMappings));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions userMetadata(Map<String, String> userMetadata) {
      return AWSEC2TemplateOptions.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions userMetadata(String key, String value) {
      return AWSEC2TemplateOptions.class.cast(super.userMetadata(key, value));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions keyPair(String keyPair) {
      return AWSEC2TemplateOptions.class.cast(super.keyPair(keyPair));
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public AWSEC2TemplateOptions mapEBSSnapshotToDeviceName(String deviceName, String snapshotId, Integer sizeInGib,
         boolean deleteOnTermination) {
      return AWSEC2TemplateOptions.class.cast(super.mapEBSSnapshotToDeviceName(deviceName, snapshotId, sizeInGib,
            deleteOnTermination));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions mapEphemeralDeviceToDeviceName(String deviceName, String virtualName) {
      return AWSEC2TemplateOptions.class.cast(super.mapEphemeralDeviceToDeviceName(deviceName, virtualName));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions mapNewVolumeToDeviceName(String deviceName, int sizeInGib, boolean deleteOnTermination) {
      return AWSEC2TemplateOptions.class.cast(super
            .mapNewVolumeToDeviceName(deviceName, sizeInGib, deleteOnTermination));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions noKeyPair() {
      return AWSEC2TemplateOptions.class.cast(super.noKeyPair());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions securityGroups(Iterable<String> groupIds) {
      return AWSEC2TemplateOptions.class.cast(super.securityGroups(groupIds));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions securityGroups(String... groupIds) {
      return AWSEC2TemplateOptions.class.cast(super.securityGroups(groupIds));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions unmapDeviceNamed(String deviceName) {
      return AWSEC2TemplateOptions.class.cast(super.unmapDeviceNamed(deviceName));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions userData(byte[] unencodedData) {
      return AWSEC2TemplateOptions.class.cast(super.userData(unencodedData));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions blockOnPort(int port, int seconds) {
      return AWSEC2TemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions inboundPorts(int... ports) {
      return AWSEC2TemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions authorizePublicKey(String publicKey) {
      return AWSEC2TemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions installPrivateKey(String privateKey) {
      return AWSEC2TemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions blockUntilRunning(boolean blockUntilRunning) {
      return AWSEC2TemplateOptions.class.cast(super.blockUntilRunning(blockUntilRunning));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions dontAuthorizePublicKey() {
      return AWSEC2TemplateOptions.class.cast(super.dontAuthorizePublicKey());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions nameTask(String name) {
      return AWSEC2TemplateOptions.class.cast(super.nameTask(name));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions runAsRoot(boolean runAsRoot) {
      return AWSEC2TemplateOptions.class.cast(super.runAsRoot(runAsRoot));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions runScript(Statement script) {
      return AWSEC2TemplateOptions.class.cast(super.runScript(script));
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions overrideLoginCredentials(LoginCredentials overridingCredentials) {
      return AWSEC2TemplateOptions.class.cast(super.overrideLoginCredentials(overridingCredentials));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions overrideLoginPassword(String password) {
      return AWSEC2TemplateOptions.class.cast(super.overrideLoginPassword(password));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions overrideLoginPrivateKey(String privateKey) {
      return AWSEC2TemplateOptions.class.cast(super.overrideLoginPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions overrideLoginUser(String loginUser) {
      return AWSEC2TemplateOptions.class.cast(super.overrideLoginUser(loginUser));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AWSEC2TemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
      return AWSEC2TemplateOptions.class.cast(super.overrideAuthenticateSudo(authenticateSudo));
   }

   /**
    * @return placementGroup to use when running the instance or null, to
    *         generate a placementGroup.
    */
   public String getPlacementGroup() {
      return placementGroup;
   }

   /**
    * @return true (default) if we are supposed to use a placementGroup
    */
   public boolean shouldAutomaticallyCreatePlacementGroup() {
      return !noPlacementGroup;
   }

   /**
    * @return true (default) if we are supposed to enable cloudwatch
    */
   public boolean isMonitoringEnabled() {
      return monitoringEnabled;
   }

   /**
    * @return subnetId to use when running the instance or null.
    */
   public String getSubnetId() {
      return subnetId;
   }

   /**
    * @return maximum spot price or null.
    */
   public Float getSpotPrice() {
      return spotPrice;
   }

   /**
    * @return options for controlling spot instance requests.
    */
   public RequestSpotInstancesOptions getSpotOptions() {
      return spotOptions;
   }

   /**
    * The Amazon resource name (ARN) of the IAM Instance Profile (IIP) to associate with the instance.
    * 
    * @see org.jclouds.aws.ec2.options.AWSRunInstancesOptions#withIAMInstanceProfileArn(String)
    */
   @SinceApiVersion("2012-06-01")
   public String getIAMInstanceProfileArn() {
      return iamInstanceProfileArn;
   }

   /**
    * The name of the IAM Instance Profile (IIP) to associate with the instance.
    * 
    * @see org.jclouds.aws.ec2.options.AWSRunInstancesOptions#withIAMInstanceProfileName(String)
    */
   @SinceApiVersion("2012-06-01")
   public String getIAMInstanceProfileName() {
      return iamInstanceProfileName;
   }
}
