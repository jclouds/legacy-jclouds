/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.atmosonline.saas.domain;

import javax.ws.rs.core.MediaType;

/**
 * metadata of the object
 * 
 * @author Adrian Cole
 */
public class MutableContentMetadata {

   private String name;
   private Long contentLength;
   private String contentType = MediaType.APPLICATION_OCTET_STREAM;
   private byte[] contentMD5;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   /**
    * Returns the total size of the downloaded object, or the chunk that's available.
    * <p/>
    * Chunking is only used when org.jclouds.http.GetOptions is called with options like tail,
    * range, or startAt.
    * 
    * @return the length in bytes that can be be obtained from {@link #getInput()}
    * @see org.jclouds.http.HttpHeaders#CONTENT_LENGTH
    * @see GetObjectOptions
    */
   public Long getContentLength() {
      return contentLength;
   }

   /**
    * @see #getContentLength
    */
   public void setContentLength(Long contentLength) {
      this.contentLength = contentLength;
   }

   /**
    * 
    * A standard MIME type describing the format of the contents. If none is provided, the default
    * is binary/octet-stream.
    * 
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17"/>
    */
   public String getContentType() {
      return contentType;
   }

   /**
    * @see #getContentType
    */
   public void setContentType(String contentType) {
      this.contentType = contentType;
   }

   /**
    * The 128-bit MD5 digest of the message (without the headers) according to RFC 1864. This header
    * can be used as a message integrity check to verify that the data is the same data that was
    * originally sent. Although it is optional, we recommend using the Content-MD5 mechanism as an
    * end-to-end integrity check.
    * 
    */
   public byte[] getContentMD5() {
      return contentMD5;
   }

   /**
    * @see #getContentMD5
    */
   public void setContentMD5(byte[] contentMD5) {
      this.contentMD5 = contentMD5;
   }

}