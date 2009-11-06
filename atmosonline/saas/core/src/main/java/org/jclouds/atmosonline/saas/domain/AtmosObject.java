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
package org.jclouds.atmosonline.saas.domain;

import java.io.IOException;

import com.google.common.collect.Multimap;
import com.google.inject.internal.Nullable;

/**
 * Amazon Atmos is designed to store objects. Objects are stored in buckets and consist of a
 * {@link ObjectMetadataAtmosObject#getData() value}, a {@link ObjectMetadata#getKey key},
 * {@link ObjectMetadata#getUserMetadata() metadata}, and an access control policy.
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonAtmos/2006-03-01/index.html?UsingObjects.html"
 *      />
 */
public interface AtmosObject extends Comparable<AtmosObject> {
   public interface Factory {
      AtmosObject create(@Nullable MutableContentMetadata contentMetadata);

      AtmosObject create(SystemMetadata systemMetadata, UserMetadata userMetadata);

      AtmosObject create(MutableContentMetadata contentMetadata, SystemMetadata systemMetadata,
               UserMetadata userMetadata);

   }

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
    * Sets entity for the request or the content from the response. If size isn't set, this will
    * attempt to discover it.
    * 
    * @param data
    *           typically InputStream for downloads, or File, byte [], String, or InputStream for
    *           uploads.
    */
   void setData(Object data);

   /**
    * @return InputStream, if downloading, or whatever was set during {@link #setData(Object)}
    */
   Object getData();

   MutableContentMetadata getContentMetadata();

   /**
    * @return System and User metadata relevant to this object.
    */
   SystemMetadata getSystemMetadata();

   UserMetadata getUserMetadata();

   Multimap<String, String> getAllHeaders();

   void setAllHeaders(Multimap<String, String> allHeaders);
}