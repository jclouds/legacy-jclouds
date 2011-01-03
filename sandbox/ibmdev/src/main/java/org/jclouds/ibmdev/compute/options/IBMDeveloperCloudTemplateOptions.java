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

package org.jclouds.ibmdev.compute.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Arrays;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.io.Payload;
import org.jclouds.util.Preconditions2;

/**
 * Contains options supported in the {@code ComputeService#runNode} operation on the "ibmdev"
 * provider. <h2>
 * Usage</h2> The recommended way to instantiate a IBMDeveloperCloudTemplateOptions object is to
 * statically import IBMDeveloperCloudTemplateOptions.* and invoke a static creation method followed
 * by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.ibmdev.compute.options.IBMDeveloperCloudTemplateOptions.Builder.*;
 * <p/>
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set<? extends NodeMetadata> set = client.runNodesWithTag(tag, 2, templateBuilder.build());
 * <code>
 * 
 * @author Adrian Cole
 */
public class IBMDeveloperCloudTemplateOptions extends TemplateOptions {

   private String keyPair = null;
   private boolean noKeyPair;

   public static final IBMDeveloperCloudTemplateOptions NONE = new IBMDeveloperCloudTemplateOptions();

   /**
    * Specifies the keypair used to run instances with
    */
   public IBMDeveloperCloudTemplateOptions keyPair(String keyPair) {
      checkNotNull(keyPair, "use noKeyPair option to request boot without a keypair");
      checkState(!noKeyPair, "you cannot specify both options keyPair and noKeyPair");
      Preconditions2.checkNotEmpty(keyPair, "keypair must be non-empty");
      this.keyPair = keyPair;
      return this;
   }

   /**
    * Do not use a keypair on instances
    */
   public IBMDeveloperCloudTemplateOptions noKeyPair() {
      checkState(keyPair == null, "you cannot specify both options keyPair and noKeyPair");
      this.noKeyPair = true;
      return this;
   }

   public static class Builder {

      /**
       * @see IBMDeveloperCloudTemplateOptions#keyPair
       */
      public static IBMDeveloperCloudTemplateOptions keyPair(String keyPair) {
         IBMDeveloperCloudTemplateOptions options = new IBMDeveloperCloudTemplateOptions();
         return IBMDeveloperCloudTemplateOptions.class.cast(options.keyPair(keyPair));
      }

      /**
       * @see IBMDeveloperCloudTemplateOptions#noKeyPair
       */
      public static IBMDeveloperCloudTemplateOptions noKeyPair() {
         IBMDeveloperCloudTemplateOptions options = new IBMDeveloperCloudTemplateOptions();
         return IBMDeveloperCloudTemplateOptions.class.cast(options.noKeyPair());
      }

      // methods that only facilitate returning the correct object type
      /**
       * @see TemplateOptions#inboundPorts
       */
      public static IBMDeveloperCloudTemplateOptions inboundPorts(int... ports) {
         IBMDeveloperCloudTemplateOptions options = new IBMDeveloperCloudTemplateOptions();
         return IBMDeveloperCloudTemplateOptions.class.cast(options.inboundPorts(ports));
      }

      /**
       * @see TemplateOptions#port
       */
      public static IBMDeveloperCloudTemplateOptions blockOnPort(int port, int seconds) {
         IBMDeveloperCloudTemplateOptions options = new IBMDeveloperCloudTemplateOptions();
         return IBMDeveloperCloudTemplateOptions.class.cast(options.blockOnPort(port, seconds));
      }

      /**
       * @see TemplateOptions#blockUntilRunning
       */
      public static IBMDeveloperCloudTemplateOptions blockUntilRunning(boolean blockUntilRunning) {
         IBMDeveloperCloudTemplateOptions options = new IBMDeveloperCloudTemplateOptions();
         return IBMDeveloperCloudTemplateOptions.class.cast(options.blockUntilRunning(blockUntilRunning));
      }

      /**
       * @see TemplateOptions#runScript
       */
      public static IBMDeveloperCloudTemplateOptions runScript(byte[] script) {
         IBMDeveloperCloudTemplateOptions options = new IBMDeveloperCloudTemplateOptions();
         return IBMDeveloperCloudTemplateOptions.class.cast(options.runScript(script));
      }

      /**
       * @see TemplateOptions#installPrivateKey
       */
      public static IBMDeveloperCloudTemplateOptions installPrivateKey(String rsaKey) {
         IBMDeveloperCloudTemplateOptions options = new IBMDeveloperCloudTemplateOptions();
         return IBMDeveloperCloudTemplateOptions.class.cast(options.installPrivateKey(rsaKey));
      }

      /**
       * @see TemplateOptions#authorizePublicKey
       */
      public static IBMDeveloperCloudTemplateOptions authorizePublicKey(String rsaKey) {
         IBMDeveloperCloudTemplateOptions options = new IBMDeveloperCloudTemplateOptions();
         return IBMDeveloperCloudTemplateOptions.class.cast(options.authorizePublicKey(rsaKey));
      }

      /**
       * @see TemplateOptions#withDetails
       */
      public static IBMDeveloperCloudTemplateOptions withDetails() {
         IBMDeveloperCloudTemplateOptions options = new IBMDeveloperCloudTemplateOptions();
         return IBMDeveloperCloudTemplateOptions.class.cast(options.withMetadata());
      }

   }

   // methods that only facilitate returning the correct object type

   /**
    * @see TemplateOptions#blockOnPort
    */
   @Override
   public IBMDeveloperCloudTemplateOptions blockOnPort(int port, int seconds) {
      return IBMDeveloperCloudTemplateOptions.class.cast(super.blockOnPort(port, seconds));
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
   public IBMDeveloperCloudTemplateOptions inboundPorts(int... ports) {
      return IBMDeveloperCloudTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * @see TemplateOptions#authorizePublicKey(String)
    */
   @Override
   @Deprecated
   public IBMDeveloperCloudTemplateOptions authorizePublicKey(String publicKey) {
      return IBMDeveloperCloudTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * @see TemplateOptions#authorizePublicKey(Payload)
    */
   @Override
   public IBMDeveloperCloudTemplateOptions authorizePublicKey(Payload publicKey) {
      return IBMDeveloperCloudTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * @see TemplateOptions#installPrivateKey(String)
    */
   @Override
   @Deprecated
   public IBMDeveloperCloudTemplateOptions installPrivateKey(String privateKey) {
      return IBMDeveloperCloudTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * @see TemplateOptions#installPrivateKey(Payload)
    */
   @Override
   public IBMDeveloperCloudTemplateOptions installPrivateKey(Payload privateKey) {
      return IBMDeveloperCloudTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * @see TemplateOptions#runScript(Payload)
    */
   @Override
   public IBMDeveloperCloudTemplateOptions runScript(Payload script) {
      return IBMDeveloperCloudTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * @see TemplateOptions#runScript(byte[])
    */
   @Override
   @Deprecated
   public IBMDeveloperCloudTemplateOptions runScript(byte[] script) {
      return IBMDeveloperCloudTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * @see TemplateOptions#withMetadata
    */
   @Override
   public IBMDeveloperCloudTemplateOptions withMetadata() {
      return IBMDeveloperCloudTemplateOptions.class.cast(super.withMetadata());
   }

   /**
    * @return keyPair to use when running the instance or null, to generate a keypair.
    */
   public String getKeyPair() {
      return keyPair;
   }

   /**
    * @return true (default) if we are supposed to use a keypair
    */
   public boolean shouldAutomaticallyCreateKeyPair() {
      return !noKeyPair;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((keyPair == null) ? 0 : keyPair.hashCode());
      result = prime * result + (noKeyPair ? 1231 : 1237);
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
      IBMDeveloperCloudTemplateOptions other = (IBMDeveloperCloudTemplateOptions) obj;
      if (keyPair == null) {
         if (other.keyPair != null)
            return false;
      } else if (!keyPair.equals(other.keyPair))
         return false;
      if (noKeyPair != other.noKeyPair)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "IBMDeveloperCloudTemplateOptions [keyPair=" + keyPair + ", noKeyPair=" + noKeyPair + ", inboundPorts="
               + Arrays.toString(inboundPorts) + ", privateKey=" + (privateKey != null) + ", publicKey="
               + (publicKey != null) + ", runScript=" + (script != null) + ", port:seconds=" + port + ":" + seconds
               + ", metadata/details: " + includeMetadata + "]";
   }

}
