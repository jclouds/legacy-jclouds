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
package org.jclouds.aws.s3.filters;

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.Constants.PROPERTY_CREDENTIAL;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_SERVICE_PATH;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;
import static org.jclouds.util.Patterns.NEWLINE_PATTERN;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.Constants;
import org.jclouds.aws.s3.Bucket;
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
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Signs the S3 request.
 * 
 * @see <a href= "http://docs.amazonwebservices.com/AmazonS3/latest/RESTAuthentication.html" />
 * @author Adrian Cole
 * 
 */
@Singleton
public class RequestAuthorizeSignature implements HttpRequestFilter, RequestSigner {
   private final String[] firstHeadersToSign = new String[] { HttpHeaders.DATE };

   public static Set<String> SPECIAL_QUERIES = ImmutableSet.of("acl", "torrent", "logging", "location",
            "requestPayment");
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

   public void filter(HttpRequest request) throws HttpException {
      replaceDateHeader(request);
      String toSign = createStringToSign(request);
      calculateAndReplaceAuthHeader(request, toSign);
      utils.logRequest(signatureLog, request, "<<");
   }

   public String createStringToSign(HttpRequest request) {
      utils.logRequest(signatureLog, request, ">>");
      StringBuilder buffer = new StringBuilder();
      // re-sign the request
      appendMethod(request, buffer);
      appendPayloadMetadata(request, buffer);
      appendHttpHeaders(request, buffer);
      appendAmzHeaders(request, buffer);
      if (isVhostStyle)
         appendBucketName(request, buffer);
      appendUriPath(request, buffer);
      if (signatureWire.enabled())
         signatureWire.output(buffer.toString());
      return buffer.toString();
   }

   void calculateAndReplaceAuthHeader(HttpRequest request, String toSign) throws HttpException {
      String signature = sign(toSign);
      if (signatureWire.enabled())
         signatureWire.input(Utils.toInputStream(signature));
      request.getHeaders().replaceValues(HttpHeaders.AUTHORIZATION,
               Collections.singletonList(authTag + " " + accessKey + ":" + signature));
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

   void replaceDateHeader(HttpRequest request) {
      request.getHeaders().replaceValues(HttpHeaders.DATE, Collections.singletonList(timeStampProvider.get()));
   }

   void appendAmzHeaders(HttpRequest request, StringBuilder toSign) {
      Set<String> headers = new TreeSet<String>(request.getHeaders().keySet());
      for (String header : headers) {
         if (header.startsWith("x-" + headerTag + "-")) {
            toSign.append(header.toLowerCase()).append(":");
            for (String value : request.getHeaders().get(header)) {
               toSign.append(Utils.replaceAll(value, NEWLINE_PATTERN, "")).append(",");
            }
            toSign.deleteCharAt(toSign.lastIndexOf(","));
            toSign.append("\n");
         }
      }
   }

   void appendPayloadMetadata(HttpRequest request, StringBuilder buffer) {
      buffer.append(utils.valueOrEmpty(request.getPayload() == null ? null : request.getPayload().getContentMD5()))
               .append("\n");
      buffer.append(utils.valueOrEmpty(request.getPayload() == null ? null : request.getPayload().getContentType()))
               .append("\n");
   }

   void appendHttpHeaders(HttpRequest request, StringBuilder toSign) {
      for (String header : firstHeadersToSign)
         toSign.append(valueOrEmpty(request.getHeaders().get(header))).append("\n");
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
            bucketName = (String) request.getArgs()[i];
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
         StringBuilder paramsToSign = new StringBuilder("?");

         String[] params = request.getEndpoint().getQuery().split("&");
         for (String param : params) {
            String[] paramNameAndValue = param.split("=");

            if (SPECIAL_QUERIES.contains(paramNameAndValue[0])) {
               paramsToSign.append(paramNameAndValue[0]);
            }
         }

         if (paramsToSign.length() > 1) {
            toSign.append(paramsToSign);
         }
      }
   }

   private String valueOrEmpty(Collection<String> collection) {
      return (collection != null && collection.size() >= 1) ? collection.iterator().next() : "";
   }
}