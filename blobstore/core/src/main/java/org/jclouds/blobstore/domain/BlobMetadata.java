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

import java.util.Map;

import org.jclouds.blobstore.internal.BlobMetadataImpl;
import org.joda.time.DateTime;

import com.google.common.collect.Multimap;
import com.google.inject.ImplementedBy;

/**
 * System and user Metadata for the {@link Blob}.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(BlobMetadataImpl.class)
public interface BlobMetadata {

   void setName(String key);

   /**
    * The key is the handle that you assign to an object that allows you retrieve it later. A key is
    * a sequence of Unicode characters whose UTF-8 encoding is at most 1024 bytes long. Each object
    * in a bucket must have a unique key.
    * 
    * @see <a href= "http://docs.amazonwebservices.com/AmazonHTTP/2006-03-01/UsingKeys.html" />
    */
   String getName();

   DateTime getLastModified();

   void setLastModified(DateTime lastModified);

   /**
    * The size of the object, in bytes.
    * 
    * @see <a href= "http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.13." />
    */
   long getSize();

   void setSize(long size);

   /**
    * A standard MIME type describing the format of the contents. If none is provided, the default
    * is binary/octet-stream.
    * 
    * @see <a href= "http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.17." />
    */
   String getContentType();

   void setContentType(String dataType);

   void setContentMD5(byte[] contentMD5);

   byte[] getContentMD5();

   void setETag(String eTag);

   /**
    * @return the eTag value stored in the Etag header returned by HTTP.
    */
   String getETag();

   void setUserMetadata(Map<String, String> userMetadata);

   /**
    * Any key-value pairs associated with the object.
    */
   Map<String, String> getUserMetadata();

   Multimap<String, String> getAllHeaders();

}