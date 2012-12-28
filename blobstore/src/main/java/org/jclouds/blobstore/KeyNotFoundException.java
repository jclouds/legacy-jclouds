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
package org.jclouds.blobstore;

import org.jclouds.rest.ResourceNotFoundException;

/**
 * Thrown when a blob cannot be located in the container.
 * 
 * @author Adrian Cole
 */
public class KeyNotFoundException extends ResourceNotFoundException {

   private String container;
   private String key;

   public KeyNotFoundException() {
      super();
   }

   public KeyNotFoundException(String container, String key, String message) {
      super(String.format("%s not found in container %s: %s", key, container, message));
      this.container = container;
      this.key = key;
   }

   public KeyNotFoundException(Throwable from) {
      super(from);
   }

   public String getContainer() {
      return container;
   }

   public String getKey() {
      return key;
   }

}
