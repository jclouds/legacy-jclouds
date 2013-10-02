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

import org.jclouds.s3.domain.CannedAccessPolicy;
import org.jclouds.s3.domain.ObjectMetadata;
import org.jclouds.s3.options.PutObjectOptions;
import org.jclouds.s3.reference.S3Headers;

/**
 * Contains options supported in the AWS S3 REST API for the PUT object operation
 *
 * @see PutObjectOptions
 * @author Andrei Savu
 */
public class AWSS3PutObjectOptions extends PutObjectOptions {

   public static class Builder {

      /**
       * @see AWSS3PutObjectOptions#storageClass
       */
      public static AWSS3PutObjectOptions storageClass(ObjectMetadata.StorageClass storageClass) {
         AWSS3PutObjectOptions options = new AWSS3PutObjectOptions();
         return options.storageClass(storageClass);
      }

      /**
       * @see AWSS3PutObjectOptions#withAcl
       */
      public static AWSS3PutObjectOptions withAcl(CannedAccessPolicy acl) {
         AWSS3PutObjectOptions options = new AWSS3PutObjectOptions();
         return options.withAcl(acl);
      }
   }

   private ObjectMetadata.StorageClass storageClass = ObjectMetadata.StorageClass.STANDARD;

   public AWSS3PutObjectOptions storageClass(ObjectMetadata.StorageClass storageClass) {
      this.storageClass = storageClass;
      if (storageClass != ObjectMetadata.StorageClass.STANDARD) {
         this.replaceHeader(S3Headers.STORAGE_CLASS, this.storageClass.toString());
      }
      return this;
   }

   public ObjectMetadata.StorageClass getStorageClass() {
      return storageClass;
   }

   @Override
   public AWSS3PutObjectOptions withAcl(CannedAccessPolicy acl) {
      return (AWSS3PutObjectOptions) super.withAcl(acl);
   }
}
