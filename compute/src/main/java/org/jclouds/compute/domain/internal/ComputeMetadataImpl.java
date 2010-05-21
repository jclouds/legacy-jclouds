/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
public class ComputeMetadataImpl extends ResourceMetadataImpl<ComputeType> implements
         ComputeMetadata {

   /** The serialVersionUID */
   private static final long serialVersionUID = 7374704415964898694L;
   private final String handle;

   public ComputeMetadataImpl(ComputeType type, String id, String name, String handle,
            Location location, URI uri, Map<String, String> userMetadata) {
      super(type, id, name, location, uri, userMetadata);
      this.handle = checkNotNull(handle, "handle");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return handle;
   }

}
