/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.vcloud.director.v1_5.features;

import java.util.Map;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.Task;

/**
 * Provides synchronous access to {@link Metadata}.
 * 
 * @see MetadataAsyncApi
 * @author danikov, Adrian Cole
 */
public interface MetadataApi {
   /**
    * Retrieves an list of metadata
    * 
    * @return a list of metadata
    */
   Metadata get();

   /**
    * Retrieves a metadata value
    * 
    * @return the metadata value, or null if not found
    */
   String get(String key);

   /**
    * Merges the metadata for a media with the information provided.
    * 
    * @return a task. This operation is asynchronous and the user should monitor the returned task status in order to
    *         check when it is completed.
    */
   Task putAll(Map<String, String> metadata);

   /**
    * Sets the metadata for the particular key for the media to the value provided. Note: this will replace any existing
    * metadata information
    * 
    * @return a task. This operation is asynchronous and the user should monitor the returned task status in order to
    *         check when it is completed.
    */
   Task put(String key, String value);

   /**
    * Deletes a metadata entry.
    * 
    * @return a task. This operation is asynchronous and the user should monitor the returned task status in order to
    *         check when it is completed.
    */
   Task remove(String key);

}
