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

import javax.inject.Inject;

import org.jclouds.blobstore.domain.Blob;

/**
 * Amazon S3 is designed to store objects. Objects are stored in {@link S3Bucket buckets} and
 * consist of a {@link org.jclouds.aws.s3.domain.S3Object#getData() value}, a
 * {@link S3Object#getKey key}, {@link S3Object.Metadata#getUserMetadata() metadata}, and an access
 * control policy.
 * 
 * @author Adrian Cole
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?UsingObjects.html"
 *      />
 */
public class S3Object extends Blob<ObjectMetadata> {

   public S3Object(ObjectMetadata metadata, Object data) {
      super(metadata, data);
   }

   public S3Object(ObjectMetadata metadata) {
      super(metadata);
   }

   public S3Object(String key, Object data) {
      this(new ObjectMetadata(key), data);
   }

   @Inject
   public S3Object() {
      this(new ObjectMetadata());
   }

   public S3Object(String key) {
      this(new ObjectMetadata(key));
   }

}
