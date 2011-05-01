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
package org.jclouds.gogrid.compute.options;

import java.util.Arrays;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.gogrid.domain.IpType;
import org.jclouds.io.Payload;

/**
 * Contains options supported in the {@code ComputeService#runNode} operation on the "gogrid"
 * provider.
 * 
 * <h2>Usage</h2>
 * The recommended way to instantiate a {@link GoGridTemplateOptions} object is to statically
 * import {@code GoGridTemplateOptions.*} and invoke a static creation method followed by an
 * instance mutator (if needed):
 * <p>
 * <code>
 * import static org.jclouds.compute.options.GoGridTemplateOptions.Builder.*;
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set<? extends NodeMetadata> set = client.runNodesWithTag(tag, 2, templateBuilder.build());
 * <code>
 * 
 * @author Adrian Cole
 * @author Andrew Kennedy
 */
public class GoGridTemplateOptions extends TemplateOptions implements Cloneable {
   @Override
   public GoGridTemplateOptions clone() {
      GoGridTemplateOptions options = new GoGridTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof GoGridTemplateOptions) {
         GoGridTemplateOptions eTo = GoGridTemplateOptions.class.cast(to);
         if (getIpType() != null)
            eTo.ipType(getIpType());
      }
   }

   private IpType ipType = null;

   public static final GoGridTemplateOptions NONE = new GoGridTemplateOptions();

   /**
    * Specifies the ipType used for network interfaces on the VMs
    */
   public GoGridTemplateOptions ipType(IpType ipType) {
      this.ipType = ipType;
      return this;
   }

   public static class Builder {
      /**
       * @see GoGridTemplateOptions#ipAddressAllocationMode
       */
      public static GoGridTemplateOptions ipType(IpType ipType) {
         GoGridTemplateOptions options = new GoGridTemplateOptions();
         return GoGridTemplateOptions.class.cast(options.ipType(ipType));
      }

      // methods that only facilitate returning the correct object type
      /**
       * @see TemplateOptions#inboundPorts
       */
      public static GoGridTemplateOptions inboundPorts(int... ports) {
         GoGridTemplateOptions options = new GoGridTemplateOptions();
         return GoGridTemplateOptions.class.cast(options.inboundPorts(ports));
      }

      /**
       * @see TemplateOptions#port
       */
      public static GoGridTemplateOptions blockOnPort(int port, int seconds) {
         GoGridTemplateOptions options = new GoGridTemplateOptions();
         return GoGridTemplateOptions.class.cast(options.blockOnPort(port, seconds));
      }

      /**
       * @see TemplateOptions#runScript
       */
      public static GoGridTemplateOptions runScript(Payload script) {
         GoGridTemplateOptions options = new GoGridTemplateOptions();
         return GoGridTemplateOptions.class.cast(options.runScript(script));
      }

      /**
       * @see TemplateOptions#installPrivateKey
       */
      public static GoGridTemplateOptions installPrivateKey(Payload rsaKey) {
         GoGridTemplateOptions options = new GoGridTemplateOptions();
         return GoGridTemplateOptions.class.cast(options.installPrivateKey(rsaKey));
      }

      /**
       * @see TemplateOptions#authorizePublicKey
       */
      public static GoGridTemplateOptions authorizePublicKey(Payload rsaKey) {
         GoGridTemplateOptions options = new GoGridTemplateOptions();
         return GoGridTemplateOptions.class.cast(options.authorizePublicKey(rsaKey));
      }

      /**
       * @see TemplateOptions#withDetails
       */
      public static GoGridTemplateOptions withDetails() {
         GoGridTemplateOptions options = new GoGridTemplateOptions();
         return GoGridTemplateOptions.class.cast(options.withMetadata());
      }
   }

   /**
    * @return ipType on the vms
    */
   public IpType getIpType() {
      return ipType;
   }

   // methods that only facilitate returning the correct object type

   /**
    * @see TemplateOptions#blockOnPort
    */
   @Override
   public GoGridTemplateOptions blockOnPort(int port, int seconds) {
      return GoGridTemplateOptions.class.cast(super.blockOnPort(port, seconds));
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
   public GoGridTemplateOptions inboundPorts(int... ports) {
      return GoGridTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * @see TemplateOptions#authorizePublicKey(String)
    */
   @Override
   public GoGridTemplateOptions authorizePublicKey(String publicKey) {
      return GoGridTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * @see TemplateOptions#authorizePublicKey(Payload)
    */
   @Override
   @Deprecated
   public GoGridTemplateOptions authorizePublicKey(Payload publicKey) {
      return GoGridTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * @see TemplateOptions#installPrivateKey(String)
    */
   @Override
   public GoGridTemplateOptions installPrivateKey(String privateKey) {
      return GoGridTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * @see TemplateOptions#installPrivateKey(Payload)
    */
   @Override
   @Deprecated
   public GoGridTemplateOptions installPrivateKey(Payload privateKey) {
      return GoGridTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * @see TemplateOptions#runScript(Payload)
    */
   @Override
   public GoGridTemplateOptions runScript(Payload script) {
      return GoGridTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * @see TemplateOptions#runScript(byte[])
    */
   @Override
   @Deprecated
   public GoGridTemplateOptions runScript(byte[] script) {
      return GoGridTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * @see TemplateOptions#withMetadata
    */
   @Override
   public GoGridTemplateOptions withMetadata() {
      return GoGridTemplateOptions.class.cast(super.withMetadata());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((ipType == null) ? 0 : ipType.hashCode());
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
      GoGridTemplateOptions other = (GoGridTemplateOptions) obj;
      if (ipType != other.ipType)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return  "[" + (ipType != null ? "ipType=" + ipType : "") + ", inboundPorts=" + Arrays.toString(inboundPorts) + ", privateKey="
            + (privateKey != null) + ", publicKey=" + (publicKey != null) + ", runScript=" + (script != null)
            + ", port:seconds=" + port + ":" + seconds + ", metadata/details: " + includeMetadata + "]";
   }

}
