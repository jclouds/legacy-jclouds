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
package org.jclouds.openstack.nova.v2_0.options;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * @author Adam Lowe
 */
public class CreateVolumeTypeOptions implements MapBinder {
   public static final CreateVolumeTypeOptions NONE = new CreateVolumeTypeOptions();

   @Inject
   protected BindToJsonPayload jsonBinder;

   protected Map<String, String> specs = ImmutableMap.of();

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      Map<String, Object> data = Maps.newHashMap();
      data.putAll(postParams);
      data.put("extra_specs", specs);
      return jsonBinder.bindToRequest(request, ImmutableMap.of("volume_type", data));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      throw new IllegalStateException("CreateWithExtraSpecs are POST operations");
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (!(object instanceof CreateVolumeTypeOptions)) return false;
      final CreateVolumeTypeOptions other = CreateVolumeTypeOptions.class.cast(object);
      return equal(specs, other.specs);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(specs);
   }

   protected ToStringHelper string() {
      return toStringHelper("").add("specs", specs);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public CreateVolumeTypeOptions specs(Map<String, String> specs) {
      this.specs = specs;
      return this;
   }

   public Map<String, String> getSpecs() {
      return specs;
   }

   public static class Builder {
      /**
       * @see CreateVolumeTypeOptions#getSpecs()
       */
      public static CreateVolumeTypeOptions specs(Map<String, String> specs) {
         return new CreateVolumeTypeOptions().specs(specs);
      }
   }

}
