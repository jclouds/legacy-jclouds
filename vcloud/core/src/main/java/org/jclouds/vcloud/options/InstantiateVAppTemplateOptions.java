/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class InstantiateVAppTemplateOptions {

   private String cpuCount;
   private String memorySizeMegabytes;
   private String diskSizeKilobytes;
   private String network;
   private Map<String, String> properties = Maps.newTreeMap();

   public InstantiateVAppTemplateOptions productProperty(String key, String value) {
      properties.put(checkNotNull(key, "key"), checkNotNull(value, "value"));
      return this;
   }

   public InstantiateVAppTemplateOptions productProperties(Map<String, String> properties) {
      this.properties.putAll(checkNotNull(properties, "properties"));
      return this;
   }

   public InstantiateVAppTemplateOptions processorCount(int cpuCount) {
      checkArgument(cpuCount >= 1, "cpuCount must be positive");
      this.cpuCount = cpuCount + "";
      return this;
   }

   public InstantiateVAppTemplateOptions memory(long megabytes) {
      checkArgument(megabytes % 512 == 0, "megabytes must be in an increment of 512");
      this.memorySizeMegabytes = megabytes + "";
      return this;
   }

   public InstantiateVAppTemplateOptions disk(long kilobytes) {
      checkArgument(kilobytes >= 1, "diskSizeKilobytes must be positive");
      this.diskSizeKilobytes = kilobytes + "";
      return this;
   }

   public InstantiateVAppTemplateOptions inNetwork(URI networkLocation) {
      this.network = checkNotNull(networkLocation, "networkLocation").toASCIIString();
      return this;
   }

   public String getCpuCount() {
      return cpuCount;
   }

   public String getMemorySizeMegabytes() {
      return memorySizeMegabytes;
   }

   public String getDiskSizeKilobytes() {
      return diskSizeKilobytes;
   }

   public String getNetwork() {
      return network;
   }

   public Map<String, String> getProperties() {
      return properties;
   }

   public static class Builder {

      /**
       * @see InstantiateVAppTemplateOptions#processorCount(int)
       */
      public static InstantiateVAppTemplateOptions processorCount(int cpuCount) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.processorCount(cpuCount);
      }

      /**
       * @see InstantiateVAppTemplateOptions#memory(int)
       */
      public static InstantiateVAppTemplateOptions memory(int megabytes) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.memory(megabytes);
      }

      /**
       * @see InstantiateVAppTemplateOptions#disk(int)
       */
      public static InstantiateVAppTemplateOptions disk(long kilobytes) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.disk(kilobytes);
      }

      /**
       * @see InstantiateVAppTemplateOptions#inNetwork(URI)
       */
      public static InstantiateVAppTemplateOptions inNetwork(URI networkLocation) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.inNetwork(networkLocation);
      }

      /**
       * @see InstantiateVAppTemplateOptions#productProperty(String,String)
       */
      public static InstantiateVAppTemplateOptions productProperty(String key, String value) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.productProperty(key, value);
      }

      /**
       * @see InstantiateVAppTemplateOptions#setProperties(Map<String, String>)
       */
      public static InstantiateVAppTemplateOptions productProperties(Map<String, String> properties) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.productProperties(properties);
      }
   }

}
