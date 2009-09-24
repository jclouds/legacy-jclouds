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
package org.jclouds.azure.storage.blob.options;

import org.jclouds.azure.storage.options.ListOptions;

/**
 * Contains options supported in the REST API for the List Blobs operation. <h2>
 * Usage</h2> The recommended way to instantiate a ListBlobsOptions object is to statically import
 * ListBlobsOptions.* and invoke a static creation method followed by an instance mutator (if
 * needed):
 * <p/>
 * <code>
 * import static org.jclouds.azure.storage.blob.options.ListBlobsOptions.Builder.*
 * import org.jclouds.azure.storage.blob.AzureBlobConnection;
 * <p/>
 * AzureBlobConnection connection = // get connection
 * Set<BlobMetadata> blobs = connection.listBlobs("containerName", delimiter("/"));
 * <code> *
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd179466.aspx" />
 * @author Adrian Cole
 */
public class ListBlobsOptions extends ListOptions {

   /**
    * When the request includes this parameter, the operation returns a {@code BlobPrefix} element
    * in the response body that acts as a placeholder for all blobs whose names begin with the same
    * substring up to the appearance of the delimiter character.
    * 
    * @param delimiter
    *           a single character or a string.
    */
   public ListBlobsOptions delimiter(String delimiter) {
      this.queryParameters.put("delimiter", delimiter);
      return this;
   }

   public static class Builder {

      /**
       * @see ListBlobsOptions#delimiter(String)
       */
      public static ListBlobsOptions delimiter(String delimiter) {
         ListBlobsOptions options = new ListBlobsOptions();
         return options.delimiter(delimiter);
      }

   }
}
