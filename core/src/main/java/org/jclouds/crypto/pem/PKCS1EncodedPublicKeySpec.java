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

package org.jclouds.crypto.pem;

import java.io.IOException;
import java.security.spec.RSAPublicKeySpec;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

/**
 * PKCS#1 encoded public key spec.
 * 
 * @author Adrian Cole
 */
public class PKCS1EncodedPublicKeySpec {

   private RSAPublicKeySpec keySpec;

   /**
    * Create a PKCS#1 keyspec from DER encoded buffer
    * 
    * @param keyBytes
    *           DER encoded octet stream
    * @throws IOException
    */
   public PKCS1EncodedPublicKeySpec(final byte[] keyBytes) throws IOException {
      decode(keyBytes);
   }

   /**
    * Get the key spec that JCE understands.
    * 
    * @return CRT keyspec defined by JCE
    */
   public RSAPublicKeySpec getKeySpec() {
      return keySpec;
   }

   /**
    * Decode PKCS#1 encoded public key into RSAPublicKeySpec.
    * <p>
    * Keys here can be in two different formats. They can have the algorithm
    * encoded, or they can have only the modulus and the public exponent.
    * <p>
    * The latter is not a valid PEM encoded file, but it is a valid DER encoded
    * RSA key, so this method should also support it.
    * 
    * @param keyBytes
    *           Encoded PKCS#1 rsa key.
    */
   private void decode(final byte[] keyBytes) throws IOException {
      RSAPublicKey pks = null;
      ASN1Sequence seq = ASN1Sequence.getInstance(keyBytes);
      try {
         // Try to parse the public key normally. If the algorithm is not
         // present in the encoded key, an IllegalArgumentException will be
         // raised.
         SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(seq);
         pks = RSAPublicKey.getInstance(info.parsePublicKey());
      } catch (IllegalArgumentException ex) {
         // If the algorithm is not found in the encoded key, try to extract
         // just the modulus and the public exponent to build the public key.
         pks = RSAPublicKey.getInstance(seq);
      }
      keySpec = new RSAPublicKeySpec(pks.getModulus(), pks.getPublicExponent());
   }
}
