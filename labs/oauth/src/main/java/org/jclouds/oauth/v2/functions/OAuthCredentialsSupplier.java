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

import com.google.common.base.Supplier;
import org.jclouds.crypto.Pems;
import org.jclouds.io.Payloads;
import org.jclouds.oauth.v2.domain.OAuthCredentials;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static org.jclouds.oauth.v2.OAuthConstants.NO_ALGORITHM;
import static org.jclouds.oauth.v2.OAuthConstants.OAUTH_ALGORITHM_NAMES_TO_KEYFACTORY_ALGORITHM_NAMES;
import static org.jclouds.oauth.v2.config.OAuthProperties.SIGNATURE_OR_MAC_ALGORITHM;

/**
 * Loads {@link OAuthCredentials} from a pem private key using the KeyFactory obtained from the
 * JWT Algorithm Name<->KeyFactory name mapping in OAuthConstants. The pem pk algorithm must match the KeyFactory
 * algorithm.
 *
 * @author David Alves
 * @see org.jclouds.oauth.v2.OAuthConstants#OAUTH_ALGORITHM_NAMES_TO_KEYFACTORY_ALGORITHM_NAMES
 */
@Singleton
public class OAuthCredentialsSupplier implements Supplier<OAuthCredentials> {


   private final String identity;
   private final String privateKeyInPemFormat;
   private final String keyFactoryAlgorithm;
   private OAuthCredentials credentials;

   @Inject
   public OAuthCredentialsSupplier(@Identity String identity,
                                   @Credential String privateKeyInPemFormat,
                                   @Named(SIGNATURE_OR_MAC_ALGORITHM) String signatureOrMacAlgorithm) {
      this.identity = identity;
      this.privateKeyInPemFormat = privateKeyInPemFormat;
      checkState(OAUTH_ALGORITHM_NAMES_TO_KEYFACTORY_ALGORITHM_NAMES.containsKey(signatureOrMacAlgorithm),
              format("No mapping for key factory for algorithm: %s", signatureOrMacAlgorithm));
      this.keyFactoryAlgorithm = OAUTH_ALGORITHM_NAMES_TO_KEYFACTORY_ALGORITHM_NAMES.get(signatureOrMacAlgorithm);
   }

   @PostConstruct
   public void loadPrivateKey() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
      if (keyFactoryAlgorithm.equals(NO_ALGORITHM)) {
         this.credentials = new OAuthCredentials.Builder().identity(identity).credential
                 (privateKeyInPemFormat).build();
         return;
      }
      KeyFactory keyFactory = KeyFactory.getInstance(keyFactoryAlgorithm);
      PrivateKey privateKey = keyFactory.generatePrivate(Pems.privateKeySpec(Payloads.newStringPayload
              (privateKeyInPemFormat)));
      this.credentials = new OAuthCredentials.Builder().identity(identity).credential
              (privateKeyInPemFormat).privateKey(privateKey).build();
   }

   @Override
   public OAuthCredentials get() {
      return this.credentials;
   }

}
