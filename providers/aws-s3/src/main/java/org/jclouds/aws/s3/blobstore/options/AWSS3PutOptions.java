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
package org.jclouds.aws.s3.blobstore.options;

import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.s3.domain.ObjectMetadata;

/**
 * Contains AWS-S3 specific options supported in the put blob operation
 *
 * @author Andrei Savu
 */
public class AWSS3PutOptions extends PutOptions {

   public static class Builder {

      /**
       * @see AWSS3PutOptions#multipart()
       */
      public static AWSS3PutOptions multipart() {
         AWSS3PutOptions options = new AWSS3PutOptions();
         return (AWSS3PutOptions) options.multipart();
      }

      /**
       * @see AWSS3PutOptions#storageClass
       */
      public static AWSS3PutOptions storageClass(ObjectMetadata.StorageClass storageClass) {
         AWSS3PutOptions options = new AWSS3PutOptions();
         return options.storageClass(storageClass);
      }
   }

   private ObjectMetadata.StorageClass storageClass;

   public AWSS3PutOptions() {
      storageClass = ObjectMetadata.StorageClass.STANDARD;
   }

   public AWSS3PutOptions(boolean multipart, ObjectMetadata.StorageClass storageClass) {
      super(multipart);
      this.storageClass = storageClass;
   }

   public AWSS3PutOptions storageClass(ObjectMetadata.StorageClass storageClass) {
      this.storageClass = storageClass;
      return this;
   }

   public ObjectMetadata.StorageClass getStorageClass() {
      return storageClass;
   }

   @Override
   public AWSS3PutOptions clone() {
      return new AWSS3PutOptions(isMultipart(), storageClass);
   }

   @Override
   public String toString() {
      return "[multipart=" + isMultipart() +
         " storageClass=" + storageClass + "]";
   }
}
