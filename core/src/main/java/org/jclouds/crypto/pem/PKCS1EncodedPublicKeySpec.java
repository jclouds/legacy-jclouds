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
/****************************************************************************
 * Copyright (c) 1998-2009 AOL LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ****************************************************************************
 *
 * @author:     zhang
 * @version:    $Revision$
 * @created:    Apr 24, 2009
 *
 * Description: A KeySpec for PKCS#1 encoded RSA private key
 *
 ****************************************************************************/

package org.jclouds.crypto.pem;

import java.io.IOException;
import java.security.spec.RSAPublicKeySpec;

import net.oauth.signature.pem.PKCS1EncodedKeySpec;

/**
 * PKCS#1 encoded public key spec.
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
    * get the modulus and public exponent by reusing {@link PKCS1EncodedKeySpec}
    */
   private void decode(byte[] keyBytes) throws IOException {
      PKCS1EncodedKeySpec privateSpec = new PKCS1EncodedKeySpec(keyBytes);

      keySpec = new RSAPublicKeySpec(privateSpec.getKeySpec().getModulus(), privateSpec.getKeySpec()
               .getPublicExponent());
   }
}
