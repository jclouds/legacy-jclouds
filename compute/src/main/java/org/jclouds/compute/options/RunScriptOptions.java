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
      public boolean isRunAsRoot() {
         return delegate.isRunAsRoot();

      }

      @Override
      public RunScriptOptions runAsRoot(boolean runAsRoot) {
         throw new IllegalArgumentException("runAsRoot is immutable");
      }

      @Override
      public RunScriptOptions withOverridingCredentials(Credentials overridingCredentials) {
         throw new IllegalArgumentException("overridingCredentials is immutable");
      }

   }

   private Credentials overridingCredentials;
   private boolean runAsRoot = true;

   public RunScriptOptions withOverridingCredentials(Credentials overridingCredentials) {
      checkNotNull(overridingCredentials, "overridingCredentials");
      checkNotNull(overridingCredentials.account, "overridingCredentials.account");
      checkNotNull(overridingCredentials.key, "overridingCredentials.key");
      this.overridingCredentials = overridingCredentials;
      return this;
   }

   public RunScriptOptions runAsRoot(boolean runAsRoot) {
      this.runAsRoot = runAsRoot;
      return this;
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
   public boolean isRunAsRoot() {
      return runAsRoot;
   }

   public static class Builder {

      public static RunScriptOptions overrideCredentialsWith(Credentials credentials) {
         RunScriptOptions options = new RunScriptOptions();
         return options.withOverridingCredentials(credentials);
      }

      public static RunScriptOptions runAsRoot(boolean value) {
         RunScriptOptions options = new RunScriptOptions();
         return options.runAsRoot(value);
      }

   }

   @Override
   public String toString() {
      return "RunScriptOptions [overridingCredentials=" + (overridingCredentials != null)
               + ", runAsRoot=" + runAsRoot + "]";
   }

}
