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
package org.jclouds.cloudstack.filters;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.io.BaseEncoding.base64;
import static com.google.common.io.ByteStreams.readBytes;
import static org.jclouds.Constants.LOGGER_SIGNATURE;
import static org.jclouds.crypto.Macs.asByteProcessor;
import static org.jclouds.http.Uris.uriBuilder;
import static org.jclouds.http.utils.Queries.queryParser;
import static org.jclouds.util.Strings2.toInputStream;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Joiner;
import org.jclouds.crypto.Crypto;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RequestSigner;
import org.jclouds.util.Strings2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteProcessor;

/**
 * 
 * @see <a href= "http://download.cloud.com/releases/2.2.0/api/user/2.2api_security_details.html" />
 * @author Adrian Cole
 * 
 */
@Singleton
public class QuerySigner implements AuthenticationFilter, RequestSigner {

   private final SignatureWire signatureWire;
   private final Supplier<Credentials> creds;
   private final Crypto crypto;
   private final HttpUtils utils;

   @Resource
   @Named(LOGGER_SIGNATURE)
   private Logger signatureLog = Logger.NULL;

   @Inject
   public QuerySigner(SignatureWire signatureWire, @Provider Supplier<Credentials> creds, Crypto crypto, HttpUtils utils) {
      this.signatureWire = signatureWire;
      this.creds = creds;
      this.crypto = crypto;
      this.utils = utils;
   }

   public HttpRequest filter(HttpRequest request) throws HttpException {
      checkNotNull(request, "request must be present");
      Multimap<String, String> decodedParams = queryParser().apply(request.getEndpoint().getRawQuery());
      addSigningParams(decodedParams);
      String stringToSign = createStringToSign(request, decodedParams);
      String signature = sign(stringToSign);
      addSignature(decodedParams, signature);
      request = request.toBuilder().endpoint(uriBuilder(request.getEndpoint()).query(decodedParams).build()).build();
      utils.logRequest(signatureLog, request, "<<");
      return request;
   }

   @VisibleForTesting
   void addSignature(Multimap<String, String> params, String signature) {
      params.replaceValues("signature", ImmutableList.of(signature));
   }

   @VisibleForTesting
   public String sign(String toSign) {
      String signature;
      try {
         ByteProcessor<byte[]> hmacSHA1 = asByteProcessor(crypto.hmacSHA1(creds.get().credential.getBytes()));
         signature = base64().encode(readBytes(toInputStream(toSign), hmacSHA1));
         if (signatureWire.enabled())
            signatureWire.input(toInputStream(signature));
         return signature;
      } catch (InvalidKeyException e) {
         throw propagate(e);
      } catch (IOException e) {
         throw propagate(e);
      }
   }

   @VisibleForTesting
   public String createStringToSign(HttpRequest request, Multimap<String, String> decodedParams) {
      utils.logRequest(signatureLog, request, ">>");
      // encode each parameter value first,
      ImmutableSortedSet.Builder<String> builder = ImmutableSortedSet.naturalOrder();
      for (Map.Entry<String, String> entry : decodedParams.entries())
         builder.add(entry.getKey() + "=" + Strings2.urlEncode(entry.getValue()));
      // then, lower case the entire query string
      String stringToSign = Joiner.on('&').join(builder.build()).toLowerCase();
      if (signatureWire.enabled())
         signatureWire.output(stringToSign);

      return stringToSign;
   }

   @VisibleForTesting
   void addSigningParams(Multimap<String, String> params) {
      params.replaceValues("apiKey", ImmutableList.of(creds.get().identity));
      params.removeAll("signature");
   }

   public String createStringToSign(HttpRequest input) {
      Multimap<String, String> decodedParams = queryParser().apply(input.getEndpoint().getQuery());
      addSigningParams(decodedParams);
      return createStringToSign(input, decodedParams);
   }

}
