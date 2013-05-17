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

/**
 * Every bucket and object in Amazon S3 has an owner, the user that created the bucket or object.
 * The owner of a bucket or object cannot be changed. However, if the object is overwritten by
 * another user (deleted and rewritten), the new object will have a new owner.
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?
 *      RESTAccessPolicy.html" />
 * @author Adrian Cole
 */
public class CanonicalUser {
   private final String id;
   private String displayName;

   public CanonicalUser(String id) {
      this.id = id;
   }

   public CanonicalUser(String id, String displayName) {
      this(id);
      this.displayName = displayName;
   }

   /**
    * To locate the CanonicalUser ID for a user, the user must perform the
    * {@link org.jclouds.s3.blobstore.S3AsyncBlobStore#list(String)} and retrieve
    * {@link BucketMetadata#getOwner()}
    */
   public String getId() {
      return id;
   }

   /**
    * read-only as is maintained by Amazon.
    */
   public String getDisplayName() {
      return displayName;
   }

   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
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
      CanonicalUser other = (CanonicalUser) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      return true;
   }

}
