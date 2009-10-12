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
package org.jclouds.azure.storage.blob.domain;

import java.net.URI;

import javax.inject.Inject;

import org.joda.time.DateTime;

import com.google.inject.internal.Nullable;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class BlobMetadata extends org.jclouds.blobstore.domain.BlobMetadata {
   /** The serialVersionUID */
   private static final long serialVersionUID = 1L;
   private URI url;
   private String contentLanguage;
   protected String dataEncoding;

   @Inject
   public BlobMetadata() {
      super();
   }

   public BlobMetadata(String key) {
      super(key);
   }

   public BlobMetadata(String currentName, URI currentUrl, DateTime currentLastModified,
            String currentETag, long currentSize, String currentContentType,
            @Nullable byte[] contentMD5, @Nullable String currentContentEncoding,
            @Nullable String currentContentLanguage) {
      this(currentName);
      setUrl(currentUrl);
      setLastModified(currentLastModified);
      setETag(currentETag);
      setSize(currentSize);
      setContentType(currentContentType);
      setContentMD5(contentMD5);
      setContentEncoding(currentContentEncoding);
      setContentLanguage(currentContentLanguage);
   }

   public void setUrl(URI url) {
      this.url = url;
   }

   public URI getUrl() {
      return url;
   }

   public void setContentLanguage(String contentLanguage) {
      this.contentLanguage = contentLanguage;
   }

   public String getContentLanguage() {
      return contentLanguage;
   }

   public void setContentEncoding(String dataEncoding) {
      this.dataEncoding = dataEncoding;
   }

   /**
    * Specifies what content encodings have been applied to the object and thus what decoding
    * mechanisms must be applied in order to obtain the media-type referenced by the Content-Type
    * header field.
    * 
    * @see <a href= "http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.11" />
    */
   public String getContentEncoding() {
      return dataEncoding;
   }
}
