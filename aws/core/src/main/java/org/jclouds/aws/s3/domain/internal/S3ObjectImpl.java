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
package org.jclouds.aws.s3.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.MutableObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.internal.BasePayloadEnclosingImpl;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * Default Implementation of {@link S3Object}.
 * 
 * @author Adrian Cole
 */
public class S3ObjectImpl extends BasePayloadEnclosingImpl implements S3Object,
         Comparable<S3Object> {
   private final MutableObjectMetadata metadata;
   private Multimap<String, String> allHeaders = LinkedHashMultimap.create();
   private AccessControlList accessControlList;

   @Inject
   public S3ObjectImpl(EncryptionService encryptionService, MutableObjectMetadata metadata) {
      super(encryptionService);
      this.metadata = metadata;
   }

   @Override
   protected void setContentMD5(byte[] md5) {
      getMetadata().setContentMD5(md5);
   }

   /**
    * {@inheritDoc}
    */
   public void setAccessControlList(AccessControlList acl) {
      this.accessControlList = acl;
   }

   /**
    * {@inheritDoc}
    */
   public AccessControlList getAccessControlList() {
      return this.accessControlList;
   }

   /**
    * {@inheritDoc}
    */
   public MutableObjectMetadata getMetadata() {
      return metadata;
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
   public int compareTo(S3Object o) {
      if (getMetadata().getKey() == null)
         return -1;
      return (this == o) ? 0 : getMetadata().getKey().compareTo(o.getMetadata().getKey());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((accessControlList == null) ? 0 : accessControlList.hashCode());
      result = prime * result + ((allHeaders == null) ? 0 : allHeaders.hashCode());
      result = prime * result + ((contentLength == null) ? 0 : contentLength.hashCode());
      result = prime * result + ((payload == null) ? 0 : payload.hashCode());
      result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
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
      S3ObjectImpl other = (S3ObjectImpl) obj;
      if (accessControlList == null) {
         if (other.accessControlList != null)
            return false;
      } else if (!accessControlList.equals(other.accessControlList))
         return false;
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
      if (payload == null) {
         if (other.payload != null)
            return false;
      } else if (!payload.equals(other.payload))
         return false;
      if (metadata == null) {
         if (other.metadata != null)
            return false;
      } else if (!metadata.equals(other.metadata))
         return false;
      return true;
   }

}
