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

package org.jclouds.compute.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.domain.Location;
import org.jclouds.domain.internal.ResourceMetadataImpl;

/**
 * @author Adrian Cole
 * @author Ivan Meredith
 */
public class ComputeMetadataImpl extends ResourceMetadataImpl<ComputeType> implements ComputeMetadata {
   /** The serialVersionUID */
   private static final long serialVersionUID = 7374704415964898694L;
   private final String id;
   private final ComputeType type;

   public ComputeMetadataImpl(ComputeType type, String providerId, String name, String id, Location location, URI uri,
         Map<String, String> userMetadata) {
      super(providerId, name, location, uri, userMetadata);
      this.id = checkNotNull(id, "id");
      this.type = checkNotNull(type, "type");
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

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      ComputeMetadataImpl other = (ComputeMetadataImpl) obj;
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
