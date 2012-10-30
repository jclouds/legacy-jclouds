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
package org.jclouds.glesys.compute.options;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import org.jclouds.compute.options.TemplateOptions;

import com.google.common.net.InetAddresses;

/**
 * Contains options supported by the
 * {@link ComputeService#createNodesInGroup(String, int, TemplateOptions)} and
 * {@link ComputeService#createNodesInGroup(String, int, TemplateOptions)} operations on the
 * <em>glesys</em> provider.
 * 
 * <h2>Usage</h2> The recommended way to instantiate a {@link GleSYSTemplateOptions} object is to
 * statically import {@code GleSYSTemplateOptions.*} and invoke a static creation method followed by
 * an instance mutator (if needed):
 * <p>
 * 
 * <pre>
 * import static org.jclouds.compute.options.GleSYSTemplateOptions.Builder.*;
 * ComputeService api = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set&lt;? extends NodeMetadata&gt; set = api.createNodesInGroup(tag, 2, templateBuilder.build());
 * </pre>
 * 
 * @author Adrian Cole
 */
public class GleSYSTemplateOptions extends TemplateOptions implements Cloneable {

   protected String ip = "any";

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
         GleSYSTemplateOptions eTo = GleSYSTemplateOptions.class.cast(to);
         eTo.ip(ip);
      }
   }

   /**
    * 
    * @see ServerApi#createWithHostnameAndRootPassword
    * @see InetAddresses#isInetAddress
    */
   public TemplateOptions ip(String ip) {
      if (ip != null)
         checkArgument("any".equals(ip) || InetAddresses.isInetAddress(ip), "ip %s is not valid", ip);
      this.ip = ip;
      return this;
   }

   public String getIp() {
      return ip;
   }

   public static final GleSYSTemplateOptions NONE = new GleSYSTemplateOptions();

   public static class Builder {

      /**
       * @see #ip
       */
      public static GleSYSTemplateOptions ip(String ip) {
         GleSYSTemplateOptions options = new GleSYSTemplateOptions();
         return GleSYSTemplateOptions.class.cast(options.ip(ip));
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
}
