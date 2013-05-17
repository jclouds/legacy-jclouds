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

import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Enables additional options for running a script.
 * 
 * @author Oleksiy Yarmula
 */
public class RunScriptOptions {

   /**
    * Default options. The default settings are:
    * <ul>
    * <li>override the credentials with ones supplied in call to
    * {@link org.jclouds.compute.ComputeService#runScriptOnNodesMatching}</li>
    * <li>run the script as root (versus running with current privileges)</li>
    * </ul>
    */
   public static final RunScriptOptions NONE = new ImmutableRunScriptOptions(new RunScriptOptions());

   public static class ImmutableRunScriptOptions extends RunScriptOptions {
      private final RunScriptOptions delegate;

      public ImmutableRunScriptOptions(RunScriptOptions delegate) {
         this.delegate = delegate;
      }

      @Override
      public String toString() {
         return delegate.toString();
      }

      @Override
      public boolean shouldRunAsRoot() {
         return delegate.shouldRunAsRoot();

      }

      @Override
      public RunScriptOptions runAsRoot(boolean runAsRoot) {
         throw new IllegalArgumentException("runAsRoot is immutable");
      }

      @Override
      public boolean shouldBlockOnComplete() {
         return delegate.shouldBlockOnComplete();

      }

      @Override
      public RunScriptOptions blockOnComplete(boolean blockOnComplete) {
         throw new IllegalArgumentException("blockOnComplete is immutable");
      }

      @Override
      public RunScriptOptions overrideLoginCredentials(LoginCredentials overridingCredentials) {
         throw new IllegalArgumentException("overridingCredentials is immutable");
      }

      @Override
      public RunScriptOptions overrideLoginPassword(String password) {
         throw new IllegalArgumentException("password is immutable");
      }

      @Override
      public RunScriptOptions overrideLoginPrivateKey(String privateKey) {
         throw new IllegalArgumentException("privateKey is immutable");
      }

      @Override
      public RunScriptOptions overrideAuthenticateSudo(boolean authenticateSudo) {
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
      public boolean shouldWrapInInitScript() {
         return delegate.shouldWrapInInitScript();
      }

      @Override
      public RunScriptOptions overrideLoginUser(String loginUser) {
         throw new IllegalArgumentException("loginUser is immutable");
      }
      
      @Override
      public RunScriptOptions wrapInInitScript(boolean wrapInInitScript) {
         throw new IllegalArgumentException("wrapInInitScript is immutable");
      }
      
      @Override
      public String getTaskName() {
         return delegate.getTaskName();
      }

      @Override
      public RunScriptOptions nameTask(String name) {
         throw new IllegalArgumentException("taskName is immutable");
      }

      @Override
      public RunScriptOptions blockOnPort(int port, int seconds) {
         throw new IllegalArgumentException("port, seconds are immutable");
      }

      @Override
      public int getPort() {
         return delegate.getPort();
      }

      @Override
      public int getSeconds() {
         return delegate.getSeconds();
      }
   }

   protected int port = -1;
   protected int seconds = -1;
   protected String taskName;
   protected boolean runAsRoot = true;
   protected boolean blockOnComplete = true;
   protected boolean wrapInInitScript = true;

   protected String loginUser;
   protected Boolean authenticateSudo;
   protected Optional<String> loginPassword;
   protected Optional<String> loginPrivateKey;

   public RunScriptOptions overrideLoginCredentials(LoginCredentials overridingCredentials) {
      checkNotNull(overridingCredentials, "overridingCredentials");
      this.loginUser = overridingCredentials.getUser();
      this.loginPassword = overridingCredentials.getOptionalPassword();
      this.loginPrivateKey = overridingCredentials.getOptionalPrivateKey();
      this.authenticateSudo = overridingCredentials.shouldAuthenticateSudo() ? true : null;
      return this;
   }
  

   public RunScriptOptions overrideLoginUser(String loginUser) {
      checkNotNull(loginUser, "loginUser");
      this.loginUser = loginUser;
      return this;
   }
  
   public RunScriptOptions overrideLoginPassword(String password) {
      checkNotNull(password, "password");
      this.loginPassword = Optional.of(password);
      return this;
   }

   public RunScriptOptions overrideLoginPrivateKey(String privateKey) {
      checkNotNull(privateKey, "privateKey");
      this.loginPrivateKey = Optional.of(privateKey);
      return this;
   }

   public RunScriptOptions overrideAuthenticateSudo(boolean authenticateSudo) {
      this.authenticateSudo = authenticateSudo;
      return this;
   }

   /**
    * @return What to call the task relating to this script; default
    *         {@code jclouds-script-timestamp} where timestamp is millis since
    *         epoch
    * 
    */
   public RunScriptOptions nameTask(String name) {
      this.taskName = name;
      return this;
   }

   public RunScriptOptions runAsRoot(boolean runAsRoot) {
      this.runAsRoot = runAsRoot;
      return this;
   }

   /**
    * default true
    * <p/>
    * 
    * @param wrapInInitScript
    *           if the command is long-running, use this option to ensure it is
    *           wrapInInitScripted properly. (ex. have jclouds wrap it an init
    *           script, nohup, etc)
    * @return
    */
   public RunScriptOptions wrapInInitScript(boolean wrapInInitScript) {
      this.wrapInInitScript = wrapInInitScript;
      return this;
   }

   /**
    * As of version 1.1.0, we cannot kick off a script unless a node is in
    * RUNNING state.
    * 
    * @param blockOnComplete
    *           (default true) false means kick off the script in the
    *           background, but don't wait for it to finish. (as of version
    *           1.1.0, implemented as nohup)
    */
   public RunScriptOptions blockOnComplete(boolean blockOnComplete) {
      this.blockOnComplete = blockOnComplete;
      return this;
   }

   /**
    * When the node is started, wait until the following port is active
    */
   public RunScriptOptions blockOnPort(int port, int seconds) {
      checkArgument(port > 0 && port < 65536, "port must be a positive integer < 65535");
      checkArgument(seconds > 0, "seconds must be a positive integer");
      this.port = port;
      this.seconds = seconds;
      return this;
   }

   public String getTaskName() {
      return taskName;
   }

   public int getPort() {
      return port;
   }

   public int getSeconds() {
      return seconds;
   }

   /**
    * 
    * @return the login user for
    *         {@link org.jclouds.compute.ComputeService#runScriptOnNode}. By
    *         default, null.
    */
   @Nullable
   public String getLoginUser() {
      return loginUser;
   }

   /**
    * 
    * @return Whether the login user should authenticate sudo during
    *         {@link org.jclouds.compute.ComputeService#runScriptOnNode}. By
    *         default, null.
    */
   @Nullable
   public Boolean shouldAuthenticateSudo() {
      return authenticateSudo;
   }

   /**
    * @return true if the login password has been configured
    */
   public boolean hasLoginPasswordOption() {
      return loginPassword != null;
   }

   /**
    * @return true if the login password is set
    */
   public boolean hasLoginPassword() {
      return hasLoginPasswordOption() && loginPassword.isPresent();
   }

   /**
    *
    * @return the login password for
    *         {@link org.jclouds.compute.ComputeService#runScriptOnNode}. By
    *         default, null.
    */
   @Nullable
   public String getLoginPassword() {
      return hasLoginPassword() ? loginPassword.get() : null;
   }

   /**
    * @return true if the login ssh key has been configured
    */
   public boolean hasLoginPrivateKeyOption() {
      return loginPrivateKey != null;
   }

   /**
    * @return true if the login ssh key is set
    */
   public boolean hasLoginPrivateKey() {
      return hasLoginPrivateKeyOption() && loginPrivateKey.isPresent();
   }

   /**
    * 
    * @return the login ssh key for
    *         {@link org.jclouds.compute.ComputeService#runScriptOnNode}. By
    *         default, null.
    */
   @Nullable
   public String getLoginPrivateKey() {
      return hasLoginPrivateKey() ? loginPrivateKey.get() : null;
   }

   /**
    * Whether to run the script as root (or run with current privileges). By
    * default, true.
    * 
    * @return value
    */
   public boolean shouldRunAsRoot() {
      return runAsRoot;
   }

   /**
    * @see #blockOnComplete(boolean)
    */
   public boolean shouldBlockOnComplete() {
      return blockOnComplete;
   }

   /**
    * Whether to wait until the script has completed. By default, true.
    * 
    * @return value
    */
   public boolean shouldWrapInInitScript() {
      return wrapInInitScript;
   }

   public static class Builder {

      public static RunScriptOptions nameTask(String name) {
         RunScriptOptions options = new RunScriptOptions();
         return options.nameTask(name);
      }

      public static RunScriptOptions overrideLoginUser(String user) {
         RunScriptOptions options = new RunScriptOptions();
         return options.overrideLoginUser(user);
      }

      public static RunScriptOptions overrideLoginPassword(String password) {
         RunScriptOptions options = new RunScriptOptions();
         return options.overrideLoginPassword(password);
      }

      public static RunScriptOptions overrideLoginPrivateKey(String privateKey) {
         RunScriptOptions options = new RunScriptOptions();
         return options.overrideLoginPrivateKey(privateKey);
      }

      public static RunScriptOptions overrideAuthenticateSudo(boolean authenticateSudo) {
         RunScriptOptions options = new RunScriptOptions();
         return options.overrideAuthenticateSudo(authenticateSudo);
      }

      public static RunScriptOptions overrideLoginCredentials(LoginCredentials credentials) {
         RunScriptOptions options = new RunScriptOptions();
         return options.overrideLoginCredentials(credentials);
      }

      public static RunScriptOptions runAsRoot(boolean value) {
         RunScriptOptions options = new RunScriptOptions();
         return options.runAsRoot(value);
      }

      /**
       * @see RunScriptOptions#blockOnComplete(boolean)
       */
      public static RunScriptOptions blockOnComplete(boolean value) {
         RunScriptOptions options = new RunScriptOptions();
         return options.blockOnComplete(value);
      }

      public static RunScriptOptions wrapInInitScript(boolean value) {
         RunScriptOptions options = new RunScriptOptions();
         return options.wrapInInitScript(value);
      }

      public static RunScriptOptions blockOnPort(int port, int seconds) {
         RunScriptOptions options = new RunScriptOptions();
         return options.blockOnPort(port, seconds);
      }

   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      RunScriptOptions that = RunScriptOptions.class.cast(o);
      return equal(this.loginUser, that.loginUser) && equal(this.loginPassword, that.loginPassword)
            && equal(this.loginPrivateKey, that.loginPrivateKey) && equal(this.authenticateSudo, that.authenticateSudo)
            && equal(this.port, that.port) && equal(this.seconds, that.seconds) && equal(this.taskName, that.taskName)
            && equal(this.runAsRoot, that.runAsRoot) && equal(this.blockOnComplete, that.blockOnComplete)
            && equal(this.wrapInInitScript, that.wrapInInitScript);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(loginUser, loginPassword, loginPrivateKey, authenticateSudo, port, seconds, taskName,
            taskName, blockOnComplete, wrapInInitScript);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      ToStringHelper toString = Objects.toStringHelper("").omitNullValues();
      toString.add("loginUser", loginUser);
      if (loginPassword != null)
         toString.add("loginPasswordPresent", true);
      if (loginPrivateKey != null)
         toString.add("loginPrivateKeyPresent", true);
      toString.add("authenticateSudo", authenticateSudo);
      if (port != -1 && seconds != -1) // TODO: not primitives
         toString.add("blockOnPort:seconds", port + ":" + seconds);
      toString.add("taskName", taskName);
      if (!runAsRoot)
         toString.add("runAsRoot", runAsRoot);
      if (!blockOnComplete)
         toString.add("blockOnComplete", blockOnComplete);
      if (!wrapInInitScript)
         toString.add("wrapInInitScript", wrapInInitScript);
      return toString;
   }

}
