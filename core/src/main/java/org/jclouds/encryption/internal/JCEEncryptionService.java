package org.jclouds.encryption.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.io.ByteArrayOutputStream;

import com.google.common.io.Closeables;

/**
 * 
 * @author Adrian Cole
 */
public class JCEEncryptionService extends BaseEncryptionService {

   public String hmacSha256Base64(String toEncode, byte[] key) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException {
      return hmacBase64(toEncode, key, "HmacSHA256");
   }

   private String hmacBase64(String toEncode, byte[] key, String algorithm) {
      byte[] resBuf = hmac(toEncode, key, algorithm);
      return toBase64String(resBuf);
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

   public String hmacSha1Base64(String toEncode, byte[] key) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException {
      return hmacBase64(toEncode, key, "HmacSHA1");
   }

   public byte[] md5(byte[] plainBytes) {
      MessageDigest eTag = getDigest();
      eTag.update(plainBytes, 0, plainBytes.length);
      return eTag.digest();
   }

   public byte[] md5(File toEncode) {
      MessageDigest eTag = getDigest();
      byte[] buffer = new byte[1024];
      int numRead = -1;
      InputStream i = null;
      try {
         i = new FileInputStream(toEncode);
         do {
            numRead = i.read(buffer);
            if (numRead > 0) {
               eTag.update(buffer, 0, numRead);
            }
         } while (numRead != -1);
      } catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         Closeables.closeQuietly(i);
      }
      return eTag.digest();
   }

   public String toBase64String(byte[] resBuf) {
      return Base64.encodeBytes(resBuf);
   }

   public MD5InputStreamResult generateMD5Result(InputStream toEncode) {
      MessageDigest eTag = getDigest();
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
      return new MD5InputStreamResult(out.toByteArray(), eTag.digest(), length);
   }

   private MessageDigest getDigest() {
      MessageDigest eTag;
      try {
         eTag = MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException("Could not find the MD5 algorithm", e);
      }
      return eTag;
   }

   public byte[] fromBase64String(String encoded) {
      return Base64.decode(encoded);
   }

}
