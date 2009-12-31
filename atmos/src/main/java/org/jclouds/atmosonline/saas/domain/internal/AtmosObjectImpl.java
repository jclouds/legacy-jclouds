/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.atmosonline.saas.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.domain.MutableContentMetadata;
import org.jclouds.atmosonline.saas.domain.SystemMetadata;
import org.jclouds.atmosonline.saas.domain.UserMetadata;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.internal.BasePayloadEnclosingImpl;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * Default Implementation of {@link AtmosObject}.
 * 
 * @author Adrian Cole
 */
public class AtmosObjectImpl extends BasePayloadEnclosingImpl implements AtmosObject,
         Comparable<AtmosObject> {
   private final UserMetadata userMetadata;
   private final MutableContentMetadata contentMetadata;
   private final SystemMetadata systemMetadata;

   private Multimap<String, String> allHeaders = LinkedHashMultimap.create();

   public AtmosObjectImpl(EncryptionService encryptionService,
            MutableContentMetadata contentMetadata) {
      this(encryptionService, contentMetadata, null, new UserMetadata());
   }

   public AtmosObjectImpl(EncryptionService encryptionService,
            MutableContentMetadata contentMetadata, SystemMetadata systemMetadata,
            UserMetadata userMetadata) {
      super(encryptionService);
      this.contentMetadata = contentMetadata;
      this.systemMetadata = systemMetadata;
      this.userMetadata = userMetadata;
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

   @Override
   public Long getContentLength() {
      return getContentMetadata().getContentLength();
   }

   @Override
   public void setContentLength(long contentLength) {
      getContentMetadata().setContentLength(contentLength);
   }

   @Override
   protected void setContentMD5(byte[] md5) {
      getContentMetadata().setContentMD5(md5);
   }

}
