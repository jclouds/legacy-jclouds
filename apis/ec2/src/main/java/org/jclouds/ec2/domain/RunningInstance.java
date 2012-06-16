/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-RunningInstancesItemType.html"
 *      />
 * @author Adrian Cole
 */
public class RunningInstance implements Comparable<RunningInstance> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      protected String region;
      protected Set<String> groupIds = Sets.newLinkedHashSet();
      protected String amiLaunchIndex;
      protected String dnsName;
      protected String imageId;
      protected String instanceId;
      protected InstanceState instanceState;
      protected String rawState;
      protected String instanceType;
      protected String ipAddress;
      protected String kernelId;
      protected String keyName;
      protected Date launchTime;
      protected String availabilityZone;
      protected String virtualizationType = "paravirtual";
      protected String platform;
      protected String privateDnsName;
      protected String privateIpAddress;
      protected String ramdiskId;
      protected String reason;
      protected RootDeviceType rootDeviceType = RootDeviceType.INSTANCE_STORE;
      protected String rootDeviceName;
      protected Map<String, BlockDevice> ebsBlockDevices = Maps.newLinkedHashMap();

      public Builder region(String region) {
         this.region = region;
         return this;
      }

      public Builder groupIds(Iterable<String> groupIds) {
         this.groupIds = ImmutableSet.copyOf(checkNotNull(groupIds, "groupIds"));
         return this;
      }

      public Builder groupId(String groupId) {
         if (groupId != null)
            this.groupIds.add(groupId);
         return this;
      }

      public Builder amiLaunchIndex(String amiLaunchIndex) {
         this.amiLaunchIndex = amiLaunchIndex;
         return this;
      }

      public Builder dnsName(String dnsName) {
         this.dnsName = dnsName;
         return this;
      }

      public Builder imageId(String imageId) {
         this.imageId = imageId;
         return this;
      }

      public Builder instanceId(String instanceId) {
         this.instanceId = instanceId;
         return this;
      }

      public Builder instanceState(InstanceState instanceState) {
         this.instanceState = instanceState;
         return this;
      }
      
      public Builder rawState(String rawState) {
         this.rawState = rawState;
         return this;
      }
      
      public Builder instanceType(String instanceType) {
         this.instanceType = instanceType;
         return this;
      }

      public Builder ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return this;
      }

      public Builder kernelId(String kernelId) {
         this.kernelId = kernelId;
         return this;
      }

      public Builder keyName(String keyName) {
         this.keyName = keyName;
         return this;
      }

      public Builder launchTime(Date launchTime) {
         this.launchTime = launchTime;
         return this;
      }

      public Builder availabilityZone(String availabilityZone) {
         this.availabilityZone = availabilityZone;
         return this;
      }

      public Builder virtualizationType(String virtualizationType) {
         this.virtualizationType = virtualizationType;
         return this;
      }

      public Builder platform(String platform) {
         this.platform = platform;
         return this;
      }

      public Builder privateDnsName(String privateDnsName) {
         this.privateDnsName = privateDnsName;
         return this;
      }

      public Builder privateIpAddress(String privateIpAddress) {
         this.privateIpAddress = privateIpAddress;
         return this;
      }

      public Builder ramdiskId(String ramdiskId) {
         this.ramdiskId = ramdiskId;
         return this;
      }

      public Builder reason(String reason) {
         this.reason = reason;
         return this;
      }

      public Builder rootDeviceType(RootDeviceType rootDeviceType) {
         this.rootDeviceType = rootDeviceType;
         return this;
      }

      public Builder rootDeviceName(String rootDeviceName) {
         this.rootDeviceName = rootDeviceName;
         return this;
      }

      public Builder devices(Map<String, BlockDevice> ebsBlockDevices) {
         this.ebsBlockDevices = ImmutableMap.copyOf(checkNotNull(ebsBlockDevices, "ebsBlockDevices"));
         return this;
      }

      public Builder device(String key, BlockDevice value) {
         if (key != null && value != null)
            this.ebsBlockDevices.put(key, value);
         return this;
      }

      public RunningInstance build() {
         return new RunningInstance(region, groupIds, amiLaunchIndex, dnsName, imageId, instanceId, instanceState,
                  rawState, instanceType, ipAddress, kernelId, keyName, launchTime, availabilityZone,
                  virtualizationType, platform, privateDnsName, privateIpAddress, ramdiskId, reason, rootDeviceType,
                  rootDeviceName, ebsBlockDevices);
      }

      public String getDnsName() {
         return dnsName;
      }

      public String getIpAddress() {
         return ipAddress;
      }

      public String getPrivateDnsName() {
         return privateDnsName;
      }

      public String getPrivateIpAddress() {
         return privateIpAddress;
      }

   }

   protected final String region;
   protected final Set<String> groupIds;
   protected final String amiLaunchIndex;
   @Nullable
   protected final String dnsName;
   protected final String imageId;
   protected final String instanceId;
   protected final InstanceState instanceState;
   protected final String rawState;
   protected final String instanceType;
   @Nullable
   protected final String ipAddress;
   @Nullable
   protected final String kernelId;
   @Nullable
   protected final String keyName;
   protected final Date launchTime;
   protected final String availabilityZone;
   protected final String virtualizationType;
   @Nullable
   protected final String platform;
   @Nullable
   protected final String privateDnsName;
   @Nullable
   protected final String privateIpAddress;
   @Nullable
   protected final String ramdiskId;
   @Nullable
   protected final String reason;
   protected final RootDeviceType rootDeviceType;
   @Nullable
   protected final String rootDeviceName;
   protected final Map<String, BlockDevice> ebsBlockDevices;

   public int compareTo(RunningInstance o) {
      return (this == o) ? 0 : getId().compareTo(o.getId());
   }

   protected RunningInstance(String region, Iterable<String> groupIds, @Nullable String amiLaunchIndex,
            @Nullable String dnsName, String imageId, String instanceId, InstanceState instanceState, String rawState,
            String instanceType, @Nullable String ipAddress, @Nullable String kernelId, @Nullable String keyName,
            Date launchTime, String availabilityZone, String virtualizationType, @Nullable String platform,
            @Nullable String privateDnsName, @Nullable String privateIpAddress, @Nullable String ramdiskId,
            @Nullable String reason, RootDeviceType rootDeviceType, @Nullable String rootDeviceName,
            Map<String, BlockDevice> ebsBlockDevices) {
      this.region = checkNotNull(region, "region");
      this.amiLaunchIndex = amiLaunchIndex; // nullable on runinstances.
      this.dnsName = dnsName; // nullable on runinstances.
      this.imageId = imageId; // nullable on runinstances.
      this.instanceId = checkNotNull(instanceId, "instanceId");
      this.instanceState = checkNotNull(instanceState, "instanceState for %s/%s", region, instanceId);
      this.rawState = checkNotNull(rawState, "rawState for %s/%s", region, instanceId);
      this.instanceType = checkNotNull(instanceType, "instanceType for %s/%s", region, instanceId);
      this.ipAddress = ipAddress;
      this.kernelId = kernelId;
      this.keyName = keyName;
      this.launchTime = launchTime;// nullable on spot.
      this.availabilityZone = availabilityZone;// nullable on spot.
      this.virtualizationType = virtualizationType;
      this.platform = platform;
      this.privateDnsName = privateDnsName;// nullable on runinstances.
      this.privateIpAddress = privateIpAddress;// nullable on runinstances.
      this.ramdiskId = ramdiskId;
      this.reason = reason;
      this.rootDeviceType = checkNotNull(rootDeviceType, "rootDeviceType for %s/%s", region, instanceId);
      this.rootDeviceName = rootDeviceName;
      this.ebsBlockDevices = ImmutableMap.copyOf(checkNotNull(ebsBlockDevices, "ebsBlockDevices for %s/%s", region, instanceId));
      this.groupIds = ImmutableSet.copyOf(checkNotNull(groupIds, "groupIds for %s/%s", region, instanceId));
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
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/2010-06-15/DeveloperGuide/" />
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
    * The current state of the instance, as returned literally from the input XML
    */
   public String getRawState() {
      return rawState;
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
    * The location where the instance launched.
    */
   public String getAvailabilityZone() {
      return availabilityZone;
   }

   /**
    * Specifies the instance's virtualization type. Valid values are paravirtual or hvm.
    */
   public String getVirtualizationType() {
      return virtualizationType;
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

   public RootDeviceType getRootDeviceType() {
      return rootDeviceType;
   }

   public String getRootDeviceName() {
      return rootDeviceName;
   }

   /**
    * EBS volumes associated with the instance.
    */
   public Map<String, BlockDevice> getEbsBlockDevices() {
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
      result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());
      result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
      result = prime * result + ((kernelId == null) ? 0 : kernelId.hashCode());
      result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
      result = prime * result + ((launchTime == null) ? 0 : launchTime.hashCode());
      result = prime * result + ((platform == null) ? 0 : platform.hashCode());
      result = prime * result + ((privateDnsName == null) ? 0 : privateDnsName.hashCode());
      result = prime * result + ((privateIpAddress == null) ? 0 : privateIpAddress.hashCode());
      result = prime * result + ((ramdiskId == null) ? 0 : ramdiskId.hashCode());
      result = prime * result + ((region == null) ? 0 : region.hashCode());
      result = prime * result + ((rootDeviceName == null) ? 0 : rootDeviceName.hashCode());
      result = prime * result + ((rootDeviceType == null) ? 0 : rootDeviceType.hashCode());
      result = prime * result + ((virtualizationType == null) ? 0 : virtualizationType.hashCode());
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
      if (ramdiskId == null) {
         if (other.ramdiskId != null)
            return false;
      } else if (!ramdiskId.equals(other.ramdiskId))
         return false;
      if (region == null) {
         if (other.region != null)
            return false;
      } else if (!region.equals(other.region))
         return false;
      if (rootDeviceName == null) {
         if (other.rootDeviceName != null)
            return false;
      } else if (!rootDeviceName.equals(other.rootDeviceName))
         return false;
      if (rootDeviceType == null) {
         if (other.rootDeviceType != null)
            return false;
      } else if (!rootDeviceType.equals(other.rootDeviceType))
         return false;
      if (virtualizationType == null) {
         if (other.virtualizationType != null)
            return false;
      } else if (!virtualizationType.equals(other.virtualizationType))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[region=" + region + ", availabilityZone=" + availabilityZone + ", instanceId=" + instanceId
               + ", instanceState=" + rawState + ", instanceType=" + instanceType + ", virtualizationType="
               + virtualizationType + ", imageId=" + imageId + ", ipAddress=" + ipAddress + ", dnsName=" + dnsName
               + ", privateIpAddress=" + privateIpAddress + ", privateDnsName=" + privateDnsName + ", keyName="
               + keyName + ", groupIds=" + groupIds  + ", platform=" + platform + ", launchTime=" + launchTime + ", rootDeviceName="
               + rootDeviceName + ", rootDeviceType=" + rootDeviceType + ", ebsBlockDevices=" + ebsBlockDevices + "]";
   }

}
