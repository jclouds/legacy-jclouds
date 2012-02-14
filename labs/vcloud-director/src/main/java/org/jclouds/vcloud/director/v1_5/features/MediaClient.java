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

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ReferenceType;
import org.jclouds.vcloud.director.v1_5.domain.Task;

/**
 * Provides synchronous access to Media.
 * <p/>
 * 
 * @see MediaAsyncClient
 * @see <a href= "http://support.theenterprisecloud.com/kb/default.asp?id=984&Lang=1&SID=" />
 * @author danikov
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface MediaClient {

   /**
    * Retrieves a media.
    * 
    * @return the media or null if not found
    */
   Media getMedia(ReferenceType<?> mediaRef);
   
   /**
    * Updates the name/description of a media.
    * 
    * @return a task. This operation is asynchronous and the user should monitor the returned 
    * task status in order to check when it is completed.
    */
   Task updateMedia(ReferenceType<?> mediaRef, Media media);
   
   /**
    * Deletes a media.
    */
   Task deleteMedia(ReferenceType<?> mediaRef);
   
   /**
    * Retrieves an owner.
    * 
    * @return the owner or null if not found
    */
   Owner getOwner(ReferenceType<?> mediaRef);
   
   /**
    * Retrieves an list of the media's metadata
    * 
    * @return a list of metadata
    */
   Metadata getMetadata(ReferenceType<?> mediaRef);
   /**
    * Merges the metadata for a media with the information provided.
    * 
    * @return a task. This operation is asynchronous and the user should monitor the returned 
    * task status in order to check when it is completed.
    */
   Task mergeMetadata(ReferenceType<?> mediaRef, Metadata metadata);

   /**
    * Retrieves a metadata entry
    * 
    * @return the metadata entry, or null if not found
    */
   MetadataEntry getMetadataEntry(ReferenceType<?> mediaRef, String key);
   
   /**
    * Sets the metadata for the particular key for the media to the value provided. 
    * Note: this will replace any existing metadata information
    * 
    * @return a task. This operation is asynchronous and the user should monitor the returned 
    * task status in order to check when it is completed.
    */
   Task setMetadata(ReferenceType<?> mediaRef, String key, MetadataValue metadataEntry);
   
   /**
    * Deletes a metadata entry.
    * 
    * @return a task. This operation is asynchronous and the user should monitor the returned 
    * task status in order to check when it is completed.
    */
   Task deleteMetadataEntry(ReferenceType<?> mediaRef, String key);
   

}
