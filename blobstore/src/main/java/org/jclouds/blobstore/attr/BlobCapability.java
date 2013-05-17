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
package org.jclouds.blobstore.attr;

/**
 * Represents the capabilities of a BlobStore
 * 
 * @author Adrian Cole
 * 
 */
public enum BlobCapability {

   /**
    * supports
    */
   CONDITIONAL_MATCH,
   
   /**
    * receive pieces of a blob via Content-Range header
    */
   CONDITIONAL_DATE,
   
   /**
    * receive pieces of a blob via Content-Range header
    */
   GET_RANGE,

   /**
    * replace pieces of a blob via Content-Range header
    */
   PUT_RANGE,

   /**
    * Enable "chunked"/"streamed" data, where the size needn't be known in advance.
    */
   CHUNKED_ENCODING,

   /**
    * blobs can have key-value pairs associated with them
    */
   METADATA,

   /**
    * blobs have an etag associated with them
    */
   ETAG,

   /**
    * blobs have a system generated ID associated with them
    */
   ID,

   /**
    * blobstore stores Content-MD5 header
    */
   MD5,

   /**
    * etag is the same value as the MD5 of the object
    */
   ETAG_EQUALS_MD5,

   /**
    * blobs will have last modified date associated with them
    */
   LAST_MODIFIED,

   /**
    * timestamps are precise in milliseconds (as opposed to seconds)
    */
   MILLISECOND_PRECISION,

   /**
    * blob size in bytes is exposed by service listing
    */
   SIZE,


   /**
    * possible to expose blobs to anonymous access
    */
   PUBLIC

}
