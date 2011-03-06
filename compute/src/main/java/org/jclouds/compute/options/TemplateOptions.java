/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.io.IOException;
import java.util.Arrays;

import org.jclouds.domain.Credentials;
import org.jclouds.io.Payload;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.util.Strings2;

import com.google.common.base.Throwables;

/**
 * Contains options supported in the {@code ComputeService#runNodesWithTag} operation. <h2>
 * Usage</h2> The recommended way to instantiate a TemplateOptions object is to statically import
 * TemplateOptions.* and invoke a static creation method followed by an instance mutator (if
 * needed):
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
public class TemplateOptions extends RunScriptOptions implements Cloneable {

   @Override
   public TemplateOptions clone() {
      TemplateOptions options = new TemplateOptions();
      copyTo(options);
      return options;
   }

   public void copyTo(TemplateOptions to) {
      if (!Arrays.equals(to.getInboundPorts(), this.getInboundPorts()))
         to.inboundPorts(this.getInboundPorts());
      if (this.getRunScript() != null)
         to.runScript(this.getRunScript());
      if (this.getPrivateKey() != null)
         to.installPrivateKey(this.getPrivateKey());
      if (this.getPublicKey() != null)
         to.authorizePublicKey(this.getPublicKey());
      if (this.getPort() != -1)
         to.blockOnPort(this.getPort(), this.getSeconds());
      if (this.isIncludeMetadata())
         to.withMetadata();
      if (!this.shouldBlockUntilRunning())
         to.blockUntilRunning(false);
      if (!this.shouldBlockOnComplete())
         to.blockOnComplete(false);
      if (this.getOverridingCredentials() != null)
         to.overrideCredentialsWith(this.getOverridingCredentials());
      if (this.getTaskName() != null)
         to.nameTask(this.getTaskName());
   }

   public static final TemplateOptions NONE = new ImmutableTemplateOptions(new TemplateOptions());

   public static class ImmutableTemplateOptions extends TemplateOptions {
      private final TemplateOptions delegate;

      @Override
      public TemplateOptions clone() {
         return delegate.clone();
      }

      @Override
      public String getTaskName() {
         return delegate.getTaskName();
      }

      @Override
      public int getPort() {
         return delegate.getPort();
      }

      @Override
      public int getSeconds() {
         return delegate.getSeconds();
      }

      @Override
      public Credentials getOverridingCredentials() {
         return delegate.getOverridingCredentials();
      }

      @Override
      public boolean shouldRunAsRoot() {
         return delegate.shouldRunAsRoot();
      }

      @Override
      public boolean shouldBlockOnComplete() {
         return delegate.shouldBlockOnComplete();
      }

      @Override
      public boolean shouldWrapInInitScript() {
         return delegate.shouldWrapInInitScript();
      }

      @Override
      public void copyTo(TemplateOptions to) {
         delegate.copyTo(to);
      }

      public ImmutableTemplateOptions(TemplateOptions delegate) {
         this.delegate = delegate;
      }

      @Override
      public String toString() {
         return delegate.toString();
      }

      @Override
      public TemplateOptions runScript(Payload script) {
         throw new IllegalArgumentException("script is immutable");
      }

      @Override
      public TemplateOptions runScript(Statement script) {
         throw new IllegalArgumentException("script is immutable");
      }

      @Override
      public TemplateOptions installPrivateKey(Payload privateKey) {
         throw new IllegalArgumentException("privateKey is immutable");
      }

      @Override
      public TemplateOptions dontAuthorizePublicKey() {
         throw new IllegalArgumentException("public key is immutable");
      }

      @Override
      @Deprecated
      public TemplateOptions authorizePublicKey(Payload publicKey) {
         throw new IllegalArgumentException("public key is immutable");
      }

      @Override
      public TemplateOptions blockOnPort(int port, int seconds) {
         throw new IllegalArgumentException("ports are immutable");
      }

      @Override
      public TemplateOptions nameTask(String name) {
         throw new IllegalArgumentException("task name is immutable");
      }

      @Override
      public TemplateOptions runAsRoot(boolean runAsRoot) {
         throw new IllegalArgumentException("runAsRoot is immutable");
      }

      @Override
      public TemplateOptions overrideCredentialsWith(Credentials overridingCredentials) {
         throw new IllegalArgumentException("credentials are immutable");
      }

      @Override
      public TemplateOptions overrideLoginUserWith(String loginUser) {
         throw new IllegalArgumentException("credentials are immutable");
      }

      @Override
      public TemplateOptions overrideLoginCredentialWith(String loginCredential) {
         throw new IllegalArgumentException("credentials are immutable");
      }

      @Override
      public TemplateOptions wrapInInitScript(boolean wrapInInitScript) {
         throw new IllegalArgumentException("wrapInInitScript is immutable");
      }

      @Override
      public TemplateOptions blockOnComplete(boolean blockOnComplete) {
         throw new IllegalArgumentException("blockOnComplete is immutable");
      }

      @Override
      public <T extends TemplateOptions> T as(Class<T> clazz) {
         return delegate.as(clazz);
      }

      @Override
      public TemplateOptions authorizePublicKey(String publicKey) {
         throw new IllegalArgumentException("publicKey is immutable");
      }

      @Override
      public TemplateOptions blockUntilRunning(boolean blockUntilRunning) {
         throw new IllegalArgumentException("blockUntilRunning is immutable");
      }

      @Override
      public int[] getInboundPorts() {
         return delegate.getInboundPorts();
      }

      @Override
      public String getPrivateKey() {
         return delegate.getPrivateKey();
      }

      @Override
      public String getPublicKey() {
         return delegate.getPublicKey();
      }

      @Override
      public Statement getRunScript() {
         return delegate.getRunScript();
      }

      @Override
      public boolean shouldBlockUntilRunning() {
         return delegate.shouldBlockUntilRunning();
      }

      @Override
      public TemplateOptions inboundPorts(int... ports) {
         throw new IllegalArgumentException("ports are immutable");
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
         throw new IllegalArgumentException("script is immutable");
      }

      @Override
      public TemplateOptions withMetadata() {
         throw new IllegalArgumentException("metadata is immutable");
      }

   }

   protected int[] inboundPorts = new int[] { 22 };

   protected Statement script;

   protected String privateKey;

   protected String publicKey;

   protected boolean includeMetadata;

   protected boolean blockUntilRunning = true;

   public int[] getInboundPorts() {
      return inboundPorts;
   }

   public Statement getRunScript() {
      return script;
   }

   public String getPrivateKey() {
      return privateKey;
   }

   public String getPublicKey() {
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
    * This script will be executed as the root user upon system startup. This script gets a
    * prologue, so no #!/bin/bash required, path set up, etc
    * <p/>
    * please use alternative that uses the {@link org.jclouds.scriptbuilder.domain.Statement} object
    * 
    * @see org.jclouds.io.Payloads
    */
   @Deprecated
   public TemplateOptions runScript(byte[] script) {
      return runScript(Statements.exec(new String(checkNotNull(script, "script"))));
   }

   /**
    * This script will be executed as the root user upon system startup. This script gets a
    * prologue, so no #!/bin/bash required, path set up, etc
    * 
    * @see org.jclouds.io.Payloads
    */
   public TemplateOptions runScript(Payload script) {
      try {
         return runScript(Statements.exec(Strings2.toStringAndClose(checkNotNull(script, "script").getInput())));
      } catch (IOException e) {
         Throwables.propagate(e);
         return this;
      }
   }

   public TemplateOptions runScript(Statement script) {
      this.script = checkNotNull(script, "script");
      return this;
   }

   /**
    * replaces the rsa ssh key used at login.
    */
   public TemplateOptions installPrivateKey(String privateKey) {
      checkArgument(checkNotNull(privateKey, "privateKey").startsWith("-----BEGIN RSA PRIVATE KEY-----"),
            "key should start with -----BEGIN RSA PRIVATE KEY-----");
      this.privateKey = privateKey;
      return this;
   }

   /**
    * replaces the rsa ssh key used at login.
    * <p/>
    * please use alternative that uses {@link java.lang.String}
    * 
    * @see org.jclouds.io.Payloads
    */
   @Deprecated
   public TemplateOptions installPrivateKey(Payload privateKey) {
      try {
         return installPrivateKey(Strings2.toStringAndClose(checkNotNull(privateKey, "privateKey").getInput()));
      } catch (IOException e) {
         Throwables.propagate(e);
         return this;
      }
   }

   public TemplateOptions dontAuthorizePublicKey() {
      this.publicKey = null;
      return this;
   }

   /**
    * authorize an rsa ssh key.
    */
   public TemplateOptions authorizePublicKey(String publicKey) {
      checkArgument(checkNotNull(publicKey, "publicKey").startsWith("ssh-rsa"), "key should start with ssh-rsa");
      this.publicKey = publicKey;
      return this;
   }

   /**
    * authorize an rsa ssh key.
    * <p/>
    * please use alternative that uses {@link java.lang.String}
    * 
    * @see org.jclouds.io.Payloads
    */
   @Deprecated
   public TemplateOptions authorizePublicKey(Payload publicKey) {
      try {
         return authorizePublicKey(Strings2.toStringAndClose(checkNotNull(publicKey, "publicKey").getInput()));
      } catch (IOException e) {
         Throwables.propagate(e);
         return this;
      }
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

   public static class Builder extends org.jclouds.compute.options.RunScriptOptions.Builder {

      public static TemplateOptions nameTask(String name) {
         TemplateOptions options = new TemplateOptions();
         return options.nameTask(name);
      }

      public static TemplateOptions overrideLoginUserWith(String user) {
         TemplateOptions options = new TemplateOptions();
         return options.overrideLoginUserWith(user);
      }

      public static TemplateOptions overrideLoginCredentialWith(String credential) {
         TemplateOptions options = new TemplateOptions();
         return options.overrideLoginCredentialWith(credential);
      }

      public static TemplateOptions overrideCredentialsWith(Credentials credentials) {
         TemplateOptions options = new TemplateOptions();
         return options.overrideCredentialsWith(credentials);
      }

      public static TemplateOptions runAsRoot(boolean value) {
         TemplateOptions options = new TemplateOptions();
         return options.runAsRoot(value);
      }

      /**
       * @see TemplateOptions#blockOnPort
       */
      public static TemplateOptions blockOnPort(int port, int seconds) {
         TemplateOptions options = new TemplateOptions();
         return options.blockOnPort(port, seconds);
      }

      /**
       * @see TemplateOptions#inboundPorts
       */
      public static TemplateOptions inboundPorts(int... ports) {
         TemplateOptions options = new TemplateOptions();
         return options.inboundPorts(ports);
      }

      /**
       * @see TemplateOptions#blockUntilRunning
       */
      public static TemplateOptions blockUntilRunning(boolean blockUntilRunning) {
         TemplateOptions options = new TemplateOptions();
         return options.blockUntilRunning(blockUntilRunning);
      }

      /**
       * please use alternative that uses the {@link org.jclouds.io.Payload} object
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
       * @see TemplateOptions#runScript
       * @see org.jclouds.io.Payloads
       */
      public static TemplateOptions runScript(Statement script) {
         TemplateOptions options = new TemplateOptions();
         return options.runScript(script);
      }

      /**
       * please use alternative that uses the {@link org.jclouds.io.Payload} object
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
       * please use alternative that uses the {@link org.jclouds.io.Payload} object
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

      public static TemplateOptions blockOnComplete(boolean value) {
         TemplateOptions options = new TemplateOptions();
         return options.blockOnComplete(value);
      }

   }

   @Override
   public String toString() {
      return "[inboundPorts=" + Arrays.toString(inboundPorts) + ", privateKey=" + (privateKey != null) + ", publicKey="
            + (publicKey != null) + ", runScript=" + (script != null) + ", blockUntilRunning=" + blockUntilRunning
            + ", blockOnComplete=" + blockOnComplete + ", port:seconds=" + port + ":" + seconds
            + ", metadata/details: " + includeMetadata + "]";
   }

   public TemplateOptions blockUntilRunning(boolean blockUntilRunning) {
      this.blockUntilRunning = blockUntilRunning;
      if (!blockUntilRunning)
         port = seconds = -1;
      return this;
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

   @Override
   public TemplateOptions blockOnPort(int port, int seconds) {
      return TemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   @Override
   public TemplateOptions nameTask(String name) {
      return TemplateOptions.class.cast(super.nameTask(name));
   }

   @Override
   public TemplateOptions runAsRoot(boolean runAsRoot) {
      return TemplateOptions.class.cast(super.runAsRoot(runAsRoot));
   }

   @Override
   public TemplateOptions overrideCredentialsWith(Credentials overridingCredentials) {
      return TemplateOptions.class.cast(super.overrideCredentialsWith(overridingCredentials));
   }

   @Override
   public TemplateOptions overrideLoginUserWith(String loginUser) {
      return TemplateOptions.class.cast(super.overrideLoginUserWith(loginUser));
   }

   @Override
   public TemplateOptions overrideLoginCredentialWith(String loginCredential) {
      return TemplateOptions.class.cast(super.overrideLoginCredentialWith(loginCredential));
   }

   @Override
   public TemplateOptions wrapInInitScript(boolean wrapInInitScript) {
      return TemplateOptions.class.cast(super.wrapInInitScript(wrapInInitScript));
   }

   @Override
   public TemplateOptions blockOnComplete(boolean blockOnComplete) {
      return TemplateOptions.class.cast(super.blockOnComplete(blockOnComplete));
   }
}
