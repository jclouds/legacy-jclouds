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
package org.jclouds.s3.predicates.validators;

import static com.google.common.base.CharMatcher.is;

import javax.inject.Inject;

import org.jclouds.predicates.validators.DnsNameValidator;

import com.google.common.base.CharMatcher;
import com.google.inject.Singleton;

/**
 * Validates name for S3 buckets. The complete requirements are listed at:
 * http://docs.amazonwebservices.com/AmazonS3/latest/index.html?BucketRestrictions.html
 * 
 * @see org.jclouds.rest.InputParamValidator
 * @see org.jclouds.predicates.Validator
 * 
 * @author Adrian Cole
 */
@Singleton
public class BucketNameValidator extends DnsNameValidator {

   @Inject
   public BucketNameValidator() {
      super(3, 63);
   }

   public void validate(String containerName) {
      super.validate(containerName);
      if (containerName.indexOf("..") != -1)
         throw exception(containerName, "Bucket names cannot contain two, adjacent periods");
      if (containerName.endsWith("-"))
         throw exception(containerName, "Bucket names should not end with a dash");

      if (containerName.indexOf("-.") != -1 || containerName.indexOf(".-") != -1)
         throw exception(
                  containerName,
                  "Bucket names cannot contain dashes next to periods (e.g., \"my-.bucket.com\" and \"my.-bucket\" are invalid)");
   }

   @Override
   protected IllegalArgumentException exception(String containerName, String reason) {
      return new IllegalArgumentException(
               String
                        .format(
                                 "Object '%s' doesn't match S3 bucket virtual host naming convention. "
                                          + "Reason: %s. For more info, please refer to http://docs.amazonwebservices.com/AmazonS3/latest/index.html?BucketRestrictions.html.",
                                 containerName, reason));
   }

   /**
    * Amazon also permits periods in the dns name.
    * It also permits underscores, although they aren't recommended.
    */
   @Override
   protected CharMatcher getAcceptableRange() {
      return super.getAcceptableRange().or(is('.')).or(is('_'));
   }
}
