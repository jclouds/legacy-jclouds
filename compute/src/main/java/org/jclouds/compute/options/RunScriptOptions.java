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

import org.jclouds.domain.Credentials;

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
      public Credentials getOverrideCredentials() {
         return delegate.getOverrideCredentials();

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
      public RunScriptOptions withOverridingCredentials(Credentials overridingCredentials) {
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
   protected Credentials overridingCredentials;
   protected boolean runAsRoot = true;
   protected boolean blockOnComplete = true;
   protected boolean wrapInInitScript = true;

   public RunScriptOptions withOverridingCredentials(Credentials overridingCredentials) {
      checkNotNull(overridingCredentials, "overridingCredentials");
      checkNotNull(overridingCredentials.identity, "overridingCredentials.identity");
      checkNotNull(overridingCredentials.credential, "overridingCredentials.key");
      this.overridingCredentials = overridingCredentials;
      return this;
   }

   /**
    * @return What to call the task relating to this script; default {@code
    *         jclouds-script-timestamp} where timestamp is millis since epoch
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
    *           if the command is long-running, use this option to ensure it is wrapInInitScripted
    *           properly. (ex. have jclouds wrap it an init script, nohup, etc)
    * @return
    */
   public RunScriptOptions wrapInInitScript(boolean wrapInInitScript) {
      this.wrapInInitScript = wrapInInitScript;
      return this;
   }

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
    * Whether to override the credentials with ones supplied in call to
    * {@link org.jclouds.compute.ComputeService#runScriptOnNodesWithTag}. By default, true.
    * 
    * @return value
    */
   public Credentials getOverrideCredentials() {
      return overridingCredentials;
   }

   /**
    * Whether to run the script as root (or run with current privileges). By default, true.
    * 
    * @return value
    */
   public boolean shouldRunAsRoot() {
      return runAsRoot;
   }

   /**
    * Whether to wait until the script has completed. By default, true.
    * 
    * @return value
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

      public static RunScriptOptions overrideCredentialsWith(Credentials credentials) {
         RunScriptOptions options = new RunScriptOptions();
         return options.withOverridingCredentials(credentials);
      }

      public static RunScriptOptions runAsRoot(boolean value) {
         RunScriptOptions options = new RunScriptOptions();
         return options.runAsRoot(value);
      }

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
      return "[overridingCredentials=" + (overridingCredentials != null) + ", port:seconds=" + port + ":" + seconds
               + ", runAsRoot=" + runAsRoot + ", blockOnComplete=" + blockOnComplete + ", wrapInInitScript=" + wrapInInitScript
               + "]";
   }

}
