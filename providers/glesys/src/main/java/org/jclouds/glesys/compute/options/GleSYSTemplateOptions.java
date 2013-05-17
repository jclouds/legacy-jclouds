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
package org.jclouds.glesys.compute.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.glesys.domain.ServerSpec;

import com.google.common.base.Objects.ToStringHelper;
import com.google.common.net.InetAddresses;

/**
 * Contains options supported by the
 * {@link ComputeService#createNodesInGroup(String, int, TemplateOptions)} and
 * {@link ComputeService#createNodesInGroup(String, int, TemplateOptions)}
 * operations on the <em>glesys</em> provider.
 * 
 * <h2>Usage</h2> The recommended way to instantiate a
 * {@link GleSYSTemplateOptions} object is to statically import
 * {@code GleSYSTemplateOptions.*} and invoke a static creation method followed
 * by an instance mutator (if needed):
 * <p>
 * 
 * <pre>
 * import static org.jclouds.compute.options.GleSYSTemplateOptions.Builder.*;
 * ComputeService api = // get connection
 * templateBuilder.options(rootPassword("caQu5rou"));
 * Set&lt;? extends NodeMetadata&gt; set = api.createNodesInGroup(tag, 2, templateBuilder.build());
 * </pre>
 * 
 * @author Adrian Cole
 */
public class GleSYSTemplateOptions extends TemplateOptions implements Cloneable {

   /**
    * The IP address to assign to the new node instance. If set to "
    * <code>any</code>" the node will be automatically assigned a free IP
    * address.
    */
   protected String ip = "any";
   /**
    * The password to set for the root user on the created server instance. If
    * left unspecified, a random password will be assigned.
    */
   protected String rootPassword = null;

   /** The monthly data transfer limit (in GB) for the server. */
   protected int transferGB = 50;

   @Override
   public GleSYSTemplateOptions clone() {
      GleSYSTemplateOptions options = new GleSYSTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof GleSYSTemplateOptions) {
         GleSYSTemplateOptions copy = GleSYSTemplateOptions.class.cast(to);
         copy.ip(ip);
         copy.rootPassword(rootPassword);
         copy.transferGB(transferGB);
      }
   }

   /**
    * Sets the IP address to assign to the new server instance. If set to "
    * <code>any</code>" the server will be automatically assigned a free IP
    * address.
    * 
    * @see ServerApi#createWithHostnameAndRootPassword
    * @see InetAddresses#isInetAddress
    */
   public GleSYSTemplateOptions ip(String ip) {
      checkNotNull(ip);
      checkArgument("any".equals(ip) || InetAddresses.isInetAddress(ip), "ip %s is not valid", ip);      
      this.ip = ip;
      return this;
   }

   /**
    * @return the IP address to assign to the new server instance.
    */
   public String getIp() {
      return ip;
   }

   /**
    * Sets the password for the root user on the created server instance. If
    * left unspecified, a random password will be assigned.
    * 
    * @see ServerApi#createWithHostnameAndRootPassword
    */
   public GleSYSTemplateOptions rootPassword(String rootPassword) {
      checkNotNull(rootPassword, "root password cannot be null");
      this.rootPassword = rootPassword;
      return this;
   }

   /**
    * @return the password set for the root user or <code>null</code> if none is
    *         set (and a random password will be assigned).
    */
   public String getRootPassword() {
      return rootPassword;
   }

   /**
    * @return <code>true</code> if a root password has been specified.
    */
   public boolean hasRootPassword() {
      return rootPassword != null;
   }

   /**
    * Sets the monthly data transfer limit (in GB) for the server.
    * 
    * @see ServerSpec#getTransferGB()
    */
   public GleSYSTemplateOptions transferGB(int transferGB) {
      checkArgument(transferGB >= 0, "transferGB value must be >= 0", transferGB);
      this.transferGB = transferGB;
      return this;
   }

   /**
    * @return the monthly data transfer limit (in GB) for the server.
    */
   public int getTransferGB() {
      return transferGB;
   }

   public static class Builder {

      /**
       * @see GleSYSTemplateOptions#ip
       */
      public static GleSYSTemplateOptions ip(String ip) {
         GleSYSTemplateOptions options = new GleSYSTemplateOptions();
         return GleSYSTemplateOptions.class.cast(options.ip(ip));
      }

      /**
       * @see GleSYSTemplateOptions#rootPassword
       */
      public static GleSYSTemplateOptions rootPassword(String rootPassword) {
         GleSYSTemplateOptions options = new GleSYSTemplateOptions();
         return GleSYSTemplateOptions.class.cast(options.rootPassword(rootPassword));
      }

      /**
       * @see GleSYSTemplateOptions#transferGB
       */
      public static GleSYSTemplateOptions transferGB(int transferGB) {
         GleSYSTemplateOptions options = new GleSYSTemplateOptions();
         return GleSYSTemplateOptions.class.cast(options.transferGB(transferGB));
      }

      // methods that only facilitate returning the correct object type

      /**
       * @see TemplateOptions#inboundPorts(int...)
       */
      public static GleSYSTemplateOptions inboundPorts(int... ports) {
         GleSYSTemplateOptions options = new GleSYSTemplateOptions();
         return GleSYSTemplateOptions.class.cast(options.inboundPorts(ports));
      }

      /**
       * @see TemplateOptions#blockOnPort(int, int)
       */
      public static GleSYSTemplateOptions blockOnPort(int port, int seconds) {
         GleSYSTemplateOptions options = new GleSYSTemplateOptions();
         return GleSYSTemplateOptions.class.cast(options.blockOnPort(port, seconds));
      }

      /**
       * @see TemplateOptions#userMetadata(Map)
       */
      public static GleSYSTemplateOptions userMetadata(Map<String, String> userMetadata) {
         GleSYSTemplateOptions options = new GleSYSTemplateOptions();
         return GleSYSTemplateOptions.class.cast(options.userMetadata(userMetadata));
      }

      /**
       * @see TemplateOptions#userMetadata(String, String)
       */
      public static GleSYSTemplateOptions userMetadata(String key, String value) {
         GleSYSTemplateOptions options = new GleSYSTemplateOptions();
         return GleSYSTemplateOptions.class.cast(options.userMetadata(key, value));
      }
   }

   // methods that only facilitate returning the correct object type

   /**
    * @see TemplateOptions#blockOnPort(int, int)
    */
   @Override
   public GleSYSTemplateOptions blockOnPort(int port, int seconds) {
      return GleSYSTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * @see TemplateOptions#inboundPorts(int...)
    */
   @Override
   public GleSYSTemplateOptions inboundPorts(int... ports) {
      return GleSYSTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * @see TemplateOptions#authorizePublicKey(String)
    */
   @Override
   public GleSYSTemplateOptions authorizePublicKey(String publicKey) {
      return GleSYSTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * @see TemplateOptions#installPrivateKey(String)
    */
   @Override
   public GleSYSTemplateOptions installPrivateKey(String privateKey) {
      return GleSYSTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GleSYSTemplateOptions userMetadata(Map<String, String> userMetadata) {
      return GleSYSTemplateOptions.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GleSYSTemplateOptions userMetadata(String key, String value) {
      return GleSYSTemplateOptions.class.cast(super.userMetadata(key, value));
   }

   @Override
   public ToStringHelper string() {
      ToStringHelper stringHelper = super.string();

      stringHelper.add("transferGB", this.transferGB);
      stringHelper.add("ip", this.ip);
      if (this.hasRootPassword()) {
         stringHelper.add("rootPasswordPresent", true);
      }

      return stringHelper;
   }

}
