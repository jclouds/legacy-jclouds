/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.blobstore.reference;

/**
 * Configuration properties and constants used in BlobStore connections.
 * 
 * @author Adrian Cole
 */
public interface BlobStoreConstants {

   /**
    * longest time a single synchronous operation can take before throwing an exception.
    */
   public static final String PROPERTY_BLOBSTORE_TIMEOUT = "jclouds.blobstore.timeout";
   /**
    * time to pause before retrying a transient failure
    */
   public static final String PROPERTY_BLOBSTORE_RETRY = "jclouds.blobstore.retry";
   /**
    * Any header starting with this prefix is considered user metadata. It will be stored with the
    * object and returned when you retrieve the object/
    */
   public static final String PROPERTY_USER_METADATA_PREFIX = "jclouds.blobstore.metaprefix";
}
