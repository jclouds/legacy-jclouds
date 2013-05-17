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
package org.jclouds.cloudsigma.compute.options;

import java.util.Map;

import org.jclouds.cloudsigma.domain.AffinityType;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.scriptbuilder.domain.Statement;

public class CloudSigmaTemplateOptions extends TemplateOptions implements Cloneable {

   public static final CloudSigmaTemplateOptions NONE = new CloudSigmaTemplateOptions();

   private AffinityType diskDriveAffinity = AffinityType.HDD;

   public CloudSigmaTemplateOptions diskDriveAffinity(AffinityType diskDriveAffinity) {
      this.diskDriveAffinity = diskDriveAffinity;
      return this;
   }

   public AffinityType getDiskDriveAffinity() {
      return diskDriveAffinity;
   }

   @Override
   public CloudSigmaTemplateOptions clone() {
      CloudSigmaTemplateOptions options = new CloudSigmaTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof CloudSigmaTemplateOptions) {
         CloudSigmaTemplateOptions cTo = CloudSigmaTemplateOptions.class.cast(to);
         cTo.diskDriveAffinity(getDiskDriveAffinity());
      }
   }

   public static class Builder {

      /**
       * @see CloudSigmaTemplateOptions#diskDriveAffinity
       */
      public static CloudSigmaTemplateOptions diskDriveAffinity(AffinityType diskDriveAffinity) {
         CloudSigmaTemplateOptions options = new CloudSigmaTemplateOptions();
         return options.diskDriveAffinity(diskDriveAffinity);
      }

      // methods that only facilitate returning the correct object type

      /**
       * @see TemplateOptions#inboundPorts
       */
      public static CloudSigmaTemplateOptions inboundPorts(int... ports) {
         CloudSigmaTemplateOptions options = new CloudSigmaTemplateOptions();
         return CloudSigmaTemplateOptions.class.cast(options.inboundPorts(ports));
      }

      /**
       * @see TemplateOptions#port
       */
      public static CloudSigmaTemplateOptions blockOnPort(int port, int seconds) {
         CloudSigmaTemplateOptions options = new CloudSigmaTemplateOptions();
         return CloudSigmaTemplateOptions.class.cast(options.blockOnPort(port, seconds));
      }

      /**
       * @see TemplateOptions#installPrivateKey
       */
      public static CloudSigmaTemplateOptions installPrivateKey(String rsaKey) {
         CloudSigmaTemplateOptions options = new CloudSigmaTemplateOptions();
         return CloudSigmaTemplateOptions.class.cast(options.installPrivateKey(rsaKey));
      }

      /**
       * @see TemplateOptions#authorizePublicKey
       */
      public static CloudSigmaTemplateOptions authorizePublicKey(String rsaKey) {
         CloudSigmaTemplateOptions options = new CloudSigmaTemplateOptions();
         return CloudSigmaTemplateOptions.class.cast(options.authorizePublicKey(rsaKey));
      }

      /**
       * @see TemplateOptions#userMetadata(Map)
       */
      public static CloudSigmaTemplateOptions userMetadata(Map<String, String> userMetadata) {
         CloudSigmaTemplateOptions options = new CloudSigmaTemplateOptions();
         return CloudSigmaTemplateOptions.class.cast(options.userMetadata(userMetadata));
      }

      public static CloudSigmaTemplateOptions overrideLoginUser(String user) {
         CloudSigmaTemplateOptions options = new CloudSigmaTemplateOptions();
         return options.overrideLoginUser(user);
      }

      public static CloudSigmaTemplateOptions overrideLoginPassword(String password) {
         CloudSigmaTemplateOptions options = new CloudSigmaTemplateOptions();
         return options.overrideLoginPassword(password);
      }

      public static CloudSigmaTemplateOptions overrideLoginPrivateKey(String privateKey) {
         CloudSigmaTemplateOptions options = new CloudSigmaTemplateOptions();
         return options.overrideLoginPrivateKey(privateKey);
      }

      public static CloudSigmaTemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
         CloudSigmaTemplateOptions options = new CloudSigmaTemplateOptions();
         return options.overrideAuthenticateSudo(authenticateSudo);
      }

      public static CloudSigmaTemplateOptions overrideLoginCredentials(LoginCredentials credentials) {
         CloudSigmaTemplateOptions options = new CloudSigmaTemplateOptions();
         return options.overrideLoginCredentials(credentials);
      }
   }

   // methods that only facilitate returning the correct object type

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudSigmaTemplateOptions blockOnPort(int port, int seconds) {
      return CloudSigmaTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudSigmaTemplateOptions inboundPorts(int... ports) {
      return CloudSigmaTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudSigmaTemplateOptions authorizePublicKey(String publicKey) {
      return CloudSigmaTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudSigmaTemplateOptions installPrivateKey(String privateKey) {
      return CloudSigmaTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudSigmaTemplateOptions blockUntilRunning(boolean blockUntilRunning) {
      return CloudSigmaTemplateOptions.class.cast(super.blockUntilRunning(blockUntilRunning));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudSigmaTemplateOptions dontAuthorizePublicKey() {
      return CloudSigmaTemplateOptions.class.cast(super.dontAuthorizePublicKey());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudSigmaTemplateOptions nameTask(String name) {
      return CloudSigmaTemplateOptions.class.cast(super.nameTask(name));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudSigmaTemplateOptions runAsRoot(boolean runAsRoot) {
      return CloudSigmaTemplateOptions.class.cast(super.runAsRoot(runAsRoot));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudSigmaTemplateOptions runScript(Statement script) {
      return CloudSigmaTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudSigmaTemplateOptions overrideLoginCredentials(LoginCredentials overridingCredentials) {
      return CloudSigmaTemplateOptions.class.cast(super.overrideLoginCredentials(overridingCredentials));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudSigmaTemplateOptions overrideLoginPassword(String password) {
      return CloudSigmaTemplateOptions.class.cast(super.overrideLoginPassword(password));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudSigmaTemplateOptions overrideLoginPrivateKey(String privateKey) {
      return CloudSigmaTemplateOptions.class.cast(super.overrideLoginPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudSigmaTemplateOptions overrideLoginUser(String loginUser) {
      return CloudSigmaTemplateOptions.class.cast(super.overrideLoginUser(loginUser));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudSigmaTemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
      return CloudSigmaTemplateOptions.class.cast(super.overrideAuthenticateSudo(authenticateSudo));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudSigmaTemplateOptions userMetadata(Map<String, String> userMetadata) {
      return CloudSigmaTemplateOptions.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudSigmaTemplateOptions userMetadata(String key, String value) {
      return CloudSigmaTemplateOptions.class.cast(super.userMetadata(key, value));
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      CloudSigmaTemplateOptions that = (CloudSigmaTemplateOptions) o;

      if (diskDriveAffinity != that.diskDriveAffinity) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (diskDriveAffinity != null ? diskDriveAffinity.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "CloudSigmaTemplateOptions{" +
         "diskDriveAffinity=" + diskDriveAffinity +
         '}';
   }
}
