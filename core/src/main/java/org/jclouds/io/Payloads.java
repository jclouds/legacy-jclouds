/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.io;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.io.ByteStreams.toByteArray;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Map;

import javax.annotation.Nullable;

import org.jclouds.crypto.CryptoStreams;
import org.jclouds.io.payloads.ByteArrayPayload;
import org.jclouds.io.payloads.FilePayload;
import org.jclouds.io.payloads.InputStreamPayload;
import org.jclouds.io.payloads.StringPayload;
import org.jclouds.io.payloads.UrlEncodedFormPayload;

import com.google.common.base.Throwables;
import com.google.common.collect.Multimap;

/**
 * 
 * @author Adrian Cole
 */
public class Payloads {
   private Payloads() {
   }

   public static Payload newPayload(Object data) {
      checkNotNull(data, "data");
      if (data instanceof Payload) {
         return (Payload) data;
      } else if (data instanceof InputStream) {
         return newInputStreamPayload((InputStream) data);
      } else if (data instanceof byte[]) {
         return newByteArrayPayload((byte[]) data);
      } else if (data instanceof String) {
         return newStringPayload((String) data);
      } else if (data instanceof File) {
         return newFilePayload((File) data);
      } else {
         throw new UnsupportedOperationException("unsupported payload type: " + data.getClass());
      }
   }

   public static InputStreamPayload newInputStreamPayload(InputStream data) {
      return new InputStreamPayload(checkNotNull(data, "data"));
   }

   public static ByteArrayPayload newByteArrayPayload(byte[] data) {
      return new ByteArrayPayload(checkNotNull(data, "data"));
   }

   public static StringPayload newStringPayload(String data) {
      return new StringPayload(checkNotNull(data, "data"));
   }

   public static FilePayload newFilePayload(File data) {
      return new FilePayload(checkNotNull(data, "data"));
   }

   public static UrlEncodedFormPayload newUrlEncodedFormPayload(Multimap<String, String> formParams, char... skips) {
      return new UrlEncodedFormPayload(formParams, skips);
   }

   public static UrlEncodedFormPayload newUrlEncodedFormPayload(Multimap<String, String> formParams,
         @Nullable Comparator<Map.Entry<String, String>> sorter, char... skips) {
      return new UrlEncodedFormPayload(formParams, sorter, skips);
   }

   /**
    * Calculates and sets {@link Payload#setContentMD5} on the payload.
    * 
    * <p/>
    * note that this will rebuffer in memory if the payload is not repeatable.
    * 
    * @param payload
    *           payload to calculate
    * @param md5
    *           digester to calculate payloads with.
    * @return new Payload with md5 set.
    * @throws IOException
    */
   public static Payload calculateMD5(Payload payload, MessageDigest md5) throws IOException {
      checkNotNull(payload, "payload");
      if (!payload.isRepeatable()) {
         MutableContentMetadata oldContentMetadata = payload.getContentMetadata();
         Payload oldPayload = payload;
         try {
            payload = newByteArrayPayload(toByteArray(payload));
         } finally {
            oldPayload.release();
         }
         oldContentMetadata.setContentLength(payload.getContentMetadata().getContentLength());
         oldContentMetadata.setContentMD5(payload.getContentMetadata().getContentMD5());
         payload.setContentMetadata(oldContentMetadata);
      }
      payload.getContentMetadata().setContentMD5(CryptoStreams.digest(payload, md5));
      return payload;
   }

   /**
    * Uses default md5 generator.
    * 
    * @see #calculateMD5(Payload, MessageDigest)
    */
   public static Payload calculateMD5(Payload payload) throws IOException {
      try {
         return calculateMD5(payload, MessageDigest.getInstance("MD5"));
      } catch (NoSuchAlgorithmException e) {
         Throwables.propagate(e);
         return null;
      }
   }

   /**
    * Calculates the md5 on a payload, replacing as necessary.
    * 
    * @see #calculateMD5(Payload, MessageDigest)
    */
   public static <T extends PayloadEnclosing> T calculateMD5(T payloadEnclosing, MessageDigest md5) throws IOException {
      checkState(payloadEnclosing != null, "payloadEnclosing");
      Payload newPayload = calculateMD5(payloadEnclosing.getPayload(), md5);
      if (newPayload != payloadEnclosing.getPayload())
         payloadEnclosing.setPayload(newPayload);
      return payloadEnclosing;
   }

   /**
    * Calculates the md5 on a payload, replacing as necessary.
    * <p/>
    * uses default md5 generator.
    * 
    * @see #calculateMD5(Payload, MessageDigest)
    */
   public static <T extends PayloadEnclosing> T calculateMD5(T payloadEnclosing) throws IOException {
      try {
         return calculateMD5(payloadEnclosing, MessageDigest.getInstance("MD5"));
      } catch (NoSuchAlgorithmException e) {
         Throwables.propagate(e);
         return null;
      }
   }
}
