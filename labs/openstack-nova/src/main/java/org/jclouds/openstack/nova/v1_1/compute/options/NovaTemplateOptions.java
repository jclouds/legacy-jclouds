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
package org.jclouds.openstack.nova.v1_1.compute.options;

import java.util.Map;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.io.Payload;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Objects;

/**
 * Contains options supported in the {@code ComputeService#runNode} operation on
 * the "openstack-nova" provider.
 * <h2>Usage</h2> The recommended way to instantiate a NovaTemplateOptions object is
 * to statically import NovaTemplateOptions.* and invoke a static creation method
 * followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.aws.ec2.compute.options.NovaTemplateOptions.Builder.*;
 * <p/>
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set<? extends NodeMetadata> set = client.createNodesInGroup(tag, 2, templateBuilder.build());
 * <code>
 *
 * @author Adam Lowe
 */
public class NovaTemplateOptions extends TemplateOptions implements Cloneable {
   @Override
   public NovaTemplateOptions clone() {
      NovaTemplateOptions options = new NovaTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof NovaTemplateOptions) {
         NovaTemplateOptions eTo = NovaTemplateOptions.class.cast(to);
         eTo.autoAssignFloatingIp(isAutoAssignFloatingIp());
      }
   }

   private boolean autoAssignFloatingIp = false;

   public static final NovaTemplateOptions NONE = new NovaTemplateOptions();

   /**
    * @see #isAutoAssignFloatingIp()
    */
   public NovaTemplateOptions autoAssignFloatingIp(boolean enable) {
      this.autoAssignFloatingIp = enable;
      return this;
   }
   
   /**
    * @return true if auto assignment of a floating ip to each vm is enabled
    */
   public boolean isAutoAssignFloatingIp() {
      return autoAssignFloatingIp;
   }

   public static class Builder {

      /**
       * @see NovaTemplateOptions#isAutoAssignFloatingIp()
       */
      public static NovaTemplateOptions autoAssignFloatingIp(boolean enable) {
         return new NovaTemplateOptions().autoAssignFloatingIp(enable);
      }

      // methods that only facilitate returning the correct object type

      /**
       * @see TemplateOptions#inboundPorts
       */
      public static NovaTemplateOptions inboundPorts(int... ports) {
         NovaTemplateOptions options = new NovaTemplateOptions();
         return NovaTemplateOptions.class.cast(options.inboundPorts(ports));
      }

      /**
       * @see TemplateOptions#port
       */
      public static NovaTemplateOptions blockOnPort(int port, int seconds) {
         NovaTemplateOptions options = new NovaTemplateOptions();
         return NovaTemplateOptions.class.cast(options.blockOnPort(port, seconds));
      }

      /**
       * @see TemplateOptions#installPrivateKey
       */
      public static NovaTemplateOptions installPrivateKey(String rsaKey) {
         NovaTemplateOptions options = new NovaTemplateOptions();
         return NovaTemplateOptions.class.cast(options.installPrivateKey(rsaKey));
      }

      /**
       * @see TemplateOptions#authorizePublicKey
       */
      public static NovaTemplateOptions authorizePublicKey(String rsaKey) {
         NovaTemplateOptions options = new NovaTemplateOptions();
         return NovaTemplateOptions.class.cast(options.authorizePublicKey(rsaKey));
      }

      /**
       * @see TemplateOptions#userMetadata
       */
      public static NovaTemplateOptions userMetadata(Map<String, String> userMetadata) {
         NovaTemplateOptions options = new NovaTemplateOptions();
         return NovaTemplateOptions.class.cast(options.userMetadata(userMetadata));
      }

      /**
       * @see TemplateOptions#overrideLoginUser
       */
      public static NovaTemplateOptions overrideLoginUser(String user) {
         NovaTemplateOptions options = new NovaTemplateOptions();
         return options.overrideLoginUser(user);
      }

      /**
       * @see TemplateOptions#overrideLoginPassword
       */
      public static NovaTemplateOptions overrideLoginPassword(String password) {
         NovaTemplateOptions options = new NovaTemplateOptions();
         return options.overrideLoginPassword(password);
      }
      
      /**
       * @see TemplateOptions#overrideLoginPrivateKey
       */
      public static NovaTemplateOptions overrideLoginPrivateKey(String privateKey) {
         NovaTemplateOptions options = new NovaTemplateOptions();
         return options.overrideLoginPrivateKey(privateKey);
      }
      
      /**
       * @see TemplateOptions#overrideAuthenticateSudo
       */
      public static NovaTemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
         NovaTemplateOptions options = new NovaTemplateOptions();
         return options.overrideAuthenticateSudo(authenticateSudo);
      }

      /**
       * @see TemplateOptions#overrideLoginCredentials
       */
      public static NovaTemplateOptions overrideLoginCredentials(LoginCredentials credentials) {
         NovaTemplateOptions options = new NovaTemplateOptions();
         return options.overrideLoginCredentials(credentials);
      }
   }

   // methods that only facilitate returning the correct object type

   /**
    * {@inheritDoc}
    */
   @Override
   public NovaTemplateOptions blockOnPort(int port, int seconds) {
      return NovaTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NovaTemplateOptions inboundPorts(int... ports) {
      return NovaTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NovaTemplateOptions authorizePublicKey(String publicKey) {
      return NovaTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NovaTemplateOptions installPrivateKey(String privateKey) {
      return NovaTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Deprecated
   @Override
   public NovaTemplateOptions runScript(Payload script) {
      return NovaTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NovaTemplateOptions blockUntilRunning(boolean blockUntilRunning) {
      return NovaTemplateOptions.class.cast(super.blockUntilRunning(blockUntilRunning));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NovaTemplateOptions dontAuthorizePublicKey() {
      return NovaTemplateOptions.class.cast(super.dontAuthorizePublicKey());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NovaTemplateOptions nameTask(String name) {
      return NovaTemplateOptions.class.cast(super.nameTask(name));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NovaTemplateOptions runAsRoot(boolean runAsRoot) {
      return NovaTemplateOptions.class.cast(super.runAsRoot(runAsRoot));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NovaTemplateOptions runScript(Statement script) {
      return NovaTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Deprecated
   @Override
   public NovaTemplateOptions overrideCredentialsWith(Credentials overridingCredentials) {
      return NovaTemplateOptions.class.cast(super.overrideCredentialsWith(overridingCredentials));
   }

   /**
    * {@inheritDoc}
    */
   @Deprecated
   @Override
   public NovaTemplateOptions overrideLoginUserWith(String loginUser) {
      return NovaTemplateOptions.class.cast(super.overrideLoginUserWith(loginUser));
   }

   /**
    * {@inheritDoc}
    */
   @Deprecated
   @Override
   public NovaTemplateOptions overrideLoginCredentialWith(String loginCredential) {
      return NovaTemplateOptions.class.cast(super.overrideLoginCredentialWith(loginCredential));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NovaTemplateOptions overrideLoginCredentials(LoginCredentials overridingCredentials) {
      return NovaTemplateOptions.class.cast(super.overrideLoginCredentials(overridingCredentials));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NovaTemplateOptions overrideLoginPassword(String password) {
      return NovaTemplateOptions.class.cast(super.overrideLoginPassword(password));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NovaTemplateOptions overrideLoginPrivateKey(String privateKey) {
      return NovaTemplateOptions.class.cast(super.overrideLoginPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NovaTemplateOptions overrideLoginUser(String loginUser) {
      return NovaTemplateOptions.class.cast(super.overrideLoginUser(loginUser));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NovaTemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
      return NovaTemplateOptions.class.cast(super.overrideAuthenticateSudo(authenticateSudo));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NovaTemplateOptions userMetadata(Map<String, String> userMetadata) {
      return NovaTemplateOptions.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NovaTemplateOptions userMetadata(String key, String value) {
      return NovaTemplateOptions.class.cast(super.userMetadata(key, value));
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), autoAssignFloatingIp);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;

      NovaTemplateOptions other = (NovaTemplateOptions) obj;
      return Objects.equal(autoAssignFloatingIp, other.autoAssignFloatingIp);
   }

   @Override
   public String toString() {
      return String.format("[autoAssignFloatingIp=%s]", autoAssignFloatingIp);
   }
}
