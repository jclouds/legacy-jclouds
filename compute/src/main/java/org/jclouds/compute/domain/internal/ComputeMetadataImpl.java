/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.compute.domain.internal;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.domain.Location;
import org.jclouds.domain.internal.ResourceMetadataImpl;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 * @author Ivan Meredith
 */
public class ComputeMetadataImpl extends ResourceMetadataImpl<ComputeType> implements ComputeMetadata {

   private final String id;
   private final ComputeType type;
   protected final Set<String> tags;

   public ComputeMetadataImpl(ComputeType type, String providerId, String name, String id, Location location, URI uri,
            Map<String, String> userMetadata, Set<String> tags) {
      super(providerId, name, location, uri, userMetadata);
      this.id = checkNotNull(id, "id");
      this.type = checkNotNull(type, "type");
      this.tags = ImmutableSet.<String> copyOf(checkNotNull(tags, "tags"));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ComputeType getType() {
      return type;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return id;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getTags() {
      return tags;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ComputeMetadataImpl that = ComputeMetadataImpl.class.cast(o);
      return super.equals(that) && equal(this.id, that.id);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), id);
   }
   
   protected ToStringHelper computeToStringPrefix() {
      return Objects.toStringHelper("").omitNullValues().add("id", getId()).add("providerId", getProviderId())
               .add("uri", getUri()).add("name", getName()).add("uri", getUri()).add("location", getLocation());
   }

   protected ToStringHelper addComputeToStringSuffix(ToStringHelper helper) {
      if (getTags().size() > 0)
         helper.add("tags", getTags());
      if (getUserMetadata().size() > 0)
         helper.add("userMetadata", getUserMetadata());
      return helper;
   }
   
   protected ToStringHelper string() {
      return addComputeToStringSuffix(computeToStringPrefix());
   }

}
