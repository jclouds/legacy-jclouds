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
package org.jclouds.aws.s3.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.blobstore.util.BlobStoreUtils.cleanRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.jclouds.aws.s3.AWSS3AsyncClient;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.s3.blobstore.S3BlobRequestSigner;
import org.jclouds.s3.blobstore.functions.BlobToObject;
import org.jclouds.s3.filters.RequestAuthorizeSignature;

import com.google.common.base.Charsets;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Diwaker Gupta
 */
public class AWSS3BlobRequestSigner extends S3BlobRequestSigner<AWSS3AsyncClient> {
   public static final String TEMPORARY_SIGNATURE_PARAM = "Signature";

   private final RequestAuthorizeSignature authSigner;
   private final String identity;
   private final DateService dateService;
   private final Provider<String> timeStampProvider;

   @Inject
   public AWSS3BlobRequestSigner(RestAnnotationProcessor processor, BlobToObject blobToObject,
         BlobToHttpGetOptions blob2HttpGetOptions, Class<AWSS3AsyncClient> interfaceClass,
         @org.jclouds.location.Provider Supplier<Credentials> credentials,
         RequestAuthorizeSignature authSigner, @TimeStamp Provider<String> timeStampProvider,
         DateService dateService) throws SecurityException, NoSuchMethodException {
      super(processor, blobToObject, blob2HttpGetOptions, interfaceClass);
      this.authSigner = authSigner;
      this.identity = credentials.get().identity;
      this.dateService = dateService;
      this.timeStampProvider = timeStampProvider;
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, long timeInSeconds) {
      checkNotNull(container, "container");
      checkNotNull(name, "name");
      throw new UnsupportedOperationException();
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob, long timeInSeconds) {
      checkNotNull(container, "container");
      checkNotNull(blob, "blob");
      throw new UnsupportedOperationException();
   }

   private HttpRequest signForTemporaryAccess(HttpRequest request, long timeInSeconds) {
      // Update the 'DATE' header
      String dateString = request.getFirstHeaderOrNull(HttpHeaders.DATE);
      if (dateString == null) {
         dateString = timeStampProvider.get();
      }
      Date date = dateService.rfc1123DateParse(dateString);
      String expiration = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(date.getTime()) + timeInSeconds);
      HttpRequest.Builder<?> builder = request.toBuilder().replaceHeader(HttpHeaders.DATE, expiration);
      String stringToSign = authSigner.createStringToSign(builder.build());
      // We MUST encode the signature because addQueryParam internally _always_ decodes values
      // and if we don't encode the signature here, the decoding may change the signature. For e.g.
      // any '+' characters in the signature will be converted to space ' ' on decoding.
      String signature = authSigner.sign(stringToSign);
      try {
         signature = URLEncoder.encode(signature, Charsets.UTF_8.name());
      } catch (UnsupportedEncodingException e) {
         throw new IllegalStateException("Bad encoding on input: " + signature, e);
      }
      HttpRequest ret = builder.addQueryParam(HttpHeaders.EXPIRES, expiration)
         .addQueryParam("AWSAccessKeyId", identity)
         // Signature MUST be the last parameter because if it isn't, even encoded '+' values in the
         // signature will be converted to a space by a subsequent addQueryParameter.
         // See HttpRequestTest.testAddBase64AndUrlEncodedQueryParams for more details.
         .addQueryParam(TEMPORARY_SIGNATURE_PARAM, signature)
         .build();
      return ret;
   }
}
