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
package org.jclouds.crypto;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

/**
 * Creates OpenSSH RSA keypairs
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://stackoverflow.com/questions/3706177/how-to-generate-ssh-compatible-id-rsa-pub-from-java"
 *      />
 */
@Beta
public class SshKeys {
   // All data type encoding is defined in the section #5 of RFC #4251. string
   // and mpint (multiple precision integer) types are encoded this way :
   private static final byte[] sshrsa = new byte[] { 0, 0, 0, 7, 's', 's', 'h', '-', 'r', 's', 'a' };

   /**
    * 
    * @param used
    *           to generate RSA key pairs
    * @return new 2048 bit keyPair
    * @see Crypto#rsaKeyPairGenerator()
    */
   public static KeyPair generateRsaKeyPair(KeyPairGenerator generator) {
      generator.initialize(2048);
      return generator.genKeyPair();
   }

   /**
    * return a "public" -> rsa public key, "private" -> its corresponding
    * private key
    */
   public static Map<String, String> generate() {
      try {
         return generate(KeyPairGenerator.getInstance("RSA"));
      } catch (NoSuchAlgorithmException e) {
         Throwables.propagate(e);
         return null;
      }
   }

   public static Map<String, String> generate(KeyPairGenerator generator) {
      KeyPair pair = generateRsaKeyPair(generator);
      Builder<String, String> builder = ImmutableMap.<String, String> builder();
      builder.put("public", encodeAsOpenSSH(RSAPublicKey.class.cast(pair.getPublic())));
      builder.put("private", encodeAsPem(RSAPrivateKey.class.cast(pair.getPrivate())));
      return builder.build();
   }

   public static String encodeAsOpenSSH(RSAPublicKey key) {
      ByteArrayDataOutput out = ByteStreams.newDataOutput();
      /* encode the "ssh-rsa" string */
      out.write(sshrsa);
      /* Encode the public exponent */
      BigInteger e = key.getPublicExponent();
      byte[] data = e.toByteArray();
      encodeUint32(data.length, out);
      out.write(data);
      /* Encode the modulus */
      BigInteger m = key.getModulus();
      data = m.toByteArray();
      encodeUint32(data.length, out);
      out.write(data);
      return "ssh-rsa " + CryptoStreams.base64(out.toByteArray());
   }

   public static String encodeAsPem(RSAPrivateKey key) {
      return Pems.pem(key.getEncoded(), Pems.PRIVATE_PKCS1_MARKER, 64);
   }

   public static void encodeUint32(int value, ByteArrayDataOutput out) {
      out.write((byte) ((value >>> 24) & 0xff));
      out.write((byte) ((value >>> 16) & 0xff));
      out.write((byte) ((value >>> 8) & 0xff));
      out.write((byte) (value & 0xff));
   }

}
