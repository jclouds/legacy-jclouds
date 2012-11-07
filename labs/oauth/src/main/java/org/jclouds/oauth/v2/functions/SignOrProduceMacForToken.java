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

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import org.jclouds.oauth.v2.domain.OAuthCredentials;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.inject.Inject;
import javax.inject.Named;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static org.jclouds.oauth.v2.OAuthConstants.NO_ALGORITHM;
import static org.jclouds.oauth.v2.OAuthConstants.OAUTH_ALGORITHM_NAMES_TO_SIGNATURE_ALGORITHM_NAMES;
import static org.jclouds.oauth.v2.config.OAuthProperties.SIGNATURE_OR_MAC_ALGORITHM;

/**
 * Function that signs/produces mac's for  OAuth tokens, provided a {@link Signature} or a {@link Mac} algorithm and
 * {@link PrivateKey}
 *
 * @author David Alves
 */
public class SignOrProduceMacForToken implements Function<byte[], byte[]> {

   private final Supplier<OAuthCredentials> credentials;
   private final String signatureOrMacAlgorithm;
   private Function<byte[], byte[]> signatureOrMacFunction;


   @Inject
   public SignOrProduceMacForToken(@Named(SIGNATURE_OR_MAC_ALGORITHM) String signatureOrMacAlgorithm,
                                   Supplier<OAuthCredentials> credentials) {
      checkState(OAUTH_ALGORITHM_NAMES_TO_SIGNATURE_ALGORITHM_NAMES.containsKey(signatureOrMacAlgorithm),
              format("the signature algorithm %s is not supported", signatureOrMacAlgorithm));
      this.signatureOrMacAlgorithm = OAUTH_ALGORITHM_NAMES_TO_SIGNATURE_ALGORITHM_NAMES.get(signatureOrMacAlgorithm);
      this.credentials = credentials;
   }

   @PostConstruct
   public void loadSignatureOrMacOrNone() throws InvalidKeyException, NoSuchAlgorithmException {
      if (signatureOrMacAlgorithm.equals(NO_ALGORITHM)) {
         this.signatureOrMacFunction = new Function<byte[], byte[]>() {
            @Override
            public byte[] apply(byte[] input) {
               return null;
            }
         };
      } else if (signatureOrMacAlgorithm.startsWith("SHA")) {
         this.signatureOrMacFunction = new SignatureGenerator(signatureOrMacAlgorithm, credentials.get().privateKey);
      } else {
         this.signatureOrMacFunction = new MessageAuthenticationCodeGenerator(signatureOrMacAlgorithm,
                 credentials.get().privateKey);
      }
   }

   @Override
   public byte[] apply(byte[] input) {
      return signatureOrMacFunction.apply(input);
   }

   private static class MessageAuthenticationCodeGenerator implements Function<byte[], byte[]> {

      private Mac mac;

      private MessageAuthenticationCodeGenerator(String macAlgorithm, PrivateKey privateKey) throws
              NoSuchAlgorithmException, InvalidKeyException {
         this.mac = Mac.getInstance(macAlgorithm);
         this.mac.init(privateKey);
      }

      @Override
      public byte[] apply(byte[] input) {
         this.mac.update(input);
         return this.mac.doFinal();
      }
   }

   private static class SignatureGenerator implements Function<byte[], byte[]> {

      private Signature signature;

      private SignatureGenerator(String signatureAlgorithm, PrivateKey privateKey) throws NoSuchAlgorithmException,
              InvalidKeyException {
         this.signature = Signature.getInstance(signatureAlgorithm);
         this.signature.initSign(privateKey);
      }

      @Override
      public byte[] apply(byte[] input) {
         try {
            signature.update(input);
            return signature.sign();
         } catch (SignatureException e) {
            throw Throwables.propagate(e);
         }
      }
   }
}
