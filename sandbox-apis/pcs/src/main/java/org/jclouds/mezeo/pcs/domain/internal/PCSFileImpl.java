/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.mezeo.pcs.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.http.internal.PayloadEnclosingImpl;
import org.jclouds.mezeo.pcs.domain.MutableFileInfo;
import org.jclouds.mezeo.pcs.domain.PCSFile;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * Default Implementation of {@link PCSFile}.
 * 
 * @author Adrian Cole
 */
public class PCSFileImpl extends PayloadEnclosingImpl implements PCSFile, Comparable<PCSFile> {

   private final MutableFileInfo metadata;
   private Multimap<String, String> allHeaders = LinkedHashMultimap.create();

   @Inject
   public PCSFileImpl(MutableFileInfo metadata) {
      super();// no MD5 support
      this.metadata = metadata;
   }

   /**
    * {@inheritDoc}
    */
   public MutableFileInfo getMetadata() {
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
   public int compareTo(PCSFile o) {
      if (getMetadata().getName() == null)
         return -1;
      return (this == o) ? 0 : getMetadata().getName().compareTo(o.getMetadata().getName());
   }

}
