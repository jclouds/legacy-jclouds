/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.s3.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.s3.S3Client;
import org.jclouds.aws.s3.reference.S3Headers;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.util.Patterns;

/**
 * Encryption, Hashing, and IO Utilities needed to sign and verify S3 requests and responses.
 * 
 * @author Adrian Cole
 */
@Singleton
public class S3Utils {

   @Inject
   AWSUtils util;

   public AWSError parseAWSErrorFromContent(HttpCommand command, HttpResponse response,
            InputStream content) throws HttpException {
      AWSError error = util.parseAWSErrorFromContent(command.getRequest(), response, content);
      if (error.getRequestId() == null)
         error.setRequestId(response.getFirstHeaderOrNull(S3Headers.REQUEST_ID));
      error.setRequestToken(response.getFirstHeaderOrNull(S3Headers.REQUEST_TOKEN));
      return error;
   }

   public AWSError parseAWSErrorFromContent(HttpCommand command, HttpResponse response,
            String content) throws HttpException {
      return parseAWSErrorFromContent(command, response, new ByteArrayInputStream(content
               .getBytes()));
   }

   public static final Pattern BUCKET_NAME_PATTERN = Pattern.compile("^[a-z0-9][-_.a-z0-9]+");

   // TODO add validatorparam so that this is actually used
   public static String validateBucketName(String bucketName) {
      checkNotNull(bucketName, "bucketName");
      checkArgument(
               BUCKET_NAME_PATTERN.matcher(bucketName).matches(),
               "bucketName name must start with a number or letter and  can only contain lowercase letters, numbers, periods (.), underscores (_), and dashes (-)");
      checkArgument(bucketName.length() > 2 && bucketName.length() < 256,
               "bucketName name must be between 3 and 255 characters long");
      checkArgument(!Patterns.IP_PATTERN.matcher(bucketName).matches(),
               "bucketName name cannot be ip address style");
      return bucketName;
   }

   /**
    * This implementation invokes {@link S3Client#deleteBucketIfEmpty} followed by
    * {@link S3Client#bucketExists} until it is true.
    */
   public static boolean deleteAndVerifyContainerGone(S3Client sync, String container) {
      sync.deleteBucketIfEmpty(container);
      return sync.bucketExists(container);
   }
}