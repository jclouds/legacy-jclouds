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
package org.jclouds.blobstore.functions.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.jclouds.blobstore.domain.MD5InputStreamResult;
import org.jclouds.blobstore.functions.GenerateMD5Result;
import org.jclouds.blobstore.internal.BlobRuntimeException;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BouncyCastleGenerateMD5Result implements GenerateMD5Result {
   public MD5InputStreamResult apply(InputStream toEncode) {
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
         throw new BlobRuntimeException("couldn't get MD5 for: " + toEncode, e);
      } finally {
         IOUtils.closeQuietly(out);
         IOUtils.closeQuietly(toEncode);
      }
      eTag.doFinal(resBuf, 0);
      return new MD5InputStreamResult(out.toByteArray(), resBuf, length);
   }
}
