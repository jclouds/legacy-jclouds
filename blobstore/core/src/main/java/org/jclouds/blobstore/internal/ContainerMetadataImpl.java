/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.blobstore.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.blobstore.domain.ContainerMetadata;

/**
 * System metadata of the Container
 * 
 * @author Adrian Cole
 */
public class ContainerMetadataImpl implements ContainerMetadata, Comparable<ContainerMetadata> {

   protected String name;

   /**
    * @see #getName()
    */
   public ContainerMetadataImpl(String name) {
      this.name = checkNotNull(name, "name");
   }

   public ContainerMetadataImpl() {
      super();
   }

   public void setName(String name) {
      this.name = checkNotNull(name, "name");
   }

   public String getName() {
      return name;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ContainerMetadataImpl other = (ContainerMetadataImpl) obj;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      return true;
   }

   public int compareTo(ContainerMetadata o) {
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }
}