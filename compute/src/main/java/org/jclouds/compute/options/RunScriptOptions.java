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

/**
 * Enables additional options for running a script.
 *
 * @author Oleksiy Yarmula
 */
public class RunScriptOptions {

    /**
     * Default options. The default settings are:
     * <ul>
     * <li>override the credentials with ones supplied in
     *          call to {@link org.jclouds.compute.ComputeService#runScriptOnNodesWithTag}</li>
     * <li>do not run the script as root (run with current privileges)</li>
     * </ul>
     */
    public static final RunScriptOptions NONE = new RunScriptOptions();

    private boolean overrideCredentials = true;
    private boolean runAsRoot = false;

    private void overrideCredentials(boolean overrideCredentials) {
        this.overrideCredentials = overrideCredentials;
    }

    private void runAsRoot(boolean runAsRoot) {
        this.runAsRoot = runAsRoot;
    }

    /**
     * Whether to override the credentials with ones supplied in
     *          call to {@link org.jclouds.compute.ComputeService#runScriptOnNodesWithTag}.
     * By default, true.
     * @return value
     */
    public boolean isOverrideCredentials() {
        return overrideCredentials;
    }

    /**
     * Whether to run the script as root (run with current privileges).
     * By default, false.
     * @return value
     */
    public boolean isRunAsRoot() {
        return runAsRoot;
    }

    public static class Builder {
        private RunScriptOptions options;

        public Builder overrideCredentials(boolean value) {
            if(options == null) options = new RunScriptOptions();
            options.overrideCredentials(value);
            return this;
        }

        public Builder runAsRoot(boolean value) {
            if(options == null) options = new RunScriptOptions();
            options.runAsRoot(value);
            return this;
        }

        public RunScriptOptions build() {
            return options;
        }
    }

}
