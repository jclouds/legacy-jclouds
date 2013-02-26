/*
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

package org.jclouds.oauth.v2.functions;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.jclouds.domain.Credentials;
import org.jclouds.location.Provider;
import org.jclouds.oauth.v2.domain.OAuthCredentials;
import org.jclouds.rest.AuthorizationException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static java.lang.String.format;
import static org.jclouds.crypto.Pems.privateKeySpec;
import static org.jclouds.io.Payloads.newStringPayload;
import static org.jclouds.oauth.v2.OAuthConstants.NO_ALGORITHM;
import static org.jclouds.oauth.v2.OAuthConstants.OAUTH_ALGORITHM_NAMES_TO_KEYFACTORY_ALGORITHM_NAMES;
import static org.jclouds.oauth.v2.config.OAuthProperties.SIGNATURE_OR_MAC_ALGORITHM;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;

/**
 * Loads {@link OAuthCredentials} from a pem private key using the KeyFactory obtained from the JWT Algorithm
 * Name<->KeyFactory name mapping in OAuthConstants. The pem pk algorithm must match the KeyFactory algorithm.
 *
 * @author David Alves
 * @see org.jclouds.oauth.v2.OAuthConstants#OAUTH_ALGORITHM_NAMES_TO_KEYFACTORY_ALGORITHM_NAMES
 */
@Singleton
public class OAuthCredentialsSupplier implements Supplier<OAuthCredentials> {

   private final Supplier<Credentials> creds;
   private final LoadingCache<Credentials, OAuthCredentials> keyCache;

   @Inject
   public OAuthCredentialsSupplier(@Provider Supplier<Credentials> creds, OAuthCredentialsForCredentials loader,
                                   @Named(SIGNATURE_OR_MAC_ALGORITHM) String signatureOrMacAlgorithm) {
      this.creds = creds;
      checkArgument(OAUTH_ALGORITHM_NAMES_TO_KEYFACTORY_ALGORITHM_NAMES.containsKey(signatureOrMacAlgorithm),
              format("No mapping for key factory for algorithm: %s", signatureOrMacAlgorithm));
      // throw out the private key related to old credentials
      this.keyCache = CacheBuilder.newBuilder().maximumSize(2).build(checkNotNull(loader, "loader"));
   }

   /**
    * it is relatively expensive to extract a private key from a PEM. cache the relationship between current credentials
    * so that the private key is only recalculated once.
    */
   @VisibleForTesting
   static class OAuthCredentialsForCredentials extends CacheLoader<Credentials, OAuthCredentials> {
      private final String keyFactoryAlgorithm;

      @Inject
      public OAuthCredentialsForCredentials(@Named(SIGNATURE_OR_MAC_ALGORITHM) String signatureOrMacAlgorithm) {
         this.keyFactoryAlgorithm = OAUTH_ALGORITHM_NAMES_TO_KEYFACTORY_ALGORITHM_NAMES.get(checkNotNull(
                 signatureOrMacAlgorithm, "signatureOrMacAlgorithm"));
      }

      @Override
      public OAuthCredentials load(Credentials in) {
         try {
            String identity = in.identity;
            String privateKeyInPemFormat = in.credential;
            if (keyFactoryAlgorithm.equals(NO_ALGORITHM)) {
               return new OAuthCredentials.Builder().identity(identity).credential(privateKeyInPemFormat).build();
            }
            KeyFactory keyFactory = KeyFactory.getInstance(keyFactoryAlgorithm);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec(newStringPayload(privateKeyInPemFormat)));
            return new OAuthCredentials.Builder().identity(identity).credential(privateKeyInPemFormat)
                    .privateKey(privateKey).build();
         } catch (IOException e) {
            throw propagate(e);
            // catch security exceptions InvalidKeySpecException and NoSuchAlgorithmException as GSE
         } catch (GeneralSecurityException e) {
            throw new AuthorizationException("security exception loading credentials. " + e.getMessage(), e);
            // catch IAE that is thrown when parsing the pk fails
         } catch (IllegalArgumentException e) {
            throw new AuthorizationException("cannot parse pk. " + e.getMessage(), e);
         }
      }
   }

   @Override
   public OAuthCredentials get() {
      try {
         // loader always throws UncheckedExecutionException so no point in using get()
         return keyCache.getUnchecked(checkNotNull(creds.get(), "credential supplier returned null"));
      } catch (UncheckedExecutionException e) {
         Throwable authorizationException = getFirstThrowableOfType(e, AuthorizationException.class);
         if (authorizationException != null) {
            throw ((AuthorizationException) authorizationException);
         }
         throw propagate(e);
      }
   }

}
