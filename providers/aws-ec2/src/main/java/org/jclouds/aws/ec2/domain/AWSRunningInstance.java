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

package org.jclouds.aws.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.RunningInstance;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-RunningInstancesItemType.html"
 *      />
 * @author Adrian Cole
 */
public class AWSRunningInstance extends RunningInstance {

   public static class Builder extends org.jclouds.ec2.domain.RunningInstance.Builder {
      private MonitoringState monitoringState;
      private String placementGroup;
      private Set<String> productCodes = Sets.newLinkedHashSet();
      private String subnetId;
      private String spotInstanceRequestId;
      private String vpcId;

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

      @Override
      public Builder amiLaunchIndex(String amiLaunchIndex) {
         return Builder.class.cast(super.amiLaunchIndex(amiLaunchIndex));
      }

      @Override
      public Builder availabilityZone(String availabilityZone) {
         return Builder.class.cast(super.availabilityZone(availabilityZone));
      }

      @Override
      public Builder devices(Map<String, BlockDevice> ebsBlockDevices) {
         return Builder.class.cast(super.devices(ebsBlockDevices));
      }

      @Override
      public Builder dnsName(String dnsName) {
         return Builder.class.cast(super.dnsName(dnsName));
      }

      @Override
      public Builder imageId(String imageId) {
         return Builder.class.cast(super.imageId(imageId));
      }

      @Override
      public Builder instanceId(String instanceId) {
         return Builder.class.cast(super.instanceId(instanceId));
      }

      @Override
      public Builder instanceState(InstanceState instanceState) {
         return Builder.class.cast(super.instanceState(instanceState));
      }

      @Override
      public Builder instanceType(String instanceType) {
         return Builder.class.cast(super.instanceType(instanceType));
      }

      @Override
      public Builder ipAddress(String ipAddress) {
         return Builder.class.cast(super.ipAddress(ipAddress));
      }

      @Override
      public Builder kernelId(String kernelId) {
         return Builder.class.cast(super.kernelId(kernelId));
      }

      @Override
      public Builder keyName(String keyName) {
         return Builder.class.cast(super.keyName(keyName));
      }

      @Override
      public Builder launchTime(Date launchTime) {
         return Builder.class.cast(super.launchTime(launchTime));
      }

      @Override
      public Builder platform(String platform) {
         return Builder.class.cast(super.platform(platform));
      }

      @Override
      public Builder privateDnsName(String privateDnsName) {
         return Builder.class.cast(super.privateDnsName(privateDnsName));
      }

      @Override
      public Builder privateIpAddress(String privateIpAddress) {
         return Builder.class.cast(super.privateIpAddress(privateIpAddress));
      }

      @Override
      public Builder ramdiskId(String ramdiskId) {
         return Builder.class.cast(super.ramdiskId(ramdiskId));
      }

      @Override
      public Builder reason(String reason) {
         return Builder.class.cast(super.reason(reason));
      }

      @Override
      public Builder region(String region) {
         return Builder.class.cast(super.region(region));
      }

      @Override
      public Builder rootDeviceName(String rootDeviceName) {
         return Builder.class.cast(super.rootDeviceName(rootDeviceName));
      }

      @Override
      public Builder rootDeviceType(RootDeviceType rootDeviceType) {
         return Builder.class.cast(super.rootDeviceType(rootDeviceType));
      }

      @Override
      public Builder virtualizationType(String virtualizationType) {
         return Builder.class.cast(super.virtualizationType(virtualizationType));
      }

      @Override
      public Builder device(String key, BlockDevice value) {
         return Builder.class.cast(super.device(key, value));
      }

      @Override
      public Builder groupId(String groupId) {
         return Builder.class.cast(super.groupId(groupId));
      }

      @Override
      public Builder groupIds(Iterable<String> groupIds) {
         return Builder.class.cast(super.groupIds(groupIds));
      }

      @Override
      public AWSRunningInstance build() {
         return new AWSRunningInstance(region, groupIds, amiLaunchIndex, dnsName, imageId, instanceId, instanceState,
                  instanceType, ipAddress, kernelId, keyName, launchTime, availabilityZone, virtualizationType,
                  platform, privateDnsName, privateIpAddress, ramdiskId, reason, rootDeviceType, rootDeviceName,
                  ebsBlockDevices, monitoringState, placementGroup, productCodes, subnetId, spotInstanceRequestId,
                  vpcId);
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

   protected AWSRunningInstance(String region, Iterable<String> groupIds, String amiLaunchIndex, String dnsName,
            String imageId, String instanceId, InstanceState instanceState, String instanceType, String ipAddress,
            String kernelId, String keyName, Date launchTime, String availabilityZone, String virtualizationType,
            String platform, String privateDnsName, String privateIpAddress, String ramdiskId, String reason,
            RootDeviceType rootDeviceType, String rootDeviceName, Map<String, BlockDevice> ebsBlockDevices,
            MonitoringState monitoringState, String placementGroup, Iterable<String> productCodes, String subnetId,
            String spotInstanceRequestId, String vpcId) {
      super(region, groupIds, amiLaunchIndex, dnsName, imageId, instanceId, instanceState, instanceType, ipAddress,
               kernelId, keyName, launchTime, availabilityZone, virtualizationType, platform, privateDnsName,
               privateIpAddress, ramdiskId, reason, rootDeviceType, rootDeviceName, ebsBlockDevices);
      this.monitoringState = checkNotNull(monitoringState, "monitoringState");
      this.placementGroup = placementGroup;
      this.productCodes = ImmutableSet.copyOf(checkNotNull(groupIds, "groupIds"));
      this.subnetId = subnetId;
      this.spotInstanceRequestId = spotInstanceRequestId;
      this.vpcId = vpcId;
   }

   /**
    * State of monitoring for the instance.
    */
   public MonitoringState getMonitoringState() {
      return monitoringState;
   }

   /**
    * The name of the placement group the instance is in (for cluster compute instances).
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
    * Specifies the VPC in which the instance is running (Amazon Virtual Private Cloud).
    */
   public String getVpcId() {
      return vpcId;
   }

   /**
    * Specifies the subnet ID in which the instance is running (Amazon Virtual Private Cloud).
    */
   public String getSubnetId() {
      return subnetId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((placementGroup == null) ? 0 : placementGroup.hashCode());
      result = prime * result + ((productCodes == null) ? 0 : productCodes.hashCode());
      result = prime * result + ((spotInstanceRequestId == null) ? 0 : spotInstanceRequestId.hashCode());
      result = prime * result + ((subnetId == null) ? 0 : subnetId.hashCode());
      result = prime * result + ((vpcId == null) ? 0 : vpcId.hashCode());
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
      AWSRunningInstance other = (AWSRunningInstance) obj;
      if (placementGroup == null) {
         if (other.placementGroup != null)
            return false;
      } else if (!placementGroup.equals(other.placementGroup))
         return false;
      if (productCodes == null) {
         if (other.productCodes != null)
            return false;
      } else if (!productCodes.equals(other.productCodes))
         return false;
      if (spotInstanceRequestId == null) {
         if (other.spotInstanceRequestId != null)
            return false;
      } else if (!spotInstanceRequestId.equals(other.spotInstanceRequestId))
         return false;
      if (subnetId == null) {
         if (other.subnetId != null)
            return false;
      } else if (!subnetId.equals(other.subnetId))
         return false;
      if (vpcId == null) {
         if (other.vpcId != null)
            return false;
      } else if (!vpcId.equals(other.vpcId))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[region=" + region + ", availabilityZone=" + availabilityZone + ", instanceId=" + instanceId
               + ", instanceState=" + instanceState + ", instanceType=" + instanceType + ", virtualizationType="
               + virtualizationType + ", imageId=" + imageId + ", ipAddress=" + ipAddress + ", dnsName=" + dnsName
               + ", privateIpAddress=" + privateIpAddress + ", privateDnsName=" + privateDnsName + ", keyName="
               + keyName + ", platform=" + platform + ", launchTime=" + launchTime + ", rootDeviceName="
               + rootDeviceName + ", rootDeviceType=" + rootDeviceType + ", ebsBlockDevices=" + ebsBlockDevices
               + ", monitoringState=" + monitoringState + ", placementGroup=" + placementGroup + ", productCodes="
               + productCodes + ", spotInstanceRequestId=" + spotInstanceRequestId + ", subnetId=" + subnetId
               + ", vpcId=" + vpcId + "]";
   }

}
