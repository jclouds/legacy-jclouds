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
package org.jclouds.aws.s3.filters;

import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.jclouds.aws.s3.blobstore.AWSS3BlobRequestSigner.TEMPORARY_SIGNATURE_PARAM;
import static org.jclouds.http.utils.Queries.queryParser;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_SERVICE_PATH;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.crypto.Crypto;
import org.jclouds.date.TimeStamp;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.s3.filters.RequestAuthorizeSignature;

import com.google.common.base.Supplier;

/**
 * Signs the AWS S3 request, supporting temporary signatures.
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AmazonS3/2006-03-01/dev/index.html?RESTAuthentication.html"
 *      />
 * 
 */
@Singleton
public class AWSRequestAuthorizeSignature extends RequestAuthorizeSignature {

   @Inject
   public AWSRequestAuthorizeSignature(SignatureWire signatureWire, @Named(PROPERTY_AUTH_TAG) String authTag,
            @Named(PROPERTY_S3_VIRTUAL_HOST_BUCKETS) boolean isVhostStyle,
            @Named(PROPERTY_S3_SERVICE_PATH) String servicePath, @Named(PROPERTY_HEADER_TAG) String headerTag,
            @org.jclouds.location.Provider Supplier<Credentials> creds,
            @TimeStamp Provider<String> timeStampProvider, Crypto crypto, HttpUtils utils) {
      super(signatureWire, authTag, isVhostStyle, servicePath, headerTag, creds, timeStampProvider, crypto, 
             utils);
   }

   @Override
   protected HttpRequest replaceAuthorizationHeader(HttpRequest request, String signature) {
      /* 
       * Only add the Authorization header if the query string doesn't already contain
       * the 'Signature' parameter, otherwise S3 will fail the request complaining about
       * duplicate authentication methods. The 'Signature' parameter will be added for signed URLs
       * with expiration.
       */
      if (queryParser().apply(request.getEndpoint().getQuery()).containsKey(TEMPORARY_SIGNATURE_PARAM)) {
         return request;
      }
      return super.replaceAuthorizationHeader(request, signature);
   }
}
