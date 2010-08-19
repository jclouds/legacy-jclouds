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
import java.util.Map;

import org.jclouds.vcloud.domain.FenceMode;

import com.google.common.collect.Maps;

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
   private String fenceMode;
   private String networkName;
   private boolean blockOnDeploy = true;
   private Map<String, String> properties = Maps.newTreeMap();

   public boolean shouldBlockOnDeploy() {
      return blockOnDeploy;
   }

   public InstantiateVAppTemplateOptions blockOnDeploy(boolean blockOnDeploy) {
      this.blockOnDeploy = blockOnDeploy;
      return this;
   }

   public InstantiateVAppTemplateOptions productProperty(String key, String value) {
      properties.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
      return this;
   }

   public InstantiateVAppTemplateOptions productProperties(Map<String, String> properties) {
      this.properties.putAll(checkNotNull(properties, "properties"));
      return this;
   }

   public InstantiateVAppTemplateOptions processorCount(int cpuCount) {
      checkArgument(cpuCount >= 1, "cpuCount must be positive");
      this.cpuCount = cpuCount + "";
      return this;
   }

   public InstantiateVAppTemplateOptions networkName(String networkName) {
      this.networkName = checkNotNull(networkName, "networkName");
      return this;
   }

   public InstantiateVAppTemplateOptions fenceMode(String fenceMode) {
      this.fenceMode = checkNotNull(fenceMode, "fenceMode").toString();
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

   public InstantiateVAppTemplateOptions inNetwork(URI networkLocation) {
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

   public String getFenceMode() {
      return fenceMode;
   }

   public Map<String, String> getProperties() {
      return properties;
   }

   public static class Builder {

      /**
       * @see InstantiateVAppTemplateOptions#blockOnDeploy
       */
      public static InstantiateVAppTemplateOptions blockOnDeploy(boolean blockOnDeploy) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.blockOnDeploy(blockOnDeploy);
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
       * @see InstantiateVAppTemplateOptions#inNetwork(URI)
       */
      public static InstantiateVAppTemplateOptions inNetwork(URI networkLocation) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.inNetwork(networkLocation);
      }

      /**
       * @see InstantiateVAppTemplateOptions#fenceMode(FenceMode)
       */
      public static InstantiateVAppTemplateOptions fenceMode(String fenceMode) {
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

      /**
       * @see InstantiateVAppTemplateOptions#productProperty(String,String)
       */
      public static InstantiateVAppTemplateOptions productProperty(String key, String value) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.productProperty(key, value);
      }

      /**
       * @see InstantiateVAppTemplateOptions#setProperties(Map<String, String>)
       */
      public static InstantiateVAppTemplateOptions productProperties(Map<String, String> properties) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.productProperties(properties);
      }
   }

   @Override
   public String toString() {
      return "InstantiateVAppTemplateOptions [cpuCount=" + cpuCount + ", memorySizeMegabytes=" + memorySizeMegabytes
               + ", diskSizeKilobytes=" + diskSizeKilobytes + ", network=" + network + ", networkName=" + networkName
               + ", fenceMode=" + fenceMode + ", properties=" + properties + "]";
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
      result = prime * result + ((properties == null) ? 0 : properties.hashCode());
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
      if (properties == null) {
         if (other.properties != null)
            return false;
      } else if (!properties.equals(other.properties))
         return false;
      return true;
   }

}
