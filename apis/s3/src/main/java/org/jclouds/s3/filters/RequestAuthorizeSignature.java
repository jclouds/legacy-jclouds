/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.s3.filters;

import static com.google.common.base.Preconditions.checkArgument;

import static org.jclouds.Constants.PROPERTY_CREDENTIAL;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_SERVICE_PATH;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.Constants;
import org.jclouds.s3.Bucket;
import org.jclouds.s3.reference.S3Headers;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.io.InputSuppliers;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RequestSigner;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.util.Strings2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

/**
 * Signs the S3 request.
 * 
 * @see <a href= "http://docs.amazonwebservices.com/AmazonS3/2006-03-01/dev/index.html?RESTAuthentication.html" />
 * @author Adrian Cole
 * 
 */
@Singleton
public class RequestAuthorizeSignature implements HttpRequestFilter, RequestSigner {
   private final String[] firstHeadersToSign = new String[] { HttpHeaders.DATE };

   /** Prefix for general Amazon headers: x-amz- */
   public static final String AMAZON_PREFIX = "x-amz-";

   public static Set<String> SIGNED_PARAMETERS = ImmutableSet.of("acl", "torrent", "logging", "location", "policy", "requestPayment", "versioning",
       "versions", "versionId", "notification", "uploadId", "uploads", "partNumber", "website",
       "response-content-type", "response-content-language", "response-expires", 
       "response-cache-control", "response-content-disposition", "response-content-encoding");
   
   private final SignatureWire signatureWire;
   private final String accessKey;
   private final String secretKey;
   private final Provider<String> timeStampProvider;
   private final Crypto crypto;
   private final HttpUtils utils;

   @Resource
   @Named(Constants.LOGGER_SIGNATURE)
   Logger signatureLog = Logger.NULL;

   private final String authTag;
   private final String headerTag;
   private final String servicePath;
   private final boolean isVhostStyle;

   @Inject
   public RequestAuthorizeSignature(SignatureWire signatureWire, @Named(PROPERTY_AUTH_TAG) String authTag,
            @Named(PROPERTY_S3_VIRTUAL_HOST_BUCKETS) boolean isVhostStyle,
            @Named(PROPERTY_S3_SERVICE_PATH) String servicePath, @Named(PROPERTY_HEADER_TAG) String headerTag,
            @Named(PROPERTY_IDENTITY) String accessKey, @Named(PROPERTY_CREDENTIAL) String secretKey,
            @TimeStamp Provider<String> timeStampProvider, Crypto crypto, HttpUtils utils) {
      this.isVhostStyle = isVhostStyle;
      this.servicePath = servicePath;
      this.headerTag = headerTag;
      this.authTag = authTag;
      this.signatureWire = signatureWire;
      this.accessKey = accessKey;
      this.secretKey = secretKey;
      this.timeStampProvider = timeStampProvider;
      this.crypto = crypto;
      this.utils = utils;
   }

   public HttpRequest filter(HttpRequest request) throws HttpException {
      request = replaceDateHeader(request);
      String signature = calculateSignature(createStringToSign(request));
      request = replaceAuthorizationHeader(request, signature);
      utils.logRequest(signatureLog, request, "<<");
      return request;
   }

   HttpRequest replaceAuthorizationHeader(HttpRequest request, String signature) {
      request = ModifyRequest.replaceHeader(request, HttpHeaders.AUTHORIZATION, authTag + " " + accessKey + ":"
               + signature);
      return request;
   }

   HttpRequest replaceDateHeader(HttpRequest request) {
      Builder<String, String> builder = ImmutableMap.builder();
      String date = timeStampProvider.get();
      builder.put(HttpHeaders.DATE, date);
      request = ModifyRequest.replaceHeaders(request, Multimaps.forMap(builder.build()));
      return request;
   }

   public String createStringToSign(HttpRequest request) {
      utils.logRequest(signatureLog, request, ">>");
      SortedSetMultimap<String, String> canonicalizedHeaders = TreeMultimap.create();            
      StringBuilder buffer = new StringBuilder();
      // re-sign the request
      appendMethod(request, buffer);
      appendPayloadMetadata(request, buffer);
      appendHttpHeaders(request, canonicalizedHeaders);
     
      // Remove default date timestamp if "x-amz-date" is set.
      if (canonicalizedHeaders.containsKey(S3Headers.ALTERNATE_DATE)) {
         canonicalizedHeaders.put("date", "");
      }

      appendAmzHeaders(canonicalizedHeaders, buffer);
      if (isVhostStyle)
         appendBucketName(request, buffer);
      appendUriPath(request, buffer);
      if (signatureWire.enabled())
         signatureWire.output(buffer.toString());
      return buffer.toString();
   }

   String calculateSignature(String toSign) throws HttpException {
      String signature = sign(toSign);
      if (signatureWire.enabled())
         signatureWire.input(Strings2.toInputStream(signature));
      return signature;
   }

   public String sign(String toSign) {
      String signature;
      try {
         signature = CryptoStreams.base64(CryptoStreams.mac(InputSuppliers.of(toSign), crypto.hmacSHA1(secretKey
                  .getBytes())));
      } catch (Exception e) {
         throw new HttpException("error signing request", e);
      }
      return signature;
   }

   void appendMethod(HttpRequest request, StringBuilder toSign) {
      toSign.append(request.getMethod()).append("\n");
   }

   @VisibleForTesting
   void appendAmzHeaders(SortedSetMultimap<String, String> canonicalizedHeaders, StringBuilder toSign) {
      for (Entry<String, String> header : canonicalizedHeaders.entries()) {
         String key = header.getKey();
         if (key.startsWith("x-" + headerTag + "-")) {
            toSign.append(String.format("%s: %s\n", key.toLowerCase(), header.getValue()));
         }
      }
   }

   void appendPayloadMetadata(HttpRequest request, StringBuilder buffer) {
      // the following request parameters are positional in their nature
      buffer.append(
               utils.valueOrEmpty(request.getPayload() == null ? null : request.getPayload().getContentMetadata()
                        .getContentMD5())).append("\n");
      buffer.append(
               utils.valueOrEmpty(request.getPayload() == null ? request.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE)
                        : request.getPayload().getContentMetadata().getContentType())).append("\n");      
      for (String header : firstHeadersToSign)
         buffer.append(valueOrEmpty(request.getHeaders().get(header))).append("\n");
   }

   @VisibleForTesting
   void appendHttpHeaders(HttpRequest request,
         SortedSetMultimap<String, String> canonicalizedHeaders) {
      Multimap<String, String> headers = request.getHeaders();
      for (Entry<String, String> header : headers.entries()) {
         if (header.getKey() == null)
            continue;
         String key = header.getKey().toString()
               .toLowerCase(Locale.getDefault());
         // Ignore any headers that are not particularly interesting.
         if (key.equalsIgnoreCase(HttpHeaders.CONTENT_TYPE) || key.equalsIgnoreCase("Content-MD5")
               || key.equalsIgnoreCase(HttpHeaders.DATE) || key.startsWith(AMAZON_PREFIX)) {
            canonicalizedHeaders.put(key, header.getValue());
         }
      }
   }

   @VisibleForTesting
   void appendBucketName(HttpRequest req, StringBuilder toSign) {
      checkArgument(req instanceof GeneratedHttpRequest<?>, "this should be a generated http request");
      GeneratedHttpRequest<?> request = GeneratedHttpRequest.class.cast(req);

      String bucketName = null;

      for (int i = 0; i < request.getJavaMethod().getParameterAnnotations().length; i++) {
         if (Iterables.any(Arrays.asList(request.getJavaMethod().getParameterAnnotations()[i]),
                  new Predicate<Annotation>() {
                     public boolean apply(Annotation input) {
                        return input.annotationType().equals(Bucket.class);
                     }
                  })) {
            bucketName = (String) request.getArgs().get(i);
            break;
         }
      }

      if (bucketName != null)
         toSign.append(servicePath).append(bucketName);
   }

   @VisibleForTesting
   void appendUriPath(HttpRequest request, StringBuilder toSign) {

      toSign.append(request.getEndpoint().getRawPath());

      // ...however, there are a few exceptions that must be included in the
      // signed URI.
      if (request.getEndpoint().getQuery() != null) {
         SortedSetMultimap<String, String> sortedParams = TreeMultimap.create();
         String[] params = request.getEndpoint().getQuery().split("&");
         for (String param : params) {
            String[] paramNameAndValue = param.split("=");
            sortedParams.put(paramNameAndValue[0], paramNameAndValue.length == 2 ? paramNameAndValue[1] : null);
         }
         char separator = '?';
         for (Entry<String, String> param: sortedParams.entries()) {
            String paramName = param.getKey();
            // Skip any parameters that aren't part of the canonical signed string
            if (SIGNED_PARAMETERS.contains(paramName) == false) continue;

            toSign.append(separator).append(paramName);
            String paramValue = param.getValue();
            if (paramValue != null) {
               toSign.append("=").append(paramValue);
            }
            separator = '&';
         }
      }
   }

   private String valueOrEmpty(Collection<String> collection) {
      return (collection != null && collection.size() >= 1) ? collection.iterator().next() : "";
   }
}
