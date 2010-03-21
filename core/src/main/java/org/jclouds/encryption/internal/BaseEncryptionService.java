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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.jclouds.encryption.EncryptionService;

import com.google.common.base.Throwables;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseEncryptionService implements EncryptionService {

   final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
            (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'a', (byte) 'b',
            (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f' };

   public String toHexString(byte[] raw) {
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

   public byte[] fromHexString(String hex) {
      if (hex.startsWith("0x"))
         hex = hex.substring(2);
      byte[] bytes = new byte[hex.length() / 2];
      for (int i = 0; i < bytes.length; i++) {
         bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
      }
      return bytes;
   }

   public String md5Hex(byte[] toEncode) throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidKeyException, UnsupportedEncodingException {
      byte[] resBuf = md5(toEncode);
      return toHexString(resBuf);
   }

   public String md5Base64(byte[] toEncode) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException {
      byte[] resBuf = md5(toEncode);
      return toBase64String(resBuf);
   }

   /**
    * @throws IOException
    */
   public byte[] md5(Object data) {
      checkNotNull(data, "data must be set before calling generateETag()");
      byte[] md5 = null;
      if (data == null) {
      } else if (data instanceof byte[]) {
         md5 = md5((byte[]) data);
      } else if (data instanceof String) {
         md5 = md5(((String) data).getBytes());
      } else if (data instanceof File) {
         try {
            md5 = md5(new FileInputStream((File) data));
         } catch (FileNotFoundException e) {
            Throwables.propagate(e);
         }
      } else if (data instanceof InputStream) {
         md5 = md5(((InputStream) data));
      } else {
         throw new UnsupportedOperationException("Content not supported " + data.getClass());
      }
      return md5;

   }

}
