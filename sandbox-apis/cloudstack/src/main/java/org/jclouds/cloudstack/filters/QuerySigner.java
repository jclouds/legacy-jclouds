/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.cloudstack.filters;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.Constants;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.io.InputSuppliers;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RequestSigner;
import org.jclouds.util.Strings2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multimap;

/**
 * 
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api/user/2.2api_security_details.html"
 *      />
 * @author Adrian Cole
 * 
 */
@Singleton
public class QuerySigner implements HttpRequestFilter, RequestSigner {

   private final SignatureWire signatureWire;
   private final String accessKey;
   private final String secretKey;
   private final Crypto crypto;
   private final HttpUtils utils;
   private final Provider<UriBuilder> builder;

   @Resource
   @Named(Constants.LOGGER_SIGNATURE)
   private Logger signatureLog = Logger.NULL;

   @Inject
   public QuerySigner(SignatureWire signatureWire, @Named(Constants.PROPERTY_IDENTITY) String accessKey,
         @Named(Constants.PROPERTY_CREDENTIAL) String secretKey, Crypto crypto, HttpUtils utils,
         Provider<UriBuilder> builder) {
      this.signatureWire = signatureWire;
      this.accessKey = accessKey;
      this.secretKey = secretKey;
      this.crypto = crypto;
      this.utils = utils;
      this.builder = builder;
   }

   public HttpRequest filter(HttpRequest request) throws HttpException {
      checkNotNull(request, "request must be present");
      Multimap<String, String> decodedParams = ModifyRequest.parseQueryToMap(request.getEndpoint().getQuery());
      addSigningParams(decodedParams);
      String stringToSign = createStringToSign(request, decodedParams);
      String signature = sign(stringToSign);
      addSignature(decodedParams, signature);
      request = request
            .toBuilder()
            .endpoint(
                  builder.get().uri(request.getEndpoint())
                        .replaceQuery(ModifyRequest.makeQueryLine(decodedParams, null)).build()).build();
      utils.logRequest(signatureLog, request, "<<");
      return request;
   }

   @VisibleForTesting
   void addSignature(Multimap<String, String> params, String signature) {
      params.replaceValues("signature", ImmutableList.of(signature));
   }

   @VisibleForTesting
   public String sign(String stringToSign) {
      String signature;
      try {
         signature = CryptoStreams.base64(CryptoStreams.mac(InputSuppliers.of(stringToSign),
               crypto.hmacSHA1(secretKey.getBytes())));
         if (signatureWire.enabled())
            signatureWire.input(Strings2.toInputStream(signature));
      } catch (Exception e) {
         throw new HttpException("error signing request", e);
      }
      return signature;
   }

   @VisibleForTesting
   public String createStringToSign(HttpRequest request, Multimap<String, String> decodedParams) {
      utils.logRequest(signatureLog, request, ">>");

      // encode each parameter value first,
      ImmutableSortedSet.Builder<String> builder = ImmutableSortedSet.<String> naturalOrder();
      for (Entry<String, String> entry : decodedParams.entries())
         builder.add(entry.getKey() + "=" + Strings2.urlEncode(entry.getValue()));
      
      // then, lower case the entire query string
      String stringToSign = Joiner.on('&').join(builder.build()).toLowerCase();
      if (signatureWire.enabled())
         signatureWire.output(stringToSign);

      return stringToSign;
   }

   @VisibleForTesting
   void addSigningParams(Multimap<String, String> params) {
      params.replaceValues("apiKey", ImmutableList.of(accessKey));
      params.removeAll("signature");
   }

   public String createStringToSign(HttpRequest input) {
      Multimap<String, String> decodedParams = ModifyRequest.parseQueryToMap(input.getEndpoint().getQuery());
      addSigningParams(decodedParams);
      return createStringToSign(input, decodedParams);
   }

}
