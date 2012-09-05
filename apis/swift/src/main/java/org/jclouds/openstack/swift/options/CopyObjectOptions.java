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

import org.jclouds.http.options.BaseHttpRequestOptions;
import org.jclouds.openstack.swift.reference.SwiftHeaders;

/**
 * Contains the source where the object should be copied from
 * 
 * @author Everett Toews
 */
public class CopyObjectOptions extends BaseHttpRequestOptions {
   public static final CopyObjectOptions NONE = new CopyObjectOptions();

   public CopyObjectOptions fromSource(String sourceContainer, String sourceName) {
      StringBuilder sourcePathBuilder = new StringBuilder();
      sourcePathBuilder.append("/").append(sourceContainer).append("/").append(sourceName);
      this.headers.put(SwiftHeaders.OBJECT_COPY_FROM, sourcePathBuilder.toString());
      
      return this;
   }
   
   public static class Builder {

      public static CopyObjectOptions fromSource(String sourceContainer, String sourceName) {
         CopyObjectOptions options = new CopyObjectOptions();
         return (CopyObjectOptions) options.fromSource(sourceContainer, sourceName);
      }	  
   }
}
