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
package org.jclouds.aws.s3.domain;

import org.jclouds.blobstore.domain.ContainerMetadata;
import org.joda.time.DateTime;

/**
 * System metadata of the S3Bucket
 * 
 * @author Adrian Cole
 */
public class BucketMetadata extends ContainerMetadata {
   protected DateTime creationDate;

   public DateTime getCreationDate() {
      return creationDate;
   }

   public void setCreationDate(DateTime creationDate) {
      this.creationDate = creationDate;
   }

   /**
    * Location constraint of the bucket.
    * 
    * @author Adrian Cole
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AmazonS3/latest/RESTBucketLocationGET.html" />
    */
   public static enum LocationConstraint {
      EU
   }

   private CanonicalUser canonicalUser;

   public BucketMetadata(String name) {
      super(name);
   }

   public BucketMetadata() {
      super();
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
   @Override
   public void setName(String name) {
      // note that we cannot enforce this, as invalid buckets may already exist
      super.setName(name);
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Metadata [canonicalUser=").append(canonicalUser).append("]");
      return builder.toString();
   }

   public String getName() {
      return name;
   }

   /**
    * Every bucket and object in Amazon S3 has an owner, the user that created the bucket or
    * object. The owner of a bucket or object cannot be changed. However, if the object is
    * overwritten by another user (deleted and rewritten), the new object will have a new owner.
    */
   public CanonicalUser getOwner() {
      return canonicalUser;
   }

   public void setOwner(CanonicalUser canonicalUser) {
      this.canonicalUser = canonicalUser;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((canonicalUser == null) ? 0 : canonicalUser.hashCode());
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
      BucketMetadata other = (BucketMetadata) obj;
      if (canonicalUser == null) {
         if (other.canonicalUser != null)
            return false;
      } else if (!canonicalUser.equals(other.canonicalUser))
         return false;
      return true;
   }

}