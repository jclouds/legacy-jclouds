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
package org.jclouds.hpcloud.object.storage.options;

import java.util.Map.Entry;

import org.jclouds.hpcloud.object.storage.reference.HPCloudObjectStorageHeaders;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.Multimap;

/**
 * Contains options supported in the REST API for the Create Container operation.
 * 
 * This specifically enabes the Swift ACL for public reads using the 'X-Container-Read'
 * header. This should be refactored into the Swift API.
 * 
 * @author Jeremy Daggett
 */
public class CreateContainerOptions extends BaseHttpRequestOptions {
   public static final CreateContainerOptions NONE = new CreateContainerOptions();

   /**
    * A name-value pair to associate with the container as metadata.
    */
   public CreateContainerOptions withMetadata(Multimap<String, String> metadata) {
      for (Entry<String, String> entry : metadata.entries()) {
    	  System.err.println(entry.getValue());
         if (entry.getKey().startsWith(HPCloudObjectStorageHeaders.USER_METADATA_PREFIX))
            this.headers.put(entry.getKey(), entry.getValue());
         else
            this.headers.put(HPCloudObjectStorageHeaders.USER_METADATA_PREFIX + entry.getKey(), 
            		    entry.getValue());
      }
      return this;
   }
   

   /**
    * Indicates whether a container may be accessed publicly
    */
   public CreateContainerOptions withPublicAccess() {
      this.headers.put(HPCloudObjectStorageHeaders.CONTAINER_READ, ".r:*,.rlistings");
      return this;
   }

   public static class Builder {

      /**
       * @see CreateContainerOptions#withPublicAccess
       */
      public static CreateContainerOptions withPublicAccess() {
         CreateContainerOptions options = new CreateContainerOptions();
         return options.withPublicAccess();
      }

      /**
       * @see CreateContainerOptions#withMetadata(Multimap<String, String>)
       */ 
      public static CreateContainerOptions withMetadata(Multimap<String, String> metadata) {
         CreateContainerOptions options = new CreateContainerOptions();
         return (CreateContainerOptions) options.withMetadata(metadata);
      }
	  
   }
}
