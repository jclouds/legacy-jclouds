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
package org.jclouds.aws.ec2.domain;

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

import com.google.common.base.Objects.ToStringHelper;
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

      @Override
      public AWSRunningInstance build() {
         return new AWSRunningInstance(region, securityGroupIdToNames, amiLaunchIndex, dnsName, imageId, instanceId,
               instanceState, rawState, instanceType, ipAddress, kernelId, keyName, launchTime, availabilityZone,
               virtualizationType, platform, privateDnsName, privateIpAddress, ramdiskId, reason, rootDeviceType,
               rootDeviceName, ebsBlockDevices, monitoringState, placementGroup, productCodes, subnetId,
               spotInstanceRequestId, vpcId, hypervisor, tags);
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

   protected AWSRunningInstance(String region, Map<String, String> securityGroupIdToNames, String amiLaunchIndex,
            String dnsName, String imageId, String instanceId, InstanceState instanceState, String rawState,
            String instanceType, String ipAddress, String kernelId, String keyName, Date launchTime,
            String availabilityZone, String virtualizationType, String platform, String privateDnsName,
            String privateIpAddress, String ramdiskId, String reason, RootDeviceType rootDeviceType,
            String rootDeviceName, Map<String, BlockDevice> ebsBlockDevices, MonitoringState monitoringState,
            String placementGroup, Iterable<String> productCodes, String subnetId, String spotInstanceRequestId,
            String vpcId, Hypervisor hypervisor, Map<String, String> tags) {
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

   @Override
   protected ToStringHelper string() {
      return super.string().add("monitoringState", monitoringState).add("placementGroup", placementGroup)
               .add("subnetId", subnetId).add("spotInstanceRequestId", spotInstanceRequestId).add("vpcId", vpcId)
               .add("hypervisor", hypervisor);
   }

}
