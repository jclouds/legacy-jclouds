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
package org.jclouds.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Strings;
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

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromRunningInstance(this);
   }
   
   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

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
      protected Map<String, String> tags = Maps.newLinkedHashMap();

      public T tags(Map<String, String> tags) {
         this.tags = ImmutableMap.copyOf(checkNotNull(tags, "tags"));
         return self();
      }

      public T tag(String key, String value) {
         if (key != null)
            this.tags.put(key, Strings.nullToEmpty(value));
         return self();
      }
      
      public T region(String region) {
         this.region = region;
         return self();
      }

      public T groupNames(Iterable<String> groupNames) {
         this.groupNames = ImmutableSet.copyOf(checkNotNull(groupNames, "groupNames"));
         return self();
      }

      public T groupName(String groupName) {
         if (groupName != null)
            this.groupNames.add(groupName);
         return self();
      }

      public T amiLaunchIndex(String amiLaunchIndex) {
         this.amiLaunchIndex = amiLaunchIndex;
         return self();
      }

      public T dnsName(String dnsName) {
         this.dnsName = dnsName;
         return self();
      }

      public T imageId(String imageId) {
         this.imageId = imageId;
         return self();
      }

      public T instanceId(String instanceId) {
         this.instanceId = instanceId;
         return self();
      }

      public T instanceState(InstanceState instanceState) {
         this.instanceState = instanceState;
         return self();
      }
      
      public T rawState(String rawState) {
         this.rawState = rawState;
         return self();
      }
      
      public T instanceType(String instanceType) {
         this.instanceType = instanceType;
         return self();
      }

      public T ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return self();
      }

      public T kernelId(String kernelId) {
         this.kernelId = kernelId;
         return self();
      }

      public T keyName(String keyName) {
         this.keyName = keyName;
         return self();
      }

      public T launchTime(Date launchTime) {
         this.launchTime = launchTime;
         return self();
      }

      public T availabilityZone(String availabilityZone) {
         this.availabilityZone = availabilityZone;
         return self();
      }

      public T virtualizationType(String virtualizationType) {
         this.virtualizationType = virtualizationType;
         return self();
      }

      public T platform(String platform) {
         this.platform = platform;
         return self();
      }

      public T privateDnsName(String privateDnsName) {
         this.privateDnsName = privateDnsName;
         return self();
      }

      public T privateIpAddress(String privateIpAddress) {
         this.privateIpAddress = privateIpAddress;
         return self();
      }

      public T ramdiskId(String ramdiskId) {
         this.ramdiskId = ramdiskId;
         return self();
      }

      public T reason(String reason) {
         this.reason = reason;
         return self();
      }

      public T rootDeviceType(RootDeviceType rootDeviceType) {
         this.rootDeviceType = rootDeviceType;
         return self();
      }

      public T rootDeviceName(String rootDeviceName) {
         this.rootDeviceName = rootDeviceName;
         return self();
      }

      public T devices(Map<String, BlockDevice> ebsBlockDevices) {
         this.ebsBlockDevices = ImmutableMap.copyOf(checkNotNull(ebsBlockDevices, "ebsBlockDevices"));
         return self();
      }

      public T device(String key, BlockDevice value) {
         if (key != null && value != null)
            this.ebsBlockDevices.put(key, value);
         return self();
      }
      
      public T fromRunningInstance(RunningInstance in) {
         return region(in.region).groupNames(in.groupNames).amiLaunchIndex(in.amiLaunchIndex).dnsName(in.dnsName)
               .imageId(in.imageId).instanceId(in.instanceId).instanceState(in.instanceState).rawState(in.rawState)
               .instanceType(in.instanceType).ipAddress(in.ipAddress).kernelId(in.kernelId).keyName(in.keyName)
               .launchTime(in.launchTime).availabilityZone(in.availabilityZone)
               .virtualizationType(in.virtualizationType).platform(in.platform).privateDnsName(in.privateDnsName)
               .privateIpAddress(in.privateIpAddress).ramdiskId(in.ramdiskId).reason(in.reason)
               .rootDeviceType(in.rootDeviceType).rootDeviceName(in.rootDeviceName).devices(in.ebsBlockDevices)
               .tags(in.tags);
      }
      
      public abstract RunningInstance build();

   }
   
   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }

      @Override
      public RunningInstance build() {
         return new RunningInstance(region, groupNames, amiLaunchIndex, dnsName, imageId, instanceId, instanceState,
               rawState, instanceType, ipAddress, kernelId, keyName, launchTime, availabilityZone, virtualizationType,
               platform, privateDnsName, privateIpAddress, ramdiskId, reason, rootDeviceType, rootDeviceName,
               ebsBlockDevices, tags);
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
   protected final Map<String, String> tags;

   protected RunningInstance(String region, Iterable<String> groupNames, @Nullable String amiLaunchIndex,
            @Nullable String dnsName, String imageId, String instanceId, InstanceState instanceState, String rawState,
            String instanceType, @Nullable String ipAddress, @Nullable String kernelId, @Nullable String keyName,
            Date launchTime, String availabilityZone, String virtualizationType, @Nullable String platform,
            @Nullable String privateDnsName, @Nullable String privateIpAddress, @Nullable String ramdiskId,
            @Nullable String reason, RootDeviceType rootDeviceType, @Nullable String rootDeviceName,
            Map<String, BlockDevice> ebsBlockDevices, Map<String, String> tags) {
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
      this.tags = ImmutableMap.<String, String> copyOf(checkNotNull(tags, "tags"));
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
    * Names of the security groups.
    */
   public Set<String> getGroupNames() {
      return groupNames;
   }

   /**
    * tags that are present in the instance
    */
   public Map<String, String> getTags() {
      return tags;
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
               .add("rootDeviceType", rootDeviceType).add("ebsBlockDevices", ebsBlockDevices).add("tags", tags);
   }

   @Override
   public String toString() {
      return string().toString();
   }


}
