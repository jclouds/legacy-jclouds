/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package net.oauth.signature.pem;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.RSAPublicKeySpec;

/**
 * PKCS#1 encoded public key spec. In oauth package as they made all classes
 * package visible.
 * 
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
   public PKCS1EncodedPublicKeySpec(byte[] keyBytes) throws IOException {
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
    * Decode PKCS#1 encoded private key into RSAPublicKeySpec.
    * 
    * <p/>
    * The ASN.1 syntax for the private key with CRT is
    * 
    * <pre>
    * -- 
    * -- Representation of RSA private key with information for the CRT algorithm.
    * --
    * RSAPrivateKey ::= SEQUENCE {
    *   version           Version, 
    *   modulus           INTEGER,  -- n
    *   publicExponent    INTEGER,  -- e
    * }
    * </pre>
    * 
    * @param keyBytes
    *           PKCS#1 encoded key
    * @throws IOException
    */

   private void decode(byte[] keyBytes) throws IOException {

      DerParser parser = new DerParser(keyBytes);

      Asn1Object sequence = parser.read();
      if (sequence.getType() != DerParser.SEQUENCE)
         throw new IOException("Invalid DER: not a sequence"); //$NON-NLS-1$

      // Parse inside the sequence
      parser = sequence.getParser();

      BigInteger modulus = parser.read().getInteger();
      BigInteger publicExp = parser.read().getInteger();

      keySpec = new RSAPublicKeySpec(modulus, publicExp);
   }
}
