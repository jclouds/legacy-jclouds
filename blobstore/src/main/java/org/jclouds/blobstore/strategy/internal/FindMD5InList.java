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
package org.jclouds.blobstore.strategy.internal;

import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.ObjectMD5;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.blobstore.strategy.ListBlobsInContainer;

import com.google.common.base.Throwables;

/**
 * Searches Content-MD5 tag for the value associated with the value
 * 
 * @author Adrian Cole
 */
@Singleton
public class FindMD5InList implements ContainsValueInListStrategy {

   protected final ObjectMD5 objectMD5;
   protected final ListBlobsInContainer getAllBlobMetadata;

   @Inject
   private FindMD5InList(ObjectMD5 objectMD5, ListBlobsInContainer getAllBlobMetadata) {
      this.objectMD5 = objectMD5;
      this.getAllBlobMetadata = getAllBlobMetadata;
   }

   public boolean execute(String containerName, Object value, ListContainerOptions options) {
      try {
         byte[] toSearch = objectMD5.apply(value);
         for (BlobMetadata metadata : getAllBlobMetadata.execute(containerName, options)) {
            if (Arrays.equals(toSearch, metadata.getContentMetadata().getContentMD5()))
               return true;
         }
         return false;
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, BlobRuntimeException.class);
         throw new BlobRuntimeException(String.format(
                  "Error searching for ETAG of value: [%2$s] in container:%1$s", containerName,
                  value), e);
      }
   }

}
