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
package org.jclouds.aws.filters;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.aws.ec2.reference.EC2Parameters.ACTION;
import static org.jclouds.aws.ec2.reference.EC2Parameters.AWS_ACCESS_KEY_ID;
import static org.jclouds.aws.ec2.reference.EC2Parameters.SIGNATURE;
import static org.jclouds.aws.ec2.reference.EC2Parameters.SIGNATURE_METHOD;
import static org.jclouds.aws.ec2.reference.EC2Parameters.SIGNATURE_VERSION;
import static org.jclouds.aws.ec2.reference.EC2Parameters.TIMESTAMP;
import static org.jclouds.aws.ec2.reference.EC2Parameters.VERSION;
import static org.jclouds.http.HttpUtils.logRequest;
import static org.jclouds.http.HttpUtils.makeQueryLine;
import static org.jclouds.http.HttpUtils.parseQueryToMap;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.Constants;
import org.jclouds.date.TimeStamp;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RequestSigner;
import org.jclouds.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

/**
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/Form-Common-Parameters.html"
 *      />
 * @author Adrian Cole
 * 
 */
@Singleton
public class FormSigner implements HttpRequestFilter, RequestSigner {

   public static String[] mandatoryParametersForSignature = new String[] { ACTION,
            SIGNATURE_METHOD, SIGNATURE_VERSION, VERSION };
   private final SignatureWire signatureWire;
   private final String accessKey;
   private final String secretKey;
   private final Provider<String> dateService;
   private final EncryptionService encryptionService;
   @Resource
   @Named(Constants.LOGGER_SIGNATURE)
   private Logger signatureLog = Logger.NULL;

   @Inject
   public FormSigner(SignatureWire signatureWire,
            @Named(Constants.PROPERTY_IDENTITY) String accessKey,
            @Named(Constants.PROPERTY_CREDENTIAL) String secretKey,
            @TimeStamp Provider<String> dateService, EncryptionService encryptionService) {
      this.signatureWire = signatureWire;
      this.accessKey = accessKey;
      this.secretKey = secretKey;
      this.dateService = dateService;
      this.encryptionService = encryptionService;
   }

   public void filter(HttpRequest request) throws HttpException {
      checkNotNull(request.getFirstHeaderOrNull(HttpHeaders.HOST),
               "request is not ready to sign; host not present");
      Multimap<String, String> decodedParams = parseQueryToMap(request.getPayload().getRawContent()
               .toString());
      request.getHeaders().removeAll(HttpHeaders.CONTENT_LENGTH);
      addSigningParams(decodedParams);
      validateParams(decodedParams);
      String stringToSign = createStringToSign(request, decodedParams);
      String signature = sign(stringToSign);
      addSignature(decodedParams, signature);
      setPayload(request, decodedParams);
      logRequest(signatureLog, request, "<<");
   }

   String[] sortForSigning(String queryLine) {
      String[] parts = queryLine.split("&");
      // 1. Sort the UTF-8 query string components by parameter name with natural byte ordering.
      Arrays.sort(parts, new Comparator<String>() {

         public int compare(String o1, String o2) {
            if (o1.startsWith("AWSAccessKeyId"))
               return -1;
            return o1.compareTo(o2);
         }

      });
      return parts;
   }

   void setPayload(HttpRequest request, Multimap<String, String> decodedParams) {
      request.setPayload(makeQueryLine(decodedParams, new Comparator<Map.Entry<String, String>>() {
         public int compare(Entry<String, String> o1, Entry<String, String> o2) {
            if (o1.getKey().startsWith("Action") || o2.getKey().startsWith("AWSAccessKeyId"))
               return -1;
            if (o1.getKey().startsWith("AWSAccessKeyId") || o2.getKey().startsWith("Action"))
               return 1;
            return o1.getKey().compareTo(o2.getKey());
         }
      }));
   }

   @VisibleForTesting
   void validateParams(Multimap<String, String> params) {
      for (String parameter : mandatoryParametersForSignature) {
         checkState(params.containsKey(parameter), "parameter " + parameter
                  + " is required for signature");
      }
   }

   @VisibleForTesting
   void addSignature(Multimap<String, String> params, String signature) {
      params.replaceValues(SIGNATURE, ImmutableList.of(signature));
   }

   @VisibleForTesting
   public String sign(String stringToSign) {
      String signature;
      try {
         signature = encryptionService.base64(encryptionService.hmacSha256(stringToSign,
                  secretKey.getBytes()));
         if (signatureWire.enabled())
            signatureWire.input(Utils.toInputStream(signature));
      } catch (Exception e) {
         throw new HttpException("error signing request", e);
      }
      return signature;
   }

   @VisibleForTesting
   public String createStringToSign(HttpRequest request, Multimap<String, String> decodedParams) {
      logRequest(signatureLog, request, ">>");
      StringBuilder stringToSign = new StringBuilder();
      // StringToSign = HTTPVerb + "\n" +
      stringToSign.append(request.getMethod()).append("\n");
      // ValueOfHostHeaderInLowercase + "\n" +
      stringToSign.append(request.getFirstHeaderOrNull(HttpHeaders.HOST).toLowerCase())
               .append("\n");
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
      return makeQueryLine(decodedParams, new Comparator<Map.Entry<String, String>>() {
         public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
            if (o1.getKey().startsWith("AWSAccessKeyId"))
               return -1;
            return o1.getKey().compareTo(o2.getKey());
         }
      });
   }

   @VisibleForTesting
   void addSigningParams(Multimap<String, String> params) {
      params.replaceValues(SIGNATURE_METHOD, ImmutableList.of("HmacSHA256"));
      params.replaceValues(SIGNATURE_VERSION, ImmutableList.of("2"));
      params.replaceValues(TIMESTAMP, ImmutableList.of(dateService.get()));
      params.replaceValues(AWS_ACCESS_KEY_ID, ImmutableList.of(accessKey));
      params.removeAll(SIGNATURE);
   }

   public String createStringToSign(HttpRequest input) {
      return createStringToSign(input, parseQueryToMap(input.getPayload().getRawContent()
               .toString()));
   }

}
