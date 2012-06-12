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
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Map.Entry;

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
public class CreateVolumeOptions implements MapBinder {
   public static final CreateVolumeOptions NONE = new CreateVolumeOptions();

   @Inject
   private BindToJsonPayload jsonBinder;

   private String name;
   private String description;
   private String volumeType;
   private String availabilityZone;
   private String snapshotId;
   private Map<String, String> metadata = ImmutableMap.of();

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      Map<String, Object> image = Maps.newHashMap();
      image.putAll(postParams);
      if (name != null)
         image.put("display_name", name);
      if (description != null)
         image.put("display_description", description);
      if (!metadata.isEmpty())
         image.put("metadata", metadata);
      return jsonBinder.bindToRequest(request, ImmutableMap.of("volume", image));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      throw new IllegalStateException("CreateVolume is a POST operation");
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (!(object instanceof CreateVolumeOptions)) return false;
      final CreateVolumeOptions other = CreateVolumeOptions.class.cast(object);
      return equal(volumeType, other.volumeType) && equal(availabilityZone, other.availabilityZone) && equal(snapshotId, other.snapshotId)
            && equal(name, other.name) && equal(description, other.description) && equal(metadata, other.metadata);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(volumeType, availabilityZone, snapshotId, name, description, metadata);
   }

   protected ToStringHelper string() {
      return toStringHelper("").add("volumeType", volumeType).add("availabilityZone", availabilityZone)
            .add("snapshotId", snapshotId).add("name", name).add("description", description).add("metadata", metadata);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   /**
    * Custom cloud server metadata can also be supplied at launch time. This
    * metadata is stored in the API system where it is retrievable by querying
    * the API for server status. The maximum size of the metadata key and value
    * is each 255 bytes and the maximum number of key-value pairs that can be
    * supplied per volume is 5.
    */
   public CreateVolumeOptions metadata(Map<String, String> metadata) {
      checkNotNull(metadata, "metadata");
      checkArgument(metadata.size() <= 5,
            "you cannot have more then 5 metadata values.  You specified: " + metadata.size());
      for (Entry<String, String> entry : metadata.entrySet()) {
         checkArgument(
               entry.getKey().getBytes().length < 255,
               String.format("maximum length of metadata key is 255 bytes.  Key specified %s is %d bytes",
                     entry.getKey(), entry.getKey().getBytes().length));
         checkArgument(entry.getKey().getBytes().length < 255, String.format(
               "maximum length of metadata value is 255 bytes.  Value specified for %s (%s) is %d bytes",
               entry.getKey(), entry.getValue(), entry.getValue().getBytes().length));
      }
      this.metadata = ImmutableMap.copyOf(metadata);
      return this;
   }

   public CreateVolumeOptions name(String name) {
      this.name = name;
      return this;
   }

   public CreateVolumeOptions description(String description) {
      this.description = description;
      return this;
   }

   public CreateVolumeOptions volumeType(String volumeType) {
      this.volumeType = volumeType;
      return this;
   }

   public CreateVolumeOptions availabilityZone(String availabilityZone) {
      this.availabilityZone = availabilityZone;
      return this;
   }

   public CreateVolumeOptions snapshotId(String snapshotId) {
      this.snapshotId = snapshotId;
      return this;
   }

   public String getVolumeType() {
      return volumeType;
   }

   public String getAvailabilityZone() {
      return availabilityZone;
   }

   public String getSnapshotId() {
      return snapshotId;
   }

   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }

   public Map<String, String> getMetadata() {
      return metadata;
   }

   public static class Builder {
      /**
       * @see CreateVolumeOptions#getName()
       */
      public static CreateVolumeOptions name(String name) {
         return new CreateVolumeOptions().name(name);
      }
      /**
       * @see CreateVolumeOptions#getDescription()
       */
      public static CreateVolumeOptions description(String description) {
         return new CreateVolumeOptions().description(description);
      }

      /**
       * @see CreateVolumeOptions#getVolumeType()
       */
      public static CreateVolumeOptions volumeType(String volumeType) {
         return new CreateVolumeOptions().volumeType(volumeType);
      }

      /**
       * @see CreateVolumeOptions#getAvailabilityZone()
       */
      public static CreateVolumeOptions availabilityZone(String availabilityZone) {
         return new CreateVolumeOptions().availabilityZone(availabilityZone);
      }

      /**
       * @see CreateVolumeOptions#getSnapshotId()
       */
      public static CreateVolumeOptions snapshotId(String snapshotId) {
         return new CreateVolumeOptions().snapshotId(snapshotId);
      }

      /**
       * @see CreateVolumeOptions#getMetadata()
       */
      public static CreateVolumeOptions metadata(Map<String, String> metadata) {
         return new CreateVolumeOptions().metadata(metadata);
      }
   }

}
