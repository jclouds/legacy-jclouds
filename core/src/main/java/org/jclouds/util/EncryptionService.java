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
package org.jclouds.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.jclouds.util.internal.JCEEncryptionService;

import com.google.inject.ImplementedBy;

/**
 * 
 * @author Adrian Cole
 */
@ImplementedBy(JCEEncryptionService.class)
public interface EncryptionService {

   String toHexString(byte[] raw);

   byte[] fromHexString(String hex);

   String hmacSha256Base64(String toEncode, byte[] key) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException;

   String hmacSha1Base64(String toEncode, byte[] key) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException;

   String md5Hex(byte[] toEncode) throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidKeyException, UnsupportedEncodingException;

   String md5Base64(byte[] toEncode) throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidKeyException;

   byte[] md5(byte[] plainBytes);

   byte[] md5(File toEncode);

   String toBase64String(byte[] resBuf);

   byte[] md5(Object data);

   MD5InputStreamResult generateMD5Result(InputStream toEncode);

   public static class MD5InputStreamResult {
      public final byte[] data;
      public final byte[] md5;
      public final long length;

      public MD5InputStreamResult(byte[] data, byte[] eTag, long length) {
         this.data = checkNotNull(data, "data");
         this.md5 = checkNotNull(eTag, "eTag");
         checkArgument(length >= 0, "length cannot me negative");
         this.length = length;
      }

   }

   byte[] fromBase64String(String encoded);

}