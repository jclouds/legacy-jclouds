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
package org.jclouds.atmos.filters;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.io.BaseEncoding.base64;
import static com.google.common.io.ByteStreams.readBytes;
import static org.jclouds.Constants.LOGGER_SIGNATURE;
import static org.jclouds.crypto.Macs.asByteProcessor;
import static org.jclouds.http.Uris.uriBuilder;
import static org.jclouds.util.Strings2.toInputStream;

import java.io.IOException;
import java.net.URI;
import java.security.InvalidKeyException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.crypto.Crypto;
import org.jclouds.date.TimeStamp;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteProcessor;

/**
 * Signs the EMC Atmos Online Storage request.
 * 
 * @see <a href="https://community.emc.com/community/labs/atmos_online" />
 * @author Adrian Cole
 * 
 */
@Singleton
public class ShareUrl implements Function<String, URI> {

   private final Supplier<Credentials> creds;
   private final Supplier<URI> provider;
   private final javax.inject.Provider<Long> timeStampProvider;
   private final Crypto crypto;

   @Resource
   Logger logger = Logger.NULL;

   @Resource
   @Named(LOGGER_SIGNATURE)
   Logger signatureLog = Logger.NULL;

   @Inject
   public ShareUrl(@Provider Supplier<Credentials> creds, @Provider Supplier<URI> provider,
         @TimeStamp javax.inject.Provider<Long> timeStampProvider, Crypto crypto) {
      this.creds = creds;
      this.provider = provider;
      this.timeStampProvider = timeStampProvider;
      this.crypto = crypto;
   }

   @Override
   public URI apply(String path) throws HttpException {
      String requestedResource = new StringBuilder().append("/rest/namespace/").append(path).toString();
      String expires = timeStampProvider.get().toString();
      String signature = signString(createStringToSign(requestedResource, expires));
      return uriBuilder(provider.get())
            .replaceQuery(ImmutableMap.of("uid", creds.get().identity, "expires", expires, "signature", signature))
            .appendPath(requestedResource).build();
   }

   public String createStringToSign(String requestedResource, String expires) {
      StringBuilder toSign = new StringBuilder();
      toSign.append("GET\n");
      toSign.append(requestedResource.toLowerCase()).append("\n");
      toSign.append(creds.get().identity).append("\n");
      toSign.append(expires);
      return toSign.toString();
   }

   public String signString(String toSign) {
      try {
         ByteProcessor<byte[]> hmacSHA1 = asByteProcessor(crypto.hmacSHA1(base64().decode(creds.get().credential)));
         return base64().encode(readBytes(toInputStream(toSign), hmacSHA1));
      } catch (InvalidKeyException e) {
         throw propagate(e);
      } catch (IOException e) {
         throw propagate(e);
      }
   }

}
