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
package org.jclouds.compute.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.util.CredentialUtils;

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
    * {@link org.jclouds.compute.ComputeService#runScriptOnNodesWithTag}</li>
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

      @Deprecated
      @Override
      public RunScriptOptions overrideLoginUserWith(String loginUser) {
         throw new IllegalArgumentException("loginUser is immutable");
      }

      @Override
      public RunScriptOptions overrideLoginUser(String loginUser) {
         throw new IllegalArgumentException("loginUser is immutable");
      }

      @Override
      public RunScriptOptions overrideLoginCredentialWith(String loginCredential) {
         throw new IllegalArgumentException("loginCredential is immutable");
      }

      @Override
      public RunScriptOptions wrapInInitScript(boolean wrapInInitScript) {
         throw new IllegalArgumentException("wrapInInitScript is immutable");
      }

      @Deprecated
      @Override
      public RunScriptOptions overrideCredentialsWith(Credentials overridingCredentials) {
         throw new IllegalArgumentException("overridingCredentials is immutable");
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
   protected String loginPassword;
   protected String loginPrivateKey;

   @Deprecated
   public RunScriptOptions overrideCredentialsWith(Credentials overridingCredentials) {
      return overrideLoginCredentials(LoginCredentials.builder(overridingCredentials).build());
   }

   public RunScriptOptions overrideLoginCredentials(LoginCredentials overridingCredentials) {
      checkNotNull(overridingCredentials, "overridingCredentials");
      this.loginUser = overridingCredentials.getUser();
      this.loginPassword = overridingCredentials.getPassword();
      this.loginPrivateKey = overridingCredentials.getPrivateKey();
      this.authenticateSudo = overridingCredentials.shouldAuthenticateSudo() ? true : null;
      return this;
   }

   @Deprecated
   public RunScriptOptions overrideLoginUserWith(String loginUser) {
      return overrideLoginUser(loginUser);
   }

   public RunScriptOptions overrideLoginUser(String loginUser) {
      checkNotNull(loginUser, "loginUser");
      this.loginUser = loginUser;
      return this;
   }

   @Deprecated
   public RunScriptOptions overrideLoginCredentialWith(String loginCredential) {
      checkNotNull(loginCredential, "loginCredential");
      if (CredentialUtils.isPrivateKeyCredential(loginCredential)) {
         this.loginPrivateKey = loginCredential;
      } else {
         this.loginPassword = loginCredential;
      }
      return this;
   }

   public RunScriptOptions overrideLoginPassword(String password) {
      checkNotNull(password, "password");
      this.loginPassword = password;
      return this;
   }

   public RunScriptOptions overrideLoginPrivateKey(String privateKey) {
      checkNotNull(privateKey, "privateKey");
      this.loginPrivateKey = privateKey;
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
    * 
    * @return the login password for
    *         {@link org.jclouds.compute.ComputeService#runScriptOnNode}. By
    *         default, null.
    */
   @Nullable
   public String getLoginPassword() {
      return loginPassword;
   }

   /**
    * 
    * @return the login ssh key for
    *         {@link org.jclouds.compute.ComputeService#runScriptOnNode}. By
    *         default, null.
    */
   @Nullable
   public String getLoginPrivateKey() {
      return loginPrivateKey;
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

      @Deprecated
      public static RunScriptOptions overrideLoginUserWith(String user) {
         RunScriptOptions options = new RunScriptOptions();
         return options.overrideLoginUserWith(user);
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

      @Deprecated
      public static RunScriptOptions overrideLoginCredentialWith(String credential) {
         RunScriptOptions options = new RunScriptOptions();
         return options.overrideLoginCredentialWith(credential);
      }

      @Deprecated
      public static RunScriptOptions overrideCredentialsWith(Credentials credentials) {
         RunScriptOptions options = new RunScriptOptions();
         return options.overrideCredentialsWith(credentials);
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
   public String toString() {
      return "[loginUser=" + loginUser + ", loginPasswordPresent=" + (loginPassword != null)
            + ", loginPrivateKeyPresent=" + (loginPrivateKey != null) + ", shouldAuthenticateSudo=" + authenticateSudo
            + ", port:seconds=" + port + ":" + seconds + ", runAsRoot=" + runAsRoot + ", blockOnComplete="
            + blockOnComplete + ", wrapInInitScript=" + wrapInInitScript + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((authenticateSudo == null) ? 0 : authenticateSudo.hashCode());
      result = prime * result + (blockOnComplete ? 1231 : 1237);
      result = prime * result + ((loginPassword == null) ? 0 : loginPassword.hashCode());
      result = prime * result + ((loginPrivateKey == null) ? 0 : loginPrivateKey.hashCode());
      result = prime * result + ((loginUser == null) ? 0 : loginUser.hashCode());
      result = prime * result + port;
      result = prime * result + (runAsRoot ? 1231 : 1237);
      result = prime * result + seconds;
      result = prime * result + ((taskName == null) ? 0 : taskName.hashCode());
      result = prime * result + (wrapInInitScript ? 1231 : 1237);
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
      RunScriptOptions other = (RunScriptOptions) obj;
      if (authenticateSudo == null) {
         if (other.authenticateSudo != null)
            return false;
      } else if (!authenticateSudo.equals(other.authenticateSudo))
         return false;
      if (blockOnComplete != other.blockOnComplete)
         return false;
      if (loginPassword == null) {
         if (other.loginPassword != null)
            return false;
      } else if (!loginPassword.equals(other.loginPassword))
         return false;
      if (loginPrivateKey == null) {
         if (other.loginPrivateKey != null)
            return false;
      } else if (!loginPrivateKey.equals(other.loginPrivateKey))
         return false;
      if (loginUser == null) {
         if (other.loginUser != null)
            return false;
      } else if (!loginUser.equals(other.loginUser))
         return false;
      if (port != other.port)
         return false;
      if (runAsRoot != other.runAsRoot)
         return false;
      if (seconds != other.seconds)
         return false;
      if (taskName == null) {
         if (other.taskName != null)
            return false;
      } else if (!taskName.equals(other.taskName))
         return false;
      if (wrapInInitScript != other.wrapInInitScript)
         return false;
      return true;
   }

}
