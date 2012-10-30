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
package org.jclouds.vcloud.compute.options;

import static com.google.common.base.Objects.equal;

import java.net.URI;
import java.util.Map;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.util.Preconditions2;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.IpAddressAllocationMode;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Contains options supported in the {@code ComputeService#runNode} operation on
 * the "vcloud" provider. <h2>
 * Usage</h2> The recommended way to instantiate a VCloudTemplateOptions object
 * is to statically import VCloudTemplateOptions.* and invoke a static creation
 * method followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.compute.options.VCloudTemplateOptions.Builder.*;
 * <p/>
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set<NodeMetadata> set = client.createNodesInGroup(tag, 2, templateBuilder.build());
 * <code>
 * 
 * @author Adrian Cole
 */
public class VCloudTemplateOptions extends TemplateOptions implements Cloneable {
   @Override
   public VCloudTemplateOptions clone() {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof VCloudTemplateOptions) {
         VCloudTemplateOptions eTo = VCloudTemplateOptions.class.cast(to);
         if (getCustomizationScript() != null)
            eTo.customizationScript(getCustomizationScript());
         if (getDescription() != null)
            eTo.description(getDescription());
         if (getIpAddressAllocationMode() != null)
            eTo.ipAddressAllocationMode(getIpAddressAllocationMode());
         if (getIpAddressAllocationMode() != null)
            eTo.ipAddressAllocationMode(getIpAddressAllocationMode());
         if (getParentNetwork() != null)
            eTo.parentNetwork(getParentNetwork());
         if (getFenceMode() != null)
            eTo.fenceMode(getFenceMode());
      }
   }

   private String description = null;
   private String customizationScript = null;
   private IpAddressAllocationMode ipAddressAllocationMode = null;
   private URI parentNetwork = null;
   private FenceMode fenceMode = null;

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      VCloudTemplateOptions that = VCloudTemplateOptions.class.cast(o);
      return super.equals(that) && equal(this.description, that.description)
            && equal(this.customizationScript, that.customizationScript)
            && equal(this.ipAddressAllocationMode, that.ipAddressAllocationMode)
            && equal(this.parentNetwork, that.parentNetwork);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), description, customizationScript, ipAddressAllocationMode,
            parentNetwork);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("description", description).add("customizationScript", customizationScript)
            .add("ipAddressAllocationMode", ipAddressAllocationMode).add("parentNetwork", parentNetwork);
   }

   /**
    * Optional description. Used for the Description of the vApp created by this
    * instantiation.
    */
   public VCloudTemplateOptions description(String description) {
      this.description = description;
      return this;
   }

   /**
    * Specifies the customizationScript used to run instances with
    */
   public VCloudTemplateOptions customizationScript(String customizationScript) {
      Preconditions2.checkNotEmpty(customizationScript, "customizationScript must be non-empty");
      this.customizationScript = customizationScript;
      return this;
   }

   /**
    * Specifies the ipAddressAllocationMode used to for network interfaces on
    * the VMs
    */
   public VCloudTemplateOptions ipAddressAllocationMode(IpAddressAllocationMode ipAddressAllocationMode) {
      this.ipAddressAllocationMode = ipAddressAllocationMode;
      return this;
   }

   /**
    * Specifies the parentNetwork to connect the the network interfaces on the
    * VMs to.
    * 
    * @see InstantiateVAppTemplateOptions#addNetworkConfig
    */
   public VCloudTemplateOptions parentNetwork(URI parentNetwork) {
      this.parentNetwork = parentNetwork;
      return this;
   }

   /**
    * How to connect to the parent network
    * 
    * @see InstantiateVAppTemplateOptions#addNetworkConfig
    */
   public VCloudTemplateOptions fenceMode(FenceMode fenceMode) {
      this.fenceMode = fenceMode;
      return this;
   }

   public static class Builder {
      /**
       * @see VCloudTemplateOptions#description
       */
      public static VCloudTemplateOptions description(String description) {
         return new VCloudTemplateOptions().description(description);
      }

      /**
       * @see VCloudTemplateOptions#customizationScript
       */
      public static VCloudTemplateOptions customizationScript(String customizationScript) {
         return new VCloudTemplateOptions().customizationScript(customizationScript);
      }

      /**
       * @see VCloudTemplateOptions#ipAddressAllocationMode
       */
      public static VCloudTemplateOptions ipAddressAllocationMode(IpAddressAllocationMode ipAddressAllocationMode) {
         return new VCloudTemplateOptions().ipAddressAllocationMode(ipAddressAllocationMode);
      }

      /**
       * @see VCloudTemplateOptions#parentNetwork(URI parentNetwork)
       */
      public static VCloudTemplateOptions parentNetwork(URI parentNetwork) {
         return new VCloudTemplateOptions().parentNetwork(parentNetwork);
      }

      /**
       * @see VCloudTemplateOptions#fenceMode(FenceMode)
       */
      public static VCloudTemplateOptions fenceMode(FenceMode fenceMode) {
         return new VCloudTemplateOptions().fenceMode(fenceMode);
      }

      // methods that only facilitate returning the correct object type
      /**
       * @see TemplateOptions#inboundPorts
       */
      public static VCloudTemplateOptions inboundPorts(int... ports) {
         VCloudTemplateOptions options = new VCloudTemplateOptions();
         return VCloudTemplateOptions.class.cast(options.inboundPorts(ports));
      }

      /**
       * @see TemplateOptions#port
       */
      public static VCloudTemplateOptions blockOnPort(int port, int seconds) {
         VCloudTemplateOptions options = new VCloudTemplateOptions();
         return VCloudTemplateOptions.class.cast(options.blockOnPort(port, seconds));
      }

      /**
       * @see TemplateOptions#userMetadata(Map)
       */
      public static VCloudTemplateOptions userMetadata(Map<String, String> userMetadata) {
         VCloudTemplateOptions options = new VCloudTemplateOptions();
         return VCloudTemplateOptions.class.cast(options.userMetadata(userMetadata));
      }

      /**
       * @see TemplateOptions#userMetadata(String, String)
       */
      public static VCloudTemplateOptions userMetadata(String key, String value) {
         VCloudTemplateOptions options = new VCloudTemplateOptions();
         return VCloudTemplateOptions.class.cast(options.userMetadata(key, value));
      }

   }

   /**
    * @return description of the vApp
    */
   public String getDescription() {
      return description;
   }

   /**
    * @return customizationScript on the vms
    */
   public String getCustomizationScript() {
      return customizationScript;
   }

   /**
    * @return ipAddressAllocationMode on the vms
    */
   public IpAddressAllocationMode getIpAddressAllocationMode() {
      return ipAddressAllocationMode;
   }

   /**
    * @return parentNetwork to connect to the vms
    */
   public URI getParentNetwork() {
      return parentNetwork;
   }

   /**
    * @return FenceMode to connect the parent network with
    */
   public FenceMode getFenceMode() {
      return fenceMode;
   }

   // methods that only facilitate returning the correct object type

   /**
    * @see TemplateOptions#blockOnPort
    */
   @Override
   public VCloudTemplateOptions blockOnPort(int port, int seconds) {
      return VCloudTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * 
    * special thing is that we do assume if you are passing groups that you have
    * everything you need already defined. for example, our option inboundPorts
    * normally creates ingress rules accordingly but if we notice you've
    * specified securityGroups, we do not mess with rules at all
    * 
    * @see TemplateOptions#inboundPorts
    */
   @Override
   public VCloudTemplateOptions inboundPorts(int... ports) {
      return VCloudTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * @see TemplateOptions#authorizePublicKey(String)
    */
   @Override
   public VCloudTemplateOptions authorizePublicKey(String publicKey) {
      return VCloudTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * @see TemplateOptions#installPrivateKey(String)
    */
   @Override
   public VCloudTemplateOptions installPrivateKey(String privateKey) {
      return VCloudTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public VCloudTemplateOptions userMetadata(Map<String, String> userMetadata) {
      return VCloudTemplateOptions.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public VCloudTemplateOptions userMetadata(String key, String value) {
      return VCloudTemplateOptions.class.cast(super.userMetadata(key, value));
   }

}
