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
package org.jclouds.blobstore.reference;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

/**
 * Configuration properties and constants used in BlobStore connections.
 * 
 * @author Adrian Cole
 */
public interface BlobStoreConstants {
   /**
    * <p/>
    * To interoperate with other S3 tools, we also accept the following:
    * <ul>
    * <li>an object named '#{dirpath}_$folder$' or '#{dirpath}/' denoting a directory marker</li>
    * <li>an object with content type set to 'application/directory' denoting a directory marker</li>
    * <li>if there exists any objects with the prefix "#{dirpath}/", then the directory is said to
    * exist</li>
    * <li>if both a file with the name of a directory and a marker for that directory exists, then
    * the *file masks the directory*, and the directory is never returned.</li>
    * </ul>
    */
   public static final String DIRECTORY_SUFFIX_ROOT = "/";
   public static final String DIRECTORY_SUFFIX_FOLDER = "_$folder$";
   public static final Collection<String> DIRECTORY_SUFFIXES =
         ImmutableList.of(DIRECTORY_SUFFIX_FOLDER, DIRECTORY_SUFFIX_ROOT);

   /**
    * Key-value implementations of BlobStore, such as S3, do not have directories. We use an empty
    * object '#{dirpath}_$folder$' with content type set to 'application/directory'.
    */
   public static final String PROPERTY_BLOBSTORE_DIRECTORY_SUFFIX = "jclouds.blobstore.directorysuffix";

   /**
    * Any header starting with this prefix is considered user metadata. It will be stored with the
    * object and returned when you retrieve the object/
    */
   public static final String PROPERTY_USER_METADATA_PREFIX = "jclouds.blobstore.metaprefix";

   public static final String BLOBSTORE_LOGGER = "jclouds.blobstore";

}
