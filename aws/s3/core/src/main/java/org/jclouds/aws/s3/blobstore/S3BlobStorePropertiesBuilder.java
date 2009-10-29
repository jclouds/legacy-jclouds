/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.s3.blobstore;

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.util.Properties;

import org.jclouds.aws.s3.S3PropertiesBuilder;
import org.jclouds.blobstore.reference.BlobStoreConstants;

/**
 * Builds properties used in S3 Blob Stores
 * 
 * @author Adrian Cole, Andrew Newdigate
 */
public class S3BlobStorePropertiesBuilder extends S3PropertiesBuilder {

   public S3BlobStorePropertiesBuilder(String id, String secret) {
      super(id, secret);
   }

   public S3BlobStorePropertiesBuilder(Properties properties) {
      super(properties);
   }

   /**
    * longest time a single synchronous operation can take before throwing an exception.
    */
   public S3BlobStorePropertiesBuilder withRequestTimeout(long milliseconds) {
      properties.setProperty(BlobStoreConstants.PROPERTY_BLOBSTORE_TIMEOUT, Long
               .toString(milliseconds));
      return this;
   }

   @Override
   protected Properties defaultProperties() {
      Properties props = super.defaultProperties();
      props.setProperty(PROPERTY_USER_METADATA_PREFIX, "x-amz-meta-");
      return props;
   }
}
