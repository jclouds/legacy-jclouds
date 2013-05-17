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

/**
 * A container that provides namespace, access control and aggregation of {@link S3Object}s
 * <p/>
 * <p/>
 * Every object stored in Amazon S3 is contained in a bucket. Buckets partition the namespace of
 * objects stored in Amazon S3 at the top level. Within a bucket, you can use any names for your
 * objects, but bucket names must be unique across all of Amazon S3.
 * <p/>
 * Buckets are similar to Internet domain names. Just as Amazon is the only owner of the domain name
 * Amazon.com, only one person or organization can own a bucket within Amazon S3. Once you create a
 * uniquely named bucket in Amazon S3, you can organize and name the objects within the bucket in
 * any way you like and the bucket will remain yours for as long as you like and as long as you have
 * the Amazon S3 identity.
 * <p/>
 * The similarities between buckets and domain names is not a coincidence there is a direct mapping
 * between Amazon S3 buckets and subdomains of s3.amazonaws.com. Objects stored in Amazon S3 are
 * addressable using the REST API under the domain bucketname.s3.amazonaws.com. For example, if the
 * object homepage.html?is stored in the Amazon S3 bucket mybucket its address would be
 * http://mybucket.s3.amazonaws.com/homepage.html?
 * 
 * @author Adrian Cole
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html" />
 */
public interface ListBucketResponse extends Set<ObjectMetadata> {

   /**
    * Limits the response to keys which begin with the indicated prefix. You can use prefixes to
    * separate a bucket into different sets of keys in a way similar to how a file system uses
    * folders.
    */
   String getPrefix();

   /**
    * Indicates where in the bucket to begin listing. The list will only include keys that occur
    * lexicographically after marker. This is convenient for pagination: To get the next page of
    * results use the last key of the current page as the marker.
    */
   String getNextMarker();

   String getMarker();

   /**
    * The maximum number of keys you'd like to see in the response body. The server might return
    * fewer than this many keys, but will not return more.
    */
   int getMaxKeys();

   /**
    * There are more then maxKeys available
    */
   boolean isTruncated();

   /**
    * Causes keys that contain the same string between the prefix and the first occurrence of the
    * delimiter to be rolled up into a single result element in the CommonPrefixes collection. These
    * rolled-up keys are not returned elsewhere in the response.
    * 
    */
   String getDelimiter();

   /**
    * Example:
    * <p/>
    * if the following keys are in the bucket
    * <p/>
    * a/1/a<br/>
    * a/1/b<br/>
    * a/2/a<br/>
    * a/2/b<br/>
    * <p/>
    * and prefix is set to <code>a/</code> and delimiter is set to <code>/</code> then
    * commonprefixes would return 1,2
    * 
    * @see org.jclouds.s3.options.ListBucketOptions#getPrefix()
    */
   Set<String> getCommonPrefixes();

   /**
    * name of the Bucket
    */
   String getName();

}
