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
package org.jclouds.atmosonline.saas.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.InputStream;

import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.domain.MutableContentMetadata;
import org.jclouds.atmosonline.saas.domain.SystemMetadata;
import org.jclouds.atmosonline.saas.domain.UserMetadata;
import org.jclouds.blobstore.domain.MD5InputStreamResult;
import org.jclouds.blobstore.functions.CalculateSize;
import org.jclouds.blobstore.functions.GenerateMD5;
import org.jclouds.blobstore.functions.GenerateMD5Result;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Default Implementation of {@link AtmosObject}.
 * 
 * @author Adrian Cole
 */
public class AtmosObjectImpl implements AtmosObject, Comparable<AtmosObject> {
   private final UserMetadata userMetadata;
   private final GenerateMD5Result generateMD5Result;
   private final GenerateMD5 generateMD5;
   private final CalculateSize calculateSize;
   private final MutableContentMetadata contentMetadata;
   private final SystemMetadata systemMetadata;
   private Object data;

   private Multimap<String, String> allHeaders = HashMultimap.create();

   public AtmosObjectImpl(GenerateMD5Result generateMD5Result, GenerateMD5 generateMD5,
            CalculateSize calculateSize, MutableContentMetadata contentMetadata) {
      this(generateMD5Result, generateMD5, calculateSize, contentMetadata, null, new UserMetadata());
   }

   public AtmosObjectImpl(GenerateMD5Result generateMD5Result, GenerateMD5 generateMD5,
            CalculateSize calculateSize, MutableContentMetadata contentMetadata,
            SystemMetadata systemMetadata, UserMetadata userMetadata) {
      this.generateMD5Result = generateMD5Result;
      this.generateMD5 = generateMD5;
      this.calculateSize = calculateSize;
      this.contentMetadata = contentMetadata;
      this.systemMetadata = systemMetadata;
      this.userMetadata = userMetadata;
   }

   /**
    * {@inheritDoc}
    */
   public void generateMD5() {
      checkState(data != null, "data");
      if (data instanceof InputStream) {
         MD5InputStreamResult result = generateMD5Result.apply((InputStream) data);
         getContentMetadata().setContentMD5(result.md5);
         getContentMetadata().setContentLength(result.length);
         setData(result.data);
      } else {
         getContentMetadata().setContentMD5(generateMD5.apply(data));
      }
   }

   /**
    * {@inheritDoc}
    */
   public Object getData() {
      return data;
   }

   /**
    * {@inheritDoc}
    */
   public void setData(Object data) {
      this.data = checkNotNull(data, "data");
      if (getContentMetadata().getContentLength() == null) {
         Long size = calculateSize.apply(data);
         if (size != null)
            getContentMetadata().setContentLength(size);
      }
   }

   /**
    * {@inheritDoc}
    */
   public MutableContentMetadata getContentMetadata() {
      return contentMetadata;
   }

   /**
    * {@inheritDoc}
    */
   public Multimap<String, String> getAllHeaders() {
      return allHeaders;
   }

   /**
    * {@inheritDoc}
    */
   public void setAllHeaders(Multimap<String, String> allHeaders) {
      this.allHeaders = checkNotNull(allHeaders, "allHeaders");
   }

   /**
    * {@inheritDoc}
    */
   public int compareTo(AtmosObject o) {
      String name = getContentMetadata().getName() != null ? getContentMetadata().getName()
               : getSystemMetadata().getObjectName();
      if (name == null)
         return -1;
      String otherName = o.getContentMetadata().getName() != null ? o.getContentMetadata()
               .getName() : o.getSystemMetadata().getObjectName();
      return (this == o) ? 0 : name.compareTo(otherName);
   }

   public SystemMetadata getSystemMetadata() {
      return systemMetadata;
   }

   public UserMetadata getUserMetadata() {
      return userMetadata;
   }

}
