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
package org.jclouds.loadbalancer.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.domain.Location;
import org.jclouds.domain.internal.ResourceMetadataImpl;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.domain.LoadBalancerType;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
public class LoadBalancerMetadataImpl extends ResourceMetadataImpl<LoadBalancerType> implements LoadBalancerMetadata {

   private final String id;
   private final LoadBalancerType type;
   private final Set<String> addresses;

   public LoadBalancerMetadataImpl(LoadBalancerType type, String providerId, String name, String id, Location location,
         URI uri, Map<String, String> userMetadata, Iterable<String> addresses) {
      super(providerId, name, location, uri, userMetadata);
      this.id = checkNotNull(id, "id");
      this.type = checkNotNull(type, "type");
      this.addresses = ImmutableSet.copyOf(checkNotNull(addresses, "addresses"));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public LoadBalancerType getType() {
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
   public Set<String> getAddresses() {
      return addresses;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((addresses == null) ? 0 : addresses.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      return result;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", providerId=" + getProviderId() + ", name=" + getName() + ", location=" + getLocation()
            + ", uri=" + getUri() + ", userMetadata=" + getUserMetadata() + ", type=" + type + ", addresses="
            + addresses + "]";
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      LoadBalancerMetadataImpl other = (LoadBalancerMetadataImpl) obj;
      if (addresses == null) {
         if (other.addresses != null)
            return false;
      } else if (!addresses.equals(other.addresses))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (type != other.type)
         return false;
      return true;
   }

}
