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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.jclouds.encryption.internal.JCEEncryptionService;
import org.jclouds.http.PayloadEnclosing;
import org.jclouds.io.Payload;
import org.jclouds.io.payloads.ByteArrayPayload;

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

   byte[] rsaEncrypt(Payload payload, Key key);

   byte[] hmacSha256(String toEncode, byte[] key);

   byte[] hmacSha1(String toEncode, byte[] key);

   byte[] sha1(InputStream toEncode);

   byte[] sha256(InputStream toEncode);

   byte[] md5(InputStream toEncode);

   /**
    * generate an MD5 Hash for the current data.
    * <p/>
    * <h2>Note</h2>
    * <p/>
    * If this is an InputStream, it will be converted to a byte array first.
    * 
    * @throws IOException
    */
   <T extends PayloadEnclosing> T generateMD5BufferingIfNotRepeatable(T payloadEnclosing);

   Payload generateMD5BufferingIfNotRepeatable(Payload in);

   ByteArrayPayload generatePayloadWithMD5For(InputStream toEncode);

   MD5OutputStream md5OutputStream(OutputStream out);

   PrivateKey privateKeyFromPEM(byte[] pem);

   public static abstract class MD5OutputStream extends FilterOutputStream {
      public MD5OutputStream(OutputStream out) {
         super(out);
      }

      public abstract byte[] getMD5();
   }

   PublicKey publicKeyFromPEM(byte[] pem);

   X509Certificate x509CertificateFromPEM(byte[] pem);

   byte[] rsaDecrypt(Payload payload, Key key);

   String toPem(X509Certificate cert);

   String toPem(PublicKey key);

   String toPem(PrivateKey key);

}