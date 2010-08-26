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

package org.jclouds.vcloud.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.vcloud.domain.network.FenceMode;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class InstantiateVAppTemplateOptions {

   private String cpuCount;
   private String memorySizeMegabytes;
   private String diskSizeKilobytes;
   private String network;
   private FenceMode fenceMode;
   private String networkName;
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

   public InstantiateVAppTemplateOptions processorCount(int cpuCount) {
      checkArgument(cpuCount >= 1, "cpuCount must be positive");
      this.cpuCount = cpuCount + "";
      return this;
   }

   /**
    * The name of the vApp internal network that you want to connect to a VDC network
    */
   public InstantiateVAppTemplateOptions networkName(String networkName) {
      this.networkName = checkNotNull(networkName, "networkName");
      return this;
   }

   public InstantiateVAppTemplateOptions fenceMode(FenceMode fenceMode) {
      this.fenceMode = checkNotNull(fenceMode, "fenceMode");
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

   public InstantiateVAppTemplateOptions network(URI networkLocation) {
      this.network = checkNotNull(networkLocation, "networkLocation").toASCIIString();
      return this;
   }

   public String getCpuCount() {
      return cpuCount;
   }

   public String getMemorySizeMegabytes() {
      return memorySizeMegabytes;
   }

   public String getDiskSizeKilobytes() {
      return diskSizeKilobytes;
   }

   public String getNetwork() {
      return network;
   }

   public String getNetworkName() {
      return networkName;
   }

   public FenceMode getFenceMode() {
      return fenceMode;
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
       * @see InstantiateVAppTemplateOptions#network(URI)
       */
      public static InstantiateVAppTemplateOptions inNetwork(URI networkLocation) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.network(networkLocation);
      }

      /**
       * @see InstantiateVAppTemplateOptions#fenceMode(FenceMode)
       */
      public static InstantiateVAppTemplateOptions fenceMode(FenceMode fenceMode) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.fenceMode(fenceMode);
      }

      /**
       * @see InstantiateVAppTemplateOptions#networkName(String)
       */
      public static InstantiateVAppTemplateOptions networkName(String networkName) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.networkName(networkName);
      }

   }

   @Override
   public String toString() {
      return "InstantiateVAppTemplateOptions [cpuCount=" + cpuCount + ", memorySizeMegabytes=" + memorySizeMegabytes
               + ", diskSizeKilobytes=" + diskSizeKilobytes + ", network=" + network + ", networkName=" + networkName
               + ", fenceMode=" + fenceMode + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((cpuCount == null) ? 0 : cpuCount.hashCode());
      result = prime * result + ((diskSizeKilobytes == null) ? 0 : diskSizeKilobytes.hashCode());
      result = prime * result + ((fenceMode == null) ? 0 : fenceMode.hashCode());
      result = prime * result + ((memorySizeMegabytes == null) ? 0 : memorySizeMegabytes.hashCode());
      result = prime * result + ((network == null) ? 0 : network.hashCode());
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
      InstantiateVAppTemplateOptions other = (InstantiateVAppTemplateOptions) obj;
      if (cpuCount == null) {
         if (other.cpuCount != null)
            return false;
      } else if (!cpuCount.equals(other.cpuCount))
         return false;
      if (diskSizeKilobytes == null) {
         if (other.diskSizeKilobytes != null)
            return false;
      } else if (!diskSizeKilobytes.equals(other.diskSizeKilobytes))
         return false;
      if (fenceMode == null) {
         if (other.fenceMode != null)
            return false;
      } else if (!fenceMode.equals(other.fenceMode))
         return false;
      if (memorySizeMegabytes == null) {
         if (other.memorySizeMegabytes != null)
            return false;
      } else if (!memorySizeMegabytes.equals(other.memorySizeMegabytes))
         return false;
      if (network == null) {
         if (other.network != null)
            return false;
      } else if (!network.equals(other.network))
         return false;
      if (networkName == null) {
         if (other.networkName != null)
            return false;
      } else if (!networkName.equals(other.networkName))
         return false;
      return true;
   }

}
