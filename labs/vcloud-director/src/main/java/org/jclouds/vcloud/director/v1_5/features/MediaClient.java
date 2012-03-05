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
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.URISupplier;

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
   Media getMedia(URISupplier mediaRef);
   
   /**
    * Updates the name/description of a media.
    * 
    * @return a task. This operation is asynchronous and the user should monitor the returned 
    * task status in order to check when it is completed.
    */
   Task updateMedia(URISupplier mediaRef, Media media);
   
   /**
    * Deletes a media.
    */
   Task deleteMedia(URISupplier mediaRef);
   
   /**
    * Retrieves an owner.
    * 
    * @return the owner or null if not found
    */
   Owner getOwner(URISupplier mediaRef);
   
   /**
    * @return synchronous access to {@link Metadata.Writeable} features
    */
   @Delegate
   MetadataClient.Writeable getMetadataClient();

}
