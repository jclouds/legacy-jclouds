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
package org.jclouds.atmos.filters;

import static org.jclouds.Constants.LOGGER_SIGNATURE;
import static org.jclouds.Constants.PROPERTY_CREDENTIAL;
import static org.jclouds.Constants.PROPERTY_IDENTITY;

import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpException;
import org.jclouds.io.InputSuppliers;
import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;

/**
 * Signs the EMC Atmos Online Storage request.
 * 
 * @see <a href="https://community.emc.com/community/labs/atmos_online" />
 * @author Adrian Cole
 * 
 */
@Singleton
public class ShareUrl implements Function<String, URI> {

   private final String uid;
   private final byte[] key;
   private final URI provider;
   private final javax.inject.Provider<Long> timeStampProvider;
   private final javax.inject.Provider<UriBuilder> uriBuilders;
   private final Crypto crypto;

   @Resource
   Logger logger = Logger.NULL;

   @Resource
   @Named(LOGGER_SIGNATURE)
   Logger signatureLog = Logger.NULL;

   @Inject
   public ShareUrl(@Named(PROPERTY_IDENTITY) String uid, @Named(PROPERTY_CREDENTIAL) String encodedKey,
            @Provider URI provider, @TimeStamp javax.inject.Provider<Long> timeStampProvider,
            javax.inject.Provider<UriBuilder> uriBuilders, Crypto crypto) {
      this.uid = uid;
      this.key = CryptoStreams.base64(encodedKey);
      this.provider = provider;
      this.uriBuilders = uriBuilders;
      this.timeStampProvider = timeStampProvider;
      this.crypto = crypto;
   }

   @Override
   public URI apply(String path) throws HttpException {
      String requestedResource = new StringBuilder().append("/rest/namespace/").append(path).toString();
      long expires = timeStampProvider.get();
      String signature = signString(createStringToSign(requestedResource, expires));
      return uriBuilders.get().uri(provider).path(requestedResource).queryParam("uid", uid).queryParam("expires",
               expires).queryParam("signature", signature).build();
   }

   public String createStringToSign(String requestedResource, long expires) {
      StringBuilder toSign = new StringBuilder();
      toSign.append("GET\n");
      toSign.append(requestedResource.toLowerCase()).append("\n");
      toSign.append(uid).append("\n");
      toSign.append(expires);
      return toSign.toString();
   }

   public String signString(String toSign) {
      String signature;
      try {
         signature = CryptoStreams.base64(CryptoStreams.mac(InputSuppliers.of(toSign), crypto.hmacSHA1(key)));
      } catch (Exception e) {
         throw new HttpException("error signing request", e);
      }
      return signature;
   }

}
