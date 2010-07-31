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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.io.Closeables.closeQuietly;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import net.oauth.signature.pem.PEMReader;
import net.oauth.signature.pem.PKCS1EncodedKeySpec;

import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.PayloadEnclosing;
import org.jclouds.io.Payload;
import org.jclouds.logging.Logger;

import com.google.common.base.Throwables;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseEncryptionService implements EncryptionService {

   @Resource
   protected Logger logger = Logger.NULL;

   protected static final int BUF_SIZE = 0x2000; // 8

   final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6',
            (byte) '7', (byte) '8', (byte) '9', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f' };

   private final KeyFactory rsaKeyFactory;
   private final CertificateFactory certFactory;

   public BaseEncryptionService(KeyFactory rsaKeyFactory, CertificateFactory certFactory) {
      this.rsaKeyFactory = rsaKeyFactory;
      this.certFactory = certFactory;
   }

   @Override
   public String hex(byte[] raw) {
      byte[] hex = new byte[2 * raw.length];
      int index = 0;

      for (byte b : raw) {
         int v = b & 0xFF;
         hex[index++] = HEX_CHAR_TABLE[v >>> 4];
         hex[index++] = HEX_CHAR_TABLE[v & 0xF];
      }
      try {
         return new String(hex, "ASCII");
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public byte[] fromHex(String hex) {
      if (hex.startsWith("0x"))
         hex = hex.substring(2);
      byte[] bytes = new byte[hex.length() / 2];
      for (int i = 0; i < bytes.length; i++) {
         bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
      }
      return bytes;
   }

   @Override
   public Payload generateMD5BufferingIfNotRepeatable(Payload payload) {
      checkNotNull(payload, "payload");
      if (!payload.isRepeatable()) {
         String oldContentType = payload.getContentType();
         payload = generatePayloadWithMD5For(payload.getInput());
         payload.setContentType(oldContentType);
      } else {
         payload.setContentMD5(md5(payload.getInput()));
      }
      return payload;

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public <T extends PayloadEnclosing> T generateMD5BufferingIfNotRepeatable(T payloadEnclosing) {
      checkState(payloadEnclosing != null, "payloadEnclosing");
      Payload newPayload = generateMD5BufferingIfNotRepeatable(payloadEnclosing.getPayload());
      if (newPayload != payloadEnclosing.getPayload())
         payloadEnclosing.setPayload(newPayload);
      return payloadEnclosing;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public PrivateKey privateKeyFromPEM(byte[] pem) {
      PEMReader reader;
      try {
         reader = new PEMReader(pem);

         byte[] bytes = reader.getDerBytes();
         KeySpec keySpec;

         if (PEMReader.PRIVATE_PKCS1_MARKER.equals(reader.getBeginMarker())) {
            keySpec = (new PKCS1EncodedKeySpec(bytes)).getKeySpec();
         } else if (PEMReader.PRIVATE_PKCS8_MARKER.equals(reader.getBeginMarker())) {
            keySpec = new PKCS8EncodedKeySpec(bytes);
         } else {
            throw new IOException("Invalid PEM file: Unknown marker for private key " + reader.getBeginMarker());
         }
         return rsaKeyFactory.generatePrivate(keySpec);
      } catch (Exception e) {
         Throwables.propagate(e);
         return null;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public PublicKey publicKeyFromPEM(byte[] pem) {
      PEMReader reader;
      try {
         reader = new PEMReader(pem);

         byte[] bytes = reader.getDerBytes();
         KeySpec keySpec;

         if (PEMReader.PUBLIC_X509_MARKER.equals(reader.getBeginMarker())) {
            keySpec = new X509EncodedKeySpec(bytes);
         } else {
            throw new IOException("Invalid PEM file: Unknown marker for public key " + reader.getBeginMarker());
         }
         return rsaKeyFactory.generatePublic(keySpec);
      } catch (Exception e) {
         Throwables.propagate(e);
         return null;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public X509Certificate x509CertificateFromPEM(byte[] pem) {
      PEMReader reader;
      try {
         reader = new PEMReader(pem);

         byte[] bytes = reader.getDerBytes();

         if (PEMReader.CERTIFICATE_X509_MARKER.equals(reader.getBeginMarker())) {
            return (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(bytes));
         } else {
            throw new IOException("Invalid PEM file: Unknown marker for public key " + reader.getBeginMarker());
         }

      } catch (Exception e) {
         Throwables.propagate(e);
         return null;
      }
   }

   @Override
   public byte[] rsaEncrypt(Payload payload, Key key) {
      // TODO convert this to BC code
      Cipher cipher = null;
      try {
         cipher = Cipher.getInstance("RSA");
         cipher.init(Cipher.ENCRYPT_MODE, key);
      } catch (NoSuchAlgorithmException e) {
         Throwables.propagate(e);
      } catch (NoSuchPaddingException e) {
         Throwables.propagate(e);
      } catch (InvalidKeyException e) {
         Throwables.propagate(e);
      }
      return cipherPayload(cipher, payload);
   }

   @Override
   public byte[] rsaDecrypt(Payload payload, Key key) {
      // TODO convert this to BC code
      Cipher cipher = null;
      try {
         cipher = Cipher.getInstance("RSA");
         cipher.init(Cipher.DECRYPT_MODE, key);
      } catch (NoSuchAlgorithmException e) {
         Throwables.propagate(e);
      } catch (NoSuchPaddingException e) {
         Throwables.propagate(e);
      } catch (InvalidKeyException e) {
         Throwables.propagate(e);
      }
      return cipherPayload(cipher, payload);
   }

   private byte[] cipherPayload(Cipher cipher, Payload payload) {
      byte[] resBuf = new byte[cipher.getOutputSize(payload.getContentLength().intValue())];
      byte[] buffer = new byte[BUF_SIZE];
      long length = 0;
      int numRead = -1;
      InputStream plainBytes = payload.getInput();
      try {
         do {
            numRead = plainBytes.read(buffer);
            if (numRead > 0) {
               length += numRead;
               cipher.update(buffer, 0, numRead);
            }
         } while (numRead != -1);
      } catch (IOException e) {
         propagate(e);
      } finally {
         closeQuietly(plainBytes);
      }
      try {
         int size = cipher.doFinal(resBuf, 0);
         return Arrays.copyOfRange(resBuf, 0, size);
      } catch (IllegalBlockSizeException e) {
         Throwables.propagate(e);
      } catch (ShortBufferException e) {
         Throwables.propagate(e);
      } catch (BadPaddingException e) {
         Throwables.propagate(e);
      }
      assert false;
      return null;
   }

   @Override
   public String toPem(X509Certificate cert) {
      try {
         return new StringBuilder("-----BEGIN CERTIFICATE-----\n").append(base64(cert.getEncoded())).append(
                  "\n-----END CERTIFICATE-----\n").toString();
      } catch (CertificateEncodingException e) {
         Throwables.propagate(e);
         return null;
      }
   }

   @Override
   public String toPem(PublicKey key) {
      return new StringBuilder("-----BEGIN PUBLIC KEY-----\n").append(base64(key.getEncoded())).append(
               "\n-----END PUBLIC KEY-----\n").toString();
   }

   @Override
   public String toPem(PrivateKey key) {
      return new StringBuilder("-----BEGIN PRIVATE KEY-----\n").append(base64(key.getEncoded())).append(
               "\n-----END PRIVATE KEY-----\n").toString();
   }
}
