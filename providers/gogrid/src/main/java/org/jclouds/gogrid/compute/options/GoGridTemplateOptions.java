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
package org.jclouds.gogrid.compute.options;

import java.util.Map;

import org.jclouds.compute.options.TemplateOptions;

/**
 * Contains options supported by the
 * {@link ComputeService#createNodesInGroup(String, int, TemplateOptions)} and
 * {@link ComputeService#createNodesInGroup(String, int, TemplateOptions)}
 * operations on the <em>gogrid</em> provider.
 * 
 * <h2>Usage</h2> The recommended way to instantiate a
 * {@link GoGridTemplateOptions} object is to statically import
 * {@code GoGridTemplateOptions.*} and invoke a static creation method followed
 * by an instance mutator (if needed):
 * <p>
 * 
 * <pre>
 * import static org.jclouds.compute.options.GoGridTemplateOptions.Builder.*;
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set&lt;? extends NodeMetadata&gt; set = client.createNodesInGroup(tag, 2, templateBuilder.build());
 * </pre>
 * 
 * TODO add GoGrid specific options
 * 
 * @author Adrian Cole
 * @author Andrew Kennedy
 */
public class GoGridTemplateOptions extends TemplateOptions implements Cloneable {
   @Override
   public GoGridTemplateOptions clone() {
      GoGridTemplateOptions options = new GoGridTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof GoGridTemplateOptions) {
         @SuppressWarnings("unused")
         GoGridTemplateOptions eTo = GoGridTemplateOptions.class.cast(to);
      }
   }

   public static final GoGridTemplateOptions NONE = new GoGridTemplateOptions();

   public static class Builder {
      // methods that only facilitate returning the correct object type

      /**
       * @see TemplateOptions#inboundPorts(int...)
       */
      public static GoGridTemplateOptions inboundPorts(int... ports) {
         GoGridTemplateOptions options = new GoGridTemplateOptions();
         return GoGridTemplateOptions.class.cast(options.inboundPorts(ports));
      }

      /**
       * @see TemplateOptions#blockOnPort(int, int)
       */
      public static GoGridTemplateOptions blockOnPort(int port, int seconds) {
         GoGridTemplateOptions options = new GoGridTemplateOptions();
         return GoGridTemplateOptions.class.cast(options.blockOnPort(port, seconds));
      }

      /**
       * @see TemplateOptions#userMetadata(Map)
       */
      public static GoGridTemplateOptions userMetadata(Map<String, String> userMetadata) {
         GoGridTemplateOptions options = new GoGridTemplateOptions();
         return GoGridTemplateOptions.class.cast(options.userMetadata(userMetadata));
      }

      /**
       * @see TemplateOptions#userMetadata(String, String)
       */
      public static GoGridTemplateOptions userMetadata(String key, String value) {
         GoGridTemplateOptions options = new GoGridTemplateOptions();
         return GoGridTemplateOptions.class.cast(options.userMetadata(key, value));
      }
   }

   // methods that only facilitate returning the correct object type

   /**
    * @see TemplateOptions#blockOnPort(int, int)
    */
   @Override
   public GoGridTemplateOptions blockOnPort(int port, int seconds) {
      return GoGridTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * @see TemplateOptions#inboundPorts(int...)
    */
   @Override
   public GoGridTemplateOptions inboundPorts(int... ports) {
      return GoGridTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * @see TemplateOptions#authorizePublicKey(String)
    */
   @Override
   public GoGridTemplateOptions authorizePublicKey(String publicKey) {
      return GoGridTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * @see TemplateOptions#installPrivateKey(String)
    */
   @Override
   public GoGridTemplateOptions installPrivateKey(String privateKey) {
      return GoGridTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoGridTemplateOptions userMetadata(Map<String, String> userMetadata) {
      return GoGridTemplateOptions.class.cast(super.userMetadata(userMetadata));
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public GoGridTemplateOptions userMetadata(String key, String value) {
      return GoGridTemplateOptions.class.cast(super.userMetadata(key, value));
   }
}
