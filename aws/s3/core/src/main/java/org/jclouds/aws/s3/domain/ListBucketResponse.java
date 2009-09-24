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

import java.util.SortedSet;

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
 * the Amazon S3 account.
 * <p/>
 * The similarities between buckets and domain names is not a coincidence—there is a direct mapping
 * between Amazon S3 buckets and subdomains of s3.amazonaws.com. Objects stored in Amazon S3 are
 * addressable using the REST API under the domain bucketname.s3.amazonaws.com. For example, if the
 * object homepage.html?is stored in the Amazon S3 bucket mybucket its address would be
 * http://mybucket.s3.amazonaws.com/homepage.html?
 * 
 * @author Adrian Cole
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html" />
 */
public interface ListBucketResponse extends org.jclouds.rest.BoundedList<ObjectMetadata> {

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
    * @see org.jclouds.aws.s3.options.ListBucketOptions#getPrefix()
    */
   public SortedSet<String> getCommonPrefixes();

   public String getBucketName();

   public String getDelimiter();

   public boolean isTruncated();

}