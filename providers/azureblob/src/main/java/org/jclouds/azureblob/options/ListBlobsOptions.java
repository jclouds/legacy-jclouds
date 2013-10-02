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

import org.jclouds.azure.storage.options.ListOptions;

/**
 * Contains options supported in the REST API for the List Blobs operation. <h2>
 * Usage</h2> The recommended way to instantiate a ListBlobsOptions object is to statically import
 * ListBlobsOptions.* and invoke a static creation method followed by an instance mutator (if
 * needed):
 * <p/>
 * <code>
 * import static org.jclouds.azureblob.options.ListBlobsOptions.Builder.*
 * import org.jclouds.azureblob.AzureBlobClient;
 * <p/>
 * AzureBlobClient connection = // get connection
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

   public String getDelimiter() {
      return this.getFirstQueryOrNull("delimiter");
   }

   public static class Builder {

      /**
       * @see ListBlobsOptions#delimiter(String)
       */
      public static ListBlobsOptions delimiter(String delimiter) {
         ListBlobsOptions options = new ListBlobsOptions();
         return options.delimiter(delimiter);
      }

      /**
       * @see ListOptions#includeMetadata()
       */
      public static ListBlobsOptions includeMetadata() {
         ListBlobsOptions options = new ListBlobsOptions();
         return options.includeMetadata();
      }

      /**
       * @see ListOptions#prefix(String)
       */
      public static ListBlobsOptions prefix(String prefix) {
         ListBlobsOptions options = new ListBlobsOptions();
         return options.prefix(prefix);
      }

      /**
       * @see ListOptions#marker(String)
       */
      public static ListBlobsOptions marker(String marker) {
         ListBlobsOptions options = new ListBlobsOptions();
         return options.marker(marker);
      }

      /**
       * @see ListOptions#maxResults(long)
       */
      public static ListBlobsOptions maxResults(int maxKeys) {
         ListBlobsOptions options = new ListBlobsOptions();
         return options.maxResults(maxKeys);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListBlobsOptions includeMetadata() {
      return (ListBlobsOptions) super.includeMetadata();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListBlobsOptions marker(String marker) {
      return (ListBlobsOptions) super.marker(marker);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListBlobsOptions maxResults(int maxresults) {
      return (ListBlobsOptions) super.maxResults(maxresults);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListBlobsOptions prefix(String prefix) {
      return (ListBlobsOptions) super.prefix(prefix);
   }
}
