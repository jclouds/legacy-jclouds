/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.compute.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.emptyToNull;

import java.util.Map;

import org.jclouds.compute.options.TemplateOptions;

/**
 * Contains options supported in the {@code ComputeService#runNode} operation on
 * the "trmk-vcloudexpress" provider. <h2>
 * Usage</h2> The recommended way to instantiate a
 * TerremarkVCloudTemplateOptions object is to statically import
 * TerremarkVCloudTemplateOptions.* and invoke a static creation method followed
 * by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.vcloud.terremark.compute.options.TerremarkVCloudTemplateOptions.Builder.*;
 * <p/>
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set<? extends NodeMetadata> set = client.runNodesWithTag(tag, 2, templateBuilder.build());
 * <code>
 * 
 * @author Adrian Cole
 */
public class TerremarkVCloudTemplateOptions extends TemplateOptions implements Cloneable {
   @Override
   public TerremarkVCloudTemplateOptions clone() {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof TerremarkVCloudTemplateOptions) {
         TerremarkVCloudTemplateOptions eTo = TerremarkVCloudTemplateOptions.class.cast(to);
         if (noKeyPair)
            eTo.noKeyPair();
         if (keyPair != null)
            eTo.keyPair = keyPair;
      }
   }

   private String keyPair = null;
   private boolean noKeyPair;

   public static final TerremarkVCloudTemplateOptions NONE = new TerremarkVCloudTemplateOptions();

   /**
    * Specifies the keypair used to run instances with
    */
   public TerremarkVCloudTemplateOptions sshKeyFingerprint(String keyPair) {
      checkState(!noKeyPair, "you cannot specify both options keyPair and noKeyPair");
      this.keyPair = checkNotNull(emptyToNull(keyPair), "use noKeyPair option to request boot without a keypair");
      return this;
   }

   /**
    * Do not use a keypair on instances
    */
   public TerremarkVCloudTemplateOptions noKeyPair() {
      checkState(keyPair == null, "you cannot specify both options keyPair and noKeyPair");
      this.noKeyPair = true;
      return this;
   }

   public static class Builder {

      /**
       * @see TerremarkVCloudTemplateOptions#sshKeyFingerprint
       */
      public static TerremarkVCloudTemplateOptions sshKeyFingerprint(String keyPair) {
         TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
         return TerremarkVCloudTemplateOptions.class.cast(options.sshKeyFingerprint(keyPair));
      }

      /**
       * @see TerremarkVCloudTemplateOptions#noKeyPair
       */
      public static TerremarkVCloudTemplateOptions noKeyPair() {
         TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
         return TerremarkVCloudTemplateOptions.class.cast(options.noKeyPair());
      }

      // methods that only facilitate returning the correct object type
      /**
       * @see TemplateOptions#inboundPorts
       */
      public static TerremarkVCloudTemplateOptions inboundPorts(int... ports) {
         TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
         return TerremarkVCloudTemplateOptions.class.cast(options.inboundPorts(ports));
      }

      /**
       * @see TemplateOptions#port
       */
      public static TerremarkVCloudTemplateOptions blockOnPort(int port, int seconds) {
         TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
         return TerremarkVCloudTemplateOptions.class.cast(options.blockOnPort(port, seconds));
      }

      /**
       * @see TemplateOptions#blockUntilRunning
       */
      public static TerremarkVCloudTemplateOptions blockUntilRunning(boolean blockUntilRunning) {
         TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
         return TerremarkVCloudTemplateOptions.class.cast(options.blockUntilRunning(blockUntilRunning));
      }

      /**
       * @see TemplateOptions#installPrivateKey
       */
      public static TerremarkVCloudTemplateOptions installPrivateKey(String rsaKey) {
         TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
         return TerremarkVCloudTemplateOptions.class.cast(options.installPrivateKey(rsaKey));
      }

      /**
       * @see TemplateOptions#authorizePublicKey
       */
      public static TerremarkVCloudTemplateOptions authorizePublicKey(String rsaKey) {
         TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
         return TerremarkVCloudTemplateOptions.class.cast(options.authorizePublicKey(rsaKey));
      }

      /**
       * @see TemplateOptions#userMetadata(Map)
       */
      public static TerremarkVCloudTemplateOptions userMetadata(Map<String, String> userMetadata) {
         TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
         return TerremarkVCloudTemplateOptions.class.cast(options.userMetadata(userMetadata));
      }

      /**
       * @see TemplateOptions#userMetadata(String, String)
       */
      public static TerremarkVCloudTemplateOptions userMetadata(String key, String value) {
         TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
         return TerremarkVCloudTemplateOptions.class.cast(options.userMetadata(key, value));
      }
   }

   // methods that only facilitate returning the correct object type

   /**
    * @see TemplateOptions#blockOnPort
    */
   @Override
   public TerremarkVCloudTemplateOptions blockOnPort(int port, int seconds) {
      return TerremarkVCloudTemplateOptions.class.cast(super.blockOnPort(port, seconds));
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
   public TerremarkVCloudTemplateOptions inboundPorts(int... ports) {
      return TerremarkVCloudTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * @see TemplateOptions#authorizePublicKey(String)
    */
   @Override
   public TerremarkVCloudTemplateOptions authorizePublicKey(String publicKey) {
      return TerremarkVCloudTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * @see TemplateOptions#installPrivateKey(String)
    */
   @Override
   public TerremarkVCloudTemplateOptions installPrivateKey(String privateKey) {
      return TerremarkVCloudTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * @see TemplateOptions#userMetadata
    */
   public TerremarkVCloudTemplateOptions userMetadata(Map<String, String> userMetadata) {
      return TerremarkVCloudTemplateOptions.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TerremarkVCloudTemplateOptions userMetadata(String key, String value) {
      return TerremarkVCloudTemplateOptions.class.cast(super.userMetadata(key, value));
   }

   /**
    * @return keyPair to use when running the instance or null, to generate a
    *         keypair.
    */
   public String getSshKeyFingerprint() {
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
      TerremarkVCloudTemplateOptions other = (TerremarkVCloudTemplateOptions) obj;
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
      return "TerremarkVCloudTemplateOptions [keyPair=" + keyPair + ", noKeyPair=" + noKeyPair + ", inboundPorts="
            + inboundPorts + ", privateKey=" + (privateKey != null) + ", publicKey="
            + (publicKey != null) + ", runScript=" + (script != null) + ", port:seconds=" + port + ":" + seconds
            + ", userMetadata: " + userMetadata + "]";
   }

}
