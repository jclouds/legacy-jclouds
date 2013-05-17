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

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import org.jclouds.blobstore.internal.InputStreamMapImpl;
import org.jclouds.blobstore.options.ListContainerOptions;

import com.google.inject.ImplementedBy;

/**
 * Map view of an {@link org.jclouds.aws.s3.domain.S3Bucket}. Provides additional methods for
 * inserting common object types.
 * <p/>
 * <h2>Note</h2> All <code>put</code> operations will invoke
 * {@link org.jclouds.aws.s3.domain.S3Object#generateETag}. By extension,
 * {@link #put(Object, Object)} will result in the InputStream being converted to a byte array. For
 * this reason, do not use {@link #put(Object, Object)} to store files. Use
 * {@link #putFile(String, File)} or {@link S3ObjectMap} instead.
 * 
 * @author Adrian Cole
 * @deprecated will be removed in jclouds 1.7. Please use {@link BlobStore}
 */
@Deprecated
@ImplementedBy(InputStreamMapImpl.class)
public interface InputStreamMap extends ListableMap<String, InputStream> {
   public static interface Factory {
      InputStreamMap create(String containerName, ListContainerOptions options);
   }

   InputStream putString(String key, String value);

   InputStream putFile(String key, File value);

   InputStream putBytes(String key, byte[] value);

   void putAllStrings(Map<? extends String, ? extends String> map);

   void putAllBytes(Map<? extends String, byte[]> map);

   void putAllFiles(Map<? extends String, ? extends File> map);

}
