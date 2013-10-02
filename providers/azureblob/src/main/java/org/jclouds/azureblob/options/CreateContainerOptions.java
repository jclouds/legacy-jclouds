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
package org.jclouds.azureblob.options;

import org.jclouds.azure.storage.options.CreateOptions;
import org.jclouds.azureblob.domain.PublicAccess;

import com.google.common.collect.Multimap;

/**
 * Contains options supported in the REST API for the Create Container operation. <h2>
 * Usage</h2> The recommended way to instantiate a CreateContainerOptions object is to statically
 * import CreateContainerOptions.* and invoke a static creation method followed by an instance
 * mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.azureblob.options.CreateContainerOptions.Builder.*
 * import org.jclouds.azureblob.AzureBlobClient;
 * <p/>
 * AzureBlobClient connection = // get connection
 * boolean createdWithPublicAccess = connection.createContainer("containerName", withPublicAccess(PublicAccess.BLOB));
 * <code> *
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd179466.aspx" />
 * @author Adrian Cole
 */
public class CreateContainerOptions extends CreateOptions {
   public static final CreateContainerOptions NONE = new CreateContainerOptions();

   @Override
   public CreateContainerOptions withMetadata(Multimap<String, String> metadata) {
      return (CreateContainerOptions) super.withMetadata(metadata);
   }

   /**
    * Indicates whether a container may be accessed publicly
    */
   public CreateContainerOptions withPublicAccess(PublicAccess access) {
      if (access != PublicAccess.PRIVATE)
         this.headers.put("x-ms-blob-public-access", access.name().toLowerCase());
      return this;
   }

   public static class Builder {

      /**
       * @see CreateContainerOptions#withPublicAccess
       */
      public static CreateContainerOptions withPublicAccess(PublicAccess access) {
         CreateContainerOptions options = new CreateContainerOptions();
         return options.withPublicAccess(access);
      }

      /**
       * @see CreateContainerOptions#withMetadata(Multimap<String, String>)
       */
      public static CreateContainerOptions withMetadata(Multimap<String, String> metadata) {
         CreateContainerOptions options = new CreateContainerOptions();
         return options.withMetadata(metadata);
      }

   }
}
