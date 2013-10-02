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
package org.jclouds.blobstore;

/**
 * Thrown when a blob was attempted to be replaced while it already exists
 * 
 * @author Adrian Cole
 */
public class KeyAlreadyExistsException extends RuntimeException {

   private String container;
   private String key;

   public KeyAlreadyExistsException() {
      super();
   }

   public KeyAlreadyExistsException(String container, String key) {
      super(String.format("%s already exists in container %s", key, container));
      this.container = container;
      this.key = key;
   }

   public KeyAlreadyExistsException(Exception from) {
      super(from);
   }

   public String getContainer() {
      return container;
   }

   public String getKey() {
      return key;
   }

}
