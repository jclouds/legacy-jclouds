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
package org.jclouds.encryption;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FilterOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.jclouds.encryption.internal.JCEEncryptionService;

import com.google.inject.ImplementedBy;

/**
 * 
 * @author Adrian Cole
 */
@ImplementedBy(JCEEncryptionService.class)
public interface EncryptionService {

   String toHexString(byte[] raw);

   byte[] fromHexString(String hex);

   String hmacSha256Base64(String toEncode, byte[] key) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException;

   String sha1Base64(String toEncode) throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidKeyException;

   String hmacSha1Base64(String toEncode, byte[] key) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException;

   String md5Hex(byte[] toEncode) throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidKeyException, UnsupportedEncodingException;

   String md5Base64(byte[] toEncode) throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidKeyException;

   byte[] md5(byte[] plainBytes);

   byte[] md5(InputStream toEncode);

   String toBase64String(byte[] resBuf);

   byte[] md5(Object data);

   MD5InputStreamResult generateMD5Result(InputStream toEncode);

   MD5OutputStream md5OutputStream(OutputStream out);

   public static abstract class MD5OutputStream extends FilterOutputStream {
      public MD5OutputStream(OutputStream out) {
         super(out);
      }

      public abstract byte[] getMD5();
   }

   public static class MD5InputStreamResult {
      public final byte[] data;
      public final byte[] md5;
      public final long length;

      public MD5InputStreamResult(byte[] data, byte[] eTag, long length) {
         this.data = checkNotNull(data, "data");
         this.md5 = checkNotNull(eTag, "eTag");
         checkArgument(length >= 0, "length cannot me negative");
         this.length = length;
      }

   }

   byte[] fromBase64String(String encoded);

   byte[] rsaPrivateEncrypt(String toSign, Key privateKey) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException;

}