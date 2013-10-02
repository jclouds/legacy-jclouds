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
package org.jclouds.aws.filters;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Ordering.natural;
import static com.google.common.io.BaseEncoding.base64;
import static com.google.common.io.ByteStreams.readBytes;
import static org.jclouds.aws.reference.FormParameters.ACTION;
import static org.jclouds.aws.reference.FormParameters.AWS_ACCESS_KEY_ID;
import static org.jclouds.aws.reference.FormParameters.SECURITY_TOKEN;
import static org.jclouds.aws.reference.FormParameters.SIGNATURE;
import static org.jclouds.aws.reference.FormParameters.SIGNATURE_METHOD;
import static org.jclouds.aws.reference.FormParameters.SIGNATURE_VERSION;
import static org.jclouds.aws.reference.FormParameters.TIMESTAMP;
import static org.jclouds.aws.reference.FormParameters.VERSION;
import static org.jclouds.crypto.Macs.asByteProcessor;
import static org.jclouds.http.utils.Queries.encodeQueryLine;
import static org.jclouds.http.utils.Queries.queryParser;
import static org.jclouds.util.Strings2.toInputStream;

import java.util.Comparator;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.Constants;
import org.jclouds.aws.domain.SessionCredentials;
import org.jclouds.crypto.Crypto;
import org.jclouds.date.TimeStamp;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RequestSigner;
import org.jclouds.rest.annotations.ApiVersion;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import com.google.common.io.ByteProcessor;

/**
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/Query-Common-Parameters.html"
 *      />
 * @author Adrian Cole
 * 
 */
@Singleton
public class FormSigner implements HttpRequestFilter, RequestSigner {

   public static final Set<String> mandatoryParametersForSignature = ImmutableSet.of(ACTION, SIGNATURE_METHOD,
         SIGNATURE_VERSION, VERSION);

   private final SignatureWire signatureWire;
   private final String apiVersion;
   private final Supplier<Credentials> creds;
   private final Provider<String> dateService;
   private final Crypto crypto;
   private final HttpUtils utils;

   @Resource
   @Named(Constants.LOGGER_SIGNATURE)
   private Logger signatureLog = Logger.NULL;

   @Inject
   public FormSigner(SignatureWire signatureWire, @ApiVersion String apiVersion,
         @org.jclouds.location.Provider Supplier<Credentials> creds, @TimeStamp Provider<String> dateService,
         Crypto crypto, HttpUtils utils) {
      this.signatureWire = signatureWire;
      this.apiVersion = apiVersion;
      this.creds = creds;
      this.dateService = dateService;
      this.crypto = crypto;
      this.utils = utils;
   }

   public HttpRequest filter(HttpRequest request) throws HttpException {
      checkNotNull(request.getFirstHeaderOrNull(HttpHeaders.HOST), "request is not ready to sign; host not present");
      Multimap<String, String> decodedParams = queryParser().apply(request.getPayload().getRawContent().toString()); 
      decodedParams.replaceValues(VERSION, ImmutableSet.of(apiVersion));
      addSigningParams(decodedParams);
      validateParams(decodedParams);
      String stringToSign = createStringToSign(request, decodedParams);
      String signature = sign(stringToSign);
      addSignature(decodedParams, signature);
      request = setPayload(request, decodedParams);
      utils.logRequest(signatureLog, request, "<<");
      return request;
   }
   
   HttpRequest setPayload(HttpRequest request, Multimap<String, String> decodedParams) {
      String queryLine = buildQueryLine(decodedParams);
      request.setPayload(queryLine);
      request.getPayload().getContentMetadata().setContentType("application/x-www-form-urlencoded");
      return request;
   }

   private static final Comparator<String> actionFirstAccessKeyLast = new Comparator<String>() {
      static final int LEFT_IS_GREATER = 1;
      static final int RIGHT_IS_GREATER = -1;

      @Override
      public int compare(String left, String right) {
         if (left == right) {
            return 0;
         }
         if ("Action".equals(right) || "AWSAccessKeyId".equals(left)) {
            return LEFT_IS_GREATER;
         }
         if ("Action".equals(left) || "AWSAccessKeyId".equals(right)) {
            return RIGHT_IS_GREATER;
         }
         return natural().compare(left, right);
      }
   };

   private static String buildQueryLine(Multimap<String, String> decodedParams) {
      Multimap<String, String> sortedParams = TreeMultimap.create(actionFirstAccessKeyLast, natural());
      sortedParams.putAll(decodedParams);
      return encodeQueryLine(sortedParams);
   }

   @VisibleForTesting
   void validateParams(Multimap<String, String> params) {
      for (String parameter : mandatoryParametersForSignature) {
         checkState(params.containsKey(parameter), "parameter " + parameter + " is required for signature");
      }
   }

   @VisibleForTesting
   void addSignature(Multimap<String, String> params, String signature) {
      params.replaceValues(SIGNATURE, ImmutableList.of(signature));
   }

   @VisibleForTesting
   public String sign(String toSign) {
      String signature;
      try {
         ByteProcessor<byte[]> hmacSHA256 = asByteProcessor(crypto.hmacSHA256(creds.get().credential.getBytes(UTF_8)));
         signature = base64().encode(readBytes(toInputStream(toSign), hmacSHA256));
         if (signatureWire.enabled())
            signatureWire.input(toInputStream(signature));
      } catch (Exception e) {
         throw new HttpException("error signing request", e);
      }
      return signature;
   }

   @VisibleForTesting
   public String createStringToSign(HttpRequest request, Multimap<String, String> decodedParams) {
      utils.logRequest(signatureLog, request, ">>");
      StringBuilder stringToSign = new StringBuilder();
      // StringToSign = HTTPVerb + "\n" +
      stringToSign.append(request.getMethod()).append("\n");
      // ValueOfHostHeaderInLowercase + "\n" +
      stringToSign.append(request.getFirstHeaderOrNull(HttpHeaders.HOST).toLowerCase()).append("\n");
      // HTTPRequestURI + "\n" +
      stringToSign.append(request.getEndpoint().getPath()).append("\n");
      // CanonicalizedFormString <from the preceding step>
      stringToSign.append(buildCanonicalizedString(decodedParams));
      if (signatureWire.enabled())
         signatureWire.output(stringToSign.toString());
      return stringToSign.toString();
   }

   @VisibleForTesting
   String buildCanonicalizedString(Multimap<String, String> decodedParams) {
      // note that aws wants to percent encode the canonicalized string without skipping '/' and '?'
      return encodeQueryLine(TreeMultimap.create(decodedParams), ImmutableList.<Character> of());
   }


   @VisibleForTesting
   void addSigningParams(Multimap<String, String> params) {
      params.removeAll(SIGNATURE);
      params.removeAll(SECURITY_TOKEN);
      Credentials current = creds.get();
      if (current instanceof SessionCredentials) {
         params.put(SECURITY_TOKEN, SessionCredentials.class.cast(current).getSessionToken());
      }
      params.replaceValues(SIGNATURE_METHOD, ImmutableList.of("HmacSHA256"));
      params.replaceValues(SIGNATURE_VERSION, ImmutableList.of("2"));
      params.replaceValues(TIMESTAMP, ImmutableList.of(dateService.get()));
      params.replaceValues(AWS_ACCESS_KEY_ID, ImmutableList.of(creds.get().identity));
   }

   public String createStringToSign(HttpRequest input) {
      return createStringToSign(input, queryParser().apply(input.getPayload().getRawContent().toString()));
   }

}
