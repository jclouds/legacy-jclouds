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
package org.jclouds.openstack.nova.v2_0.compute.options;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.util.Preconditions2;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * Contains options supported in the {@code ComputeService#runNode} operation on the
 * "openstack-nova" provider. <h2>Usage</h2> The recommended way to instantiate a
 * NovaTemplateOptions object is to statically import NovaTemplateOptions.* and invoke a static
 * creation method followed by an instance mutator (if needed):
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
         eTo.autoAssignFloatingIp(shouldAutoAssignFloatingIp());
         eTo.securityGroupNames(getSecurityGroupNames());
         eTo.generateKeyPair(shouldGenerateKeyPair());
         eTo.keyPairName(getKeyPairName());
         if (getUserData() != null) {
             eTo.userData(getUserData());
         }
      }
   }

   protected boolean autoAssignFloatingIp = false;
   protected Set<String> securityGroupNames = ImmutableSet.of();
   protected boolean generateKeyPair = false;
   protected String keyPairName;
   protected byte[] userData;

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      NovaTemplateOptions that = NovaTemplateOptions.class.cast(o);
      return super.equals(that) && equal(this.autoAssignFloatingIp, that.autoAssignFloatingIp)
            && equal(this.securityGroupNames, that.securityGroupNames)
            && equal(this.generateKeyPair, that.generateKeyPair)
            && equal(this.keyPairName, that.keyPairName)
            && Arrays.equals(this.userData, that.userData);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), autoAssignFloatingIp, securityGroupNames, generateKeyPair, keyPairName, userData);
   }

   @Override
   public ToStringHelper string() {
      ToStringHelper toString = super.string();
      if (!autoAssignFloatingIp)
         toString.add("autoAssignFloatingIp", autoAssignFloatingIp);
      if (securityGroupNames.size() != 0)
         toString.add("securityGroupNames", securityGroupNames);
      if (generateKeyPair)
         toString.add("generateKeyPair", generateKeyPair);
      toString.add("keyPairName", keyPairName);
      toString.add("userData", userData);
      return toString;
   }

   public static final NovaTemplateOptions NONE = new NovaTemplateOptions();

   /**
    * @see #shouldAutoAssignFloatingIp()
    */
   public NovaTemplateOptions autoAssignFloatingIp(boolean enable) {
      this.autoAssignFloatingIp = enable;
      return this;
   }

   /**
    * @see #shouldGenerateKeyPair()
    */
   public NovaTemplateOptions generateKeyPair(boolean enable) {
      this.generateKeyPair = enable;
      return this;
   }

   /**
    * @see #shouldGenerateKeyPair()
    */
   public NovaTemplateOptions keyPairName(String keyPairName) {
      this.keyPairName = keyPairName;
      return this;
   }

   /**
    *
    * @see org.jclouds.openstack.nova.v2_0.options.CreateServerOptions#getSecurityGroupNames
    */
   public NovaTemplateOptions securityGroupNames(String... securityGroupNames) {
      return securityGroupNames(ImmutableSet.copyOf(checkNotNull(securityGroupNames, "securityGroupNames")));
   }

   /**
    * @see org.jclouds.openstack.nova.v2_0.options.CreateServerOptions#getSecurityGroupNames
    */
   public NovaTemplateOptions securityGroupNames(Iterable<String> securityGroupNames) {
      for (String groupName : checkNotNull(securityGroupNames, "securityGroupNames"))
         Preconditions2.checkNotEmpty(groupName, "all security groups must be non-empty");
      this.securityGroupNames = ImmutableSet.copyOf(securityGroupNames);
      return this;
   }

   /**
    * <h3>Note</h3>
    * 
    * This requires that {@link NovaClient#getFloatingIPExtensionForZone(String)} to return
    * {@link Optional#isPresent present}
    * 
    * @return true if auto assignment of a floating ip to each vm is enabled
    */
   public boolean shouldAutoAssignFloatingIp() {
      return autoAssignFloatingIp;
   }

   /**
    * Specifies the keypair used to run instances with
    * @return the keypair to be used
    */
   public String getKeyPairName() {
      return keyPairName;
   }
   
   /**
    * <h3>Note</h3>
    *
    * This requires that {@link NovaClient#getKeyPairExtensionForZone(String)} to return
    * {@link Optional#isPresent present}
    *
    * @return true if auto generation of keypairs is enabled
    */
   public boolean shouldGenerateKeyPair() {
      return generateKeyPair;
   }
   
   /**
    * @see org.jclouds.openstack.nova.v2_0.options.CreateServerOptions#getSecurityGroupNames
    */
   public Set<String> getSecurityGroupNames() {
      return securityGroupNames;
   }

   public byte[] getUserData() {
       return userData;
    }

   public static class Builder {

      /**
       * @see NovaTemplateOptions#shouldAutoAssignFloatingIp()
       */
      public static NovaTemplateOptions autoAssignFloatingIp(boolean enable) {
         return new NovaTemplateOptions().autoAssignFloatingIp(enable);
      }

      /**
       * @see NovaTemplateOptions#shouldGenerateKeyPair() 
       */
      public static NovaTemplateOptions generateKeyPair(boolean enable) {
         return new NovaTemplateOptions().generateKeyPair(enable);
      }

      /**
       * @see NovaTemplateOptions#getKeyPairName() 
       */
      public static NovaTemplateOptions keyPairName(String keyPairName) {
         return new NovaTemplateOptions().keyPairName(keyPairName);
      }
      
      /**
       * @see org.jclouds.openstack.nova.v2_0.options.CreateServerOptions#getSecurityGroupNames
       */
      public static NovaTemplateOptions securityGroupNames(String... groupNames) {
         NovaTemplateOptions options = new NovaTemplateOptions();
         return NovaTemplateOptions.class.cast(options.securityGroupNames(groupNames));
      }

      /**
       * @see org.jclouds.openstack.nova.v2_0.options.CreateServerOptions#getSecurityGroupNames
       */
      public static NovaTemplateOptions securityGroupNames(Iterable<String> groupNames) {
         NovaTemplateOptions options = new NovaTemplateOptions();
         return NovaTemplateOptions.class.cast(options.securityGroupNames(groupNames));
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
      
      /**
       * @see TemplateOptions#blockUntilRunning
       */
      public static NovaTemplateOptions blockUntilRunning(boolean blockUntilRunning) {
         NovaTemplateOptions options = new NovaTemplateOptions();
         return options.blockUntilRunning(blockUntilRunning);
      }
      
      /**
       * @see NovaTemplateOptions#userData
       */
      public static NovaTemplateOptions userData(byte[] userData) {
         NovaTemplateOptions options = new NovaTemplateOptions();
         return NovaTemplateOptions.class.cast(options.userData(userData));
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

   /**
    * User data as bytes (not base64-encoded)
    */
   public NovaTemplateOptions userData(byte[] userData) {
       // This limit may not be needed for nova
       checkArgument(checkNotNull(userData, "userData").length <= 16 * 1024,
               "userData cannot be larger than 16kb");
       this.userData = userData;
       return this;
   }

}
