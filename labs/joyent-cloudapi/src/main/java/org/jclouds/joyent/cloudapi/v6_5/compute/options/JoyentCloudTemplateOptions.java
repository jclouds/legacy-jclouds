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
package org.jclouds.joyent.cloudapi.v6_5.compute.options;

import static com.google.common.base.Objects.equal;

import java.util.Map;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Contains options supported in the {@code ComputeService#createNodesInGroup} operation on the
 * "joyent-cloudapi" provider. 
 * 
 * <h2>Usage</h2> The recommended way to instantiate a
 * JoyentCloudTemplateOptions object is to statically import JoyentCloudTemplateOptions.Builder.* and
 * invoke a static creation method followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.joyent.cloudapi.v6_5.compute.options.JoyentCloudTemplateOptions.Builder.*;
 * <p/>
 * ComputeService api = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set<? extends NodeMetadata> set = api.createNodesInGroup(tag, 2, templateBuilder.build());
 * <code>
 * 
 * @author Adrian Cole
 */
public class JoyentCloudTemplateOptions extends TemplateOptions implements Cloneable {
   @Override
   public JoyentCloudTemplateOptions clone() {
      JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof JoyentCloudTemplateOptions) {
         JoyentCloudTemplateOptions eTo = JoyentCloudTemplateOptions.class.cast(to);
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
      JoyentCloudTemplateOptions that = JoyentCloudTemplateOptions.class.cast(o);
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
   public JoyentCloudTemplateOptions generateKey(boolean enable) {
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
       * @see JoyentCloudTemplateOptions#shouldGenerateKey() 
       */
      public static JoyentCloudTemplateOptions generateKey(boolean enable) {
         return new JoyentCloudTemplateOptions().generateKey(enable);
      }
      
      // methods that only facilitate returning the correct object type

      /**
       * @see TemplateOptions#inboundPorts
       */
      public static JoyentCloudTemplateOptions inboundPorts(int... ports) {
         JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
         return JoyentCloudTemplateOptions.class.cast(options.inboundPorts(ports));
      }

      /**
       * @see TemplateOptions#port
       */
      public static JoyentCloudTemplateOptions blockOnPort(int port, int seconds) {
         JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
         return JoyentCloudTemplateOptions.class.cast(options.blockOnPort(port, seconds));
      }

      /**
       * @see TemplateOptions#installPrivateKey
       */
      public static JoyentCloudTemplateOptions installPrivateKey(String rsaKey) {
         JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
         return JoyentCloudTemplateOptions.class.cast(options.installPrivateKey(rsaKey));
      }

      /**
       * @see TemplateOptions#authorizePublicKey
       */
      public static JoyentCloudTemplateOptions authorizePublicKey(String rsaKey) {
         JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
         return JoyentCloudTemplateOptions.class.cast(options.authorizePublicKey(rsaKey));
      }

      /**
       * @see TemplateOptions#userMetadata
       */
      public static JoyentCloudTemplateOptions userMetadata(Map<String, String> userMetadata) {
         JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
         return JoyentCloudTemplateOptions.class.cast(options.userMetadata(userMetadata));
      }

      /**
       * @see TemplateOptions#overrideLoginUser
       */
      public static JoyentCloudTemplateOptions overrideLoginUser(String user) {
         JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
         return options.overrideLoginUser(user);
      }

      /**
       * @see TemplateOptions#overrideLoginPassword
       */
      public static JoyentCloudTemplateOptions overrideLoginPassword(String password) {
         JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
         return options.overrideLoginPassword(password);
      }

      /**
       * @see TemplateOptions#overrideLoginPrivateKey
       */
      public static JoyentCloudTemplateOptions overrideLoginPrivateKey(String privateKey) {
         JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
         return options.overrideLoginPrivateKey(privateKey);
      }

      /**
       * @see TemplateOptions#overrideAuthenticateSudo
       */
      public static JoyentCloudTemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
         JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
         return options.overrideAuthenticateSudo(authenticateSudo);
      }

      /**
       * @see TemplateOptions#overrideLoginCredentials
       */
      public static JoyentCloudTemplateOptions overrideLoginCredentials(LoginCredentials credentials) {
         JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
         return options.overrideLoginCredentials(credentials);
      }
      
      /**
       * @see TemplateOptions#blockUntilRunning
       */
      public static JoyentCloudTemplateOptions blockUntilRunning(boolean blockUntilRunning) {
         JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
         return options.blockUntilRunning(blockUntilRunning);
      }

   }

   // methods that only facilitate returning the correct object type

   /**
    * {@inheritDoc}
    */
   @Override
   public JoyentCloudTemplateOptions blockOnPort(int port, int seconds) {
      return JoyentCloudTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JoyentCloudTemplateOptions inboundPorts(int... ports) {
      return JoyentCloudTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JoyentCloudTemplateOptions authorizePublicKey(String publicKey) {
      return JoyentCloudTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JoyentCloudTemplateOptions installPrivateKey(String privateKey) {
      return JoyentCloudTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JoyentCloudTemplateOptions blockUntilRunning(boolean blockUntilRunning) {
      return JoyentCloudTemplateOptions.class.cast(super.blockUntilRunning(blockUntilRunning));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JoyentCloudTemplateOptions dontAuthorizePublicKey() {
      return JoyentCloudTemplateOptions.class.cast(super.dontAuthorizePublicKey());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JoyentCloudTemplateOptions nameTask(String name) {
      return JoyentCloudTemplateOptions.class.cast(super.nameTask(name));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JoyentCloudTemplateOptions runAsRoot(boolean runAsRoot) {
      return JoyentCloudTemplateOptions.class.cast(super.runAsRoot(runAsRoot));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JoyentCloudTemplateOptions runScript(Statement script) {
      return JoyentCloudTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JoyentCloudTemplateOptions overrideLoginCredentials(LoginCredentials overridingCredentials) {
      return JoyentCloudTemplateOptions.class.cast(super.overrideLoginCredentials(overridingCredentials));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JoyentCloudTemplateOptions overrideLoginPassword(String password) {
      return JoyentCloudTemplateOptions.class.cast(super.overrideLoginPassword(password));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JoyentCloudTemplateOptions overrideLoginPrivateKey(String privateKey) {
      return JoyentCloudTemplateOptions.class.cast(super.overrideLoginPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JoyentCloudTemplateOptions overrideLoginUser(String loginUser) {
      return JoyentCloudTemplateOptions.class.cast(super.overrideLoginUser(loginUser));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JoyentCloudTemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
      return JoyentCloudTemplateOptions.class.cast(super.overrideAuthenticateSudo(authenticateSudo));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JoyentCloudTemplateOptions userMetadata(Map<String, String> userMetadata) {
      return JoyentCloudTemplateOptions.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JoyentCloudTemplateOptions userMetadata(String key, String value) {
      return JoyentCloudTemplateOptions.class.cast(super.userMetadata(key, value));
   }

}
