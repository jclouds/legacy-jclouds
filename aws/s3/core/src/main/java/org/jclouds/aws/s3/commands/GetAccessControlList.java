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
package org.jclouds.aws.s3.commands;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.http.HttpMethod;
import org.jclouds.http.commands.callables.xml.ParseSax;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * A GET request operation directed at an object or bucket URI with the "acl" parameter retrieves
 * the Access Control List (ACL) settings for that S3 item.
 * <p />
 * To list a bucket or object's ACL, you must have READ_ACP access to the item.
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/RESTAccessPolicy.html"/>
 * @author James Murty
 */
public class GetAccessControlList extends S3FutureCommand<AccessControlList> {

   @Inject
   public GetAccessControlList(URI endPoint, ParseSax<AccessControlList> accessControlListParser,
            @Assisted("bucketName") String bucket) {
      super(endPoint, HttpMethod.GET, "/?acl", accessControlListParser, bucket);
   }

   @Inject
   public GetAccessControlList(URI endPoint, ParseSax<AccessControlList> accessControlListParser,
            @Assisted("bucketName") String bucket, @Assisted("objectKey") String objectKey) {
      super(endPoint, HttpMethod.GET, "/" + objectKey + "?acl", accessControlListParser, bucket);
   }

   @Override
   public AccessControlList get() throws InterruptedException, ExecutionException {
      try {
         return super.get();
      } catch (ExecutionException e) {
         return attemptNotFound(e);
      }
   }

   @VisibleForTesting
   AccessControlList attemptNotFound(ExecutionException e) throws ExecutionException {
      if (e.getCause() != null && e.getCause() instanceof AWSResponseException) {
         AWSResponseException responseException = (AWSResponseException) e.getCause();
         if ("NoSuchBucket".equals(responseException.getError().getCode())
                  || "NoSuchObject".equals(responseException.getError().getCode())) {
            return AccessControlList.NOT_FOUND;
         }
      }
      throw e;
   }

   @Override
   public AccessControlList get(long l, TimeUnit timeUnit) throws InterruptedException,
            ExecutionException, TimeoutException {
      try {
         return super.get(l, timeUnit);
      } catch (ExecutionException e) {
         return attemptNotFound(e);
      }
   }
}