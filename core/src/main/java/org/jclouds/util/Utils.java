/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.jclouds.logging.Logger;

import com.google.common.base.Charsets;
import com.google.common.base.Supplier;
import com.google.common.collect.ComputationException;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.io.OutputSupplier;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class Utils {
   public static final String UTF8_ENCODING = "UTF-8";

   /**
    * Returns a factory that will supply instances of {@link OutputStream} that read from the given
    * outputStream.
    * 
    * @param url
    *           the URL to read from
    * @return the factory
    */
   public static OutputSupplier<OutputStream> newOutputStreamSupplier(final OutputStream output) {
      checkNotNull(output, "output");
      return new OutputSupplier<OutputStream>() {
         public OutputStream getOutput() throws IOException {
            return output;
         }
      };
   }

   public static boolean enventuallyTrue(Supplier<Boolean> assertion, long inconsistencyMillis)
            throws InterruptedException {

      for (int i = 0; i < 30; i++) {
         if (!assertion.get()) {
            Thread.sleep(inconsistencyMillis / 30);
            continue;
         }
         return true;
      }
      return false;

   }

   @Resource
   protected static Logger logger = Logger.NULL;

   /**
    * 
    * @param <E>
    *           Exception type you'd like rethrown
    * @param e
    *           Exception you are inspecting
    * @throws E
    */
   public static void rethrowIfRuntime(Exception e) {
      if (e instanceof ExecutionException || e instanceof ComputationException) {
         Throwable nested = e.getCause();
         if (nested instanceof Error)
            throw (Error) nested;
         e = (Exception) nested;
      }

      if (e instanceof RuntimeException) {
         throw (RuntimeException) e;
      }
   }

   /**
    * 
    * @param <E>
    *           Exception type you'd like rethrown
    * @param e
    *           Exception you are inspecting
    * @throws E
    */
   @SuppressWarnings("unchecked")
   public static <E extends Exception> Exception rethrowIfRuntimeOrSameType(Exception e) throws E {
      if (e instanceof ExecutionException || e instanceof ComputationException) {
         Throwable nested = e.getCause();
         if (nested instanceof Error)
            throw (Error) nested;
         e = (Exception) nested;
      }

      if (e instanceof RuntimeException) {
         throw (RuntimeException) e;
      } else {
         try {
            throw (E) e;
         } catch (ClassCastException throwAway) {
            // using cce as there's no way to do instanceof E in current java
         }
      }
      return e;
   }

   public static String toStringAndClose(InputStream input) throws IOException {
      try {
         return new String(ByteStreams.toByteArray(input), Charsets.UTF_8);
      } finally {
         Closeables.closeQuietly(input);
      }
   }

   public static InputStream toInputStream(String in) {
      try {
         return ByteStreams.newInputStreamSupplier(in.getBytes(Charsets.UTF_8)).getInput();
      } catch (IOException e) {
         logger.warn(e, "Failed to convert %s to an inputStream", in);
         throw new RuntimeException(e);
      }
   }

   /**
    * Encode the given string with the given encoding, if possible. If the encoding fails with
    * {@link UnsupportedEncodingException}, log a warning and fall back to the system's default
    * encoding.
    * 
    * @see {@link String#getBytes(String)}
    * @see {@link String#getBytes()} - used as fall-back.
    * 
    * @param str
    * @param encoding
    * @return
    */
   public static byte[] encodeString(String str, String encoding) {
      try {
         return str.getBytes(encoding);
      } catch (UnsupportedEncodingException e) {
         logger.warn(e, "Failed to encode string to bytes with encoding " + encoding
                  + ". Falling back to system's default encoding");
         return str.getBytes();
      }
   }

   /**
    * Encode the given string with the UTF-8 encoding, the sane default. In the very unlikely event
    * the encoding fails with {@link UnsupportedEncodingException}, log a warning and fall back to
    * the system's default encoding.
    * 
    * @param str
    * @return
    */
   public static byte[] encodeString(String str) {
      return encodeString(str, UTF8_ENCODING);
   }

   /**
    * Decode the given string with the given encoding, if possible. If the decoding fails with
    * {@link UnsupportedEncodingException}, log a warning and fall back to the system's default
    * encoding.
    * 
    * @param bytes
    * @param encoding
    * @return
    */
   public static String decodeString(byte[] bytes, String encoding) {
      try {
         return new String(bytes, encoding);
      } catch (UnsupportedEncodingException e) {
         logger.warn(e, "Failed to decode bytes to string with encoding " + encoding
                  + ". Falling back to system's default encoding");
         return new String(bytes);
      }
   }

   /**
    * Decode the given string with the UTF-8 encoding, the sane default. In the very unlikely event
    * the encoding fails with {@link UnsupportedEncodingException}, log a warning and fall back to
    * the system's default encoding.
    * 
    * @param str
    * @return
    */
   public static String decodeString(byte[] bytes) {
      return decodeString(bytes, UTF8_ENCODING);
   }

   public static final Pattern pattern = Pattern.compile("\\{(.+?)\\}");

   /**
    * replaces tokens that are expressed as <code>{token}</code>
    * 
    * <p/>
    * ex. if input is "hello {where}"<br/>
    * and replacements is "where" -> "world" <br/>
    * then replaceTokens returns "hello world"
    * 
    * @param input
    *           source to replace
    * @param replacements
    *           token/value pairs
    */
   public static String replaceTokens(String input, Map<String, String> replacements) {
      Matcher matcher = pattern.matcher(input);
      StringBuilder builder = new StringBuilder();
      int i = 0;
      while (matcher.find()) {
         String replacement = replacements.get(matcher.group(1));
         builder.append(input.substring(i, matcher.start()));
         if (replacement == null)
            builder.append(matcher.group(0));
         else
            builder.append(replacement);
         i = matcher.end();
      }
      builder.append(input.substring(i, input.length()));
      return builder.toString();
   }

}
