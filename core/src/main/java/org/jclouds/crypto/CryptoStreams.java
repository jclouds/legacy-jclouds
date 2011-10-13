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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Mac;

import org.jclouds.encryption.internal.Base64;
import org.jclouds.io.InputSuppliers;

import com.google.common.annotations.Beta;
import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;

/**
 * functions related to but not in {@link com.google.common.io.ByteStreams}
 * 
 * @author Adrian Cole
 */
@Beta
public class CryptoStreams {

   public static String hex(byte[] in) {
      byte[] hex = new byte[2 * in.length];
      int index = 0;

      for (byte b : in) {
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

   public static byte[] hex(String s) {
      int len = s.length();
      byte[] data = new byte[len / 2];
      for (int i = 0; i < len; i += 2) {
         data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
      }
      return data;
   }

   public static String base64(byte[] in) {
      return Base64.encodeBytes(in, Base64.DONT_BREAK_LINES);
   }

   public static byte[] base64(String in) {
      return Base64.decode(in);
   }

   /**
    * @see #md5
    * @see #hex
    */
   public static String md5Hex(InputSupplier<? extends InputStream> supplier) throws IOException {
      return hex(md5(supplier));
   }

   /**
    * @see #md5
    * @see #base64
    */
   public static String md5Base64(InputSupplier<? extends InputStream> supplier) throws IOException {
      return base64(md5(supplier));
   }

   /**
    * Computes and returns the MAC value for a supplied input stream. The mac
    * object is reset when this method returns successfully.
    * 
    * @param supplier
    *           the input stream factory
    * @param mac
    *           the mac object
    * @return the result of {@link Mac#doFinal()} after updating the mac object
    *         with all of the bytes in the stream and encoding in Base64
    * @throws IOException
    *            if an I/O error occurs
    */
   public static String macBase64(InputSupplier<? extends InputStream> supplier, final Mac mac) throws IOException {
      return base64(mac(supplier, mac));
   }

   /**
    * Computes and returns the Digest value for a supplied input stream. The
    * digest object is reset when this method returns successfully.
    * 
    * @param supplier
    *           the input stream factory
    * @param md
    *           the digest object
    * @return the result of {@link MessageDigest#digest()} after updating the
    *         digest object with all of the bytes in the stream
    * @throws IOException
    *            if an I/O error occurs
    */
   public static byte[] digest(InputSupplier<? extends InputStream> supplier, final MessageDigest md)
         throws IOException {
      return com.google.common.io.ByteStreams.readBytes(supplier, new ByteProcessor<byte[]>() {
         public boolean processBytes(byte[] buf, int off, int len) {
            md.update(buf, off, len);
            return true;
         }

         public byte[] getResult() {
            return md.digest();
         }
      });
   }

   /**
    * Computes and returns the MD5 value for a supplied input stream. A digest
    * object is created and disposed of at runtime, consider using
    * {@link #digest} to be more efficient.
    * 
    * @param supplier
    *           the input stream factory
    * 
    * @return the result of {@link MessageDigest#digest()} after updating the
    *         md5 object with all of the bytes in the stream
    * @throws IOException
    *            if an I/O error occurs
    */
   public static byte[] md5(InputSupplier<? extends InputStream> supplier) throws IOException {
      try {
         return digest(supplier, MessageDigest.getInstance("MD5"));
      } catch (NoSuchAlgorithmException e) {
         Throwables.propagate(e);
         return null;
      }
   }

   public static byte[] md5(byte[] in) {
      try {
         return md5(ByteStreams.newInputStreamSupplier(in));
      } catch (IOException e) {
         Throwables.propagate(e);
         return null;
      }
   }

   /**
    * Computes and returns the MAC value for a supplied input stream. The mac
    * object is reset when this method returns successfully.
    * 
    * @param supplier
    *           the input stream factory
    * @param mac
    *           the mac object
    * @return the result of {@link Mac#doFinal()} after updating the mac object
    *         with all of the bytes in the stream
    * @throws IOException
    *            if an I/O error occurs
    */
   public static byte[] mac(InputSupplier<? extends InputStream> supplier, final Mac mac) throws IOException {
      return com.google.common.io.ByteStreams.readBytes(checkNotNull(supplier, "supplier"),
            new ByteProcessor<byte[]>() {
               public boolean processBytes(byte[] buf, int off, int len) {
                  mac.update(buf, off, len);
                  return true;
               }

               public byte[] getResult() {
                  return mac.doFinal();
               }
            });
   }

   /**
    * Computes and returns the base64 value for a supplied input stream.
    * 
    * @param supplier
    *           the input stream factory
    * 
    * @return the result of base 64 encoding all of the bytes in the stream
    * @throws IOException
    *            if an I/O error occurs
    */
   public static String base64Encode(InputSupplier<? extends InputStream> supplier) throws IOException {
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      return com.google.common.io.ByteStreams.readBytes(InputSuppliers.base64Encoder(supplier),
            new ByteProcessor<String>() {
               public boolean processBytes(byte[] buf, int off, int len) {
                  out.write(buf, off, len);
                  return true;
               }

               public String getResult() {
                  return new String(out.toByteArray(), Charsets.UTF_8);
               }
            });
   }

   /**
    * Computes and returns the unencoded value for an input stream which is
    * encoded in Base64.
    * 
    * @param supplier
    *           the input stream factory
    * 
    * @return the result of base 64 decoding all of the bytes in the stream
    * @throws IOException
    *            if an I/O error occurs
    */
   public static byte[] base64Decode(InputSupplier<? extends InputStream> supplier) throws IOException {
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      return com.google.common.io.ByteStreams.readBytes(InputSuppliers.base64Decoder(supplier),
            new ByteProcessor<byte[]>() {
               public boolean processBytes(byte[] buf, int off, int len) {
                  out.write(buf, off, len);
                  return true;
               }

               public byte[] getResult() {
                  return out.toByteArray();
               }
            });
   }

   final static byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5',
         (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e',
         (byte) 'f' };

   /**
    * Computes and returns the hex value for a supplied input stream.
    * 
    * @param supplier
    *           the input stream factory
    * 
    * @return the result of hex encoding all of the bytes in the stream
    * @throws IOException
    *            if an I/O error occurs
    */
   public static String hexEncode(InputSupplier<? extends InputStream> supplier) throws IOException {
      final StringBuilder out = new StringBuilder();
      return com.google.common.io.ByteStreams.readBytes(supplier, new ByteProcessor<String>() {
         public boolean processBytes(byte[] buf, int off, int len) {
            char[] hex = new char[2 * len];
            int index = 0;

            for (int i = off; i < off + len; i++) {
               byte b = buf[i];
               int v = b & 0xFF;
               hex[index++] = (char) HEX_CHAR_TABLE[v >>> 4];
               hex[index++] = (char) HEX_CHAR_TABLE[v & 0xF];
            }
            out.append(hex);
            return true;
         }

         public String getResult() {
            return out.toString();
         }
      });
   }

   /**
    * Computes and returns the unencoded value for an input stream which is
    * encoded in hex.
    * 
    * @param supplier
    *           the input stream factory
    * 
    * @return the result of hex decoding all of the bytes in the stream
    * @throws IOException
    *            if an I/O error occurs
    */
   public static byte[] hexDecode(InputSupplier<? extends InputStream> supplier) throws IOException {
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      return com.google.common.io.ByteStreams.readBytes(supplier, new ByteProcessor<byte[]>() {
         int currentPos = 0;

         public boolean processBytes(byte[] buf, int off, int len) {
            try {
               if (currentPos == 0 && new String(Arrays.copyOfRange(buf, off, 2), "ASCII").equals("0x")) {
                  off += 2;
               }
               byte[] decoded = hex(new String(Arrays.copyOfRange(buf, off, len), "ASCII"));
               out.write(decoded, 0, decoded.length);
               currentPos += len;
            } catch (UnsupportedEncodingException e) {
               throw new IllegalStateException("ASCII must be supported");
            }
            return true;
         }

         public byte[] getResult() {
            return out.toByteArray();
         }
      });
   }

}
