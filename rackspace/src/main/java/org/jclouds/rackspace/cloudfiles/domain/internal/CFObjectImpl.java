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
package org.jclouds.rackspace.cloudfiles.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.internal.BasePayloadEnclosingImpl;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * Default Implementation of {@link CFObject}.
 * 
 * @author Adrian Cole
 */
public class CFObjectImpl extends BasePayloadEnclosingImpl implements CFObject,
         Comparable<CFObject> {
   private final MutableObjectInfoWithMetadata info;
   private Multimap<String, String> allHeaders = LinkedHashMultimap.create();

   @Inject
   public CFObjectImpl(EncryptionService encryptionService, MutableObjectInfoWithMetadata info) {
      super(encryptionService);
      this.info = info;
   }

   @Override
   protected void setContentMD5(byte[] md5) {
      getInfo().setHash(md5);
   }

   /**
    * {@inheritDoc}
    */
   public MutableObjectInfoWithMetadata getInfo() {
      return info;
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
   public int compareTo(CFObject o) {
      if (getInfo().getName() == null)
         return -1;
      return (this == o) ? 0 : getInfo().getName().compareTo(o.getInfo().getName());
   }

}
