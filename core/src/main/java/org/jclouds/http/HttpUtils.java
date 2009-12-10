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
package org.jclouds.http;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Base64;
import org.jclouds.logging.Logger;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Multimap;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

public class HttpUtils {
   public static final Pattern URI_PATTERN = Pattern.compile("([a-z0-9]+)://([^:]*):(.*)@(.*)");
   public static final Pattern PATTERN_THAT_BREAKS_URI = Pattern.compile("[a-z0-9]+://.*/.*@.*"); // slash

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
    * Used to extract the URI and authentication data from a String. Note that the java URI class
    * breaks, if there are special characters like '/' present. Otherwise, we wouldn't need this
    * class, and we could simply use URI.create("uri").getUserData();
    * 
    * @author Adrian Cole
    */
   public static URI createUri(String uriPath) {
      if (uriPath.indexOf('@') != 1) {
         List<String> parts = Lists.newArrayList(Splitter.on('@').split(uriPath));
         String path = parts.remove(parts.size() - 1);
         if (parts.size() == 2) {
            parts = Lists.newArrayList(urlEncode(parts.get(0) + "@" + parts.get(1), '/', ':'));
         }
         parts.add(urlEncode(path, '/', ':'));
         uriPath = Joiner.on('@').join(parts);
      } else {
         List<String> parts = Lists.newArrayList(Splitter.on('/').split(uriPath));
         String path = parts.remove(parts.size() - 1);
         parts.add(urlEncode(path, ':'));
         uriPath = Joiner.on('/').join(parts);
      }

      if (PATTERN_THAT_BREAKS_URI.matcher(uriPath).matches()) {
         // Compile and use regular expression
         Matcher matcher = URI_PATTERN.matcher(uriPath);
         if (matcher.find()) {
            String scheme = matcher.group(1);
            String rest = matcher.group(4);
            String account = matcher.group(2);
            String key = matcher.group(3);
            return URI
                     .create(String.format("%s://%s:%s@%s", scheme, account, urlEncode(key), rest));
         } else {
            throw new IllegalArgumentException("bad syntax");
         }
      } else {
         return URI.create(uriPath);
      }
   }

   public static final Pattern IP_PATTERN = Pattern
            .compile("b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).)"
                     + "{3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)b");
   static final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1', (byte) '2', (byte) '3',
            (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'a',
            (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f' };

   public static String toHexString(byte[] raw) {
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

   public static void logRequest(Logger logger, HttpRequest request, String prefix) {
      if (logger.isDebugEnabled()) {
         logger.debug("%s %s", prefix, request.getRequestLine().toString());
         for (Entry<String, String> header : request.getHeaders().entries()) {
            if (header.getKey() != null)
               logger.debug("%s %s: %s", prefix, header.getKey(), header.getValue());
         }
      }
   }

   public static void logResponse(Logger logger, HttpResponse response, String prefix) {
      if (logger.isDebugEnabled()) {
         logger.debug("%s %s", prefix, response.getStatusLine().toString());
         for (Entry<String, String> header : response.getHeaders().entries()) {
            logger.debug("%s %s: %s", prefix, header.getKey(), header.getValue());
         }
      }
   }

   public static byte[] fromHexString(String hex) {
      if (hex.startsWith("0x"))
         hex = hex.substring(2);
      byte[] bytes = new byte[hex.length() / 2];
      for (int i = 0; i < bytes.length; i++) {
         bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
      }
      return bytes;
   }

   public static String hmacSha256Base64(String toEncode, byte[] key)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
      Digest digest = new SHA256Digest();
      return hmacBase64(toEncode, key, digest);
   }

   private static String hmacBase64(String toEncode, byte[] key, Digest digest) {
      byte[] resBuf = hmac(toEncode, key, digest);
      return toBase64String(resBuf);
   }

   public static byte[] hmac(String toEncode, byte[] key, Digest digest) {
      HMac hmac = new HMac(digest);
      byte[] resBuf = new byte[hmac.getMacSize()];
      byte[] plainBytes = Utils.encodeString(toEncode);
      byte[] keyBytes = key;
      hmac.init(new KeyParameter(keyBytes));
      hmac.update(plainBytes, 0, plainBytes.length);
      hmac.doFinal(resBuf, 0);
      return resBuf;
   }

   public static String hmacSha1Base64(String toEncode, byte[] key)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
      Digest digest = new SHA1Digest();
      return hmacBase64(toEncode, key, digest);
   }

   public static String md5Hex(byte[] toEncode) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException, UnsupportedEncodingException {
      byte[] resBuf = md5(toEncode);
      return toHexString(resBuf);
   }

   public static String md5Base64(byte[] toEncode) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException {
      byte[] resBuf = md5(toEncode);
      return toBase64String(resBuf);
   }

   public static byte[] md5(byte[] plainBytes) {
      MD5Digest eTag = new MD5Digest();
      byte[] resBuf = new byte[eTag.getDigestSize()];
      eTag.update(plainBytes, 0, plainBytes.length);
      eTag.doFinal(resBuf, 0);
      return resBuf;
   }

   public static byte[] md5(File toEncode) {
      MD5Digest eTag = new MD5Digest();
      byte[] resBuf = new byte[eTag.getDigestSize()];
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
         IOUtils.closeQuietly(i);
      }
      eTag.doFinal(resBuf, 0);
      return resBuf;
   }

   public static String toBase64String(byte[] resBuf) {
      return new String(Base64.encode(resBuf));
   }

   public static Long calculateSize(Object data) {
      Long size = null;
      if (data instanceof byte[]) {
         size = new Long(((byte[]) data).length);
      } else if (data instanceof String) {
         size = new Long(((String) data).length());
      } else if (data instanceof File) {
         size = ((File) data).length();
      }
      return size;
   }

   /**
    * @throws IOException
    */
   public static byte[] md5(Object data) {
      checkNotNull(data, "data must be set before calling generateETag()");
      byte[] md5 = null;
      if (data == null) {
      } else if (data instanceof byte[]) {
         md5 = md5((byte[]) data);
      } else if (data instanceof String) {
         md5 = md5(((String) data).getBytes());
      } else if (data instanceof File) {
         md5 = md5(((File) data));
      } else if (data instanceof InputStream) {
         md5 = generateMD5Result(((InputStream) data)).md5;
      } else {
         throw new UnsupportedOperationException("Content not supported " + data.getClass());
      }
      return md5;

   }

   public static MD5InputStreamResult generateMD5Result(InputStream toEncode) {
      MD5Digest eTag = new MD5Digest();
      byte[] resBuf = new byte[eTag.getDigestSize()];
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
         IOUtils.closeQuietly(out);
         IOUtils.closeQuietly(toEncode);
      }
      eTag.doFinal(resBuf, 0);
      return new MD5InputStreamResult(out.toByteArray(), resBuf, length);
   }

   public static class MD5InputStreamResult {
      public final byte[] data;
      public final byte[] md5;
      public final long length;

      MD5InputStreamResult(byte[] data, byte[] eTag, long length) {
         this.data = checkNotNull(data, "data");
         this.md5 = checkNotNull(eTag, "eTag");
         checkArgument(length >= 0, "length cannot me negative");
         this.length = length;
      }

   }

   public static void copy(InputStream input, OutputStream output) throws IOException {
      byte[] buffer = new byte[1024];
      long length = 0;
      int numRead = -1;
      try {
         do {
            numRead = input.read(buffer);
            if (numRead > 0) {
               length += numRead;
               output.write(buffer, 0, numRead);
            }
         } while (numRead != -1);
      } finally {
         output.close();
         IOUtils.closeQuietly(input);
      }
   }

   public static String sortAndConcatHeadersIntoString(Multimap<String, String> headers) {
      StringBuffer buffer = new StringBuffer();
      SortedSetMultimap<String, String> sortedMap = TreeMultimap.create();
      sortedMap.putAll(headers);
      for (Entry<String, String> header : sortedMap.entries()) {
         if (header.getKey() != null)
            buffer.append(String.format("%s: %s\n", header.getKey(), header.getValue()));
      }
      return buffer.toString();
   }

   public static byte[] fromBase64String(String encoded) {
      return Base64.decode(encoded);
   }

}
