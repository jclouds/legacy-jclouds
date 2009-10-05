/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.aws.s3.samples;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.s3.S3Context;
import org.jclouds.aws.s3.S3ContextFactory;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;

/**
 * This the Main class of an Application that demonstrates the use of the CreateListOwnedBuckets
 * class.
 * 
 * Usage is: java MainApp \"accesskeyid\" \"secretekey\" \"bucketName\"
 * 
 * @author Carlos Fernandes
 */
public class MainApp {

   public static int PARAMETERS = 3;
   public static String INVALID_SYNTAX = "Invalid number of parameters. Syntax is: \"accesskeyid\" \"secretekey\" \"bucketName\" ";

   @SuppressWarnings("unchecked")
   public static void main(String[] args) throws InterruptedException, ExecutionException,
            TimeoutException, IOException {

      if (args.length < PARAMETERS)
         throw new IllegalArgumentException(INVALID_SYNTAX);

      // Args
      String accesskeyid = args[0];
      String secretkey = args[1];
      String bucketName = args[2];

      // Init
      BlobStoreContext context = S3ContextFactory.createContext(accesskeyid, secretkey);

      try {

         // Create Bucket
         ((S3Context) context).getApi().putBucketIfNotExists(bucketName).get(10, TimeUnit.SECONDS);
         BlobMap blobMap = context.createBlobMap(bucketName);
         Blob blob = context.newBlob("test");
         blob.setData("testdata");
         blobMap.put("test", blob);

         // List bucket
         for (BucketMetadata bucketObj : ((S3Context) context).getApi().listOwnedBuckets()) {
            System.out.println(String.format("  %1$s", bucketObj));
            System.out.println(String.format(": %1$s entries%n", context.createInputStreamMap(
                     bucketObj.getName()).size()));
         }

      } finally {
         // Close connecton
         context.close();
      }

   }

}
