/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.aws.s3.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.filters.RequestAuthorizeSignature;
import org.jclouds.aws.s3.reference.S3Headers;
import org.jclouds.aws.s3.xml.S3ParserFactory;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpFutureCommand;
import org.jclouds.http.HttpResponse;

/**
 * Encryption, Hashing, and IO Utilities needed to sign and verify S3 requests and responses.
 * 
 * @author Adrian Cole
 */
public class S3Utils extends AWSUtils {

   public static AWSError parseAWSErrorFromContent(S3ParserFactory parserFactory,
            HttpFutureCommand<?> command, HttpResponse response, InputStream content)
            throws HttpException {
      AWSError error = parserFactory.createErrorParser().parse(content);
      error.setRequestId(response.getFirstHeaderOrNull(S3Headers.REQUEST_ID));
      error.setRequestToken(response.getFirstHeaderOrNull(S3Headers.REQUEST_TOKEN));
      if ("SignatureDoesNotMatch".equals(error.getCode()))
         error.setStringSigned(RequestAuthorizeSignature.createStringToSign(command.getRequest()));
      return error;

   }

   public static AWSError parseAWSErrorFromContent(S3ParserFactory parserFactory,
            HttpFutureCommand<?> command, HttpResponse response, String content)
            throws HttpException {
      return parseAWSErrorFromContent(parserFactory, command, response, new ByteArrayInputStream(
               content.getBytes()));
   }

   public static String validateBucketName(String bucketName) {
      checkNotNull(bucketName, "bucketName");
      checkArgument(bucketName.matches("^[a-z0-9].*"),
               "bucketName name must start with a number or letter");
      checkArgument(
               bucketName.matches("^[-_.a-z0-9]+"),
               "bucketName name can only contain lowercase letters, numbers, periods (.), underscores (_), and dashes (-)");
      checkArgument(bucketName.length() > 2 && bucketName.length() < 256,
               "bucketName name must be between 3 and 255 characters long");
      checkArgument(!IP_PATTERN.matcher(bucketName).matches(),
               "bucketName name cannot be ip address style");
      return bucketName;
   }

   public static long calculateSize(Object data) {
      long size = -1;
      if (data instanceof byte[]) {
         size = ((byte[]) data).length;
      } else if (data instanceof String) {
         size = ((String) data).length();
      } else if (data instanceof File) {
         size = ((File) data).length();
      }
      return size;
   }

   /**
    * @throws IOException
    */
   public static byte[] md5(Object data) throws IOException {
      checkNotNull(data, "data must be set before calling generateMd5()");
      byte[] md5 = null;
      if (data == null) {
      } else if (data instanceof byte[]) {
         md5 = S3Utils.md5((byte[]) data);
      } else if (data instanceof String) {
         md5 = S3Utils.md5(((String) data).getBytes());
      } else if (data instanceof File) {
         md5 = S3Utils.md5(((File) data));
      } else {
         throw new UnsupportedOperationException("Content not supported " + data.getClass());
      }
      return md5;

   }

   public static Md5InputStreamResult generateMd5Result(InputStream toEncode) throws IOException {
      MD5Digest md5 = new MD5Digest();
      byte[] resBuf = new byte[md5.getDigestSize()];
      byte[] buffer = new byte[1024];
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      long length = 0;
      int numRead = -1;
      try {
         do {
            numRead = toEncode.read(buffer);
            if (numRead > 0) {
               length += numRead;
               md5.update(buffer, 0, numRead);
               out.write(buffer, 0, numRead);
            }
         } while (numRead != -1);
      } finally {
         out.close();
         IOUtils.closeQuietly(toEncode);
      }
      md5.doFinal(resBuf, 0);
      return new Md5InputStreamResult(out.toByteArray(), resBuf, length);
   }

   public static class Md5InputStreamResult {
      public final byte[] data;
      public final byte[] md5;
      public final long length;

      Md5InputStreamResult(byte[] data, byte[] md5, long length) {
         this.data = checkNotNull(data, "data");
         this.md5 = checkNotNull(md5, "md5");
         checkArgument(length >= 0, "length cannot me negative");
         this.length = length;
      }

   }

   public static String getContentAsStringAndClose(S3Object object) throws IOException {
      checkNotNull(object, "s3Object");
      checkNotNull(object.getData(), "s3Object.content");
      Object o = object.getData();

      if (o instanceof InputStream) {
         String returnVal = toStringAndClose((InputStream) o);
         if (object.getMetadata().getContentType().indexOf("xml") >= 0) {

         }
         return returnVal;
      } else {
         throw new IllegalArgumentException("Object type not supported: " + o.getClass().getName());
      }
   }
}