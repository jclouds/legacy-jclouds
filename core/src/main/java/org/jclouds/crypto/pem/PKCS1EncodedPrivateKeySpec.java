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
import java.math.BigInteger;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;

/**
 * PKCS#1 encoded private key spec.
 * 
 * @author Ignasi Barrera
 */
public class PKCS1EncodedPrivateKeySpec {

   private RSAPrivateCrtKeySpec keySpec;

   /**
    * Create a PKCS#1 keyspec from DER encoded buffer
    * 
    * @param keyBytes
    *           DER encoded octet stream
    * @throws IOException
    */
   public PKCS1EncodedPrivateKeySpec(final byte[] keyBytes) throws IOException {
      decode(keyBytes);
   }

   /**
    * Get the key spec that JCE understands.
    * 
    * @return CRT keyspec defined by JCE
    */
   public RSAPrivateKeySpec getKeySpec() {
      return keySpec;
   }

   /**
    * Decode PKCS#1 encoded private key into RSAPrivateCrtKeySpec.
    * 
    * @param keyBytes
    *           Encoded PKCS#1 rsa key.
    */
   private void decode(final byte[] keyBytes) throws IOException {
      ASN1Sequence seq = (ASN1Sequence) ASN1Object.fromByteArray(keyBytes);
      RSAPrivateKeyStructure rsa = new RSAPrivateKeyStructure(seq);

      BigInteger mod = rsa.getModulus();
      BigInteger pubExp = rsa.getPublicExponent();
      BigInteger privExp = rsa.getPrivateExponent();
      BigInteger p1 = rsa.getPrime1();
      BigInteger p2 = rsa.getPrime2();
      BigInteger exp1 = rsa.getExponent1();
      BigInteger exp2 = rsa.getExponent2();
      BigInteger crtCoef = rsa.getCoefficient();

      keySpec = new RSAPrivateCrtKeySpec(mod, pubExp, privExp, p1, p2, exp1, exp2, crtCoef);
   }
}
