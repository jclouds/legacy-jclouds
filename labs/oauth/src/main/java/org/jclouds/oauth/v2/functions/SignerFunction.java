/**
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
import com.google.inject.Singleton;
import org.jclouds.oauth.v2.domain.OAuthCredentials;

import javax.inject.Inject;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

/**
 * Function that signs OAuth tokens, provided a {@link Signature} algorithm and {@link PrivateKey}
 *
 * @author David Alves
 */
@Singleton
public class SignerFunction implements Function<byte[], byte[]> {

   private final Signature signature;

   @Inject
   public SignerFunction(Supplier<Signature> signature, Supplier<OAuthCredentials> credentials) throws
           InvalidKeyException {
      this.signature = signature.get();
      this.signature.initSign(credentials.get().privateKey);
   }

   @Override
   public byte[] apply(byte[] input) {
      try {
         this.signature.update(input);
         return this.signature.sign();
      } catch (SignatureException e) {
         throw Throwables.propagate(e);
      }
   }
}
