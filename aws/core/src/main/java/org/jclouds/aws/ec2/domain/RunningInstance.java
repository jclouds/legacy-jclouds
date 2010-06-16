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
package org.jclouds.aws.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jclouds.aws.ec2.domain.Attachment.Status;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.internal.Nullable;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-RunningInstancesItemType.html"
 *      />
 * @author Adrian Cole
 */
public class RunningInstance implements Comparable<RunningInstance> {

   public static class EbsBlockDevice {
      private final String volumeId;
      private final Attachment.Status attachmentStatus;
      private final Date attachTime;
      private final boolean deleteOnTermination;

      public EbsBlockDevice(String volumeId, Status attachmentStatus, Date attachTime,
               boolean deleteOnTermination) {
         super();
         this.volumeId = volumeId;
         this.attachmentStatus = attachmentStatus;
         this.attachTime = attachTime;
         this.deleteOnTermination = deleteOnTermination;
      }

      public EbsBlockDevice(String volumeId, boolean deleteOnTermination) {
         this(volumeId, null, null, deleteOnTermination);
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((attachTime == null) ? 0 : attachTime.hashCode());
         result = prime * result + ((attachmentStatus == null) ? 0 : attachmentStatus.hashCode());
         result = prime * result + (deleteOnTermination ? 1231 : 1237);
         result = prime * result + ((volumeId == null) ? 0 : volumeId.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         EbsBlockDevice other = (EbsBlockDevice) obj;
         if (attachTime == null) {
            if (other.attachTime != null)
               return false;
         } else if (!attachTime.equals(other.attachTime))
            return false;
         if (attachmentStatus == null) {
            if (other.attachmentStatus != null)
               return false;
         } else if (!attachmentStatus.equals(other.attachmentStatus))
            return false;
         if (deleteOnTermination != other.deleteOnTermination)
            return false;
         if (volumeId == null) {
            if (other.volumeId != null)
               return false;
         } else if (!volumeId.equals(other.volumeId))
            return false;
         return true;
      }

      public String getVolumeId() {
         return volumeId;
      }

      public Attachment.Status getAttachmentStatus() {
         return attachmentStatus;
      }

      public Date getAttachTime() {
         return attachTime;
      }

      public boolean isDeleteOnTermination() {
         return deleteOnTermination;
      }

   }

   private final String region;
   private final Set<String> groupIds = Sets.newLinkedHashSet();

   private final String amiLaunchIndex;
   @Nullable
   private final String dnsName;
   private final String imageId;
   private final String instanceId;
   private final InstanceState instanceState;
   private final String instanceType;
   @Nullable
   private final String ipAddress;
   @Nullable
   private final String kernelId;
   @Nullable
   private final String keyName;
   private final Date launchTime;
   private final boolean monitoring;
   private final String availabilityZone;
   @Nullable
   private final String platform;
   @Nullable
   private final String privateDnsName;
   @Nullable
   private final String privateIpAddress;
   private final Set<String> productCodes = Sets.newLinkedHashSet();
   @Nullable
   private final String ramdiskId;
   @Nullable
   private final String reason;
   @Nullable
   private final String subnetId;
   @Nullable
   private final String vpcId;
   private final RootDeviceType rootDeviceType;
   @Nullable
   private final String rootDeviceName;
   private final Map<String, EbsBlockDevice> ebsBlockDevices = Maps.newHashMap();

   public int compareTo(RunningInstance o) {
      return (this == o) ? 0 : getId().compareTo(o.getId());
   }

   public RunningInstance(String region, Iterable<String> groupIds,
            @Nullable String amiLaunchIndex, @Nullable String dnsName, String imageId,
            String instanceId, InstanceState instanceState, String instanceType,
            @Nullable String ipAddress, @Nullable String kernelId, @Nullable String keyName,
            Date launchTime, boolean monitoring, String availabilityZone,
            @Nullable String platform, @Nullable String privateDnsName,
            @Nullable String privateIpAddress, Set<String> productCodes,
            @Nullable String ramdiskId, @Nullable String reason, @Nullable String subnetId,
            @Nullable String vpcId, RootDeviceType rootDeviceType, @Nullable String rootDeviceName,
            Map<String, EbsBlockDevice> ebsBlockDevices) {
      Iterables.addAll(this.groupIds, checkNotNull(groupIds, "groupIds"));
      this.region = checkNotNull(region, "region");
      this.amiLaunchIndex = amiLaunchIndex; // nullable on runinstances.
      this.dnsName = dnsName; // nullable on runinstances.
      this.imageId = imageId; // nullable on runinstances.
      this.instanceId = checkNotNull(instanceId, "instanceId");
      this.instanceState = checkNotNull(instanceState, "instanceState");
      this.instanceType = checkNotNull(instanceType, "instanceType");
      this.ipAddress = ipAddress;
      this.kernelId = kernelId;
      this.keyName = keyName;
      this.launchTime = checkNotNull(launchTime, "launchTime");
      this.monitoring = checkNotNull(monitoring, "monitoring");
      this.availabilityZone = checkNotNull(availabilityZone, "availabilityZone");
      this.platform = platform;
      this.privateDnsName = privateDnsName; // nullable on runinstances.
      this.privateIpAddress = privateIpAddress;
      Iterables.addAll(this.productCodes, checkNotNull(productCodes, "productCodes"));
      this.ramdiskId = ramdiskId;
      this.reason = reason;
      this.subnetId = subnetId;
      this.vpcId = vpcId;
      this.rootDeviceType = checkNotNull(rootDeviceType, "rootDeviceType");
      this.rootDeviceName = rootDeviceName;
      this.getEbsBlockDevices().putAll(checkNotNull(ebsBlockDevices, "ebsBlockDevices"));
   }

   /**
    * Instance Ids are scoped to the region.
    */
   public String getRegion() {
      return region;
   }

   /**
    * The AMI launch index, which can be used to find this instance within the launch group. For
    * more information, go to the Metadata section of the Amazon Elastic Compute Cloud Developer
    * Guide.
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/2009-11-30/DeveloperGuide/" />
    */
   public String getAmiLaunchIndex() {
      return amiLaunchIndex;
   }

   /**
    * The public DNS name assigned to the instance. This DNS name is contactable from outside the
    * Amazon EC2 network. This element remains empty until the instance enters a running state.
    */
   public String getDnsName() {
      return dnsName;
   }

   /**
    * Image ID of the AMI used to launch the instance.
    */
   public String getImageId() {
      return imageId;
   }

   /**
    * Unique ID of the instance launched.
    */
   public String getId() {
      return instanceId;
   }

   /**
    * The current state of the instance.
    */
   public InstanceState getInstanceState() {
      return instanceState;
   }

   /**
    * The instance type.
    */
   public String getInstanceType() {
      return instanceType;
   }

   /**
    * Specifies the IP address of the instance.
    */
   public String getIpAddress() {
      return ipAddress;
   }

   /**
    * Optional. Kernel associated with this instance.
    */
   public String getKernelId() {
      return kernelId;
   }

   /**
    * If this instance was launched with an associated key pair, this displays the key pair name.
    */
   public String getKeyName() {
      return keyName;
   }

   /**
    * The time the instance launched.
    */
   public Date getLaunchTime() {
      return launchTime;
   }

   /**
    * Specifies whether monitoring is enabled for the instance.
    */
   public boolean isMonitoring() {
      return monitoring;
   }

   /**
    * The location where the instance launched.
    */
   public String getAvailabilityZone() {
      return availabilityZone;
   }

   /**
    * Platform of the instance (e.g., Windows).
    */
   public String getPlatform() {
      return platform;
   }

   /**
    * The private DNS name assigned to the instance. This DNS name can only be used inside the
    * Amazon EC2 network. This element remains empty until the instance enters a running state.
    */
   public String getPrivateDnsName() {
      return privateDnsName;
   }

   /**
    * Specifies the private IP address that is assigned to the instance (Amazon VPC).
    */
   public String getPrivateIpAddress() {
      return privateIpAddress;
   }

   /**
    * Product codes attached to this instance.
    */
   public Set<String> getProductCodes() {
      return productCodes;
   }

   /**
    * Optional. RAM disk associated with this instance.
    */
   public String getRamdiskId() {
      return ramdiskId;
   }

   /**
    * Reason for the most recent state transition. This might be an empty string.
    */
   public String getReason() {
      return reason;
   }

   /**
    * Specifies the subnet ID in which the instance is running (Amazon Virtual Private Cloud).
    */
   public String getSubnetId() {
      return subnetId;
   }

   /**
    * Specifies the VPC in which the instance is running (Amazon Virtual Private Cloud).
    */
   public String getVpcId() {
      return vpcId;
   }

   public RootDeviceType getRootDeviceType() {
      return rootDeviceType;
   }

   public String getRootDeviceName() {
      return rootDeviceName;
   }

   /**
    * EBS volumes associated with the instance.
    */
   public Map<String, EbsBlockDevice> getEbsBlockDevices() {
      return ebsBlockDevices;
   }

   /**
    * Names of the security groups.
    */
   public Set<String> getGroupIds() {
      return groupIds;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((amiLaunchIndex == null) ? 0 : amiLaunchIndex.hashCode());
      result = prime * result + ((availabilityZone == null) ? 0 : availabilityZone.hashCode());
      result = prime * result + ((dnsName == null) ? 0 : dnsName.hashCode());
      result = prime * result + ((ebsBlockDevices == null) ? 0 : ebsBlockDevices.hashCode());
      result = prime * result + ((groupIds == null) ? 0 : groupIds.hashCode());
      result = prime * result + ((imageId == null) ? 0 : imageId.hashCode());
      result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
      result = prime * result + ((instanceState == null) ? 0 : instanceState.hashCode());
      result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());
      result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
      result = prime * result + ((kernelId == null) ? 0 : kernelId.hashCode());
      result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
      result = prime * result + ((launchTime == null) ? 0 : launchTime.hashCode());
      result = prime * result + (monitoring ? 1231 : 1237);
      result = prime * result + ((platform == null) ? 0 : platform.hashCode());
      result = prime * result + ((privateDnsName == null) ? 0 : privateDnsName.hashCode());
      result = prime * result + ((privateIpAddress == null) ? 0 : privateIpAddress.hashCode());
      result = prime * result + ((productCodes == null) ? 0 : productCodes.hashCode());
      result = prime * result + ((ramdiskId == null) ? 0 : ramdiskId.hashCode());
      result = prime * result + ((reason == null) ? 0 : reason.hashCode());
      result = prime * result + ((region == null) ? 0 : region.hashCode());
      result = prime * result + ((subnetId == null) ? 0 : subnetId.hashCode());
      result = prime * result + ((vpcId == null) ? 0 : vpcId.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      RunningInstance other = (RunningInstance) obj;
      if (amiLaunchIndex == null) {
         if (other.amiLaunchIndex != null)
            return false;
      } else if (!amiLaunchIndex.equals(other.amiLaunchIndex))
         return false;
      if (availabilityZone == null) {
         if (other.availabilityZone != null)
            return false;
      } else if (!availabilityZone.equals(other.availabilityZone))
         return false;
      if (dnsName == null) {
         if (other.dnsName != null)
            return false;
      } else if (!dnsName.equals(other.dnsName))
         return false;
      if (ebsBlockDevices == null) {
         if (other.ebsBlockDevices != null)
            return false;
      } else if (!ebsBlockDevices.equals(other.ebsBlockDevices))
         return false;
      if (groupIds == null) {
         if (other.groupIds != null)
            return false;
      } else if (!groupIds.equals(other.groupIds))
         return false;
      if (imageId == null) {
         if (other.imageId != null)
            return false;
      } else if (!imageId.equals(other.imageId))
         return false;
      if (instanceId == null) {
         if (other.instanceId != null)
            return false;
      } else if (!instanceId.equals(other.instanceId))
         return false;
      if (instanceState == null) {
         if (other.instanceState != null)
            return false;
      } else if (!instanceState.equals(other.instanceState))
         return false;
      if (instanceType == null) {
         if (other.instanceType != null)
            return false;
      } else if (!instanceType.equals(other.instanceType))
         return false;
      if (ipAddress == null) {
         if (other.ipAddress != null)
            return false;
      } else if (!ipAddress.equals(other.ipAddress))
         return false;
      if (kernelId == null) {
         if (other.kernelId != null)
            return false;
      } else if (!kernelId.equals(other.kernelId))
         return false;
      if (keyName == null) {
         if (other.keyName != null)
            return false;
      } else if (!keyName.equals(other.keyName))
         return false;
      if (launchTime == null) {
         if (other.launchTime != null)
            return false;
      } else if (!launchTime.equals(other.launchTime))
         return false;
      if (monitoring != other.monitoring)
         return false;
      if (platform == null) {
         if (other.platform != null)
            return false;
      } else if (!platform.equals(other.platform))
         return false;
      if (privateDnsName == null) {
         if (other.privateDnsName != null)
            return false;
      } else if (!privateDnsName.equals(other.privateDnsName))
         return false;
      if (privateIpAddress == null) {
         if (other.privateIpAddress != null)
            return false;
      } else if (!privateIpAddress.equals(other.privateIpAddress))
         return false;
      if (productCodes == null) {
         if (other.productCodes != null)
            return false;
      } else if (!productCodes.equals(other.productCodes))
         return false;
      if (ramdiskId == null) {
         if (other.ramdiskId != null)
            return false;
      } else if (!ramdiskId.equals(other.ramdiskId))
         return false;
      if (reason == null) {
         if (other.reason != null)
            return false;
      } else if (!reason.equals(other.reason))
         return false;
      if (region == null) {
         if (other.region != null)
            return false;
      } else if (!region.equals(other.region))
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
      return "RunningInstance [amiLaunchIndex=" + amiLaunchIndex + ", availabilityZone="
               + availabilityZone + ", dnsName=" + dnsName + ", ebsBlockDevices=" + ebsBlockDevices
               + ", groupIds=" + groupIds + ", imageId=" + imageId + ", instanceId=" + instanceId
               + ", instanceState=" + instanceState + ", instanceType=" + instanceType
               + ", ipAddress=" + ipAddress + ", kernelId=" + kernelId + ", keyName=" + keyName
               + ", launchTime=" + launchTime + ", monitoring=" + monitoring + ", platform="
               + platform + ", privateDnsName=" + privateDnsName + ", privateIpAddress="
               + privateIpAddress + ", productCodes=" + productCodes + ", ramdiskId=" + ramdiskId
               + ", reason=" + reason + ", region=" + region + ", rootDeviceName=" + rootDeviceName
               + ", rootDeviceType=" + rootDeviceType + ", subnetId=" + subnetId + ", vpcId="
               + vpcId + "]";
   }

}