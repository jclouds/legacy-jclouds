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

package org.jclouds.vcloud.compute.options;

import java.util.Arrays;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.io.Payload;
import org.jclouds.util.Preconditions2;

/**
 * Contains options supported in the {@code ComputeService#runNode} operation on the "vcloud"
 * provider. <h2>
 * Usage</h2> The recommended way to instantiate a VCloudTemplateOptions object is to statically
 * import VCloudTemplateOptions.* and invoke a static creation method followed by an instance
 * mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.compute.options.VCloudTemplateOptions.Builder.*;
 * <p/>
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set<? extends NodeMetadata> set = client.runNodesWithTag(tag, 2, templateBuilder.build());
 * <code>
 * 
 * @author Adrian Cole
 */
public class VCloudTemplateOptions extends TemplateOptions {

   private String customizationScript = null;

   public static final VCloudTemplateOptions NONE = new VCloudTemplateOptions();

   /**
    * Specifies the customizationScript used to run instances with
    */
   public VCloudTemplateOptions customizationScript(String customizationScript) {
      Preconditions2.checkNotEmpty(customizationScript, "customizationScript must be non-empty");
      this.customizationScript = customizationScript;
      return this;
   }

   public static class Builder {

      /**
       * @see VCloudTemplateOptions#customizationScript
       */
      public static VCloudTemplateOptions customizationScript(String customizationScript) {
         VCloudTemplateOptions options = new VCloudTemplateOptions();
         return VCloudTemplateOptions.class.cast(options.customizationScript(customizationScript));
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
       * @see TemplateOptions#runScript
       */
      public static VCloudTemplateOptions runScript(Payload script) {
         VCloudTemplateOptions options = new VCloudTemplateOptions();
         return VCloudTemplateOptions.class.cast(options.runScript(script));
      }

      /**
       * @see TemplateOptions#installPrivateKey
       */
      public static VCloudTemplateOptions installPrivateKey(Payload rsaKey) {
         VCloudTemplateOptions options = new VCloudTemplateOptions();
         return VCloudTemplateOptions.class.cast(options.installPrivateKey(rsaKey));
      }

      /**
       * @see TemplateOptions#authorizePublicKey
       */
      public static VCloudTemplateOptions authorizePublicKey(Payload rsaKey) {
         VCloudTemplateOptions options = new VCloudTemplateOptions();
         return VCloudTemplateOptions.class.cast(options.authorizePublicKey(rsaKey));
      }

      /**
       * @see TemplateOptions#withDetails
       */
      public static VCloudTemplateOptions withDetails() {
         VCloudTemplateOptions options = new VCloudTemplateOptions();
         return VCloudTemplateOptions.class.cast(options.withMetadata());
      }

   }

   /**
    * @return customizationScript on the vms
    */
   public String getCustomizationScript() {
      return customizationScript;
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
    * special thing is that we do assume if you are passing groups that you have everything you need
    * already defined. for example, our option inboundPorts normally creates ingress rules
    * accordingly but if we notice you've specified securityGroups, we do not mess with rules at all
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
    * @see TemplateOptions#authorizePublicKey(Payload)
    */
   @Override
   @Deprecated
   public VCloudTemplateOptions authorizePublicKey(Payload publicKey) {
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
    * @see TemplateOptions#installPrivateKey(Payload)
    */
   @Override
   @Deprecated
   public VCloudTemplateOptions installPrivateKey(Payload privateKey) {
      return VCloudTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * @see TemplateOptions#runScript(Payload)
    */
   @Override
   public VCloudTemplateOptions runScript(Payload script) {
      return VCloudTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * @see TemplateOptions#runScript(byte[])
    */
   @Override
   @Deprecated
   public VCloudTemplateOptions runScript(byte[] script) {
      return VCloudTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * @see TemplateOptions#withMetadata
    */
   @Override
   public VCloudTemplateOptions withMetadata() {
      return VCloudTemplateOptions.class.cast(super.withMetadata());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((customizationScript == null) ? 0 : customizationScript.hashCode());
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
      return true;
   }

   @Override
   public String toString() {
      return "[customizationScript=" + customizationScript + ", inboundPorts=" + Arrays.toString(inboundPorts)
               + ", privateKey=" + (privateKey != null) + ", publicKey=" + (publicKey != null) + ", runScript="
               + (script != null) + ", port:seconds=" + port + ":" + seconds + ", metadata/details: " + includeMetadata
               + "]";
   }

}
