/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.s3.filters;

import static com.google.common.collect.Iterables.get;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.jclouds.http.utils.Queries.parseQueryToMap;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_SERVICE_PATH;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;
import static org.jclouds.util.Strings2.toInputStream;

import java.util.Collection;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.Constants;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.io.InputSuppliers;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RequestSigner;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.s3.util.S3Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

/**
 * Signs the S3 request.
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AmazonS3/2006-03-01/dev/index.html?RESTAuthentication.html"
 *      />
 * @author Adrian Cole
 * 
 */
@Singleton
public class RequestAuthorizeSignature implements HttpRequestFilter, RequestSigner {

   private static final Collection<String> FIRST_HEADERS_TO_SIGN = ImmutableList.of(HttpHeaders.DATE);

   private static final Set<String> SIGNED_PARAMETERS = ImmutableSet.of("acl", "torrent", "logging", "location", "policy",
            "requestPayment", "versioning", "versions", "versionId", "notification", "uploadId", "uploads",
            "partNumber", "website", "response-content-type", "response-content-language", "response-expires",
            "response-cache-control", "response-content-disposition", "response-content-encoding", "delete");

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
            @Identity String accessKey, @Credential String secretKey,
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
      request = request.toBuilder().replaceHeader(HttpHeaders.AUTHORIZATION, authTag + " " + accessKey + ":" + signature).build();
      return request;
   }

   HttpRequest replaceDateHeader(HttpRequest request) {
      request = request.toBuilder().replaceHeader(HttpHeaders.DATE, timeStampProvider.get()).build();
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
      if (canonicalizedHeaders.containsKey("x-" + headerTag + "-date")) {
         canonicalizedHeaders.removeAll("date");
      }

      appendAmzHeaders(canonicalizedHeaders, buffer);
      appendBucketName(request, buffer);
      appendUriPath(request, buffer);
      if (signatureWire.enabled())
         signatureWire.output(buffer.toString());
      return buffer.toString();
   }

   String calculateSignature(String toSign) throws HttpException {
      String signature = sign(toSign);
      if (signatureWire.enabled())
         signatureWire.input(toInputStream(signature));
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
            toSign.append(String.format("%s:%s\n", key.toLowerCase(), header.getValue()));
         }
      }
   }

   void appendPayloadMetadata(HttpRequest request, StringBuilder buffer) {
      // note that we fall back to headers, and some requests such as ?uploads do not have a
      // payload, yet specify payload related parameters
      buffer.append(
               request.getPayload() == null ? Strings.nullToEmpty(request.getFirstHeaderOrNull("Content-MD5")) :
                        HttpUtils.nullToEmpty(request.getPayload() == null ? null : request.getPayload().getContentMetadata()
                                 .getContentMD5())).append("\n");
      buffer.append(
               Strings.nullToEmpty(request.getPayload() == null ? request.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE)
                        : request.getPayload().getContentMetadata().getContentType())).append("\n");
      for (String header : FIRST_HEADERS_TO_SIGN)
         buffer.append(HttpUtils.nullToEmpty(request.getHeaders().get(header))).append("\n");
   }

   @VisibleForTesting
   void appendHttpHeaders(HttpRequest request, SortedSetMultimap<String, String> canonicalizedHeaders) {
      Multimap<String, String> headers = request.getHeaders();
      for (Entry<String, String> header : headers.entries()) {
         if (header.getKey() == null)
            continue;
         String key = header.getKey().toString().toLowerCase(Locale.getDefault());
         // Ignore any headers that are not particularly interesting.
         if (key.equalsIgnoreCase(HttpHeaders.CONTENT_TYPE) || key.equalsIgnoreCase("Content-MD5")
                  || key.equalsIgnoreCase(HttpHeaders.DATE) || key.startsWith("x-" + headerTag + "-")) {
            canonicalizedHeaders.put(key, header.getValue());
         }
      }
   }

   @VisibleForTesting
   void appendBucketName(HttpRequest req, StringBuilder toSign) {
      String bucketName = S3Utils.getBucketName(req);

      // If we have a payload/bucket/container that is not all lowercase, vhost-style URLs are not an option and must be
      // automatically converted to their path-based equivalent.  This should only be possible for AWS-S3 since it is
      // the only S3 implementation configured to allow uppercase payload/bucket/container names.
      //
      // http://code.google.com/p/jclouds/issues/detail?id=992
      if (isVhostStyle && bucketName!= null && bucketName.equals(bucketName.toLowerCase()))
         toSign.append(servicePath).append(bucketName);
   }

   @VisibleForTesting
   void appendUriPath(HttpRequest request, StringBuilder toSign) {

      toSign.append(request.getEndpoint().getRawPath());

      // ...however, there are a few exceptions that must be included in the
      // signed URI.
      if (request.getEndpoint().getQuery() != null) {
         Multimap<String, String> params = parseQueryToMap(request.getEndpoint().getQuery());
         char separator = '?';
         for (String paramName : Ordering.natural().sortedCopy(params.keySet())) {
            // Skip any parameters that aren't part of the canonical signed string
            if (!SIGNED_PARAMETERS.contains(paramName))
               continue;
            toSign.append(separator).append(paramName);
            String paramValue = get(params.get(paramName), 0);
            if (paramValue != null) {
               toSign.append("=").append(paramValue);
            }
            separator = '&';
         }
      }
   }

}
