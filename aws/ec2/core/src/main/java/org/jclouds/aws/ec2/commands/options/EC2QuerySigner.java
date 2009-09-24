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
package org.jclouds.aws.ec2.commands.options;

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.aws.ec2.reference.CommonEC2Parameters.ACTION;
import static org.jclouds.aws.ec2.reference.CommonEC2Parameters.AWS_ACCESS_KEY_ID;
import static org.jclouds.aws.ec2.reference.CommonEC2Parameters.EXPIRES;
import static org.jclouds.aws.ec2.reference.CommonEC2Parameters.SIGNATURE;
import static org.jclouds.aws.ec2.reference.CommonEC2Parameters.SIGNATURE_METHOD;
import static org.jclouds.aws.ec2.reference.CommonEC2Parameters.SIGNATURE_VERSION;
import static org.jclouds.aws.ec2.reference.CommonEC2Parameters.TIMESTAMP;
import static org.jclouds.aws.ec2.reference.CommonEC2Parameters.VERSION;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.aws.ec2.reference.CommonEC2Parameters;
import org.jclouds.aws.reference.AWSConstants;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.util.DateService;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Contains the base options needed for all EC2 QUERY API operations.<h2>
 * Extend this class in the following way to avoid massive boilerplate code: Usage:
 * <p/>
 * 
 * <pre>
 * public static class MyRequestOptions extends BaseEC2RequestOptions&lt;MyRequestOptions&gt; {
 *    static {
 *       realClass = MyRequestOptions.class;
 *    }
 * 
 *    &#064;Override
 *    public String getAction() {
 *       return &quot;MyRequest&quot;;
 *    }
 * 
 *    public String getId() {
 *       return queryParameters.get(&quot;id&quot;);
 *    }
 * 
 *    public MyRequestOptions withId(String id) {
 *       encodeAndReplaceParameter(&quot;id&quot;, id);
 *       return this;
 *    }
 * 
 *    public static class Builder extends BaseEC2RequestOptions.Builder {
 *       public static MyRequestOptions withId(String id) {
 *          MyRequestOptions options = new MyRequestOptions();
 *          return options.withId(id);
 *       }
 *    }
 * }
 * </pre>
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/Query-Common-Parameters.html"
 *      />
 * @see CommonEC2Parameters
 * @author Adrian Cole
 * 
 */
public class EC2QuerySigner implements HttpRequestFilter {

   public static String[] mandatoryParametersForSignature = new String[] { ACTION,
            SIGNATURE_METHOD, SIGNATURE_VERSION, VERSION };
   private final String accessKey;
   private final String secretKey;
   private final DateService dateService;

   @Inject
   public EC2QuerySigner(@Named(AWSConstants.PROPERTY_AWS_ACCESSKEYID) String accessKey,
            @Named(AWSConstants.PROPERTY_AWS_SECRETACCESSKEY) String secretKey,
            DateService dateService) {
      this.accessKey = accessKey;
      this.secretKey = secretKey;
      this.dateService = dateService;
   }

   public HttpRequest filter(HttpRequest request) throws HttpException {
      validateRequest(request);
      request = addSigningParamsToRequest(request);
      String stringToSign = buildStringToSign(request);
      String signature = sign(stringToSign);
      return addSignatureToRequest(request, signature);
   }

   private void validateRequest(HttpRequest request) {
      for (String parameter : mandatoryParametersForSignature) {
         checkState(request.getEndpoint().getQuery().contains(parameter), "parameter " + parameter
                  + " is required for signature");
      }
      checkState(request.getHeaders().get(HttpHeaders.HOST) != null,
               "request is not ready to sign; host not present");
   }

   private HttpRequest addSignatureToRequest(HttpRequest request, String signature) {
      UriBuilder builder = UriBuilder.fromUri(request.getEndpoint());
      builder.queryParam(SIGNATURE, signature);
      return new HttpRequest(request.getMethod(), builder.build(), request.getHeaders(), request
               .getEntity());
   }

   private String sign(String stringToSign) {
      String signature;
      try {
         signature = HttpUtils.hmacSha256Base64(stringToSign, secretKey.getBytes());
      } catch (Exception e) {
         throw new HttpException("error signing request", e);
      }
      return signature;
   }

   private String buildStringToSign(HttpRequest request) {
      // 1. Sort the UTF-8 query string components by parameter name with natural byte ordering.
      // -- as queryParameters are a SortedSet, they are already sorted.
      // 2. URL encode the parameter name and values according to the following rules...
      // -- all queryParameters are URL encoded on the way in
      // 3. Separate the encoded parameter names from their encoded values with the equals sign,
      // even if the parameter value is empty.
      // -- we do not allow null values.
      // 4. Separate the name-value pairs with an ampersand.
      // -- buildQueryString() does this.
      StringBuilder toSign = new StringBuilder();
      toSign.append(request.getMethod()).append("\n").append(
               request.getEndpoint().getHost().toLowerCase()).append("\n").append("/").append("\n");
      toSign.append(request.getEndpoint().getQuery());
      String stringToSign = toSign.toString();
      return stringToSign;
   }

   private HttpRequest addSigningParamsToRequest(HttpRequest request) {
      UriBuilder builder = UriBuilder.fromUri(request.getEndpoint());
      builder.queryParam(SIGNATURE_METHOD, "HmacSHA256");
      builder.queryParam(SIGNATURE_VERSION, "2");
      builder.queryParam(VERSION, "2009-04-04");

      // timestamp is incompatible with expires
      if (request.getEndpoint().getQuery().contains(EXPIRES)) {
         // TODO tune this if necessary
         builder.queryParam(TIMESTAMP, dateService.iso8601DateFormat());
      }
      builder.queryParam(AWS_ACCESS_KEY_ID, accessKey);
      return new HttpRequest(request.getMethod(), builder.build(), request.getHeaders(), request
               .getEntity());
   }

}
