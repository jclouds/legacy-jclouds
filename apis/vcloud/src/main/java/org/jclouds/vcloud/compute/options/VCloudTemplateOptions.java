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

import java.util.Map;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.util.Preconditions2;
import org.jclouds.vcloud.domain.network.IpAddressAllocationMode;

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
      }
   }

   private String description = null;
   private String customizationScript = null;
   private IpAddressAllocationMode ipAddressAllocationMode = null;

   public static final VCloudTemplateOptions NONE = new VCloudTemplateOptions();

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

   public static class Builder {
      /**
       * @see VCloudTemplateOptions#description
       */
      public static VCloudTemplateOptions description(String description) {
         VCloudTemplateOptions options = new VCloudTemplateOptions();
         return VCloudTemplateOptions.class.cast(options.description(description));
      }

      /**
       * @see VCloudTemplateOptions#customizationScript
       */
      public static VCloudTemplateOptions customizationScript(String customizationScript) {
         VCloudTemplateOptions options = new VCloudTemplateOptions();
         return VCloudTemplateOptions.class.cast(options.customizationScript(customizationScript));
      }

      /**
       * @see VCloudTemplateOptions#ipAddressAllocationMode
       */
      public static VCloudTemplateOptions ipAddressAllocationMode(IpAddressAllocationMode ipAddressAllocationMode) {
         VCloudTemplateOptions options = new VCloudTemplateOptions();
         return VCloudTemplateOptions.class.cast(options.ipAddressAllocationMode(ipAddressAllocationMode));
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

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((customizationScript == null) ? 0 : customizationScript.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((ipAddressAllocationMode == null) ? 0 : ipAddressAllocationMode.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      VCloudTemplateOptions other = (VCloudTemplateOptions) obj;
      if (customizationScript == null) {
         if (other.customizationScript != null)
            return false;
      } else if (!customizationScript.equals(other.customizationScript))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (ipAddressAllocationMode != other.ipAddressAllocationMode)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[customizationScript=" + (customizationScript != null) + ", description=" + description
            + ", ipAddressAllocationMode=" + ipAddressAllocationMode + ", inboundPorts="
            + inboundPorts + ", privateKey=" + (privateKey != null) + ", publicKey="
            + (publicKey != null) + ", runScript=" + (script != null) + ", port:seconds=" + port + ":" + seconds
            + ", userMetadata: " + userMetadata + "]";
   }

}
