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

package org.jclouds.io.payloads;

import static com.google.common.collect.Iterables.any;
import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.jclouds.crypto.CryptoStreams;
import org.jclouds.io.MutableContentMetadata;

import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;

/**
 * @author Adrian Cole
 */
public class BaseMutableContentMetadata implements MutableContentMetadata, Serializable {
   @Override
   public void setPropertiesFromHttpHeaders(Multimap<String, String> headers) {
      boolean chunked = any(headers.entries(), new Predicate<Entry<String, String>>() {
         @Override
         public boolean apply(Entry<String, String> input) {
            return "Transfer-Encoding".equalsIgnoreCase(input.getKey()) && "chunked".equalsIgnoreCase(input.getValue());
         }
      });
      for (Entry<String, String> header : headers.entries()) {
         if (!chunked && CONTENT_LENGTH.equalsIgnoreCase(header.getKey())) {
            setContentLength(new Long(header.getValue()));
         } else if ("Content-MD5".equalsIgnoreCase(header.getKey())) {
            setContentMD5(CryptoStreams.base64(header.getValue()));
         } else if (CONTENT_TYPE.equalsIgnoreCase(header.getKey())) {
            setContentType(header.getValue());
         } else if ("Content-Disposition".equalsIgnoreCase(header.getKey())) {
            setContentDisposition(header.getValue());
         } else if ("Content-Encoding".equalsIgnoreCase(header.getKey())) {
            setContentEncoding(header.getValue());
         } else if ("Content-Language".equalsIgnoreCase(header.getKey())) {
            setContentLanguage(header.getValue());
         }
      }
   }

   /** The serialVersionUID */
   private static final long serialVersionUID = 4572381435863125873L;

   protected String contentType = "application/unknown";
   protected Long contentLength;
   protected byte[] contentMD5;
   protected String contentDisposition;
   protected String contentLanguage;
   protected String contentEncoding;

   public BaseMutableContentMetadata() {
      super();
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
   public void setContentLength(@Nullable Long contentLength) {
      this.contentLength = contentLength;
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
   public void setContentMD5(byte[] md5) {
      if (md5 != null) {
         byte[] retval = new byte[md5.length];
         System.arraycopy(md5, 0, retval, 0, md5.length);
         this.contentMD5 = md5;
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
   public void setContentType(@Nullable String contentType) {
      this.contentType = contentType;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setContentDisposition(@Nullable String contentDisposition) {
      this.contentDisposition = contentDisposition;
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
   public void setContentLanguage(@Nullable String contentLanguage) {
      this.contentLanguage = contentLanguage;
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
   public void setContentEncoding(@Nullable String contentEncoding) {
      this.contentEncoding = contentEncoding;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getContentEncoding() {
      return this.contentEncoding;
   }

   @Override
   public String toString() {
      return "[contentType=" + contentType + ", contentLength=" + contentLength + ", contentDisposition="
            + contentDisposition + ", contentEncoding=" + contentEncoding + ", contentLanguage=" + contentLanguage
            + ", contentMD5=" + Arrays.toString(contentMD5) + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((contentDisposition == null) ? 0 : contentDisposition.hashCode());
      result = prime * result + ((contentEncoding == null) ? 0 : contentEncoding.hashCode());
      result = prime * result + ((contentLanguage == null) ? 0 : contentLanguage.hashCode());
      result = prime * result + ((contentLength == null) ? 0 : contentLength.hashCode());
      result = prime * result + Arrays.hashCode(contentMD5);
      result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      BaseMutableContentMetadata other = (BaseMutableContentMetadata) obj;
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
      return true;
   }

}