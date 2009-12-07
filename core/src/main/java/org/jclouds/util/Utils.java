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

import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ComputationException;
import com.google.common.collect.MapMaker;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class Utils {
   public static final String UTF8_ENCODING = "UTF-8";

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
    * Web browsers do not always handle '+' characters well, use the well-supported '%20' instead.
    */
   public static String urlEncode(String in, char... skipEncode) {
      if (isUrlEncoded(in))
         return in;
      try {
         String returnVal = URLEncoder.encode(in, "UTF-8").replaceAll("\\+", "%20").replaceAll(
                  "\\*", "%2A").replaceAll("%7E", "~");
         for (char c : skipEncode) {
            returnVal = returnVal.replaceAll(plainToEncodedChars.get(c + ""), c + "");
         }
         return returnVal;
      } catch (UnsupportedEncodingException e) {
         throw new IllegalStateException("Bad encoding on input: " + in, e);
      }
   }

   public static boolean isUrlEncoded(String in) {
      return in.matches(".*%[a-fA-F0-9][a-fA-F0-9].*");
   }

   static Map<String, String> plainToEncodedChars = new MapMaker()
            .makeComputingMap(new Function<String, String>() {
               public String apply(String plain) {
                  try {
                     return URLEncoder.encode(plain, "UTF-8");
                  } catch (UnsupportedEncodingException e) {
                     throw new IllegalStateException("Bad encoding on input: " + plain, e);
                  }
               }
            });

   public static String urlDecode(String in) {
      try {
         return URLDecoder.decode(in, "UTF-8");
      } catch (UnsupportedEncodingException e) {
         throw new IllegalStateException("Bad encoding on input: " + in, e);
      }
   }

   /**
    * Content stream may need to be read. However, we should always close the http stream.
    */
   public static byte[] closeClientButKeepContentStream(HttpResponse response) {
      if (response.getContent() != null) {
         try {
            byte[] data = IOUtils.toByteArray(response.getContent());
            response.setContent(new ByteArrayInputStream(data));
            return data;
         } catch (IOException e) {
            logger.error(e, "Error consuming input");
         } finally {
            IOUtils.closeQuietly(response.getContent());
         }
      }
      return null;
   }

   public static URI parseEndPoint(String hostHeader) {
      URI redirectURI = URI.create(hostHeader);
      String scheme = redirectURI.getScheme();

      checkState(redirectURI.getScheme().startsWith("http"), String.format(
               "header %s didn't parse an http scheme: [%s]", hostHeader, scheme));
      int port = redirectURI.getPort() > 0 ? redirectURI.getPort() : redirectURI.getScheme()
               .equals("https") ? 443 : 80;
      String host = redirectURI.getHost();
      checkState(!host.matches("[/]"), String.format(
               "header %s didn't parse an http host correctly: [%s]", hostHeader, host));
      URI endPoint = URI.create(String.format("%s://%s:%d", scheme, host, port));
      return endPoint;
   }

   public static URI replaceHostInEndPoint(URI endPoint, String host) {
      return URI.create(endPoint.toString().replace(endPoint.getHost(), host));
   }

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
         return IOUtils.toString(input);
      } finally {
         IOUtils.closeQuietly(input);
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
