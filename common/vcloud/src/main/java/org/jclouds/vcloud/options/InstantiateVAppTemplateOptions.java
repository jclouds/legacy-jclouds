/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.vcloud.domain.network.NetworkConfig;

import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class InstantiateVAppTemplateOptions {
   private Set<NetworkConfig> networkConfig = Sets.newLinkedHashSet();

   private Boolean customizeOnInstantiate;
   private String cpuCount;
   private String memorySizeMegabytes;
   private String diskSizeKilobytes;

   private boolean block = true;
   private boolean deploy = true;
   private boolean powerOn = true;

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

   public InstantiateVAppTemplateOptions disk(long kilobytes) {
      checkArgument(kilobytes >= 1, "diskSizeKilobytes must be positive");
      this.diskSizeKilobytes = kilobytes + "";
      return this;
   }

   /**
    * {@networkConfig VAppTemplate}s have internal networks that can be connected in order to access
    * the internet or other external networks.
    * 
    * <h4>default behaviour if you don't use this option</h4> By default, we connect the first
    * internal {@networkConfig
    * org.jclouds.vcloud.domain.VAppTemplate#getNetworkSection network in the vapp template}to a
    * default chosen from the org or specified via {@networkConfig
    * org.jclouds.vcloud.reference.VCloudConstants#PROPERTY_VCLOUD_DEFAULT_NETWORK} using the
    * {@networkConfig org.jclouds.vcloud.domain.FenceMode#BRIDGED} or an override
    * set by the property {@networkConfig
    * org.jclouds.vcloud.reference.VCloudConstants#PROPERTY_VCLOUD_DEFAULT_FENCEMODE}.
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

   public String getDiskSizeKilobytes() {
      return diskSizeKilobytes;
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
       * @see InstantiateVAppTemplateOptions#disk(int)
       */
      public static InstantiateVAppTemplateOptions disk(long kilobytes) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.disk(kilobytes);
      }

      /**
       * @see InstantiateVAppTemplateOptions#addNetworkConfig
       */
      public static InstantiateVAppTemplateOptions addNetworkConfig(NetworkConfig networkConfig) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.addNetworkConfig(networkConfig);
      }

   }

   @Override
   public String toString() {
      return "InstantiateVAppTemplateOptions [cpuCount=" + cpuCount + ", memorySizeMegabytes=" + memorySizeMegabytes
               + ", diskSizeKilobytes=" + diskSizeKilobytes + ", networkConfig=" + networkConfig
               + ", customizeOnInstantiate=" + customizeOnInstantiate + ", deploy=" + (deploy) + ", powerOn="
               + (powerOn) + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (block ? 1231 : 1237);
      result = prime * result + ((cpuCount == null) ? 0 : cpuCount.hashCode());
      result = prime * result + ((customizeOnInstantiate == null) ? 0 : customizeOnInstantiate.hashCode());
      result = prime * result + (deploy ? 1231 : 1237);
      result = prime * result + ((diskSizeKilobytes == null) ? 0 : diskSizeKilobytes.hashCode());
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
      if (diskSizeKilobytes == null) {
         if (other.diskSizeKilobytes != null)
            return false;
      } else if (!diskSizeKilobytes.equals(other.diskSizeKilobytes))
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
