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
package org.jclouds.ibm.smartcloud.compute.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Arrays;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.ibm.smartcloud.options.CreateInstanceOptions;
import org.jclouds.io.Payload;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.util.Preconditions2;

/**
 * Contains options supported in the {@code ComputeService#runNode} operation on the
 * "ibm.smartcloud" provider. <h2>
 * Usage</h2> The recommended way to instantiate a IBMSmartCloudTemplateOptions object is to
 * statically import IBMSmartCloudTemplateOptions.* and invoke a static creation method followed by
 * an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.ibm.smartcloud.compute.options.IBMSmartCloudTemplateOptions.Builder.*;
 * <p/>
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set<? extends NodeMetadata> set = client.runNodesWithTag(tag, 2, templateBuilder.build());
 * <code>
 * 
 * @author Adrian Cole
 */
public class IBMSmartCloudTemplateOptions extends TemplateOptions implements Cloneable {
   @Override
   public IBMSmartCloudTemplateOptions clone() {
      IBMSmartCloudTemplateOptions options = new IBMSmartCloudTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof IBMSmartCloudTemplateOptions) {
         IBMSmartCloudTemplateOptions eTo = IBMSmartCloudTemplateOptions.class.cast(to);
         if (getKeyPair() != null)
            eTo.keyPair(getKeyPair());
         if (!shouldAutomaticallyCreateKeyPair())
            eTo.noKeyPair();
         if (!isMiniEphemeral)
            eTo.isMiniEphemeral(false);
      }
   }

   private String keyPair = null;
   private boolean noKeyPair;
   private boolean isMiniEphemeral = true;

   public static final IBMSmartCloudTemplateOptions NONE = new IBMSmartCloudTemplateOptions();

   /**
    * @see CreateInstanceOptions#isMiniEphemeral
    */
   public IBMSmartCloudTemplateOptions isMiniEphemeral(boolean isMiniEphemeral) {
      this.isMiniEphemeral = isMiniEphemeral;
      return this;
   }

   /**
    * Specifies the keypair used to run instances with
    */
   public IBMSmartCloudTemplateOptions keyPair(String keyPair) {
      checkNotNull(keyPair, "use noKeyPair option to request boot without a keypair");
      checkState(!noKeyPair, "you cannot specify both options keyPair and noKeyPair");
      Preconditions2.checkNotEmpty(keyPair, "keypair must be non-empty");
      this.keyPair = keyPair;
      return this;
   }

   /**
    * Do not use a keypair on instances
    */
   public IBMSmartCloudTemplateOptions noKeyPair() {
      checkState(keyPair == null, "you cannot specify both options keyPair and noKeyPair");
      this.noKeyPair = true;
      return this;
   }

   public static class Builder {

      /**
       * @see IBMSmartCloudTemplateOptions#isMiniEphemeral
       */
      public static IBMSmartCloudTemplateOptions isMiniEphemeral(boolean isMiniEphemeral) {
         IBMSmartCloudTemplateOptions options = new IBMSmartCloudTemplateOptions();
         return options.isMiniEphemeral(isMiniEphemeral);
      }

      /**
       * @see IBMSmartCloudTemplateOptions#keyPair
       */
      public static IBMSmartCloudTemplateOptions keyPair(String keyPair) {
         IBMSmartCloudTemplateOptions options = new IBMSmartCloudTemplateOptions();
         return IBMSmartCloudTemplateOptions.class.cast(options.keyPair(keyPair));
      }

      /**
       * @see IBMSmartCloudTemplateOptions#noKeyPair
       */
      public static IBMSmartCloudTemplateOptions noKeyPair() {
         IBMSmartCloudTemplateOptions options = new IBMSmartCloudTemplateOptions();
         return IBMSmartCloudTemplateOptions.class.cast(options.noKeyPair());
      }


      // methods that only facilitate returning the correct object type
      /**
       * @see TemplateOptions#inboundPorts
       */
      public static IBMSmartCloudTemplateOptions inboundPorts(int... ports) {
         IBMSmartCloudTemplateOptions options = new IBMSmartCloudTemplateOptions();
         return IBMSmartCloudTemplateOptions.class.cast(options.inboundPorts(ports));
      }

      /**
       * @see TemplateOptions#port
       */
      public static IBMSmartCloudTemplateOptions blockOnPort(int port, int seconds) {
         IBMSmartCloudTemplateOptions options = new IBMSmartCloudTemplateOptions();
         return IBMSmartCloudTemplateOptions.class.cast(options.blockOnPort(port, seconds));
      }

      /**
       * @see TemplateOptions#runScript
       */
      public static IBMSmartCloudTemplateOptions runScript(byte[] script) {
         IBMSmartCloudTemplateOptions options = new IBMSmartCloudTemplateOptions();
         return IBMSmartCloudTemplateOptions.class.cast(options.runScript(script));
      }

      /**
       * @see TemplateOptions#installPrivateKey
       */
      public static IBMSmartCloudTemplateOptions installPrivateKey(String rsaKey) {
         IBMSmartCloudTemplateOptions options = new IBMSmartCloudTemplateOptions();
         return IBMSmartCloudTemplateOptions.class.cast(options.installPrivateKey(rsaKey));
      }

      /**
       * @see TemplateOptions#authorizePublicKey
       */
      public static IBMSmartCloudTemplateOptions authorizePublicKey(String rsaKey) {
         IBMSmartCloudTemplateOptions options = new IBMSmartCloudTemplateOptions();
         return IBMSmartCloudTemplateOptions.class.cast(options.authorizePublicKey(rsaKey));
      }

      /**
       * @see TemplateOptions#withDetails
       */
      public static IBMSmartCloudTemplateOptions withDetails() {
         IBMSmartCloudTemplateOptions options = new IBMSmartCloudTemplateOptions();
         return IBMSmartCloudTemplateOptions.class.cast(options.withMetadata());
      }

   }

   // methods that only facilitate returning the correct object type

   /**
    * {@inheritDoc}
    */
   @Override
   public IBMSmartCloudTemplateOptions blockOnPort(int port, int seconds) {
      return IBMSmartCloudTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public IBMSmartCloudTemplateOptions inboundPorts(int... ports) {
      return IBMSmartCloudTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public IBMSmartCloudTemplateOptions authorizePublicKey(String publicKey) {
      return IBMSmartCloudTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @Deprecated
   public IBMSmartCloudTemplateOptions authorizePublicKey(Payload publicKey) {
      return IBMSmartCloudTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public IBMSmartCloudTemplateOptions installPrivateKey(String privateKey) {
      return IBMSmartCloudTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @Deprecated
   public IBMSmartCloudTemplateOptions installPrivateKey(Payload privateKey) {
      return IBMSmartCloudTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public IBMSmartCloudTemplateOptions runScript(Payload script) {
      return IBMSmartCloudTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @Deprecated
   public IBMSmartCloudTemplateOptions runScript(byte[] script) {
      return IBMSmartCloudTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public IBMSmartCloudTemplateOptions withMetadata() {
      return IBMSmartCloudTemplateOptions.class.cast(super.withMetadata());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public IBMSmartCloudTemplateOptions blockUntilRunning(boolean blockUntilRunning) {
      return IBMSmartCloudTemplateOptions.class.cast(super.blockUntilRunning(blockUntilRunning));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public IBMSmartCloudTemplateOptions dontAuthorizePublicKey() {
      return IBMSmartCloudTemplateOptions.class.cast(super.dontAuthorizePublicKey());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public IBMSmartCloudTemplateOptions nameTask(String name) {
      return IBMSmartCloudTemplateOptions.class.cast(super.nameTask(name));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public IBMSmartCloudTemplateOptions runAsRoot(boolean runAsRoot) {
      return IBMSmartCloudTemplateOptions.class.cast(super.runAsRoot(runAsRoot));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public IBMSmartCloudTemplateOptions runScript(Statement script) {
      return IBMSmartCloudTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public IBMSmartCloudTemplateOptions overrideCredentialsWith(Credentials overridingCredentials) {
      return IBMSmartCloudTemplateOptions.class.cast(super.overrideCredentialsWith(overridingCredentials));
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

   /**
    * @see CreateInstanceOptions#isMiniEphemeral
    */
   public boolean isMiniEphemeral() {
      return isMiniEphemeral;
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
      IBMSmartCloudTemplateOptions other = (IBMSmartCloudTemplateOptions) obj;
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
      return "[keyPair=" + keyPair + ", noKeyPair=" + noKeyPair + ", isMiniEphemeral=" + isMiniEphemeral
               + ", inboundPorts=" + Arrays.toString(inboundPorts) + ", privateKey=" + (privateKey != null)
               + ", publicKey=" + (publicKey != null) + ", runScript=" + (script != null) + ", port:seconds=" + port
               + ":" + seconds + ", metadata/details: " + includeMetadata + "]";
   }

}
