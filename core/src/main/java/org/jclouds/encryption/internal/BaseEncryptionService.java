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

import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;

import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.PayloadEnclosing;
import org.jclouds.io.Payload;
import org.jclouds.logging.Logger;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseEncryptionService implements EncryptionService {
   
   @Resource
   protected Logger logger = Logger.NULL;
   
   protected static final int BUF_SIZE = 0x2000; // 8


   final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
            (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'a', (byte) 'b',
            (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f' };

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
    * @
    */
   @Override
   public <T extends PayloadEnclosing> T generateMD5BufferingIfNotRepeatable(T payloadEnclosing) {
      checkState(payloadEnclosing != null, "payloadEnclosing");
      Payload newPayload = generateMD5BufferingIfNotRepeatable(payloadEnclosing.getPayload());
      if (newPayload != payloadEnclosing.getPayload())
         payloadEnclosing.setPayload(newPayload);
      return payloadEnclosing;
   }
}
