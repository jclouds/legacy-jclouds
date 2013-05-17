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
package org.jclouds.atmos.domain;

import org.jclouds.atmos.domain.internal.AtmosObjectImpl.AtmosObjectFactory;
import org.jclouds.io.PayloadEnclosing;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.Multimap;
import com.google.inject.ImplementedBy;

/**
 * 
 * @author Adrian Cole
 */
public interface AtmosObject extends PayloadEnclosing, Comparable<AtmosObject> {
   @ImplementedBy(AtmosObjectFactory.class)
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
