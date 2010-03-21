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
package org.jclouds.encryption.bouncycastle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.inject.Singleton;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Base64;
import org.jclouds.encryption.internal.BaseEncryptionService;
import org.jclouds.util.Utils;

import com.google.common.io.Closeables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BouncyCastleEncryptionService extends BaseEncryptionService {
   public String hmacSha256Base64(String toEncode, byte[] key) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException {
      Digest digest = new SHA256Digest();
      return hmacBase64(toEncode, key, digest);
   }

   private String hmacBase64(String toEncode, byte[] key, Digest digest) {
      byte[] resBuf = hmac(toEncode, key, digest);
      return toBase64String(resBuf);
   }

   public byte[] hmac(String toEncode, byte[] key, Digest digest) {
      HMac hmac = new HMac(digest);
      byte[] resBuf = new byte[hmac.getMacSize()];
      byte[] plainBytes = Utils.encodeString(toEncode);
      byte[] keyBytes = key;
      hmac.init(new KeyParameter(keyBytes));
      hmac.update(plainBytes, 0, plainBytes.length);
      hmac.doFinal(resBuf, 0);
      return resBuf;
   }

   public String hmacSha1Base64(String toEncode, byte[] key) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException {
      Digest digest = new SHA1Digest();
      return hmacBase64(toEncode, key, digest);
   }

   public byte[] md5(byte[] plainBytes) {
      MD5Digest eTag = new MD5Digest();
      byte[] resBuf = new byte[eTag.getDigestSize()];
      eTag.update(plainBytes, 0, plainBytes.length);
      eTag.doFinal(resBuf, 0);
      return resBuf;
   }

   public byte[] md5(InputStream toEncode) {
      MD5Digest eTag = new MD5Digest();
      byte[] resBuf = new byte[eTag.getDigestSize()];
      byte[] buffer = new byte[1024];
      int numRead = -1;
      try {
         do {
            numRead = toEncode.read(buffer);
            if (numRead > 0) {
               eTag.update(buffer, 0, numRead);
            }
         } while (numRead != -1);
      } catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         Closeables.closeQuietly(toEncode);
      }
      eTag.doFinal(resBuf, 0);
      return resBuf;
   }

   public String toBase64String(byte[] resBuf) {
      return new String(Base64.encode(resBuf));
   }

   public MD5InputStreamResult generateMD5Result(InputStream toEncode) {
      MD5Digest eTag = new MD5Digest();
      byte[] resBuf = new byte[eTag.getDigestSize()];
      byte[] buffer = new byte[1024];
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
         throw new RuntimeException(e);
      } finally {
         Closeables.closeQuietly(out);
         Closeables.closeQuietly(toEncode);
      }
      eTag.doFinal(resBuf, 0);
      return new MD5InputStreamResult(out.toByteArray(), resBuf, length);
   }

   public byte[] fromBase64String(String encoded) {
      return Base64.decode(encoded);
   }

}
