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
package org.jclouds.azure.storage.blob.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.InputStream;

import javax.inject.Inject;

import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.azure.storage.blob.domain.MutableBlobProperties;
import org.jclouds.blobstore.domain.MD5InputStreamResult;
import org.jclouds.blobstore.functions.CalculateSize;
import org.jclouds.blobstore.functions.GenerateMD5;
import org.jclouds.blobstore.functions.GenerateMD5Result;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Default Implementation of {@link AzureBlob}.
 * 
 * @author Adrian Cole
 */
public class AzureBlobImpl implements AzureBlob, Comparable<AzureBlob> {
   private final GenerateMD5Result generateMD5Result;
   private final GenerateMD5 generateMD5;
   private final CalculateSize calculateSize;
   private final MutableBlobProperties properties;
   private Object data;
   private Multimap<String, String> allHeaders = HashMultimap.create();
   private Long contentLength;

   @Inject
   public AzureBlobImpl(GenerateMD5Result generateMD5Result, GenerateMD5 generateMD5,
            CalculateSize calculateSize, MutableBlobProperties properties) {
      this.generateMD5Result = generateMD5Result;
      this.generateMD5 = generateMD5;
      this.calculateSize = calculateSize;
      this.properties = properties;
   }
   
   /**
    * {@inheritDoc}
    */
   public void generateMD5() {
      checkState(data != null, "data");
      if (data instanceof InputStream) {
         MD5InputStreamResult result = generateMD5Result.apply((InputStream) data);
         getProperties().setContentMD5(result.md5);
         setContentLength(result.length);
         setData(result.data);
      } else {
         getProperties().setContentMD5(generateMD5.apply(data));
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
      if (getContentLength() == null) {
         Long size = calculateSize.apply(data);
         if (size != null)
            this.setContentLength(size);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Long getContentLength() {
      return contentLength;
   }

   /**
    * {@inheritDoc}
    */
   public void setContentLength(long contentLength) {
      this.contentLength = contentLength;
   }

   /**
    * {@inheritDoc}
    */
   public MutableBlobProperties getProperties() {
      return properties;
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
   public int compareTo(AzureBlob o) {
      if (getProperties().getName() == null)
         return -1;
      return (this == o) ? 0 : getProperties().getName().compareTo(o.getProperties().getName());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((allHeaders == null) ? 0 : allHeaders.hashCode());
      result = prime * result + ((contentLength == null) ? 0 : contentLength.hashCode());
      result = prime * result + ((data == null) ? 0 : data.hashCode());
      result = prime * result + ((properties == null) ? 0 : properties.hashCode());
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
      AzureBlobImpl other = (AzureBlobImpl) obj;
      if (allHeaders == null) {
         if (other.allHeaders != null)
            return false;
      } else if (!allHeaders.equals(other.allHeaders))
         return false;
      if (contentLength == null) {
         if (other.contentLength != null)
            return false;
      } else if (!contentLength.equals(other.contentLength))
         return false;
      if (data == null) {
         if (other.data != null)
            return false;
      } else if (!data.equals(other.data))
         return false;
      if (properties == null) {
         if (other.properties != null)
            return false;
      } else if (!properties.equals(other.properties))
         return false;
      return true;
   }

}
