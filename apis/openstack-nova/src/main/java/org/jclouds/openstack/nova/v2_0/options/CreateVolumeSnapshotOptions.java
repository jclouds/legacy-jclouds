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
public class CreateVolumeSnapshotOptions implements MapBinder {
   public static final CreateVolumeSnapshotOptions NONE = new CreateVolumeSnapshotOptions();

   @Inject
   private BindToJsonPayload jsonBinder;

   private String name;
   private String description;
   private boolean force = false;

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      Map<String, Object> data = Maps.newHashMap(postParams);
      if (name != null)
         data.put("display_name", name);
      if (description != null)
         data.put("display_description", description);
      if (force)
         data.put("force", "true");
      return jsonBinder.bindToRequest(request, ImmutableMap.of("snapshot", data));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      throw new IllegalStateException("CreateSnapshot is a POST operation");
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (!(object instanceof CreateVolumeSnapshotOptions)) return false;
      final CreateVolumeSnapshotOptions other = CreateVolumeSnapshotOptions.class.cast(object);
      return equal(name, other.name) && equal(description, other.description);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, description);
   }

   protected ToStringHelper string() {
      return toStringHelper("").add("name", name).add("description", description);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public CreateVolumeSnapshotOptions name(String name) {
      this.name = name;
      return this;
   }

   public CreateVolumeSnapshotOptions description(String description) {
      this.description = description;
      return this;
   }

   public CreateVolumeSnapshotOptions force() {
      this.force = true;
      return this;
   }
   
   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }

   public boolean isForce() {
      return force;
   }

   public static class Builder {
      /**
       * @see CreateVolumeSnapshotOptions#getName()
       */
      public static CreateVolumeSnapshotOptions name(String name) {
         return new CreateVolumeSnapshotOptions().name(name);
      }
      /**
       * @see CreateVolumeSnapshotOptions#getDescription()
       */
      public static CreateVolumeSnapshotOptions description(String description) {
         return new CreateVolumeSnapshotOptions().description(description);
      }

      /**
       * @see CreateVolumeSnapshotOptions#isForce()
       */
      public static CreateVolumeSnapshotOptions force() {
         return new CreateVolumeSnapshotOptions().force();
      }
   }

}
