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
package org.jclouds.vcloud.options;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.vcloud.domain.network.NetworkConfig;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class InstantiateVAppTemplateOptions {
   private Set<NetworkConfig> networkConfig = Sets.newLinkedHashSet();

   private Boolean customizeOnInstantiate;
   private String description = null;
   private boolean deploy = true;
   private boolean powerOn = true;

   public String getDescription() {
      return description;
   }

   public boolean shouldDeploy() {
      return deploy;
   }

   public boolean shouldPowerOn() {
      return powerOn;
   }

   /**
    * Optional description. Used for the Description of the vApp created by this
    * instantiation.
    */
   public InstantiateVAppTemplateOptions description(String description) {
      this.description = description;
      return this;
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

   public static class Builder {

      /**
       * @see InstantiateVAppTemplateOptions#description
       */
      public static InstantiateVAppTemplateOptions description(String description) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.description(description);
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
       * @see InstantiateVAppTemplateOptions#addNetworkConfig
       */
      public static InstantiateVAppTemplateOptions addNetworkConfig(NetworkConfig networkConfig) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.addNetworkConfig(networkConfig);
      }

   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof InstantiateVAppTemplateOptions) {
         final InstantiateVAppTemplateOptions other = InstantiateVAppTemplateOptions.class.cast(object);
         return equal(networkConfig, other.networkConfig)
               && equal(customizeOnInstantiate, other.customizeOnInstantiate) && equal(description, other.description)
               && equal(deploy, other.deploy) && equal(powerOn, other.powerOn);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(networkConfig, customizeOnInstantiate, description, deploy, powerOn);
   }
   
   @Override
   public String toString(){
      return string().toString();
   }
   
   protected ToStringHelper string() {
      ToStringHelper toString = Objects.toStringHelper("").omitNullValues();
      toString.add("customizeOnInstantiate", customizeOnInstantiate).add("description", description);
      if (networkConfig.size() > 0)
         toString.add("networkConfig", networkConfig);
      if (!deploy)
         toString.add("deploy", deploy);
      if (!powerOn)
         toString.add("powerOn", powerOn);
      return toString;
   }

}
