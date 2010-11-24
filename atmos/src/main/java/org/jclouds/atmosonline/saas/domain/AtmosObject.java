/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.atmosonline.saas.domain;

import javax.annotation.Nullable;

import org.jclouds.io.PayloadEnclosing;

import com.google.common.collect.Multimap;

/**
 * Amazon Atmos is designed to store objects. Objects are stored in buckets and consist of a
 * {@link ObjectMetadataAtmosObject#getInput() value}, a {@link ObjectMetadata#getKey key},
 * {@link ObjectMetadata#getUserMetadata() metadata}, and an access control policy.
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonAtmos/2006-03-01/index.html?UsingObjects.html"
 *      />
 */
public interface AtmosObject extends PayloadEnclosing, Comparable<AtmosObject> {
   public interface Factory {
      AtmosObject create(@Nullable MutableContentMetadata contentMetadata);

      AtmosObject create(SystemMetadata systemMetadata, UserMetadata userMetadata);

      AtmosObject create(MutableContentMetadata contentMetadata, SystemMetadata systemMetadata,
               UserMetadata userMetadata);

   }

   MutableContentMetadata getContentMetadata();

   /**
    * @return System and User metadata relevant to this object.
    */
   SystemMetadata getSystemMetadata();

   UserMetadata getUserMetadata();

   Multimap<String, String> getAllHeaders();

   void setAllHeaders(Multimap<String, String> allHeaders);
}