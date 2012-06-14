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
package org.jclouds.joyent.sdc.v6_5.options;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Gerald Pereira
 * 
 */
public class CreateMachineOptions implements MapBinder {
   @Inject
   private BindToJsonPayload jsonBinder;

   private Map<String, String> metadata = ImmutableMap.of();
   private Map<String, String> tag = ImmutableMap.of();

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof CreateMachineOptions) {
         final CreateMachineOptions other = CreateMachineOptions.class.cast(object);
         return equal(tag, tag) && equal(metadata, other.metadata);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(tag, metadata);
   }

   protected ToStringHelper string() {
      return toStringHelper("").add("metadata", metadata).add("tag", tag);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      MachineRequest machine = new MachineRequest(checkNotNull(postParams.get("name"), "name parameter not present")
               .toString(), checkNotNull(postParams.get("package"), "package parameter not present").toString(),
               checkNotNull(postParams.get("dataset"), "dataset parameter not present").toString());

      if (metadata.size() > 0)
         machine.metadata = metadata;
      if (tag.size() > 0)
         machine.tag = tag;

      return bindToRequest(request, machine);
   }

   /**
    * An arbitrary set of metadata key/value pairs can be set at provision time, but they must be
    * prefixed with "metadata."
    */
   public CreateMachineOptions metadata(Map<String, String> metadata) {
      checkNotNull(metadata, "metadata");
      this.metadata = ImmutableMap.copyOf(metadata);
      return this;
   }

   /**
    * An arbitrary set of tags can be set at provision time, but they must be prefixed with "tag."
    */
   public CreateMachineOptions tag(Map<String, String> tag) {
      checkNotNull(tag, "tag");
      this.tag = ImmutableMap.copyOf(tag);
      return this;
   }

   @SuppressWarnings("unused")
   private class MachineRequest {
      final String name;
      @SerializedName("package")
      final String packageSDC;
      final String dataset;
      Map<String, String> metadata;
      Map<String, String> tag;

      private MachineRequest(String name, String packageSDC, String dataset) {
         this.name = name;
         this.packageSDC = packageSDC;
         this.dataset = dataset;
      }

   }

   public static class Builder {

      /**
       * @see CreateMachineOptions#metadata(Map<String, String>)
       */
      public static CreateMachineOptions metadata(Map<String, String> metadata) {
         CreateMachineOptions options = new CreateMachineOptions();
         return options.metadata(metadata);
      }

      /**
       * @see CreateMachineOptions#tag(Map<String, String>)
       */
      public static CreateMachineOptions tag(Map<String, String> tag) {
         CreateMachineOptions options = new CreateMachineOptions();
         return options.tag(tag);
      }
   }

}
