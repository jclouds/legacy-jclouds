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
package org.jclouds.compute.options;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.jclouds.domain.LoginCredentials;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

/**
 * Contains options supported in the {@code ComputeService#createNodesInGroup}
 * operation. <h2>
 * Usage</h2> The recommended way to instantiate a TemplateOptions object is to
 * statically import TemplateOptions.* and invoke a static creation method
 * followed by any/all desired instance mutators.
 * <p/>
 * <code>
 * import static org.jclouds.compute.options.TemplateOptions.Builder.*;
 * <p/>
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set<? extends NodeMetadata> set = client.createNodesInGroup(tag, 2, templateBuilder.build());
 * <code>
 * <p/>
 * Note that options can only be assigned to a builder once, so if assigning e.g. inboundPorts and tags,
 * that must be done in with mutators in a single call to options --
 * <code>templateBuilder.options(inboundPorts(22, 80, 8080, 443).tags("I love it!"))</code>
 * -- not as repeated calls to <code>options</code>.
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
      if (this.getUserMetadata().size() > 0)
         to.userMetadata(this.getUserMetadata());
      if (this.getTags().size() > 0)
         to.tags(getTags());
      if (!this.shouldBlockUntilRunning())
         to.blockUntilRunning(false);
      if (!this.shouldBlockOnComplete())
         to.blockOnComplete(false);
      if (this.getLoginUser() != null)
         to.overrideLoginUser(this.getLoginUser());
      if (this.getLoginPassword() != null)
         to.overrideLoginPassword(this.getLoginPassword());
      if (this.getLoginPrivateKey() != null)
         to.overrideLoginPrivateKey(this.getLoginPrivateKey());
      if (this.shouldAuthenticateSudo() != null)
         to.overrideAuthenticateSudo(this.shouldAuthenticateSudo());
      if (this.getTaskName() != null)
         to.nameTask(this.getTaskName());
   }

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

      /**
       * unsupported as objects of this class are immutable
       */
      @Override
      public TemplateOptions runScript(Statement script) {
         throw new IllegalArgumentException("script is immutable");
      }

      @Override
      public TemplateOptions dontAuthorizePublicKey() {
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
      public TemplateOptions wrapInInitScript(boolean wrapInInitScript) {
         throw new IllegalArgumentException("wrapInInitScript is immutable");
      }

      @Override
      public TemplateOptions blockOnComplete(boolean blockOnComplete) {
         throw new IllegalArgumentException("blockOnComplete is immutable");
      }

      @Override
      public TemplateOptions overrideLoginCredentials(LoginCredentials overridingCredentials) {
         throw new IllegalArgumentException("overridingCredentials is immutable");
      }

      @Override
      public TemplateOptions overrideLoginPassword(String password) {
         throw new IllegalArgumentException("password is immutable");
      }

      @Override
      public TemplateOptions overrideLoginPrivateKey(String privateKey) {
         throw new IllegalArgumentException("privateKey is immutable");
      }

      @Override
      public TemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
         throw new IllegalArgumentException("authenticateSudo is immutable");
      }

      @Override
      public String getLoginUser() {
         return delegate.getLoginUser();
      }

      @Override
      public Boolean shouldAuthenticateSudo() {
         return delegate.shouldAuthenticateSudo();
      }

      @Override
      public String getLoginPassword() {
         return delegate.getLoginPassword();
      }

      @Override
      public String getLoginPrivateKey() {
         return delegate.getLoginPrivateKey();
      }

      @Override
      public TemplateOptions overrideLoginUser(String loginUser) {
         throw new IllegalArgumentException("loginUser is immutable");
      }

      @Override
      public <T extends TemplateOptions> T as(Class<T> clazz) {
         return delegate.as(clazz);
      }

      @Override
      public TemplateOptions authorizePublicKey(String publicKey) {
         throw new IllegalArgumentException("publicKey is immutable");
      }

      /**
       * unsupported as objects of this class are immutable
       */
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
      public Set<String> getTags() {
         return delegate.getTags();
      }

      @Override
      public TemplateOptions tags(Iterable<String> tags) {
         throw new IllegalArgumentException("tags are immutable");
      }

      @Override
      public TemplateOptions userMetadata(Map<String, String> userMetadata) {
         throw new IllegalArgumentException("userMetadata is immutable");
      }

      @Override
      public TemplateOptions userMetadata(String key, String value) {
         throw new IllegalArgumentException("userMetadata is immutable");
      }

      @Override
      public Map<String, String> getUserMetadata() {
         return delegate.getUserMetadata();
      }

   }

   private static final Set<Integer> DEFAULT_INBOUND_PORTS = ImmutableSet.of(22);

   public static final TemplateOptions NONE = new ImmutableTemplateOptions(new TemplateOptions());
   
   protected Set<Integer> inboundPorts = DEFAULT_INBOUND_PORTS;

   protected Statement script;

   protected Set<String> tags = ImmutableSet.of();

   protected String privateKey;

   protected String publicKey;

   protected boolean blockUntilRunning = true;

   protected Map<String, String> userMetadata = Maps.newLinkedHashMap();

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      TemplateOptions that = TemplateOptions.class.cast(o);
      return super.equals(that) && equal(this.inboundPorts, that.inboundPorts) && equal(this.script, that.script)
            && equal(this.publicKey, that.publicKey) && equal(this.privateKey, that.privateKey)
            && equal(this.blockUntilRunning, that.blockUntilRunning) && equal(this.tags, that.tags)
            && equal(this.userMetadata, that.userMetadata);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), inboundPorts, script, publicKey, privateKey, blockUntilRunning, tags,
            userMetadata);
   }

   @Override
   public ToStringHelper string() {
      ToStringHelper toString = super.string();
      if (!DEFAULT_INBOUND_PORTS.equals(inboundPorts))
         toString.add("inboundPorts", inboundPorts);
      if (script != null)
         toString.add("scriptPresent", true);
      if (publicKey != null)
         toString.add("publicKeyPresent", true);
      if (privateKey != null)
         toString.add("privateKeyPresent", true);
      if (!blockUntilRunning)
         toString.add("blockUntilRunning", blockUntilRunning);
      if (tags.size() != 0)
         toString.add("tags", tags);
      if (userMetadata.size() != 0)
         toString.add("userMetadata", userMetadata);
      return toString;
   }

   public int[] getInboundPorts() {
      return Ints.toArray(inboundPorts);
   }

   public Statement getRunScript() {
      return script;
   }

   public Set<String> getTags() {
      return tags;
   }

   public String getPrivateKey() {
      return privateKey;
   }

   public String getPublicKey() {
      return publicKey;
   }

   /**
    * @see TemplateOptions#blockUntilRunning(boolean)
    */
   public boolean shouldBlockUntilRunning() {
      return blockUntilRunning;
   }

   public <T extends TemplateOptions> T as(Class<T> clazz) {
      return clazz.cast(this);
   }

   /**
    * This script will be executed as the root user upon system startup. This
    * script gets a prologue, so no #!/bin/bash required, path set up, etc
    * 
    */
   public TemplateOptions runScript(String script) {
      return runScript(Statements.exec(script));
   }
   
   /**
    * This script will be executed as the root user upon system startup. This
    * script gets a prologue, so no #!/bin/bash required, path set up, etc
    * 
    */
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
    * assigns tags to the created nodes
    */
   public TemplateOptions tags(Iterable<String> tags) {
      this.tags = ImmutableSet.copyOf(checkNotNull(tags, "tags"));
      return this;
   }

   /**
    * Opens the set of ports to public access.
    */
   public TemplateOptions inboundPorts(int... ports) {
      for (int port : ports)
         checkArgument(port > 0 && port < 65536, "port must be a positive integer < 65535");
      this.inboundPorts = ImmutableSet.copyOf(Ints.asList(ports));
      return this;
   }

   public static class Builder extends org.jclouds.compute.options.RunScriptOptions.Builder {

      public static TemplateOptions nameTask(String name) {
         TemplateOptions options = new TemplateOptions();
         return options.nameTask(name);
      }

      public static TemplateOptions overrideLoginUser(String user) {
         TemplateOptions options = new TemplateOptions();
         return options.overrideLoginUser(user);
      }

      public static TemplateOptions overrideLoginPassword(String password) {
         TemplateOptions options = new TemplateOptions();
         return options.overrideLoginPassword(password);
      }

      public static TemplateOptions overrideLoginPrivateKey(String privateKey) {
         TemplateOptions options = new TemplateOptions();
         return options.overrideLoginPrivateKey(privateKey);
      }

      public static TemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
         TemplateOptions options = new TemplateOptions();
         return options.overrideAuthenticateSudo(authenticateSudo);
      }

      public static TemplateOptions overrideLoginCredentials(LoginCredentials credentials) {
         TemplateOptions options = new TemplateOptions();
         return options.overrideLoginCredentials(credentials);
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
       * @see TemplateOptions#tags
       */
      public static TemplateOptions tags(Iterable<String> tags) {
         TemplateOptions options = new TemplateOptions();
         return options.tags(tags);
      }

      /**
       * @see TemplateOptions#blockUntilRunning(boolean)
       */
      public static TemplateOptions blockUntilRunning(boolean blockUntilRunning) {
         TemplateOptions options = new TemplateOptions();
         return options.blockUntilRunning(blockUntilRunning);
      }

      /**
       * @see TemplateOptions#runScript(Statement)
       */
      public static TemplateOptions runScript(Statement script) {
         TemplateOptions options = new TemplateOptions();
         return options.runScript(script);
      }
      
      /**
       * @see TemplateOptions#runScript(String)
       */
      public static TemplateOptions runScript(String script) {
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
         public static TemplateOptions installPrivateKey(String rsaKey) {
         TemplateOptions options = new TemplateOptions();
         return options.installPrivateKey(rsaKey);
      }

      /**
       * please use alternative that uses the {@link org.jclouds.io.Payload}
       * object
       * 
       * @see #authorizePublicKey(String)
       */
      public static TemplateOptions authorizePublicKey(String rsaKey) {
         TemplateOptions options = new TemplateOptions();
         return options.authorizePublicKey(rsaKey);
      }

      /**
       * @see TemplateOptions#userMetadata(Map)
       */
      public static TemplateOptions userMetadata(Map<String, String> userMetadata) {
         TemplateOptions options = new TemplateOptions();
         return options.userMetadata(userMetadata);
      }

      /**
       * @see TemplateOptions#userMetadata(String, String)
       */
      public static TemplateOptions userMetadata(String key, String value) {
         TemplateOptions options = new TemplateOptions();
         return options.userMetadata(key, value);
      }

      public static TemplateOptions blockOnComplete(boolean value) {
         TemplateOptions options = new TemplateOptions();
         return options.blockOnComplete(value);
      }

   }
   
   /**
    * <h4>Note</h4> As of version 1.1.0, this option is incompatible with
    * {@link TemplateOptions#runScript(Statement)} and
    * {@link RunScriptOptions#blockOnComplete(boolean)}, as all current
    * implementations utilize ssh in order to execute scripts.
    * 
    * @param blockUntilRunning
    *           (default true) whether to block until the nodes in this template
    *           are in {@link Status#RUNNING} state
    */
   public TemplateOptions blockUntilRunning(boolean blockUntilRunning) {
      this.blockUntilRunning = blockUntilRunning;
      if (!blockUntilRunning)
         port = seconds = -1;
      return this;
   }

   /**
    * 
    * @param userMetadata
    *           user-defined metadata to assign to this server
    */
   public TemplateOptions userMetadata(Map<String, String> userMetadata) {
      this.userMetadata.putAll(checkNotNull(userMetadata, "userMetadata"));
      return this;
   }

   /**
    * 
    * @param key
    *           key to place into the metadata map
    * @param value
    *           value to associate with that key
    */
   public TemplateOptions userMetadata(String key, String value) {
      this.userMetadata.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
      return this;
   }

   /**
    * @see #userMetadata(Map)
    */
   public Map<String, String> getUserMetadata() {
      return userMetadata;
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
   public TemplateOptions wrapInInitScript(boolean wrapInInitScript) {
      return TemplateOptions.class.cast(super.wrapInInitScript(wrapInInitScript));
   }

   @Override
   public TemplateOptions blockOnComplete(boolean blockOnComplete) {
      return TemplateOptions.class.cast(super.blockOnComplete(blockOnComplete));
   }

   @Override
   public TemplateOptions overrideLoginCredentials(LoginCredentials overridingCredentials) {
      return TemplateOptions.class.cast(super.overrideLoginCredentials(overridingCredentials));
   }

   @Override
   public TemplateOptions overrideLoginPassword(String password) {
      return TemplateOptions.class.cast(super.overrideLoginPassword(password));
   }

   @Override
   public TemplateOptions overrideLoginPrivateKey(String privateKey) {
      return TemplateOptions.class.cast(super.overrideLoginPrivateKey(privateKey));
   }

   @Override
   public TemplateOptions overrideLoginUser(String loginUser) {
      return TemplateOptions.class.cast(super.overrideLoginUser(loginUser));
   }

   @Override
   public TemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
      return TemplateOptions.class.cast(super.overrideAuthenticateSudo(authenticateSudo));
   }

}
