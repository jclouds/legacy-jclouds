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
package org.jclouds.azureblob.domain;

/**
 * Indicates whether data in the container may be accessed publicly and the level of access.
 * 
 * @author Adrian Cole
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd179469.aspx"/>
 */
public enum PublicAccess {
   /**
    * Indicates full public read access for container and blob data. Clients can enumerate blobs
    * within the container via anonymous request, but cannot enumerate containers within the storage
    * account.
    */
   CONTAINER,
   /**
    * Indicates public read access for blobs. Blob data within this container can be read via
    * anonymous request, but container data is not available. Clients cannot enumerate blobs within
    * the container via anonymous request.
    */
   BLOB,
   /**
    * the container is private to the account owner.
    */
   PRIVATE;

}
