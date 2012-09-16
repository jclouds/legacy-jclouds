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

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
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
      protected Set<String> groupNames = Sets.newLinkedHashSet();
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

      public Builder groupNames(Iterable<String> groupNames) {
         this.groupNames = ImmutableSet.copyOf(checkNotNull(groupNames, "groupNames"));
         return this;
      }

      public Builder groupName(String groupName) {
         if (groupName != null)
            this.groupNames.add(groupName);
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
         return new RunningInstance(region, groupNames, amiLaunchIndex, dnsName, imageId, instanceId, instanceState,
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
   protected final Set<String> groupNames;
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

   protected RunningInstance(String region, Iterable<String> groupNames, @Nullable String amiLaunchIndex,
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
      this.groupNames = ImmutableSet.copyOf(checkNotNull(groupNames, "groupNames for %s/%s", region, instanceId));
   }

   /**
    * To be removed in jclouds 1.6 <h4>Warning</h4>
    * 
    * Especially on EC2 clones that may not support regions, this value is fragile. Consider
    * alternate means to determine context.
    */
   @Deprecated
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
    * @see #getGroupNames()
    */
   @Deprecated
   public Set<String> getGroupIds() {
      return getGroupNames();
   }
   
   /**
    * Names of the security groups.
    */
   public Set<String> getGroupNames() {
      return groupNames;
   }

   @Override
   public int compareTo(RunningInstance other) {
      return ComparisonChain.start().compare(region, other.region).compare(instanceId, other.instanceId, Ordering.natural().nullsLast()).result();
   }
   
   @Override
   public int hashCode() {
      return Objects.hashCode(region, instanceId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      RunningInstance that = RunningInstance.class.cast(obj);
      return Objects.equal(this.region, that.region) && Objects.equal(this.instanceId, that.instanceId);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("region", region)
               .add("availabilityZone", availabilityZone).add("id", instanceId).add("state", rawState)
               .add("type", instanceType).add("virtualizationType", virtualizationType).add("imageId", imageId)
               .add("ipAddress", ipAddress).add("dnsName", dnsName).add("privateIpAddress", privateIpAddress)
               .add("privateDnsName", privateDnsName).add("keyName", keyName).add("groupNames", groupNames)
               .add("platform", platform).add("launchTime", launchTime).add("rootDeviceName", rootDeviceName)
               .add("rootDeviceType", rootDeviceType).add("ebsBlockDevices", ebsBlockDevices);
   }

   @Override
   public String toString() {
      return string().toString();
   }


}
