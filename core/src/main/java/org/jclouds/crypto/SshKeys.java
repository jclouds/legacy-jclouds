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
package org.jclouds.crypto;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Throwables.propagate;
import static org.jclouds.crypto.CryptoStreams.base64;
import static org.jclouds.crypto.CryptoStreams.hex;
import static org.jclouds.crypto.CryptoStreams.md5;
import static org.jclouds.crypto.Pems.privateKeySpec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Map;

import org.bouncycastle.openssl.PEMWriter;
import org.jclouds.io.InputSuppliers;
import org.jclouds.util.Strings2;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.io.InputSupplier;

/**
 * Utilities for ssh key pairs
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://stackoverflow.com/questions/3706177/how-to-generate-ssh-compatible-id-rsa-pub-from-java"
 *      />
 */
@Beta
public class SshKeys {

   /**
    * Executes {@link Pems#publicKeySpecFromOpenSSH(InputSupplier)} on the string which was OpenSSH
    * Base64 Encoded {@code id_rsa.pub}
    * 
    * @param idRsaPub
    *           formatted {@code ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAB...}
    * @see Pems#publicKeySpecFromOpenSSH(InputSupplier)
    */
   public static RSAPublicKeySpec publicKeySpecFromOpenSSH(String idRsaPub) {
      try {
         return publicKeySpecFromOpenSSH(InputSuppliers.of(idRsaPub));
      } catch (IOException e) {
         propagate(e);
         return null;
      }
   }

   /**
    * Returns {@link RSAPublicKeySpec} which was OpenSSH Base64 Encoded {@code id_rsa.pub}
    * 
    * @param supplier
    *           the input stream factory, formatted {@code ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAB...}
    * 
    * @return the {@link RSAPublicKeySpec} which was OpenSSH Base64 Encoded {@code id_rsa.pub}
    * @throws IOException
    *            if an I/O error occurs
    */
   public static RSAPublicKeySpec publicKeySpecFromOpenSSH(InputSupplier<? extends InputStream> supplier)
            throws IOException {
      InputStream stream = supplier.getInput();
      Iterable<String> parts = Splitter.on(' ').split(Strings2.toStringAndClose(stream));
      checkArgument(Iterables.size(parts) >= 2 && "ssh-rsa".equals(Iterables.get(parts, 0)),
               "bad format, should be: ssh-rsa AAAAB3...");
      stream = new ByteArrayInputStream(CryptoStreams.base64(Iterables.get(parts, 1)));
      String marker = new String(readLengthFirst(stream));
      checkArgument("ssh-rsa".equals(marker), "looking for marker ssh-rsa but got %s", marker);
      BigInteger publicExponent = new BigInteger(readLengthFirst(stream));
      BigInteger modulus = new BigInteger(readLengthFirst(stream));
      return new RSAPublicKeySpec(modulus, publicExponent);
   }

   // http://www.ietf.org/rfc/rfc4253.txt
   static byte[] readLengthFirst(InputStream in) throws IOException {
      int byte1 = in.read();
      int byte2 = in.read();
      int byte3 = in.read();
      int byte4 = in.read();
      int length = (byte1 << 24) + (byte2 << 16) + (byte3 << 8) + (byte4 << 0);
      byte[] val = new byte[length];
      in.read(val, 0, length);
      return val;
   }

   /**
    * 
    * @param generator
    *           to generate RSA key pairs
    * @param rand
    *           for initializing {@code generator}
    * @return new 2048 bit keyPair
    * @see Crypto#rsaKeyPairGenerator()
    */
   public static KeyPair generateRsaKeyPair(KeyPairGenerator generator, SecureRandom rand) {
      generator.initialize(2048, rand);
      return generator.genKeyPair();
   }

   /**
    * return a "public" -> rsa public key, "private" -> its corresponding private key
    */
   public static Map<String, String> generate() {
      try {
         return generate(KeyPairGenerator.getInstance("RSA"), new SecureRandom());
      } catch (NoSuchAlgorithmException e) {
         propagate(e);
         return null;
      }
   }

   public static Map<String, String> generate(KeyPairGenerator generator, SecureRandom rand) {
      KeyPair pair = generateRsaKeyPair(generator, rand);
      Builder<String, String> builder = ImmutableMap.builder();
      builder.put("public", encodeAsOpenSSH(RSAPublicKey.class.cast(pair.getPublic())));
      builder.put("private", encodeAsPem(RSAPrivateKey.class.cast(pair.getPrivate())));
      return builder.build();
   }

   public static String encodeAsOpenSSH(RSAPublicKey key) {
      byte[] keyBlob = keyBlob(key.getPublicExponent(), key.getModulus());
      return "ssh-rsa " + base64(keyBlob);
   }

   public static String encodeAsPem(RSAPrivateKey key) {
      StringWriter stringWriter = new StringWriter();
      PEMWriter pemFormatWriter = new PEMWriter(stringWriter);
      try {
         pemFormatWriter.writeObject(key);
         pemFormatWriter.close();
      } catch (IOException e) {
         Throwables.propagate(e);
      }
      return stringWriter.toString();
      // TODO: understand why pem isn't passing testCanGenerate where keys are
      // checked to match.
      // return pem(key.getEncoded(), PRIVATE_PKCS1_MARKER, 64);
   }

   /**
    * @param privateKeyPEM
    *           RSA private key in PEM format
    * @param publicKeyOpenSSH
    *           RSA public key in OpenSSH format
    * @return true if the keypairs match
    */
   public static boolean privateKeyMatchesPublicKey(String privateKeyPEM, String publicKeyOpenSSH) {
      KeySpec privateKeySpec = privateKeySpec(privateKeyPEM);
      checkArgument(privateKeySpec instanceof RSAPrivateCrtKeySpec,
               "incorrect format expected RSAPrivateCrtKeySpec was %s", privateKeySpec);
      return privateKeyMatchesPublicKey(RSAPrivateCrtKeySpec.class.cast(privateKeySpec),
               publicKeySpecFromOpenSSH(publicKeyOpenSSH));
   }

   /**
    * @return true if the keypairs match
    */
   public static boolean privateKeyMatchesPublicKey(RSAPrivateCrtKeySpec privateKey, RSAPublicKeySpec publicKey) {
      return privateKey.getPublicExponent().equals(publicKey.getPublicExponent())
               && privateKey.getModulus().equals(publicKey.getModulus());
   }

   /**
    * @return true if the keypair has the same fingerprint as supplied
    */
   public static boolean privateKeyHasFingerprint(RSAPrivateCrtKeySpec privateKey, String fingerprint) {
      return fingerprint(privateKey.getPublicExponent(), privateKey.getModulus()).equals(fingerprint);
   }

   /**
    * @param privateKeyPEM
    *           RSA private key in PEM format
    * @param fingerprint
    *           ex. {@code 2b:a9:62:95:5b:8b:1d:61:e0:92:f7:03:10:e9:db:d9}
    * @return true if the keypair has the same fingerprint as supplied
    */
   public static boolean privateKeyHasFingerprint(String privateKeyPEM, String fingerprint) {
      KeySpec privateKeySpec = privateKeySpec(privateKeyPEM);
      checkArgument(privateKeySpec instanceof RSAPrivateCrtKeySpec,
               "incorrect format expected RSAPrivateCrtKeySpec was %s", privateKeySpec);
      return privateKeyHasFingerprint(RSAPrivateCrtKeySpec.class.cast(privateKeySpec), fingerprint);
   }

   /**
    * @param privateKeyPEM
    *           RSA private key in PEM format
    * @return fingerprint ex. {@code 2b:a9:62:95:5b:8b:1d:61:e0:92:f7:03:10:e9:db:d9}
    */
   public static String fingerprintPrivateKey(String privateKeyPEM) {
      KeySpec privateKeySpec = privateKeySpec(privateKeyPEM);
      checkArgument(privateKeySpec instanceof RSAPrivateCrtKeySpec,
               "incorrect format expected RSAPrivateCrtKeySpec was %s", privateKeySpec);
      RSAPrivateCrtKeySpec certKeySpec = RSAPrivateCrtKeySpec.class.cast(privateKeySpec);
      return fingerprint(certKeySpec.getPublicExponent(), certKeySpec.getModulus());
   }

   /**
    * @param publicKeyOpenSSH
    *           RSA public key in OpenSSH format
    * @return fingerprint ex. {@code 2b:a9:62:95:5b:8b:1d:61:e0:92:f7:03:10:e9:db:d9}
    */
   public static String fingerprintPublicKey(String publicKeyOpenSSH) {
      RSAPublicKeySpec publicKeySpec = publicKeySpecFromOpenSSH(publicKeyOpenSSH);
      return fingerprint(publicKeySpec.getPublicExponent(), publicKeySpec.getModulus());
   }

   /**
    * @return true if the keypair has the same SHA1 fingerprint as supplied
    */
   public static boolean privateKeyHasSha1(RSAPrivateCrtKeySpec privateKey, String fingerprint) {
      return sha1(privateKey).equals(fingerprint);
   }

   /**
    * @param privateKeyPEM
    *           RSA private key in PEM format
    * @param sha1HexColonDelimited
    *           ex. {@code 2b:a9:62:95:5b:8b:1d:61:e0:92:f7:03:10:e9:db:d9}
    * @return true if the keypair has the same fingerprint as supplied
    */
   public static boolean privateKeyHasSha1(String privateKeyPEM, String sha1HexColonDelimited) {
      KeySpec privateKeySpec = privateKeySpec(privateKeyPEM);
      checkArgument(privateKeySpec instanceof RSAPrivateCrtKeySpec,
               "incorrect format expected RSAPrivateCrtKeySpec was %s", privateKeySpec);
      return privateKeyHasSha1(RSAPrivateCrtKeySpec.class.cast(privateKeySpec), sha1HexColonDelimited);
   }

   /**
    * @param privateKeyPEM
    *           RSA private key in PEM format
    * @return sha1HexColonDelimited ex. {@code 2b:a9:62:95:5b:8b:1d:61:e0:92:f7:03:10:e9:db:d9}
    */
   public static String sha1PrivateKey(String privateKeyPEM) {
      KeySpec privateKeySpec = privateKeySpec(privateKeyPEM);
      checkArgument(privateKeySpec instanceof RSAPrivateCrtKeySpec,
               "incorrect format expected RSAPrivateCrtKeySpec was %s", privateKeySpec);
      RSAPrivateCrtKeySpec certKeySpec = RSAPrivateCrtKeySpec.class.cast(privateKeySpec);
      return sha1(certKeySpec);
   }

   /**
    * Create a SHA-1 digest of the DER encoded private key.
    * 
    * @param publicExponent
    * @param modulus
    * 
    * @return hex sha1HexColonDelimited ex. {@code 2b:a9:62:95:5b:8b:1d:61:e0:92:f7:03:10:e9:db:d9}
    */
   public static String sha1(RSAPrivateCrtKeySpec privateKey) {
      try {
         String sha1 = Joiner.on(":").join(
                  Splitter.fixedLength(2).split(
                           hex(CryptoStreams.sha1(KeyFactory.getInstance("RSA").generatePrivate(privateKey)
                                    .getEncoded()))));
         return sha1;
      } catch (InvalidKeySpecException e) {
         propagate(e);
         return null;
      } catch (NoSuchAlgorithmException e) {
         propagate(e);
         return null;
      }
   }

   /**
    * @return true if the keypair has the same fingerprint as supplied
    */
   public static boolean publicKeyHasFingerprint(RSAPublicKeySpec publicKey, String fingerprint) {
      return fingerprint(publicKey.getPublicExponent(), publicKey.getModulus()).equals(fingerprint);
   }

   /**
    * @param publicKeyOpenSSH
    *           RSA public key in OpenSSH format
    * @param fingerprint
    *           ex. {@code 2b:a9:62:95:5b:8b:1d:61:e0:92:f7:03:10:e9:db:d9}
    * @return true if the keypair has the same fingerprint as supplied
    */
   public static boolean publicKeyHasFingerprint(String publicKeyOpenSSH, String fingerprint) {
      return publicKeyHasFingerprint(publicKeySpecFromOpenSSH(publicKeyOpenSSH), fingerprint);
   }

   /**
    * Create a fingerprint per the following <a
    * href="http://tools.ietf.org/html/draft-friedl-secsh-fingerprint-00" >spec</a>
    * 
    * @param publicExponent
    * @param modulus
    * 
    * @return hex fingerprint ex. {@code 2b:a9:62:95:5b:8b:1d:61:e0:92:f7:03:10:e9:db:d9}
    */
   public static String fingerprint(BigInteger publicExponent, BigInteger modulus) {
      byte[] keyBlob = keyBlob(publicExponent, modulus);
      return Joiner.on(":").join(Splitter.fixedLength(2).split(hex(md5(keyBlob))));
   }

   public static byte[] keyBlob(BigInteger publicExponent, BigInteger modulus) {
      try {
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         writeLengthFirst("ssh-rsa".getBytes(), out);
         writeLengthFirst(publicExponent.toByteArray(), out);
         writeLengthFirst(modulus.toByteArray(), out);
         return out.toByteArray();
      } catch (IOException e) {
         propagate(e);
         return null;
      }
   }

   // http://www.ietf.org/rfc/rfc4253.txt
   static void writeLengthFirst(byte[] array, ByteArrayOutputStream out) throws IOException {
      out.write((array.length >>> 24) & 0xFF);
      out.write((array.length >>> 16) & 0xFF);
      out.write((array.length >>> 8) & 0xFF);
      out.write((array.length >>> 0) & 0xFF);
      if (array.length == 1 && array[0] == (byte) 0x00)
         out.write(new byte[0]);
      else
         out.write(array);
   }
}
