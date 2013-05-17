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
package org.jclouds.aws.ec2.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.Hypervisor;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.SinceApiVersion;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-RunningInstancesItemType.html"
 *      />
 * @author Adrian Cole
 */
public class AWSRunningInstance extends RunningInstance {
   
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromRunningInstance(this);
   }
   
   public static class Builder extends org.jclouds.ec2.domain.RunningInstance.Builder<Builder> {
      private MonitoringState monitoringState;
      private String placementGroup;
      private Set<String> productCodes = Sets.newLinkedHashSet();
      private String subnetId;
      private String spotInstanceRequestId;
      private String vpcId;
      private Hypervisor hypervisor;
      private Map<String, String> securityGroupIdToNames = Maps.newLinkedHashMap();
      private String iamInstanceProfileArn;
      private String iamInstanceProfileId;

      public Builder securityGroupIdToNames(Map<String, String> securityGroupIdToNames) {
         this.securityGroupIdToNames = ImmutableMap.copyOf(checkNotNull(securityGroupIdToNames,
               "securityGroupIdToNames"));
         return this;
      }

      public Builder securityGroupIdToName(String key, String value) {
         if (key != null && value != null)
            this.securityGroupIdToNames.put(key, value);
         return this;
      }

      public Builder monitoringState(MonitoringState monitoringState) {
         this.monitoringState = monitoringState;
         return this;
      }

      public Builder placementGroup(String placementGroup) {
         this.placementGroup = placementGroup;
         return this;
      }

      public Builder productCodes(Iterable<String> productCodes) {
         this.productCodes = ImmutableSet.copyOf(checkNotNull(productCodes, "productCodes"));
         return this;
      }

      public Builder productCode(String productCode) {
         if (productCode != null)
            this.productCodes.add(productCode);
         return this;
      }

      public Builder subnetId(String subnetId) {
         this.subnetId = subnetId;
         return this;
      }

      public Builder spotInstanceRequestId(String spotInstanceRequestId) {
         this.spotInstanceRequestId = spotInstanceRequestId;
         return this;
      }

      public Builder vpcId(String vpcId) {
         this.vpcId = vpcId;
         return this;
      }
      
      public Builder hypervisor(Hypervisor hypervisor) {
         this.hypervisor = hypervisor;
         return this;
      }

      /**
       * @see AWSRunningInstance#getIAMInstanceProfile()
       */
      public Builder iamInstanceProfileArn(String iamInstanceProfileArn) {
         this.iamInstanceProfileArn = iamInstanceProfileArn;
         return this;
      }

      /**
       * @see AWSRunningInstance#getIAMInstanceProfile()
       */
      public Builder iamInstanceProfileId(String iamInstanceProfileId) {
         this.iamInstanceProfileId = iamInstanceProfileId;
         return this;
      }

      @Override
      public AWSRunningInstance build() {
         Optional<IAMInstanceProfile> iamInstanceProfile = Optional.absent();
         if (iamInstanceProfileArn != null && iamInstanceProfileId != null) {
            iamInstanceProfile = Optional.of(IAMInstanceProfile.forArnAndId(iamInstanceProfileArn,
                  iamInstanceProfileId));
         }
         return new AWSRunningInstance(region, securityGroupIdToNames, amiLaunchIndex, dnsName, imageId, instanceId,
               instanceState, rawState, instanceType, ipAddress, kernelId, keyName, launchTime, availabilityZone,
               virtualizationType, platform, privateDnsName, privateIpAddress, ramdiskId, reason, rootDeviceType,
               rootDeviceName, ebsBlockDevices, monitoringState, placementGroup, productCodes, subnetId,
               spotInstanceRequestId, vpcId, hypervisor, tags, iamInstanceProfile);
      }
      
      @Override
      public Builder fromRunningInstance(RunningInstance in) {
         super.fromRunningInstance(in);
         if (in instanceof AWSRunningInstance) {
            AWSRunningInstance awsIn = AWSRunningInstance.class.cast(in);
            monitoringState(awsIn.monitoringState).placementGroup(awsIn.placementGroup)
                  .productCodes(awsIn.productCodes).subnetId(awsIn.subnetId)
                  .spotInstanceRequestId(awsIn.spotInstanceRequestId).vpcId(awsIn.vpcId).hypervisor(awsIn.hypervisor)
                  .securityGroupIdToNames(awsIn.securityGroupIdToNames);
            if (awsIn.getIAMInstanceProfile().isPresent()) {
               iamInstanceProfileArn(awsIn.getIAMInstanceProfile().get().getArn());
               iamInstanceProfileId(awsIn.getIAMInstanceProfile().get().getId());
            }
         }
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

   }

   private final MonitoringState monitoringState;
   @Nullable
   private final String placementGroup;
   private final Set<String> productCodes;
   @Nullable
   private final String subnetId;
   @Nullable
   private final String spotInstanceRequestId;
   @Nullable
   private final String vpcId;
   private final Hypervisor hypervisor;
   private final Map<String, String> securityGroupIdToNames;
   private final Optional<IAMInstanceProfile> iamInstanceProfile;

   protected AWSRunningInstance(String region, Map<String, String> securityGroupIdToNames, String amiLaunchIndex,
            String dnsName, String imageId, String instanceId, InstanceState instanceState, String rawState,
            String instanceType, String ipAddress, String kernelId, String keyName, Date launchTime,
            String availabilityZone, String virtualizationType, String platform, String privateDnsName,
            String privateIpAddress, String ramdiskId, String reason, RootDeviceType rootDeviceType,
            String rootDeviceName, Map<String, BlockDevice> ebsBlockDevices, MonitoringState monitoringState,
            String placementGroup, Iterable<String> productCodes, String subnetId, String spotInstanceRequestId,
            String vpcId, Hypervisor hypervisor, Map<String, String> tags, Optional<IAMInstanceProfile> iamInstanceProfile) {
      super(region, securityGroupIdToNames.values(), amiLaunchIndex, dnsName, imageId, instanceId, instanceState,
               rawState, instanceType, ipAddress, kernelId, keyName, launchTime, availabilityZone, virtualizationType,
               platform, privateDnsName, privateIpAddress, ramdiskId, reason, rootDeviceType, rootDeviceName,
               ebsBlockDevices, tags);
      this.monitoringState = checkNotNull(monitoringState, "monitoringState");
      this.placementGroup = placementGroup;
      this.productCodes = ImmutableSet.copyOf(checkNotNull(productCodes, "productCodes"));
      this.subnetId = subnetId;
      this.spotInstanceRequestId = spotInstanceRequestId;
      this.vpcId = vpcId;
      this.hypervisor = checkNotNull(hypervisor, "hypervisor");
      this.securityGroupIdToNames = ImmutableMap.<String, String> copyOf(checkNotNull(securityGroupIdToNames,
            "securityGroupIdToNames"));
      this.iamInstanceProfile = checkNotNull(iamInstanceProfile, "iamInstanceProfile of %s", instanceId);
   }

   public Map<String, String> getSecurityGroupIdToNames() {
      return securityGroupIdToNames;
   }

   /**
    * State of monitoring for the instance.
    */
   public MonitoringState getMonitoringState() {
      return monitoringState;
   }

   /**
    * The name of the placement group the instance is in (for cluster compute
    * instances).
    */
   public String getPlacementGroup() {
      return placementGroup;
   }

   /**
    * Product codes attached to this instance.
    */
   public Set<String> getProductCodes() {
      return productCodes;
   }

   /**
    * The ID of the Spot Instance request
    */
   public String getSpotInstanceRequestId() {
      return spotInstanceRequestId;
   }

   /**
    * Specifies the VPC in which the instance is running (Amazon Virtual Private
    * Cloud).
    */
   public String getVpcId() {
      return vpcId;
   }
   
   /**
    * hypervisor of the VM
    * @see Hypervisor
    */
   public Hypervisor getHypervisor() {
      return hypervisor;
   }
   
   /**
    * Specifies the subnet ID in which the instance is running (Amazon Virtual
    * Private Cloud).
    */
   public String getSubnetId() {
      return subnetId;
   }

   /**
    * The IAM Instance Profile (IIP) associated with the instance.
    */
   @SinceApiVersion("2012-06-01")
   public Optional<IAMInstanceProfile> getIAMInstanceProfile() {
      return iamInstanceProfile;
   }

   @Override
   protected ToStringHelper string() {
      return super.string().add("monitoringState", monitoringState).add("placementGroup", placementGroup)
               .add("subnetId", subnetId).add("spotInstanceRequestId", spotInstanceRequestId).add("vpcId", vpcId)
               .add("hypervisor", hypervisor).add("iamInstanceProfile", iamInstanceProfile.orNull());
   }

   public static class IAMInstanceProfile {
      public static IAMInstanceProfile forArnAndId(String arn, String id) {
         return new IAMInstanceProfile(arn, id);
      }

      private final String arn;
      private final String id;

      private IAMInstanceProfile(String arn, String id) {
         this.arn = checkNotNull(arn, "arn");
         this.id = checkNotNull(id, "id for %s", arn);
      }

      /**
       * The Amazon resource name (ARN) of the IAM Instance Profile (IIP) to associate with the instance.
       */
      public String getArn() {
         return arn;
      }

      /**
       * The ID of the IAM Instance Profile ID (IIP) associated with the instance.
       */
      public String getId() {
         return id;
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(arn, id);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         IAMInstanceProfile that = IAMInstanceProfile.class.cast(obj);
         return equal(this.arn, that.arn) && equal(this.id, that.id);
      }

      @Override
      public String toString() {
         return toStringHelper("").add("arn", arn).add("id", id).toString();
      }
   }
}
