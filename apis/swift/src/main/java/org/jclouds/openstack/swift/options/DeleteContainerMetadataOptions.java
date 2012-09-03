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
package org.jclouds.openstack.swift.options;

import java.util.List;

import org.jclouds.http.options.BaseHttpRequestOptions;
import org.jclouds.openstack.swift.reference.SwiftHeaders;

/**
 * Contains metadata keys for the metadata to delete from a Container.
 * 
 * @author Everett Toews
 */
public class DeleteContainerMetadataOptions extends BaseHttpRequestOptions {
   public static final DeleteContainerMetadataOptions NONE = new DeleteContainerMetadataOptions();

   /**
    * A name-value pair to associate with the container as metadata.
    */
   public DeleteContainerMetadataOptions deleteMetadata(List<String> metadataKeys) {
      for (String metadataKey : metadataKeys) {
         if (metadataKey.startsWith(SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX)) {
            this.headers.put(metadataKey, "");
      	 } else {
            this.headers.put(SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX + metadataKey, "");
   	     }
      }
      
      return this;
   }
   
   public static class Builder {

      public static DeleteContainerMetadataOptions deleteMetadata(List<String> metadataKeys) {
         DeleteContainerMetadataOptions options = new DeleteContainerMetadataOptions();
         return (DeleteContainerMetadataOptions) options.deleteMetadata(metadataKeys);
      }	  
   }
}
