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

import java.util.Set;

import org.jclouds.s3.domain.AccessControlList.Grant;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Each Amazon S3 bucket has an associated XML sub-resource that you can read and write in order to
 * inspect or change the logging status for that bucket.
 * 
 * @author Adrian Cole
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/latest/index.html?ServerLogs.html"/>
 */
public class BucketLogging {
   private final String targetBucket;
   private final String targetPrefix;
   private final Set<Grant> targetGrants = Sets.newHashSet();

   /**
    * 
    * @param targetBucket
    *           {@link #getTargetBucket}
    * @param targetPrefix
    *           {@link #getTargetPrefix}
    * @param targetGrants
    *           {@link #getTargetGrants}
    */
   public BucketLogging(String targetBucket, String targetPrefix, Iterable<Grant> targetGrants) {
      this.targetBucket = targetBucket;
      this.targetPrefix = targetPrefix;
      Iterables.addAll(this.targetGrants, targetGrants);
   }

   /**
    * 
    * @param targetBucket
    *           {@link #getTargetBucket}
    * @param targetPrefix
    *           {@link #getTargetPrefix}
    */
   public BucketLogging(String targetBucket, String targetPrefix) {
      this.targetBucket = targetBucket;
      this.targetPrefix = targetPrefix;
   }

   /**
    * The bucket owner is automatically granted FULL_CONTROL to all logs delivered to the bucket.
    * This optional element enables you grant access to others. Any specified TargetGrants are added
    * to the default ACL. For more information about ACLs, see Access Control Lists.
    */
   public Set<Grant> getTargetGrants() {
      return targetGrants;
   }

   /**
    * Specifies the bucket where server access logs will be delivered. You can have your logs
    * delivered to any bucket that you own, including the same bucket that is being logged. You can
    * also configure multiple buckets to deliver their logs to the same target bucket. In this case
    * you should choose a different TargetPrefix for each source bucket so that the delivered log
    * files can be distinguished by key.
    * <p/>
    * <h3>Note</h3>
    * 
    * The source and the target buckets must be in the same location. For more information about
    * bucket location constraints, see Buckets and Regions
    */
   public String getTargetBucket() {
      return targetBucket;
   }

   /**
    * This element lets you specify a prefix for the keys that the delivered log files will be
    * stored under. For information on how the key name for log files is constructed, see Delivery
    * of Server Access Logs.
    */
   public String getTargetPrefix() {
      return targetPrefix;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((targetBucket == null) ? 0 : targetBucket.hashCode());
      result = prime * result + ((targetGrants == null) ? 0 : targetGrants.hashCode());
      result = prime * result + ((targetPrefix == null) ? 0 : targetPrefix.hashCode());
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
      BucketLogging other = (BucketLogging) obj;
      if (targetBucket == null) {
         if (other.targetBucket != null)
            return false;
      } else if (!targetBucket.equals(other.targetBucket))
         return false;
      if (targetGrants == null) {
         if (other.targetGrants != null)
            return false;
      } else if (!targetGrants.equals(other.targetGrants))
         return false;
      if (targetPrefix == null) {
         if (other.targetPrefix != null)
            return false;
      } else if (!targetPrefix.equals(other.targetPrefix))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "BucketLogging [targetBucket=" + targetBucket + ", targetGrants=" + targetGrants
               + ", targetPrefix=" + targetPrefix + "]";
   }

}
