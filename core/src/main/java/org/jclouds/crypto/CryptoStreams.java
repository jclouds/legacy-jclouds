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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

   private static char[] hex(byte[] in, int offset, int len) {
      char[] hex = new char[2 * len];
      int index = 0;

      for (int i = offset; i < offset + len; ++i) {
         byte b = in[i];
         int v = b & 0xFF;
         hex[index++] = HEX_CHAR_TABLE[v >>> 4];
         hex[index++] = HEX_CHAR_TABLE[v & 0xF];
      }
      return hex;
   }

   public static String hex(byte[] in) {
      return new String(hex(in, 0, in.length));
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
   
   /**
    * encodes value substituting {@code '-' and '_'} for {@code '+' and '/'},
    * and without adding trailing {@code '='} padding.
    * 
    * <h3>Note</h3>
    * This utility will be replaced with Guava 14+ BaseEncoding.base64Url()
    * 
    * @see <a
    *      href="http://en.wikipedia.org/wiki/Base64#URL_applications">url-safe
    *      encoding</a>
    */
   @Beta
   public static String base64Url(byte[] in) {
      String provisional = base64(in);
      int length = provisional.length();
      if (length == 0)
         return provisional;
      // we know base64 is in 4 character chunks, so out of bounds risk here
      else if (provisional.charAt(length - 2) == '=')
         length-=2;
      else if (provisional.charAt(length - 1) == '=')
         length-=1;
      
      char[] tmp = new char[length];

      for (int i = 0; i < length; i++) {
         char c = provisional.charAt(i);
         switch (c) {
         case '+':
            tmp[i] = '-';
            break;
         case '/':
            tmp[i] = '_';
            break;
         default:
            tmp[i] = c;
            break;
         }
      }
      return new String(tmp);
   }
   
   /**
    * decodes base 64 encoded string, regardless of whether padding {@code '='} padding is present.
    * 
    * Note this seamlessly handles the URL-safe case where {@code '-' and '_'} are substituted for {@code '+' and '/'}.
    * 
    * @see <a
    *      href="http://en.wikipedia.org/wiki/Base64#URL_applications">url-safe
    *      encoding</a>
    */
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
    * @see #md5Hex
    */
   public static String md5Hex(final String in) {
      try {
         return md5Hex(new InputSupplier<InputStream>() {
            @Override
            public InputStream getInput() throws IOException {
               return new ByteArrayInputStream(in.getBytes());
            }
         });
      } catch (IOException e) {
         // risk is not here when reading from in.getBytes() so wrapping in runtime
         throw Throwables.propagate(e);
      }
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
    * Computes and returns the SHA1 value for a supplied input stream. A digest
    * object is created and disposed of at runtime, consider using
    * {@link #digest} to be more efficient.
    * 
    * @param supplier
    *           the input stream factory
    * 
    * @return the result of {@link MessageDigest#digest()} after updating the
    *         sha1 object with all of the bytes in the stream
    * @throws IOException
    *            if an I/O error occurs
    */
   public static byte[] sha1(InputSupplier<? extends InputStream> supplier) throws IOException {
      try {
         return digest(supplier, MessageDigest.getInstance("SHA1"));
      } catch (NoSuchAlgorithmException e) {
         throw Throwables.propagate(e);
      }
   }

   public static byte[] sha1(byte[] in) {
      try {
         return sha1(ByteStreams.newInputStreamSupplier(in));
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
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
         throw Throwables.propagate(e);
      }
   }

   public static byte[] md5(byte[] in) {
      try {
         return md5(ByteStreams.newInputStreamSupplier(in));
      } catch (IOException e) {
         throw Throwables.propagate(e);
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

   private static final char[] HEX_CHAR_TABLE = {
      '0', '1', '2', '3', '4', '5', '6', '7',
      '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
   };

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
            out.append(hex(buf, off, len));
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
            if (currentPos == 0 && new String(Arrays.copyOfRange(buf, off, 2), Charsets.US_ASCII).equals("0x")) {
               off += 2;
            }
            byte[] decoded = hex(new String(Arrays.copyOfRange(buf, off, len), Charsets.US_ASCII));
            out.write(decoded, 0, decoded.length);
            currentPos += len;
            return true;
         }

         public byte[] getResult() {
            return out.toByteArray();
         }
      });
   }

}
