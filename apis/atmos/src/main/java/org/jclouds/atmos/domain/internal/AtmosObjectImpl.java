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
package org.jclouds.atmos.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.atmos.domain.MutableContentMetadata;
import org.jclouds.atmos.domain.SystemMetadata;
import org.jclouds.atmos.domain.UserMetadata;
import org.jclouds.http.internal.PayloadEnclosingImpl;
import org.jclouds.io.Payload;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * Default Implementation of {@link AtmosObject}.
 * 
 * @author Adrian Cole
 */
public class AtmosObjectImpl extends PayloadEnclosingImpl implements AtmosObject, Comparable<AtmosObject> {
   @Singleton
   public static class AtmosObjectFactory implements AtmosObject.Factory {

      @Inject
      Provider<MutableContentMetadata> metadataProvider;

      public AtmosObject create(MutableContentMetadata contentMetadata) {
         return new AtmosObjectImpl(contentMetadata != null ? contentMetadata : metadataProvider.get());
      }

      public AtmosObject create(SystemMetadata systemMetadata, UserMetadata userMetadata) {
         return new AtmosObjectImpl(metadataProvider.get(), systemMetadata, userMetadata);
      }

      public AtmosObject create(MutableContentMetadata contentMetadata, SystemMetadata systemMetadata,
               UserMetadata userMetadata) {
         return new AtmosObjectImpl(contentMetadata, systemMetadata, userMetadata);
      }
   }

   private final UserMetadata userMetadata;
   private final SystemMetadata systemMetadata;

   public SystemMetadata getSystemMetadata() {
      return systemMetadata;
   }

   public UserMetadata getUserMetadata() {
      return userMetadata;
   }

   private MutableContentMetadata contentMetadata;
   private Multimap<String, String> allHeaders = LinkedHashMultimap.create();

   public AtmosObjectImpl(MutableContentMetadata contentMetadata, SystemMetadata systemMetadata,
            UserMetadata userMetadata) {
      this.contentMetadata = contentMetadata;
      this.systemMetadata = systemMetadata;
      this.userMetadata = userMetadata;
   }

   @Inject
   public AtmosObjectImpl(MutableContentMetadata contentMetadata) {
      this(contentMetadata, null, new UserMetadata());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MutableContentMetadata getContentMetadata() {
      return contentMetadata;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Multimap<String, String> getAllHeaders() {
      return allHeaders;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setAllHeaders(Multimap<String, String> allHeaders) {
      this.allHeaders = checkNotNull(allHeaders, "allHeaders");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int compareTo(AtmosObject o) {
      if (getContentMetadata().getName() == null)
         return -1;
      return (this == o) ? 0 : getContentMetadata().getName().compareTo(o.getContentMetadata().getName());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((contentMetadata == null) ? 0 : contentMetadata.hashCode());
      result = prime * result + ((systemMetadata == null) ? 0 : systemMetadata.hashCode());
      result = prime * result + ((userMetadata == null) ? 0 : userMetadata.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      AtmosObjectImpl other = (AtmosObjectImpl) obj;
      if (contentMetadata == null) {
         if (other.contentMetadata != null)
            return false;
      } else if (!contentMetadata.equals(other.contentMetadata))
         return false;
      if (systemMetadata == null) {
         if (other.systemMetadata != null)
            return false;
      } else if (!systemMetadata.equals(other.systemMetadata))
         return false;
      if (userMetadata == null) {
         if (other.userMetadata != null)
            return false;
      } else if (!userMetadata.equals(other.userMetadata))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[contentMetadata=" + contentMetadata + "]";
   }

   @Override
   public void setPayload(Payload data) {
      this.payload = data;
      this.contentMetadata = new DelegatingMutableContentMetadata(contentMetadata.getUri(), contentMetadata.getName(),
               contentMetadata.getPath(), payload.getContentMetadata());
   }
}
