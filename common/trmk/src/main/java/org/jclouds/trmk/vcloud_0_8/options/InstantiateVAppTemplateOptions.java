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
package org.jclouds.trmk.vcloud_0_8.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.trmk.vcloud_0_8.domain.FenceMode;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class InstantiateVAppTemplateOptions {
   public static class NetworkConfig {
      @Nullable
      private final String networkName;
      private final URI parentNetwork;
      @Nullable
      private final FenceMode fenceMode;

      /**
       * 
       * Create a new NetworkConfig.
       * 
       * @param networkName
       *           a valid {@networkConfig
       *           org.jclouds.vcloud.domain.VAppTemplate#getNetworkSection
       *           network in the vapp template}, or null to have us choose
       *           default
       * @param parentNetwork
       *           a valid {@networkConfig
       *           org.jclouds.vcloud.domain.Org#getNetworks in the Org}
       * @param fenceMode
       *           how to manage the relationship between the two networks
       */
      public NetworkConfig(String networkName, URI parentNetwork, FenceMode fenceMode) {
         this.networkName = networkName;
         this.parentNetwork = checkNotNull(parentNetwork, "parentNetwork");
         this.fenceMode = fenceMode;
      }

      public NetworkConfig(URI parentNetwork) {
         this(null, parentNetwork, null);
      }

      /**
       * A name for the network. If the
       * {@link org.jclouds.vcloud.domain.VAppTemplate#getNetworkSection}
       * includes a {@link NetworkSection.Network} network element, the name you
       * specify for the vApp network must match the name specified in that
       * element's name attribute.
       * 
       * @return
       */
      public String getNetworkName() {
         return networkName;
      }

      /**
       * 
       * @return A reference to the organization network to which this network
       *         connects.
       */
      public URI getParentNetwork() {
         return parentNetwork;
      }

      /**
       * A value of bridged indicates that this vApp network is connected
       * directly to the organization network.
       */
      public FenceMode getFenceMode() {
         return fenceMode;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((fenceMode == null) ? 0 : fenceMode.hashCode());
         result = prime * result + ((parentNetwork == null) ? 0 : parentNetwork.hashCode());
         result = prime * result + ((networkName == null) ? 0 : networkName.hashCode());
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
         NetworkConfig other = (NetworkConfig) obj;
         if (fenceMode == null) {
            if (other.fenceMode != null)
               return false;
         } else if (!fenceMode.equals(other.fenceMode))
            return false;
         if (parentNetwork == null) {
            if (other.parentNetwork != null)
               return false;
         } else if (!parentNetwork.equals(other.parentNetwork))
            return false;
         if (networkName == null) {
            if (other.networkName != null)
               return false;
         } else if (!networkName.equals(other.networkName))
            return false;
         return true;
      }

      @Override
      public String toString() {
         return "[networkName=" + networkName + ", parentNetwork=" + parentNetwork + ", fenceMode=" + fenceMode + "]";
      }
   }

   private Set<NetworkConfig> networkConfig = Sets.newLinkedHashSet();

   private Boolean customizeOnInstantiate;
   private String cpuCount;
   private String memorySizeMegabytes;

   private boolean block = true;
   private boolean deploy = true;
   private boolean powerOn = true;

   private final Map<String, String> properties = Maps.newLinkedHashMap();

   public InstantiateVAppTemplateOptions sshKeyFingerprint(String sshKeyFingerprint) {
      productProperty("sshKeyFingerprint", sshKeyFingerprint);
      return this;
   }

   public InstantiateVAppTemplateOptions primaryDNS(String primaryDNS) {
      productProperty("primaryDNS", primaryDNS);
      return this;
   }

   public InstantiateVAppTemplateOptions secondaryDNS(String secondaryDNS) {
      productProperty("secondaryDNS", secondaryDNS);
      return this;
   }

   public InstantiateVAppTemplateOptions withPassword(String password) {
      productProperty("password", password);
      return this;
   }

   public InstantiateVAppTemplateOptions inGroup(String group) {
      productProperty("group", group);
      return this;
   }

   public InstantiateVAppTemplateOptions inRow(String row) {
      productProperty("row", row);
      return this;
   }

   public InstantiateVAppTemplateOptions productProperties(Map<String, String> properties) {
      this.properties.putAll(properties);
      return this;
   }

   public InstantiateVAppTemplateOptions productProperty(String key, String value) {
      this.properties.put(key, value);
      return this;
   }

   public Map<String, String> getProperties() {
      return properties;
   }

   public boolean shouldBlock() {
      return block;
   }

   public boolean shouldDeploy() {
      return deploy;
   }

   public boolean shouldPowerOn() {
      return powerOn;
   }

   /**
    * deploy the vapp after it is instantiated?
    */
   public InstantiateVAppTemplateOptions deploy(boolean deploy) {
      this.deploy = deploy;
      return this;
   }

   /**
    * powerOn the vapp after it is instantiated?
    */
   public InstantiateVAppTemplateOptions powerOn(boolean powerOn) {
      this.powerOn = powerOn;
      return this;
   }

   /**
    * block until instantiate or deployment operations complete?
    */
   public InstantiateVAppTemplateOptions block(boolean block) {
      this.block = block;
      return this;
   }

   /**
    * If true, then customization is executed for all children that include a
    * GuestCustomizationSection.
    */
   public InstantiateVAppTemplateOptions customizeOnInstantiate(boolean customizeOnInstantiate) {
      this.customizeOnInstantiate = customizeOnInstantiate;
      return this;
   }

   public InstantiateVAppTemplateOptions processorCount(int cpuCount) {
      checkArgument(cpuCount >= 1, "cpuCount must be positive");
      this.cpuCount = cpuCount + "";
      return this;
   }

   public InstantiateVAppTemplateOptions memory(long megabytes) {
      checkArgument(megabytes >= 1, "megabytes must be positive");
      this.memorySizeMegabytes = megabytes + "";
      return this;
   }

   /**
    * {@networkConfig VAppTemplate}s have internal networks that can be
    * connected in order to access the internet or other external networks.
    * 
    * <h4>default behaviour if you don't use this option</h4> By default, we
    * connect the first internal {@networkConfig
    * org.jclouds.vcloud.domain.VAppTemplate#getNetworkSection network in the
    * vapp template}to a default chosen from the org or specified via
    * {@networkConfig
    * org.jclouds.vcloud.reference.VCloudConstants#
    * PROPERTY_VCLOUD_DEFAULT_NETWORK} using the {@networkConfig
    *  org.jclouds.vcloud.domain.FenceMode#BRIDGED} or an
    * override set by the property {@networkConfig
    * org.jclouds.vcloud.reference.VCloudConstants#
    * PROPERTY_VCLOUD_DEFAULT_FENCEMODE}.
    */
   public InstantiateVAppTemplateOptions addNetworkConfig(NetworkConfig networkConfig) {
      this.networkConfig.add(checkNotNull(networkConfig, "networkConfig"));
      return this;
   }

   public Set<NetworkConfig> getNetworkConfig() {
      return networkConfig;
   }

   public String getCpuCount() {
      return cpuCount;
   }

   public Boolean shouldCustomizeOnInstantiate() {
      return customizeOnInstantiate;
   }

   public String getMemorySizeMegabytes() {
      return memorySizeMegabytes;
   }

   public static class Builder {

      /**
       * @see InstantiateVAppTemplateOptions#block
       */
      public static InstantiateVAppTemplateOptions block(boolean block) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.block(block);
      }

      /**
       * @see InstantiateVAppTemplateOptions#deploy
       */
      public static InstantiateVAppTemplateOptions deploy(boolean deploy) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.deploy(deploy);
      }

      /**
       * @see InstantiateVAppTemplateOptions#powerOn
       */
      public static InstantiateVAppTemplateOptions powerOn(boolean powerOn) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.powerOn(powerOn);
      }

      /**
       * @see InstantiateVAppTemplateOptions#processorCount(int)
       */
      public static InstantiateVAppTemplateOptions processorCount(int cpuCount) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.processorCount(cpuCount);
      }

      /**
       * @see InstantiateVAppTemplateOptions#customizeOnInstantiate
       */
      public static InstantiateVAppTemplateOptions customizeOnInstantiate(Boolean customizeOnInstantiate) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.customizeOnInstantiate(customizeOnInstantiate);
      }

      /**
       * @see InstantiateVAppTemplateOptions#memory(int)
       */
      public static InstantiateVAppTemplateOptions memory(int megabytes) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.memory(megabytes);
      }

      /**
       * @see InstantiateVAppTemplateOptions#addNetworkConfig
       */
      public static InstantiateVAppTemplateOptions addNetworkConfig(NetworkConfig networkConfig) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.addNetworkConfig(networkConfig);
      }

      /**
       * @see InstantiateVAppTemplateOptions#withPassword(String)
       */
      public static InstantiateVAppTemplateOptions withPassword(String password) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.withPassword(password);
      }

      /**
       * @see InstantiateVAppTemplateOptions#inGroup(String)
       */
      public static InstantiateVAppTemplateOptions inGroup(String group) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.inGroup(group);
      }

      /**
       * @see InstantiateVAppTemplateOptions#inRow(String)
       */
      public static InstantiateVAppTemplateOptions inRow(String row) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.inRow(row);
      }

      /**
       * @see InstantiateVAppTemplateOptions#sshKeyFingerprint(String)
       */
      public static InstantiateVAppTemplateOptions sshKeyFingerprint(String sshKeyFingerprint) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.sshKeyFingerprint(sshKeyFingerprint);
      }

      /**
       * @see InstantiateVAppTemplateOptions#primaryDNS(String)
       */
      public static InstantiateVAppTemplateOptions primaryDNS(String primaryDNS) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.primaryDNS(primaryDNS);
      }

      /**
       * @see InstantiateVAppTemplateOptions#secondaryDNS(String)
       */
      public static InstantiateVAppTemplateOptions secondaryDNS(String secondaryDNS) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.secondaryDNS(secondaryDNS);
      }

      /**
       * @see InstantiateVAppTemplateOptions#productProperty(String, String)
       */
      public static InstantiateVAppTemplateOptions productProperty(String key, String value) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.productProperty(key, value);
      }

      /**
       * @see InstantiateVAppTemplateOptions#productProperties(Map<String ,
       *      String>)
       */
      public static InstantiateVAppTemplateOptions productProperties(Map<String, String> properties) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.productProperties(properties);
      }

   }

   @Override
   public String toString() {
      return "[cpuCount=" + cpuCount + ", memorySizeMegabytes=" + memorySizeMegabytes + ", networkConfig="
            + networkConfig + ", customizeOnInstantiate=" + customizeOnInstantiate + ", deploy=" + deploy
            + ", powerOn=" + powerOn + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (block ? 1231 : 1237);
      result = prime * result + ((cpuCount == null) ? 0 : cpuCount.hashCode());
      result = prime * result + ((customizeOnInstantiate == null) ? 0 : customizeOnInstantiate.hashCode());
      result = prime * result + (deploy ? 1231 : 1237);
      result = prime * result + ((memorySizeMegabytes == null) ? 0 : memorySizeMegabytes.hashCode());
      result = prime * result + ((networkConfig == null) ? 0 : networkConfig.hashCode());
      result = prime * result + (powerOn ? 1231 : 1237);
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
      InstantiateVAppTemplateOptions other = (InstantiateVAppTemplateOptions) obj;
      if (block != other.block)
         return false;
      if (cpuCount == null) {
         if (other.cpuCount != null)
            return false;
      } else if (!cpuCount.equals(other.cpuCount))
         return false;
      if (customizeOnInstantiate == null) {
         if (other.customizeOnInstantiate != null)
            return false;
      } else if (!customizeOnInstantiate.equals(other.customizeOnInstantiate))
         return false;
      if (deploy != other.deploy)
         return false;
      if (memorySizeMegabytes == null) {
         if (other.memorySizeMegabytes != null)
            return false;
      } else if (!memorySizeMegabytes.equals(other.memorySizeMegabytes))
         return false;
      if (networkConfig == null) {
         if (other.networkConfig != null)
            return false;
      } else if (!networkConfig.equals(other.networkConfig))
         return false;
      if (powerOn != other.powerOn)
         return false;
      return true;
   }

}
