/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.compute.options;

import org.jclouds.compute.options.TemplateOptions;

/**
 * Contains options supported by the
 * {@link ComputeService#createNodesInGroup(String, int, TemplateOptions)} and
 * {@link ComputeService#createNodesInGroup(String, int, TemplateOptions)}
 * operations on the <em>Abiquo</em> provider.
 * 
 * @author Ignasi Barrera
 */
public class AbiquoTemplateOptions extends TemplateOptions implements Cloneable {
   public static final AbiquoTemplateOptions NONE = new AbiquoTemplateOptions();

   private Integer overrideCores;

   private Integer overrideRam;

   private String vncPassword;

   @Override
   public TemplateOptions clone() {
      AbiquoTemplateOptions options = new AbiquoTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(final TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof AbiquoTemplateOptions) {
         AbiquoTemplateOptions options = AbiquoTemplateOptions.class.cast(to);
         options.overrideCores(overrideCores);
         options.overrideRam(overrideRam);
         options.vncPassword(vncPassword);
      }
   }

   /**
    * Override the number of cores set by the hardware profile.
    * 
    * @return The template options with the number of cores.
    */
   public AbiquoTemplateOptions overrideCores(final Integer overrideCores) {
      this.overrideCores = overrideCores;
      return this;
   }

   public Integer getOverrideCores() {
      return overrideCores;
   }

   /**
    * Override the amount of ram set by the hardware profile.
    * 
    * @return The template options with the amount of ram.
    */
   public AbiquoTemplateOptions overrideRam(final Integer overrideRam) {
      this.overrideRam = overrideRam;
      return this;
   }

   public Integer getOverrideRam() {
      return overrideRam;
   }

   /**
    * Set the VNC password to access the virtual machine.
    * <p>
    * By default virtual machines does not have VNC access password protected.
    * 
    * @return The template options with the VNC password.
    */
   public AbiquoTemplateOptions vncPassword(final String vncPassword) {
      this.vncPassword = vncPassword;
      return this;
   }

   public String getVncPassword() {
      return vncPassword;
   }

   public static class Builder {
      /**
       * @see AbiquoTemplateOptions#overrideCores(int)
       */
      public static AbiquoTemplateOptions overrideCores(final Integer overrideCores) {
         AbiquoTemplateOptions options = new AbiquoTemplateOptions();
         return options.overrideCores(overrideCores);
      }

      /**
       * @see AbiquoTemplateOptions#overrideRam(int)
       */
      public static AbiquoTemplateOptions overrideRam(final Integer overrideRam) {
         AbiquoTemplateOptions options = new AbiquoTemplateOptions();
         return options.overrideRam(overrideRam);
      }

      /**
       * @see AbiquoTemplateOptions#vncPassword(String)
       */
      public static AbiquoTemplateOptions vncPassword(final String vncPassword) {
         AbiquoTemplateOptions options = new AbiquoTemplateOptions();
         return options.vncPassword(vncPassword);
      }
   }
}
