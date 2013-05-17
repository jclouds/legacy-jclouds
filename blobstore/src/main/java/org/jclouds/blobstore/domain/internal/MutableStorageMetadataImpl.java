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
package org.jclouds.blobstore.domain.internal;

import java.util.Date;

import org.jclouds.blobstore.domain.MutableStorageMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.domain.internal.MutableResourceMetadataImpl;

/**
 * Used to construct new resources or modify existing ones.
 * 
 * @author Adrian Cole
 */
public class MutableStorageMetadataImpl extends MutableResourceMetadataImpl<StorageType> implements
         MutableStorageMetadata {

   private String eTag;
   private Date creationDate;
   private Date lastModified;

   public MutableStorageMetadataImpl() {
      super();
   }

   public MutableStorageMetadataImpl(StorageMetadata from) {
      super(from);
      this.eTag = from.getETag();
      this.lastModified = from.getLastModified();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getETag() {
      return eTag;
   }

   @Override
   public Date getCreationDate() {
      return creationDate;
   }

   @Override
   public void setCreationDate(Date creationDate) {
      this.creationDate = creationDate;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Date getLastModified() {
      return lastModified;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setLastModified(Date lastModified) {
      this.lastModified = lastModified;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setETag(String eTag) {
      this.eTag = eTag;
   }

}
