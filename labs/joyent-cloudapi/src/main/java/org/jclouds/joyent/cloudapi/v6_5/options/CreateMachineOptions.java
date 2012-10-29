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
package org.jclouds.joyent.cloudapi.v6_5.options;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jclouds.http.options.BaseHttpRequestOptions;
import org.jclouds.util.Maps2;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class CreateMachineOptions extends BaseHttpRequestOptions {
   private String name;
   private String pkg;
   private Map<String, String> metadata = ImmutableMap.of();

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof CreateMachineOptions) {
         final CreateMachineOptions other = CreateMachineOptions.class.cast(object);
         return equal(name, other.name) && equal(pkg, other.pkg) && equal(metadata, other.metadata);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, pkg, metadata);
   }

   @Override
   public String toString() {
      ToStringHelper toString = Objects.toStringHelper("").omitNullValues();
      toString.add("name", name).add("package", name);
      if (metadata.size() > 0)
         toString.add("metadata", metadata);
      return toString.toString();
   }

   @Override
   public Multimap<String, String> buildQueryParameters() {
      Multimap<String, String> params = super.buildQueryParameters();
      if (name != null)
         params.put("name", name);
      if (pkg != null)
         params.put("package", pkg);
      params.putAll(Multimaps.forMap(Maps2.transformKeys(metadata, new Function<String, String>() {

         @Override
         public String apply(String input) {
            return "metadata." + input;
         }

      })));
      return params;
   }

   /**
    * friendly name for this machine; default is a randomly generated name
    */
   public CreateMachineOptions name(String name) {
      this.name = checkNotNull(name, "name");
      return this;
   }

   /**
    * Name of the package to use on provisioning; default is indicated in
    * {@link PackageApi#list}
    */
   public CreateMachineOptions packageName(String packageName) {
      this.pkg = checkNotNull(packageName, "packageName");
      return this;
   }

   /**
    * An arbitrary set of metadata key/value pairs.
    */
   public CreateMachineOptions metadata(Map<String, String> metadata) {
      checkNotNull(metadata, "metadata");
      this.metadata = ImmutableMap.copyOf(metadata);
      return this;
   }

   public static class Builder {

      /**
       * @see CreateMachineOptions#name
       */
      public static CreateMachineOptions name(String name) {
         CreateMachineOptions options = new CreateMachineOptions();
         return options.name(name);
      }

      /**
       * @see CreateMachineOptions#packageName
       */
      public static CreateMachineOptions packageName(String packageName) {
         CreateMachineOptions options = new CreateMachineOptions();
         return options.packageName(packageName);
      }

      /**
       * @see CreateMachineOptions#metadata(Map<String, String>)
       */
      public static CreateMachineOptions metadata(Map<String, String> metadata) {
         CreateMachineOptions options = new CreateMachineOptions();
         return options.metadata(metadata);
      }

   }

}
