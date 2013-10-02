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
package org.jclouds.aws.s3.predicates.validators;

import javax.inject.Inject;

import org.jclouds.s3.predicates.validators.BucketNameValidator;

import com.google.inject.Singleton;

/**
 * Validates name for AWS S3 buckets. The complete requirements are listed at:
 * http://docs.amazonwebservices.com/AmazonS3/latest/index.html?BucketRestrictions.html
 * 
 * @see org.jclouds.rest.InputParamValidator
 * @see org.jclouds.predicates.Validator
 * 
 * @author Adrian Cole, Jeremy Whitlock
 */
@Singleton
public class AWSS3BucketNameValidator extends BucketNameValidator {

   @Inject
   AWSS3BucketNameValidator() {
      super();
   }

   public void validate(String containerName) {
      // AWS S3 allows for upper case characters in bucket names (US Standard region only) and behind the scenes will
      // use the lower-cased version of the bucket name for its DNS name.  So for AWS S3, we will lowercase the bucket
      // name prior to validation.  For all other regions than US Standard region, we will let AWS throw handle the
      // error.
      //
      // http://code.google.com/p/jclouds/issues/detail?id=992
      //
      // It would be nice to scope this more lax validator to only the us regions, since based on AWS S3 documentation,
      // this is only necessary for the us regions.
      super.validate(containerName.toLowerCase());
   }

}
