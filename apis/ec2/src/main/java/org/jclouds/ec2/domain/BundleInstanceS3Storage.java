/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.javax.annotation.Nullable;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-BundleInstanceS3StorageType.html"
 *      />
 * @author Adrian Cole
 */
public class BundleInstanceS3Storage {
   private final String ccessKeyId;
   private final String bucket;
   private final String prefix;
   private final String secretAccessKey;
   private final String uploadPolicy;
   private final String uploadPolicySignature;

   public BundleInstanceS3Storage(@Nullable String ccessKeyId, String bucket, String prefix,
         @Nullable String secretAccessKey, @Nullable String uploadPolicy, @Nullable String uploadPolicySignature) {
      this.ccessKeyId = ccessKeyId;
      this.bucket = checkNotNull(bucket, "bucket");
      this.prefix = checkNotNull(prefix, "prefix");
      this.secretAccessKey = secretAccessKey;
      this.uploadPolicy = uploadPolicy;
      this.uploadPolicySignature = uploadPolicySignature;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((ccessKeyId == null) ? 0 : ccessKeyId.hashCode());
      result = prime * result + ((bucket == null) ? 0 : bucket.hashCode());
      result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
      result = prime * result + ((secretAccessKey == null) ? 0 : secretAccessKey.hashCode());
      result = prime * result + ((uploadPolicy == null) ? 0 : uploadPolicy.hashCode());
      result = prime * result + ((uploadPolicySignature == null) ? 0 : uploadPolicySignature.hashCode());
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
      BundleInstanceS3Storage other = (BundleInstanceS3Storage) obj;
      if (ccessKeyId == null) {
         if (other.ccessKeyId != null)
            return false;
      } else if (!ccessKeyId.equals(other.ccessKeyId))
         return false;
      if (bucket == null) {
         if (other.bucket != null)
            return false;
      } else if (!bucket.equals(other.bucket))
         return false;
      if (prefix == null) {
         if (other.prefix != null)
            return false;
      } else if (!prefix.equals(other.prefix))
         return false;
      if (secretAccessKey == null) {
         if (other.secretAccessKey != null)
            return false;
      } else if (!secretAccessKey.equals(other.secretAccessKey))
         return false;
      if (uploadPolicy == null) {
         if (other.uploadPolicy != null)
            return false;
      } else if (!uploadPolicy.equals(other.uploadPolicy))
         return false;
      if (uploadPolicySignature == null) {
         if (other.uploadPolicySignature != null)
            return false;
      } else if (!uploadPolicySignature.equals(other.uploadPolicySignature))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[ccessKeyId=" + ccessKeyId + ", bucket=" + bucket + ", prefix=" + prefix + ", secreAccessKey="
            + secretAccessKey + ", uploadPolicy=" + uploadPolicy + ", uploadPolicySignature=" + uploadPolicySignature
            + "]";
   }


   /**
    * 
    * @returnThe bucket in which to store the AMI. You can specify a bucket that
    *            you already own or a new bucket that Amazon EC2 creates on your
    *            behalf. If you specify a bucket that belongs to someone else,
    *            Amazon EC2 returns an error.
    */
   public String getBucket() {
      return bucket;
   }

   /**
    * 
    * @return Specifies the beginning of the file name of the AMI.
    */
   public String getPrefix() {
      return prefix;
   }



   /**
    * 
    * @return An Amazon S3 upload policy that gives Amazon EC2 permission to
    *         upload items into Amazon S3 on the user's behalf. For more
    *         information on bundling in Windows, go to the Amazon Elastic
    *         Compute Cloud Developer Guide and Amazon Elastic Compute Cloud
    *         Getting Started
    */
   public String getUploadPolicy() {
      return uploadPolicy;
   }

   /**
    * 
    * @return The signature of the Base64 encoded JSON document.
    */
   public String getUploadPolicySignature() {
      return uploadPolicySignature;
   }
}
