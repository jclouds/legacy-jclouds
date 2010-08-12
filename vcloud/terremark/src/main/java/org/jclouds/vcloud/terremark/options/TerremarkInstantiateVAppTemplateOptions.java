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

package org.jclouds.vcloud.terremark.options;

import java.net.URI;
import java.util.Map;

import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class TerremarkInstantiateVAppTemplateOptions extends
      InstantiateVAppTemplateOptions {

   public TerremarkInstantiateVAppTemplateOptions sshKeyFingerprint(
         String sshKeyFingerprint) {
      productProperty("sshKeyFingerprint", sshKeyFingerprint);
      return this;
   }

   public TerremarkInstantiateVAppTemplateOptions primaryDNS(String primaryDNS) {
      productProperty("primaryDNS", primaryDNS);
      return this;
   }

   public TerremarkInstantiateVAppTemplateOptions secondaryDNS(
         String secondaryDNS) {
      productProperty("secondaryDNS", secondaryDNS);
      return this;
   }

   public TerremarkInstantiateVAppTemplateOptions withPassword(String password) {
      productProperty("password", password);
      return this;
   }

   public TerremarkInstantiateVAppTemplateOptions inGroup(String group) {
      productProperty("group", group);
      return this;
   }

   public TerremarkInstantiateVAppTemplateOptions inRow(String row) {
      productProperty("row", row);
      return this;
   }

   public static class Builder {

      /**
       * @see TerremarkInstantiateVAppTemplateOptions#processorCount(int)
       */
      public static TerremarkInstantiateVAppTemplateOptions processorCount(
            int cpuCount) {
         TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
         return options.processorCount(cpuCount);
      }

      /**
       * @see TerremarkInstantiateVAppTemplateOptions#memory(long)
       */
      public static TerremarkInstantiateVAppTemplateOptions memory(
            long megabytes) {
         TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
         return options.memory(megabytes);
      }

      /**
       * @see TerremarkInstantiateVAppTemplateOptions#disk(long)
       */
      public static TerremarkInstantiateVAppTemplateOptions disk(long kilobytes) {
         TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
         return options.disk(kilobytes);
      }

      /**
       * @see TerremarkInstantiateVAppTemplateOptions#inNetwork(URI)
       */
      public static TerremarkInstantiateVAppTemplateOptions inNetwork(
            URI networkLocation) {
         TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
         return options.inNetwork(networkLocation);
      }

      /**
       * @see TerremarkInstantiateVAppTemplateOptions#withPassword(String)
       */
      public static TerremarkInstantiateVAppTemplateOptions withPassword(
            String password) {
         TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
         return options.withPassword(password);
      }

      /**
       * @see TerremarkInstantiateVAppTemplateOptions#inGroup(String)
       */
      public static TerremarkInstantiateVAppTemplateOptions inGroup(String group) {
         TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
         return options.inGroup(group);
      }

      /**
       * @see TerremarkInstantiateVAppTemplateOptions#inRow(String)
       */
      public static TerremarkInstantiateVAppTemplateOptions inRow(String row) {
         TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
         return options.inRow(row);
      }

      /**
       * @see TerremarkInstantiateVAppTemplateOptions#sshKeyFingerprint(String)
       */
      public static TerremarkInstantiateVAppTemplateOptions sshKeyFingerprint(
            String sshKeyFingerprint) {
         TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
         return options.sshKeyFingerprint(sshKeyFingerprint);
      }

      /**
       * @see TerremarkInstantiateVAppTemplateOptions#primaryDNS(String)
       */
      public static TerremarkInstantiateVAppTemplateOptions primaryDNS(
            String primaryDNS) {
         TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
         return options.primaryDNS(primaryDNS);
      }

      /**
       * @see TerremarkInstantiateVAppTemplateOptions#secondaryDNS(String)
       */
      public static TerremarkInstantiateVAppTemplateOptions secondaryDNS(
            String secondaryDNS) {
         TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
         return options.secondaryDNS(secondaryDNS);
      }

      /**
       * @see TerremarkInstantiateVAppTemplateOptions#productProperty(String,
       *      String)
       */
      public static TerremarkInstantiateVAppTemplateOptions productProperty(
            String key, String value) {
         TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
         return (TerremarkInstantiateVAppTemplateOptions) options
               .productProperty(key, value);
      }

      /**
       * @see 
       *      TerremarkInstantiateVAppTemplateOptions#productProperties(Map<String
       *      , String>)
       */
      public static TerremarkInstantiateVAppTemplateOptions productProperties(
            Map<String, String> properties) {
         TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
         return (TerremarkInstantiateVAppTemplateOptions) options
               .productProperties(properties);
      }

   }

   @Override
   public TerremarkInstantiateVAppTemplateOptions processorCount(int cpuCount) {
      return (TerremarkInstantiateVAppTemplateOptions) super
            .processorCount(cpuCount);
   }

   @Override
   public TerremarkInstantiateVAppTemplateOptions inNetwork(URI networkLocation) {
      return (TerremarkInstantiateVAppTemplateOptions) super
            .inNetwork(networkLocation);
   }

   @Override
   public TerremarkInstantiateVAppTemplateOptions memory(long megabytes) {
      return (TerremarkInstantiateVAppTemplateOptions) super.memory(megabytes);
   }

   @Override
   public TerremarkInstantiateVAppTemplateOptions disk(long kilobytes) {
      throw new IllegalArgumentException(
            "changing the boot disk size is unsupported in terremark");
   }

   @Override
   public TerremarkInstantiateVAppTemplateOptions productProperties(
         Map<String, String> properties) {
      return (TerremarkInstantiateVAppTemplateOptions) super
            .productProperties(properties);
   }

   @Override
   public TerremarkInstantiateVAppTemplateOptions productProperty(String key,
         String value) {
      return (TerremarkInstantiateVAppTemplateOptions) super.productProperty(
            key, value);
   }
}
