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

import java.io.FilterOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;

import org.jclouds.encryption.internal.JCEEncryptionService;
import org.jclouds.http.Payload;
import org.jclouds.http.payloads.ByteArrayPayload;

import com.google.inject.ImplementedBy;

/**
 * 
 * @author Adrian Cole
 */
@ImplementedBy(JCEEncryptionService.class)
public interface EncryptionService {
   String base64(byte[] toEncode);

   byte[] fromBase64(String encoded);

   String hex(byte[] toEncode);

   byte[] fromHex(String encoded);

   byte[] rsaSign(String toSign, Key privateKey);

   byte[] hmacSha256(String toEncode, byte[] key);

   byte[] hmacSha1(String toEncode, byte[] key);

   byte[] sha1(InputStream toEncode);

   byte[] sha256(InputStream toEncode);

   byte[] md5(InputStream toEncode);

   Payload generateMD5BufferingIfNotRepeatable(Payload in);

   ByteArrayPayload generatePayloadWithMD5For(InputStream toEncode);

   MD5OutputStream md5OutputStream(OutputStream out);

   public static abstract class MD5OutputStream extends FilterOutputStream {
      public MD5OutputStream(OutputStream out) {
         super(out);
      }

      public abstract byte[] getMD5();
   }

}