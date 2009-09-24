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
package org.jclouds.rackspace.cloudfiles.domain;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class ContainerMetadata extends org.jclouds.blobstore.domain.ContainerMetadata {
   private long count;
   private long bytes;

   public ContainerMetadata() {
      super();
   }

   public ContainerMetadata(String name) {
      super(name);
   }

   public ContainerMetadata(String name, long count, long bytes) {
      super(name);
      this.count = count;
      this.bytes = bytes;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("ContainerMetadata [bytes=").append(bytes).append(", count=").append(count)
               .append(", name=").append(name).append("]");
      return builder.toString();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (bytes ^ (bytes >>> 32));
      result = prime * result + (int) (count ^ (count >>> 32));
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
      ContainerMetadata other = (ContainerMetadata) obj;
      if (bytes != other.bytes)
         return false;
      if (count != other.count)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      return true;
   }

   public String getName() {
      return name;
   }

   public long getCount() {
      return count;
   }

   public long getBytes() {
      return bytes;
   }

}
