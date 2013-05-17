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
package org.jclouds.softlayer.compute.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jclouds.compute.options.TemplateOptions;

import com.google.common.net.InternetDomainName;

/**
 * Contains options supported by the
 * {@link ComputeService#createNodesInGroup(String, int, TemplateOptions)} and
 * {@link ComputeService#createNodesInGroup(String, int, TemplateOptions)}
 * operations on the <em>gogrid</em> provider.
 * 
 * <h2>Usage</h2> The recommended way to instantiate a
 * {@link SoftLayerTemplateOptions} object is to statically import
 * {@code SoftLayerTemplateOptions.*} and invoke a static creation method
 * followed by an instance mutator (if needed):
 * <p>
 * 
 * <pre>
 * import static org.jclouds.compute.options.SoftLayerTemplateOptions.Builder.*;
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set&lt;? extends NodeMetadata&gt; set = client.createNodesInGroup(tag, 2, templateBuilder.build());
 * </pre>
 * 
 * @author Adrian Cole
 */
public class SoftLayerTemplateOptions extends TemplateOptions implements Cloneable {

   protected String domainName = "jclouds.org";

   @Override
   public SoftLayerTemplateOptions clone() {
      SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof SoftLayerTemplateOptions) {
         SoftLayerTemplateOptions eTo = SoftLayerTemplateOptions.class.cast(to);
         eTo.domainName(domainName);
      }
   }

   /**
    * will replace the default domain used when ordering virtual guests. Note
    * this needs to contain a public suffix!
    * 
    * @see VirtualGuestClient#orderVirtualGuest
    * @see InternetDomainName#hasPublicSuffix
    */
   public TemplateOptions domainName(String domainName) {
      checkNotNull(domainName, "domainName was null");
      checkArgument(InternetDomainName.from(domainName).hasPublicSuffix(), "domainName %s has no public suffix",
            domainName);
      this.domainName = domainName;
      return this;
   }

   public String getDomainName() {
      return domainName;
   }

   public static final SoftLayerTemplateOptions NONE = new SoftLayerTemplateOptions();

   public static class Builder {

      /**
       * @see #domainName
       */
      public static SoftLayerTemplateOptions domainName(String domainName) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.domainName(domainName));
      }

      // methods that only facilitate returning the correct object type

      /**
       * @see TemplateOptions#inboundPorts(int...)
       */
      public static SoftLayerTemplateOptions inboundPorts(int... ports) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.inboundPorts(ports));
      }

      /**
       * @see TemplateOptions#blockOnPort(int, int)
       */
      public static SoftLayerTemplateOptions blockOnPort(int port, int seconds) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.blockOnPort(port, seconds));
      }

      /**
       * @see TemplateOptions#userMetadata(Map)
       */
      public static SoftLayerTemplateOptions userMetadata(Map<String, String> userMetadata) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.userMetadata(userMetadata));
      }

      /**
       * @see TemplateOptions#userMetadata(String, String)
       */
      public static SoftLayerTemplateOptions userMetadata(String key, String value) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.userMetadata(key, value));
      }
   }

   // methods that only facilitate returning the correct object type

   /**
    * @see TemplateOptions#blockOnPort(int, int)
    */
   @Override
   public SoftLayerTemplateOptions blockOnPort(int port, int seconds) {
      return SoftLayerTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * @see TemplateOptions#inboundPorts(int...)
    */
   @Override
   public SoftLayerTemplateOptions inboundPorts(int... ports) {
      return SoftLayerTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * @see TemplateOptions#authorizePublicKey(String)
    */
   @Override
   public SoftLayerTemplateOptions authorizePublicKey(String publicKey) {
      return SoftLayerTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * @see TemplateOptions#installPrivateKey(String)
    */
   @Override
   public SoftLayerTemplateOptions installPrivateKey(String privateKey) {
      return SoftLayerTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions userMetadata(Map<String, String> userMetadata) {
      return SoftLayerTemplateOptions.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions userMetadata(String key, String value) {
      return SoftLayerTemplateOptions.class.cast(super.userMetadata(key, value));
   }
}
