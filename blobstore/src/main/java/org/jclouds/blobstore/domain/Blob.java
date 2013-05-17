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
package org.jclouds.blobstore.domain;

import org.jclouds.io.PayloadEnclosing;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.Multimap;

/**
 * Value type for an HTTP Blob service. Blobs are stored in containers and consist of a
 * {@link MutableBlobMetadata#getName name}, {@link Payload payload}, and
 * {@link MutableBlobMetadata metadata}.
 * 
 * @author Adrian Cole
 */
public interface Blob extends PayloadEnclosing, Comparable<Blob> {
   /**
    * Allows you to construct blobs without knowing the implementation type
    */
   public interface Factory {
      /**
       * Creates a blob, optionally setting its metadata to a known value. This is useful in making
       * copies of blobs.
       */
      Blob create(@Nullable MutableBlobMetadata metadata);
   }

   /**
    * @return System and User metadata relevant to this object.
    */
   MutableBlobMetadata getMetadata();

   /**
    * @return headers returned from the services
    */
   Multimap<String, String> getAllHeaders();

   /**
    * 
    * @see #getAllHeaders
    */
   void setAllHeaders(Multimap<String, String> allHeaders);

}
