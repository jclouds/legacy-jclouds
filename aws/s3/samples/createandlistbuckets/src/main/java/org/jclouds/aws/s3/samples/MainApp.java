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
package org.jclouds.aws.s3.samples;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.s3.blobstore.S3BlobStoreContextFactory;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.ResourceType;

/**
 * This the Main class of an Application that demonstrates the use of the CreateListOwnedContainers
 * class.
 * 
 * Usage is: java MainApp \"accesskeyid\" \"secretekey\" \"ContainerName\"
 * 
 * @author Carlos Fernandes
 */
public class MainApp {

   public static int PARAMETERS = 3;
   public static String INVALID_SYNTAX = "Invalid number of parameters. Syntax is: \"accesskeyid\" \"secretekey\" \"ContainerName\" ";

   @SuppressWarnings("unchecked")
   public static void main(String[] args) throws InterruptedException, ExecutionException,
            TimeoutException, IOException {

      if (args.length < PARAMETERS)
         throw new IllegalArgumentException(INVALID_SYNTAX);

      // Args
      String accesskeyid = args[0];
      String secretkey = args[1];
      String ContainerName = args[2];

      // Init
      BlobStoreContext context = S3BlobStoreContextFactory.createContext(accesskeyid, secretkey);

      try {

         // Create Container
         BlobStore blobStore = context.getBlobStore();
         blobStore.createContainer(ContainerName).get(10, TimeUnit.SECONDS);
         
         Blob blob = context.getBlobStore().newBlob();
         blob.getMetadata().setName("test");
         blob.setData("testdata");
         blobStore.putBlob("test", blob).get(10, TimeUnit.SECONDS);;

         // List Container
         for (ResourceMetadata resourceMd : blobStore.list().get(10, TimeUnit.SECONDS)) {
            System.out.println(String.format("  %1$s", resourceMd));
            if (resourceMd.getType() == ResourceType.CONTAINER ||resourceMd.getType() == ResourceType.FOLDER){
               System.out.println(String.format(": %1$s entries%n", context.createInputStreamMap(
                        resourceMd.getName()).size()));
            }
         }

      } finally {
         // Close connecton
         context.close();
      }

   }

}
