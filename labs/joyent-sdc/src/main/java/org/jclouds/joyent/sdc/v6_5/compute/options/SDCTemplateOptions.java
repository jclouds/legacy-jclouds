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
package org.jclouds.joyent.sdc.v6_5.compute.options;

import static com.google.common.base.Objects.equal;

import java.util.Map;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Contains options supported in the {@code ComputeService#runNode} operation on the
 * "joyent-sdc" provider. <h2>Usage</h2> The recommended way to instantiate a
 * SDCTemplateOptions object is to statically import SDCTemplateOptions.* and invoke a static
 * creation method followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.aws.ec2.compute.options.SDCTemplateOptions.Builder.*;
 * <p/>
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set<? extends NodeMetadata> set = client.createNodesInGroup(tag, 2, templateBuilder.build());
 * <code>
 * 
 * @author Adrian Cole
 */
public class SDCTemplateOptions extends TemplateOptions implements Cloneable {
   @Override
   public SDCTemplateOptions clone() {
      SDCTemplateOptions options = new SDCTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof SDCTemplateOptions) {
         SDCTemplateOptions eTo = SDCTemplateOptions.class.cast(to);
         eTo.generateKey(shouldGenerateKey());
      }
   }

   protected boolean generateKey = false;

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      SDCTemplateOptions that = SDCTemplateOptions.class.cast(o);
      return super.equals(that) && equal(this.generateKey, that.generateKey);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), generateKey);
   }

   @Override
   public ToStringHelper string() {
      ToStringHelper toString = super.string();
      if (generateKey)
         toString.add("generateKey", generateKey);
      return toString;
   }

   /**
    * @see #shouldGenerateKey()
    */
   public SDCTemplateOptions generateKey(boolean enable) {
      this.generateKey = enable;
      return this;
   }
   
   /**
    *
    * @return true if auto generation of keys is enabled
    */
   public boolean shouldGenerateKey() {
      return generateKey;
   }
   
   public static class Builder {

      /**
       * @see SDCTemplateOptions#shouldGenerateKey() 
       */
      public static SDCTemplateOptions generateKey(boolean enable) {
         return new SDCTemplateOptions().generateKey(enable);
      }
      
      // methods that only facilitate returning the correct object type

      /**
       * @see TemplateOptions#inboundPorts
       */
      public static SDCTemplateOptions inboundPorts(int... ports) {
         SDCTemplateOptions options = new SDCTemplateOptions();
         return SDCTemplateOptions.class.cast(options.inboundPorts(ports));
      }

      /**
       * @see TemplateOptions#port
       */
      public static SDCTemplateOptions blockOnPort(int port, int seconds) {
         SDCTemplateOptions options = new SDCTemplateOptions();
         return SDCTemplateOptions.class.cast(options.blockOnPort(port, seconds));
      }

      /**
       * @see TemplateOptions#installPrivateKey
       */
      public static SDCTemplateOptions installPrivateKey(String rsaKey) {
         SDCTemplateOptions options = new SDCTemplateOptions();
         return SDCTemplateOptions.class.cast(options.installPrivateKey(rsaKey));
      }

      /**
       * @see TemplateOptions#authorizePublicKey
       */
      public static SDCTemplateOptions authorizePublicKey(String rsaKey) {
         SDCTemplateOptions options = new SDCTemplateOptions();
         return SDCTemplateOptions.class.cast(options.authorizePublicKey(rsaKey));
      }

      /**
       * @see TemplateOptions#userMetadata
       */
      public static SDCTemplateOptions userMetadata(Map<String, String> userMetadata) {
         SDCTemplateOptions options = new SDCTemplateOptions();
         return SDCTemplateOptions.class.cast(options.userMetadata(userMetadata));
      }

      /**
       * @see TemplateOptions#overrideLoginUser
       */
      public static SDCTemplateOptions overrideLoginUser(String user) {
         SDCTemplateOptions options = new SDCTemplateOptions();
         return options.overrideLoginUser(user);
      }

      /**
       * @see TemplateOptions#overrideLoginPassword
       */
      public static SDCTemplateOptions overrideLoginPassword(String password) {
         SDCTemplateOptions options = new SDCTemplateOptions();
         return options.overrideLoginPassword(password);
      }

      /**
       * @see TemplateOptions#overrideLoginPrivateKey
       */
      public static SDCTemplateOptions overrideLoginPrivateKey(String privateKey) {
         SDCTemplateOptions options = new SDCTemplateOptions();
         return options.overrideLoginPrivateKey(privateKey);
      }

      /**
       * @see TemplateOptions#overrideAuthenticateSudo
       */
      public static SDCTemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
         SDCTemplateOptions options = new SDCTemplateOptions();
         return options.overrideAuthenticateSudo(authenticateSudo);
      }

      /**
       * @see TemplateOptions#overrideLoginCredentials
       */
      public static SDCTemplateOptions overrideLoginCredentials(LoginCredentials credentials) {
         SDCTemplateOptions options = new SDCTemplateOptions();
         return options.overrideLoginCredentials(credentials);
      }
      
      /**
       * @see TemplateOptions#blockUntilRunning
       */
      public static SDCTemplateOptions blockUntilRunning(boolean blockUntilRunning) {
         SDCTemplateOptions options = new SDCTemplateOptions();
         return options.blockUntilRunning(blockUntilRunning);
      }

   }

   // methods that only facilitate returning the correct object type

   /**
    * {@inheritDoc}
    */
   @Override
   public SDCTemplateOptions blockOnPort(int port, int seconds) {
      return SDCTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SDCTemplateOptions inboundPorts(int... ports) {
      return SDCTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SDCTemplateOptions authorizePublicKey(String publicKey) {
      return SDCTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SDCTemplateOptions installPrivateKey(String privateKey) {
      return SDCTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SDCTemplateOptions blockUntilRunning(boolean blockUntilRunning) {
      return SDCTemplateOptions.class.cast(super.blockUntilRunning(blockUntilRunning));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SDCTemplateOptions dontAuthorizePublicKey() {
      return SDCTemplateOptions.class.cast(super.dontAuthorizePublicKey());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SDCTemplateOptions nameTask(String name) {
      return SDCTemplateOptions.class.cast(super.nameTask(name));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SDCTemplateOptions runAsRoot(boolean runAsRoot) {
      return SDCTemplateOptions.class.cast(super.runAsRoot(runAsRoot));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SDCTemplateOptions runScript(Statement script) {
      return SDCTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SDCTemplateOptions overrideLoginCredentials(LoginCredentials overridingCredentials) {
      return SDCTemplateOptions.class.cast(super.overrideLoginCredentials(overridingCredentials));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SDCTemplateOptions overrideLoginPassword(String password) {
      return SDCTemplateOptions.class.cast(super.overrideLoginPassword(password));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SDCTemplateOptions overrideLoginPrivateKey(String privateKey) {
      return SDCTemplateOptions.class.cast(super.overrideLoginPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SDCTemplateOptions overrideLoginUser(String loginUser) {
      return SDCTemplateOptions.class.cast(super.overrideLoginUser(loginUser));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SDCTemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
      return SDCTemplateOptions.class.cast(super.overrideAuthenticateSudo(authenticateSudo));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SDCTemplateOptions userMetadata(Map<String, String> userMetadata) {
      return SDCTemplateOptions.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SDCTemplateOptions userMetadata(String key, String value) {
      return SDCTemplateOptions.class.cast(super.userMetadata(key, value));
   }

}
