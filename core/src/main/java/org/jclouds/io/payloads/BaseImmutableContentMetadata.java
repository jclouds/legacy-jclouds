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
package org.jclouds.io.payloads;

import java.util.Arrays;
import java.util.Date;

import org.jclouds.io.ContentMetadata;
import org.jclouds.io.ContentMetadataBuilder;

import com.google.common.base.Objects;

/**
 * @author Adrian Cole
 */
public class BaseImmutableContentMetadata implements ContentMetadata {

   protected String contentType;
   protected Long contentLength;
   protected byte[] contentMD5;
   protected String contentDisposition;
   protected String contentLanguage;
   protected String contentEncoding;
   protected Date expires;

   public BaseImmutableContentMetadata(String contentType, Long contentLength, byte[] contentMD5,
            String contentDisposition, String contentLanguage, String contentEncoding, Date expires) {
      this.contentType = contentType;
      this.contentLength = contentLength;
      this.contentMD5 = contentMD5;
      this.contentDisposition = contentDisposition;
      this.contentLanguage = contentLanguage;
      this.contentEncoding = contentEncoding;
      this.expires = expires;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Long getContentLength() {
      return contentLength;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public byte[] getContentMD5() {
      if (contentMD5 != null) {
         byte[] retval = new byte[contentMD5.length];
         System.arraycopy(this.contentMD5, 0, retval, 0, contentMD5.length);
         return retval;
      } else {
         return null;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getContentType() {
      return contentType;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getContentDisposition() {
      return this.contentDisposition;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getContentLanguage() {
      return this.contentLanguage;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getContentEncoding() {
      return this.contentEncoding;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Date getExpires() {
      return expires;
   }

   @Override
   public String toString() {
      return "[contentType=" + contentType + ", contentLength=" + contentLength + ", contentDisposition="
               + contentDisposition + ", contentEncoding=" + contentEncoding + ", contentLanguage=" + contentLanguage
               + ", contentMD5=" + Arrays.toString(contentMD5) + ", expires = " + expires + "]";
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
      BaseImmutableContentMetadata other = (BaseImmutableContentMetadata) obj;
      if (contentDisposition == null) {
         if (other.contentDisposition != null)
            return false;
      } else if (!contentDisposition.equals(other.contentDisposition))
         return false;
      if (contentEncoding == null) {
         if (other.contentEncoding != null)
            return false;
      } else if (!contentEncoding.equals(other.contentEncoding))
         return false;
      if (contentLanguage == null) {
         if (other.contentLanguage != null)
            return false;
      } else if (!contentLanguage.equals(other.contentLanguage))
         return false;
      if (contentLength == null) {
         if (other.contentLength != null)
            return false;
      } else if (!contentLength.equals(other.contentLength))
         return false;
      if (!Arrays.equals(contentMD5, other.contentMD5))
         return false;
      if (contentType == null) {
         if (other.contentType != null)
            return false;
      } else if (!contentType.equals(other.contentType))
         return false;
      if (!Objects.equal(expires, other.expires)) {
         return false;
      }
      return true;
   }

   @Override
   public ContentMetadataBuilder toBuilder() {
      return ContentMetadataBuilder.fromContentMetadata(this);
   }

}
