/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.mezeo.pcs2.domain.internal;

import java.net.URI;

import org.jclouds.blobstore.domain.ResourceType;
import org.jclouds.mezeo.pcs2.domain.ContainerInfo;
import org.joda.time.DateTime;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class ContainerInfoImpl extends ResourceInfoImpl implements ContainerInfo {
   private final URI contents;

   public ContainerInfoImpl(URI url, String name, DateTime created, boolean inProject,
            DateTime modified, String owner, int version, boolean shared, DateTime accessed,
            long bytes, URI contents, URI tags, URI metadata, URI parent) {
      super(ResourceType.FOLDER, url, name, created, inProject, modified, owner, version, shared,
               accessed, bytes, tags, metadata, parent);
      this.contents = contents;
   }

   public URI getContents() {
      return contents;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((contents == null) ? 0 : contents.hashCode());
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
      ContainerInfoImpl other = (ContainerInfoImpl) obj;
      if (contents == null) {
         if (other.contents != null)
            return false;
      } else if (!contents.equals(other.contents))
         return false;
      return true;
   }

}
