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
package org.jclouds.azure.storage.blob.domain;

import java.net.URI;
import java.util.Map;

import org.jclouds.azure.storage.blob.domain.internal.MutableBlobPropertiesImpl;
import org.joda.time.DateTime;

import com.google.inject.ImplementedBy;

/**
 * 
 * @author Adrian Cole
 * 
 */
@ImplementedBy(MutableBlobPropertiesImpl.class)
public interface MutableBlobProperties extends BlobProperties {
   /**
    * @see ListableContainerProperties#setUrl
    */
   void setUrl(URI url);

   /**
    * @see ListableContainerProperties#setName
    */
   void setName(String name);

   /**
    * @see ListableContainerProperties#setLastModified
    */
   void setLastModified(DateTime lastModified);

   /**
    * @see ListableContainerProperties#setETag
    */
   void setETag(String eTag);

   /**
    * @see ListableContainerProperties#setSize
    */
   void setSize(long size);

   /**
    * @see ListableContainerProperties#setContentMD5
    */
   void setContentMD5(byte[] md5);

   /**
    * @see ListableContainerProperties#setContentType
    */
   void setContentType(String contentType);

   /**
    * @see ListableContainerProperties#setContentEncoding
    */
   void setContentEncoding(String contentEncoding);

   /**
    * @see ListableContainerProperties#setContentLanguage
    */
   void setContentLanguage(String contentLanguage);

   /**
    * @see ListableContainerProperties#setMetadata
    */
   void setMetadata(Map<String, String> metadata);

}
