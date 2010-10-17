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

package org.jclouds.compute.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import org.jclouds.compute.domain.internal.ComputeMetadataImpl;
import org.jclouds.domain.Location;
import org.jclouds.domain.ResourceMetadataBuilder;

/**
 * 
 * @author Adrian Cole
 */
public class ComputeMetadataBuilder extends ResourceMetadataBuilder<ComputeType> {
   protected String id;
   protected ComputeType type;

   public ComputeMetadataBuilder(ComputeType type) {
      super();
      this.type = checkNotNull(type, "type");
   }

   public ComputeMetadataBuilder id(String id) {
      this.id = id;
      return this;
   }

   /**
    * set id and providerId to the same value;
    */
   public ComputeMetadataBuilder ids(String id) {
      id(id).providerId(id);
      return this;
   }

   @Override
   public ComputeMetadataBuilder providerId(String providerId) {
      return ComputeMetadataBuilder.class.cast(super.providerId(providerId));
   }

   @Override
   public ComputeMetadataBuilder name(String name) {
      return ComputeMetadataBuilder.class.cast(super.name(name));
   }

   @Override
   public ComputeMetadataBuilder location(Location location) {
      return ComputeMetadataBuilder.class.cast(super.location(location));
   }

   @Override
   public ComputeMetadataBuilder uri(URI uri) {
      return ComputeMetadataBuilder.class.cast(super.uri(uri));
   }

   @Override
   public ComputeMetadataBuilder userMetadata(Map<String, String> userMetadata) {
      return ComputeMetadataBuilder.class.cast(super.userMetadata(userMetadata));
   }

   public ComputeMetadata build() {
      return new ComputeMetadataImpl(type, providerId, name, id, location, uri, userMetadata);
   }

   public static ComputeMetadataBuilder fromComputeMetadata(ComputeMetadata in) {
      return new ComputeMetadataBuilder(in.getType()).id(in.getId()).location(in.getLocation()).name(in.getName())
            .uri(in.getUri()).userMetadata(in.getUserMetadata());
   }
}