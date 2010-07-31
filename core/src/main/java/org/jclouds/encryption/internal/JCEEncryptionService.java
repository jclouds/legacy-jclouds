/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.encryption.internal;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.io.Closeables.closeQuietly;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.jclouds.io.payloads.ByteArrayPayload;

/**
 * 
 * @author Adrian Cole
 */
public class JCEEncryptionService extends BaseEncryptionService {

   public JCEEncryptionService(KeyFactory rsaKeyFactory, CertificateFactory certFactory) {
      super(rsaKeyFactory, certFactory);
   }

   public JCEEncryptionService() throws NoSuchAlgorithmException, CertificateException {
      this(KeyFactory.getInstance("RSA"), CertificateFactory.getInstance("X.509"));
   }

   @Override
   public byte[] hmacSha256(String toEncode, byte[] key) {
      return hmac(toEncode, key, "HmacSHA256");
   }

   @Override
   public byte[] hmacSha1(String toEncode, byte[] key) {
      return hmac(toEncode, key, "HmacSHA1");
   }

   public byte[] hmac(String toEncode, byte[] key, String algorithm) {
      SecretKeySpec signingKey = new SecretKeySpec(key, algorithm);

      Mac mac = null;
      try {
         mac = Mac.getInstance(algorithm);
      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException("Could not find the " + algorithm + " algorithm", e);
      }
      try {
         mac.init(signingKey);
      } catch (InvalidKeyException e) {
         throw new RuntimeException("Could not initialize the " + algorithm + " algorithm", e);
      }
      return mac.doFinal(toEncode.getBytes());
   }

   @Override
   public byte[] md5(InputStream toEncode) {
      MessageDigest eTag = getDigest();
      byte[] buffer = new byte[BUF_SIZE];
      int numRead = -1;
      try {
         do {
            numRead = toEncode.read(buffer);
            if (numRead > 0) {
               eTag.update(buffer, 0, numRead);
            }
         } while (numRead != -1);
      } catch (IOException e) {
         propagate(e);
      } finally {
         closeQuietly(toEncode);
      }
      return eTag.digest();
   }

   @Override
   public String base64(byte[] resBuf) {
      return Base64.encodeBytes(resBuf, Base64.DONT_BREAK_LINES);
   }

   @Override
   public byte[] fromBase64(String encoded) {
      return Base64.decode(encoded);
   }

   @Override
   public ByteArrayPayload generatePayloadWithMD5For(InputStream toEncode) {
      MessageDigest eTag = getDigest();
      byte[] buffer = new byte[BUF_SIZE];
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      long length = 0;
      int numRead = -1;
      try {
         do {
            numRead = toEncode.read(buffer);
            if (numRead > 0) {
               length += numRead;
               eTag.update(buffer, 0, numRead);
               out.write(buffer, 0, numRead);
            }
         } while (numRead != -1);
      } catch (IOException e) {
         propagate(e);
      } finally {
         closeQuietly(out);
         closeQuietly(toEncode);
      }
      return new ByteArrayPayload(out.toByteArray(), eTag.digest());
   }

   private static MessageDigest getDigest() {
      MessageDigest eTag;
      try {
         eTag = MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException("Could not find the MD5 algorithm", e);
      }
      return eTag;
   }

   @Override
   public MD5OutputStream md5OutputStream(OutputStream out) {
      return new JCEMD5OutputStream(out);
   }

   private static class JCEMD5OutputStream extends MD5OutputStream {
      public JCEMD5OutputStream(OutputStream out) {
         super(new DigestOutputStream(out, getDigest()));
      }

      @Override
      public byte[] getMD5() {
         MessageDigest digest = ((DigestOutputStream) out).getMessageDigest();
         return digest.digest();
      }
   }

   @Override
   public byte[] sha1(InputStream plainBytes) {
      return digest(plainBytes, "SHA1");
   }

   @Override
   public byte[] sha256(InputStream plainBytes) {
      return digest(plainBytes, "SHA256");
   }

   private byte[] digest(InputStream plainBytes, String algorithm) {
      MessageDigest digest;
      try {
         digest = MessageDigest.getInstance(algorithm);
      } catch (NoSuchAlgorithmException e1) {
         propagate(e1);
         return null;
      }
      byte[] buffer = new byte[BUF_SIZE];
      long length = 0;
      int numRead = -1;
      try {
         do {
            numRead = plainBytes.read(buffer);
            if (numRead > 0) {
               length += numRead;
               digest.update(buffer, 0, numRead);
            }
         } while (numRead != -1);
      } catch (IOException e) {
         propagate(e);
      } finally {
         closeQuietly(plainBytes);
      }

      return digest.digest();
   }

}
