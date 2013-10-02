/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.io;

import java.util.Arrays;
import java.util.Date;

import org.jclouds.io.payloads.BaseImmutableContentMetadata;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * @author Adrian Cole
 */
public class ContentMetadataBuilder {

   public static ContentMetadataBuilder create() {
      return new ContentMetadataBuilder();
   }

   protected String contentType = "application/unknown";
   protected Long contentLength;
   protected byte[] contentMD5;
   protected String contentDisposition;
   protected String contentLanguage;
   protected String contentEncoding;
   protected Date expires;

   public ContentMetadataBuilder contentLength(@Nullable Long contentLength) {
      this.contentLength = contentLength;
      return this;
   }

   public ContentMetadataBuilder contentMD5(byte[] md5) {
      if (md5 != null) {
         byte[] retval = new byte[md5.length];
         System.arraycopy(md5, 0, retval, 0, md5.length);
         this.contentMD5 = md5;
      }
      return this;

   }

   public ContentMetadataBuilder contentType(@Nullable String contentType) {
      this.contentType = contentType;
      return this;
   }

   public ContentMetadataBuilder contentDisposition(@Nullable String contentDisposition) {
      this.contentDisposition = contentDisposition;
      return this;

   }

   public ContentMetadataBuilder contentLanguage(@Nullable String contentLanguage) {
      this.contentLanguage = contentLanguage;
      return this;
   }

   public ContentMetadataBuilder contentEncoding(@Nullable String contentEncoding) {
      this.contentEncoding = contentEncoding;
      return this;
   }

   public ContentMetadataBuilder expires(@Nullable Date expires) {
      this.expires = expires;
      return this;
   }

   public ContentMetadata build() {
      return new BaseImmutableContentMetadata(contentType, contentLength, contentMD5, contentDisposition,
               contentLanguage, contentEncoding, expires);
   }

   public static ContentMetadataBuilder fromContentMetadata(ContentMetadata in) {
      return new ContentMetadataBuilder().contentType(in.getContentType()).contentLength(in.getContentLength())
               .contentMD5(in.getContentMD5()).contentDisposition(in.getContentDisposition()).contentLanguage(
                        in.getContentLanguage()).contentEncoding(in.getContentEncoding()).expires(in.getExpires());
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(contentDisposition, contentEncoding, contentLanguage, contentLength, 
               contentMD5, contentType, expires);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ContentMetadataBuilder other = (ContentMetadataBuilder) obj;
      return Objects.equal(contentDisposition, other.contentDisposition) &&
             Objects.equal(contentEncoding, other.contentEncoding) &&
             Objects.equal(contentLanguage, other.contentLanguage) &&
             Objects.equal(contentLength, other.contentLength) &&
             Arrays.equals(contentMD5, other.contentMD5) &&
             Objects.equal(contentType, other.contentType) &&
             Objects.equal(expires, other.expires);
   }

   @Override
   public String toString() {
      return "[contentDisposition=" + contentDisposition + ", contentEncoding=" + contentEncoding
               + ", contentLanguage=" + contentLanguage + ", contentLength=" + contentLength + ", contentMD5="
               + Arrays.toString(contentMD5) + ", contentType=" + contentType + ", expires=" + expires + "]";
   }
}
