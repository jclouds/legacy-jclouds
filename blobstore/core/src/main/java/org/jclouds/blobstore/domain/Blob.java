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
package org.jclouds.blobstore.domain;

import java.awt.Container;
import java.io.IOException;

import org.jclouds.blobstore.internal.BlobImpl;

import com.google.inject.ImplementedBy;

/**
 * Value type for an HTTP Blob service. Blobs are stored in {@link Container containers} and consist
 * of a {@link org.jclouds.blobstore.domain.Value#getData() value}, a {@link Blob#getKey key and
 * 
 * @link Blob.Metadata#getUserMetadata() metadata}
 * 
 * @author Adrian Cole
 */
@ImplementedBy(BlobImpl.class)
public interface Blob<M extends BlobMetadata> {

   /**
    * @see BlobMetadata#getName()
    */
   String getName();

   /**
    * Sets entity for the request or the content from the response. If size isn't set, this will
    * attempt to discover it.
    * 
    * @param data
    *           typically InputStream for downloads, or File, byte [], String, or InputStream for
    *           uploads.
    */
   void setData(Object data);

   /**
    * generate an MD5 Hash for the current data.
    * <p/>
    * <h2>Note</h2>
    * <p/>
    * If this is an InputStream, it will be converted to a byte array first.
    * 
    * @throws IOException
    *            if there is a problem generating the hash.
    */
   void generateMD5() throws IOException;

   /**
    * @return InputStream, if downloading, or whatever was set during {@link #setData(Object)}
    */
   Object getData();

   /**
    * @return System and User metadata relevant to this object.
    */
   M getMetadata();

   void setContentLength(long contentLength);

   /**
    * Returns the total size of the downloaded object, or the chunk that's available.
    * <p/>
    * Chunking is only used when org.jclouds.http.GetOptions is called with options like tail,
    * range, or startAt.
    * 
    * @return the length in bytes that can be be obtained from {@link #getData()}
    * @see org.jclouds.http.HttpHeaders#CONTENT_LENGTH
    * @see GetObjectOptions
    */
   long getContentLength();

   void setContentRange(String contentRange);

   /**
    * If this is not-null, {@link #getContentLength() } will the size of chunk of the Value available
    * via {@link #getData()}
    * 
    * @see org.jclouds.http.HttpHeaders#CONTENT_RANGE
    * @see GetObjectOptions
    */
   String getContentRange();

   void setMetadata(M metadata);

}