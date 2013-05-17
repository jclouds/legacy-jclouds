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
package org.jclouds.openstack.swift;

import org.jclouds.rest.ResourceNotFoundException;

/**
 * Thrown when an object cannot be copied.
 * 
 * @author Everett Toews
 */
public class CopyObjectException extends ResourceNotFoundException {

   private String sourcePath;
   private String destinationPath;

   public CopyObjectException() {
      super();
   }

   public CopyObjectException(String sourcePath, String destinationPath, String message) {
      super(String.format("Either the source path %s or the destination path %s was not found. " +
      		"(message: %s)", sourcePath, destinationPath, message));
      this.sourcePath = sourcePath;
      this.destinationPath = destinationPath;
   }

   public CopyObjectException(Exception from) {
      super(from);
   }

   public String getSourcePath() {
      return sourcePath;
   }

   public String getDestinationPath() {
      return destinationPath;
   }

}
