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
package org.jclouds.route53.filters;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.io.BaseEncoding.base64;
import static com.google.common.io.ByteStreams.readBytes;
import static javax.ws.rs.core.HttpHeaders.DATE;
import static org.jclouds.crypto.Macs.asByteProcessor;
import static org.jclouds.util.Strings2.toInputStream;

import java.io.IOException;
import java.security.InvalidKeyException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.aws.domain.SessionCredentials;
import org.jclouds.crypto.Crypto;
import org.jclouds.date.TimeStamp;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.rest.RequestSigner;

import com.google.common.base.Supplier;
import com.google.common.io.ByteProcessor;

/**
 * Signs the Route53 request.
 * 
 * @see <a href=
 *      "http://docs.aws.amazon.com/Route53/latest/DeveloperGuide/RESTAuthentication.html#StringToSign"
 *      />
 * @author Adrian Cole
 * 
 */
@Singleton
public class RestAuthentication implements HttpRequestFilter, RequestSigner {

   private final Supplier<Credentials> creds;
   private final Provider<String> timeStampProvider;
   private final Crypto crypto;

   @Inject
   public RestAuthentication(@org.jclouds.location.Provider Supplier<Credentials> creds,
         @TimeStamp Provider<String> timeStampProvider, Crypto crypto) {
      this.creds = creds;
      this.timeStampProvider = timeStampProvider;
      this.crypto = crypto;
   }

   public HttpRequest filter(HttpRequest request) throws HttpException {
      Credentials current = creds.get();
      if (current instanceof SessionCredentials) {
         request = replaceSecurityTokenHeader(request, SessionCredentials.class.cast(current));
      }
      request = replaceDateHeader(request, timeStampProvider.get());
      String signature = sign(createStringToSign(request));
      return replaceAuthorizationHeader(request, signature);
   }

   private HttpRequest replaceSecurityTokenHeader(HttpRequest request, SessionCredentials current) {
      return request.toBuilder().replaceHeader("x-amz-security-token", current.getSessionToken()).build();
   }

   private HttpRequest replaceDateHeader(HttpRequest request, String timestamp) {
      request = request.toBuilder().replaceHeader(DATE, timestamp).build();
      return request;
   }

   @Override
   public String createStringToSign(HttpRequest input) {
      return input.getFirstHeaderOrNull(DATE);
   }
   
   @Override
   public String sign(String toSign) {
      try {
         ByteProcessor<byte[]> hmacSHA256 = asByteProcessor(crypto.hmacSHA256(creds.get().credential.getBytes(UTF_8)));
         return base64().encode(readBytes(toInputStream(toSign), hmacSHA256));
      } catch (InvalidKeyException e) {
         throw propagate(e);
      } catch (IOException e) {
         throw propagate(e);
      }
   }

   private HttpRequest replaceAuthorizationHeader(HttpRequest request, String signature) {
      request = request
            .toBuilder()
            .replaceHeader("X-Amzn-Authorization",
                  "AWS3-HTTPS AWSAccessKeyId=" + creds.get().identity + ",Algorithm=HmacSHA256,Signature=" + signature)
            .build();
      return request;
   }


}
