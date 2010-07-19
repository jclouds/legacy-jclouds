/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.compute.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.io.Payloads.newByteArrayPayload;
import static org.jclouds.io.Payloads.newStringPayload;

import java.util.Arrays;

import org.jclouds.io.Payload;

/**
 * Contains options supported in the {@code ComputeService#runNodesWithTag}
 * operation. <h2>
 * Usage</h2> The recommended way to instantiate a TemplateOptions object is to
 * statically import TemplateOptions.* and invoke a static creation method
 * followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.compute.options.TemplateOptions.Builder.*;
 * <p/>
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set<? extends NodeMetadata> set = client.runNodesWithTag(tag, 2, templateBuilder.build());
 * <code>
 * 
 * @author Adrian Cole
 */
public class TemplateOptions {

   public static final TemplateOptions NONE = new ImmutableTemplateOptions(new TemplateOptions());

   public static class ImmutableTemplateOptions extends TemplateOptions {
      private final TemplateOptions delegate;

      public ImmutableTemplateOptions(TemplateOptions delegate) {
         this.delegate = delegate;
      }

      @Override
      public String toString() {
         return delegate.toString();
      }

      @Override
      public <T extends TemplateOptions> T as(Class<T> clazz) {
         return delegate.as(clazz);
      }

      @Override
      public TemplateOptions authorizePublicKey(String publicKey) {
         throw new IllegalArgumentException("authorizePublicKey is immutable");
      }

      @Override
      public TemplateOptions blockUntilRunning(boolean blockUntilRunning) {
         throw new IllegalArgumentException("blockUntilRunning is immutable");
      }

      @Override
      public TemplateOptions blockOnPort(int port, int seconds) {
         throw new IllegalArgumentException("port, seconds are immutable");
      }

      @Override
      public int[] getInboundPorts() {
         return delegate.getInboundPorts();
      }

      @Override
      public int getPort() {
         return delegate.getPort();
      }

      @Override
      public Payload getPrivateKey() {
         return delegate.getPrivateKey();
      }

      @Override
      public Payload getPublicKey() {
         return delegate.getPublicKey();
      }

      @Override
      public Payload getRunScript() {
         return delegate.getRunScript();
      }

      @Override
      public int getSeconds() {
         return delegate.getSeconds();
      }

      @Override
      public boolean shouldBlockUntilRunning() {
         return delegate.shouldBlockUntilRunning();
      }

      @Override
      public TemplateOptions inboundPorts(int... ports) {
         throw new IllegalArgumentException("ports is immutable");
      }

      @Override
      public TemplateOptions installPrivateKey(String privateKey) {
         throw new IllegalArgumentException("privateKey is immutable");
      }

      @Override
      public boolean isIncludeMetadata() {
         return delegate.isIncludeMetadata();
      }

      @Override
      public TemplateOptions runScript(byte[] script) {
         throw new IllegalArgumentException("withMetadata is immutable");
      }

      @Override
      public TemplateOptions withMetadata() {
         throw new IllegalArgumentException("withMetadata is immutable");
      }

   }

   protected int[] inboundPorts = new int[] { 22 };

   protected Payload script;

   protected Payload privateKey;

   protected Payload publicKey;

   protected int port = -1;

   protected int seconds = -1;

   protected boolean includeMetadata;

   protected boolean blockUntilRunning = true;

   public int getPort() {
      return port;
   }

   public int getSeconds() {
      return seconds;
   }

   public int[] getInboundPorts() {
      return inboundPorts;
   }

   public Payload getRunScript() {
      return script;
   }

   public Payload getPrivateKey() {
      return privateKey;
   }

   public Payload getPublicKey() {
      return publicKey;
   }

   public boolean isIncludeMetadata() {
      return includeMetadata;
   }

   public boolean shouldBlockUntilRunning() {
      return blockUntilRunning;
   }

   public <T extends TemplateOptions> T as(Class<T> clazz) {
      return clazz.cast(this);
   }

   /**
    * When the node is started, wait until the following port is active
    */
   public TemplateOptions blockOnPort(int port, int seconds) {
      checkArgument(port > 0 && port < 65536, "port must be a positive integer < 65535");
      checkArgument(seconds > 0, "seconds must be a positive integer");
      this.port = port;
      this.seconds = seconds;
      return this;
   }

   /**
    * This script will be executed as the root user upon system startup. This
    * script gets a prologue, so no #!/bin/bash required, path set up, etc
    * <p/>
    * please use alternative that uses the {@link org.jclouds.io.Payload} object
    * 
    * @see org.jclouds.io.Payloads
    */
   @Deprecated
   public TemplateOptions runScript(byte[] script) {
      return runScript(newByteArrayPayload(checkNotNull(script, "script")));
   }

   /**
    * This script will be executed as the root user upon system startup. This
    * script gets a prologue, so no #!/bin/bash required, path set up, etc
    * 
    * @see org.jclouds.io.Payloads
    */
   public TemplateOptions runScript(Payload script) {
      checkArgument(
            checkNotNull(checkNotNull(script, "script").getContentLength(), "script.contentLength") <= 16 * 1024,
            "script cannot be larger than 16kb");
      this.script = script;
      return this;
   }

   /**
    * replaces the rsa ssh key used at login.
    * <p/>
    * please use alternative that uses the {@link org.jclouds.io.Payload} object
    * 
    * @see org.jclouds.io.Payloads
    */
   @Deprecated
   public TemplateOptions installPrivateKey(String privateKey) {
      checkArgument(checkNotNull(privateKey, "privateKey").startsWith("-----BEGIN RSA PRIVATE KEY-----"),
            "key should start with -----BEGIN RSA PRIVATE KEY-----");
      Payload payload = newStringPayload(privateKey);
      payload.setContentType("text/plain");
      return installPrivateKey(payload);
   }

   /**
    * replaces the rsa ssh key used at login.
    * 
    * @see org.jclouds.io.Payloads
    */
   public TemplateOptions installPrivateKey(Payload privateKey) {
      this.privateKey = checkNotNull(privateKey, "privateKey");
      return this;
   }

   public TemplateOptions dontAuthorizePublicKey() {
      this.publicKey = null;
      return this;
   }

   /**
    * if true, return when node(s) are NODE_RUNNING, if false, return as soon as
    * the server is provisioned.
    * <p/>
    * default is true
    */
   public TemplateOptions blockUntilRunning(boolean blockUntilRunning) {
      this.blockUntilRunning = blockUntilRunning;
      if (!blockUntilRunning)
         port = seconds = -1;
      return this;
   }

   /**
    * authorize an rsa ssh key.
    * <p/>
    * please use alternative that uses the {@link org.jclouds.io.Payload} object
    * 
    * @see org.jclouds.io.Payloads
    */
   @Deprecated
   public TemplateOptions authorizePublicKey(String publicKey) {
      checkArgument(checkNotNull(publicKey, "publicKey").startsWith("ssh-rsa"), "key should start with ssh-rsa");
      Payload payload = newStringPayload(publicKey);
      payload.setContentType("text/plain");
      return authorizePublicKey(payload);
   }

   /**
    * authorize an rsa ssh key.
    * 
    * @see org.jclouds.io.Payloads
    */
   public TemplateOptions authorizePublicKey(Payload publicKey) {
      this.publicKey = checkNotNull(publicKey, "publicKey");
      return this;
   }

   /**
    * Opens the set of ports to public access.
    */
   public TemplateOptions inboundPorts(int... ports) {
      for (int port : ports)
         checkArgument(port > 0 && port < 65536, "port must be a positive integer < 65535");
      this.inboundPorts = ports;
      return this;
   }

   public TemplateOptions withMetadata() {
      this.includeMetadata = true;
      return this;
   }

   public static class Builder {

      /**
       * @see TemplateOptions#inboundPorts
       */
      public static TemplateOptions inboundPorts(int... ports) {
         TemplateOptions options = new TemplateOptions();
         return options.inboundPorts(ports);
      }

      /**
       * @see TemplateOptions#port
       */
      public static TemplateOptions blockOnPort(int port, int seconds) {
         TemplateOptions options = new TemplateOptions();
         return options.blockOnPort(port, seconds);
      }

      /**
       * @see TemplateOptions#blockUntilRunning
       */
      public static TemplateOptions blockUntilRunning(boolean blockUntilRunning) {
         TemplateOptions options = new TemplateOptions();
         return options.blockUntilRunning(blockUntilRunning);
      }

      /**
       * please use alternative that uses the {@link org.jclouds.io.Payload}
       * object
       * 
       * @see org.jclouds.io.Payloads
       * @see #runScript(Payload)
       */
      @Deprecated
      public static TemplateOptions runScript(byte[] script) {
         TemplateOptions options = new TemplateOptions();
         return options.runScript(script);
      }

      /**
       * @see TemplateOptions#runScript
       * @see org.jclouds.io.Payloads
       */
      public static TemplateOptions runScript(Payload script) {
         TemplateOptions options = new TemplateOptions();
         return options.runScript(script);
      }

      /**
       * please use alternative that uses the {@link org.jclouds.io.Payload}
       * object
       * 
       * @see org.jclouds.io.Payloads
       * @see #installPrivateKey(Payload)
       */
      @Deprecated
      public static TemplateOptions installPrivateKey(String rsaKey) {
         TemplateOptions options = new TemplateOptions();
         return options.installPrivateKey(rsaKey);
      }

      /**
       * @see TemplateOptions#installPrivateKey
       * @see org.jclouds.io.Payloads
       */
      public static TemplateOptions installPrivateKey(Payload rsaKey) {
         TemplateOptions options = new TemplateOptions();
         return options.installPrivateKey(rsaKey);
      }

      /**
       * please use alternative that uses the {@link org.jclouds.io.Payload}
       * object
       * 
       * @see org.jclouds.io.Payloads
       * @see #authorizePublicKey(Payload)
       */
      @Deprecated
      public static TemplateOptions authorizePublicKey(String rsaKey) {
         TemplateOptions options = new TemplateOptions();
         return options.authorizePublicKey(rsaKey);
      }

      /**
       * @see TemplateOptions#authorizePublicKey(Payload)
       * @see org.jclouds.io.Payloads
       */
      public static TemplateOptions authorizePublicKey(Payload rsaKey) {
         TemplateOptions options = new TemplateOptions();
         return options.authorizePublicKey(rsaKey);
      }

      public static TemplateOptions withDetails() {
         TemplateOptions options = new TemplateOptions();
         return options.withMetadata();
      }

   }

   @Override
   public String toString() {
      return "TemplateOptions [inboundPorts=" + Arrays.toString(inboundPorts) + ", privateKey=" + (privateKey != null)
            + ", publicKey=" + (publicKey != null) + ", runScript=" + (script != null) + ", blockUntilRunning="
            + blockUntilRunning + ", port:seconds=" + port + ":" + seconds + ", metadata/details: " + includeMetadata
            + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (blockUntilRunning ? 1231 : 1237);
      result = prime * result + Arrays.hashCode(inboundPorts);
      result = prime * result + (includeMetadata ? 1231 : 1237);
      result = prime * result + port;
      result = prime * result + ((privateKey == null) ? 0 : privateKey.hashCode());
      result = prime * result + ((publicKey == null) ? 0 : publicKey.hashCode());
      result = prime * result + ((script == null) ? 0 : script.hashCode());
      result = prime * result + seconds;
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      TemplateOptions other = (TemplateOptions) obj;
      if (blockUntilRunning != other.blockUntilRunning)
         return false;
      if (!Arrays.equals(inboundPorts, other.inboundPorts))
         return false;
      if (includeMetadata != other.includeMetadata)
         return false;
      if (port != other.port)
         return false;
      if (privateKey == null) {
         if (other.privateKey != null)
            return false;
      } else if (!privateKey.equals(other.privateKey))
         return false;
      if (publicKey == null) {
         if (other.publicKey != null)
            return false;
      } else if (!publicKey.equals(other.publicKey))
         return false;
      if (script == null) {
         if (other.script != null)
            return false;
      } else if (!script.equals(other.script))
         return false;
      if (seconds != other.seconds)
         return false;
      return true;
   }
}
