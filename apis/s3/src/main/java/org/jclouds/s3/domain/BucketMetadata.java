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
package org.jclouds.s3.domain;

import java.util.Date;

/**
 * System metadata of the S3Bucket
 * 
 * @author Adrian Cole
 */
public class BucketMetadata implements Comparable<BucketMetadata> {
   private final Date creationDate;
   private final String name;
   private final CanonicalUser owner;

   public BucketMetadata(String name, Date creationDate, CanonicalUser owner) {
      this.name = name;
      this.creationDate = creationDate;
      this.owner = owner;
   }

   /**
    * Every bucket and object in Amazon S3 has an owner, the user that created the bucket or object.
    * The owner of a bucket or object cannot be changed. However, if the object is overwritten by
    * another user (deleted and rewritten), the new object will have a new owner.
    */
   public CanonicalUser getOwner() {
      return owner;
   }

   public Date getCreationDate() {
      return creationDate;
   }

   /**
    * To comply with Amazon S3 requirements, bucket names must:
    * <p/>
    * Contain lowercase letters, numbers, periods (.), underscores (_), and dashes (-)
    * <p/>
    * Start with a number or letter
    * <p/>
    * Be between 3 and 255 characters long
    * <p/>
    * Not be in an IP address style (e.g., "192.168.5.4")
    */
   public String getName() {
      return name;
   }

   public int compareTo(BucketMetadata o) {
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
      BucketMetadata other = (BucketMetadata) obj;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (owner == null) {
         if (other.owner != null)
            return false;
      } else if (!owner.equals(other.owner))
         return false;
      return true;
   }
}
