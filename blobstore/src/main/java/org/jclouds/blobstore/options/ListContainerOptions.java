/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.blobstore.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Contains options supported in the list container operation. <h2>
 * Usage</h2> The recommended way to instantiate a ListOptions object is to statically import
 * ListContainerOptions.* and invoke a static creation method followed by an instance mutator (if
 * needed):
 * <p/>
 * <code>
 * import static org.jclouds.blobstore.options.ListContainerOptions.Builder.*
 * <p/>
 * BlobStore connection = // get connection
 * Future<ListResponse<ResourceMetadata>> list = connection.list("container",inDirectory("home/users").maxResults(1000));
 * <code>
 * 
 * @author Adrian Cole
 */
public class ListContainerOptions extends ListOptions {

   private String dir;

   private boolean recursive;

   public String getDir() {
      return dir;
   }

   public boolean isRecursive() {
      return recursive;
   }

   /**
    * Returns a pseudo-directory listing.
    * 
    */
   public ListContainerOptions inDirectory(String dir) {
      checkArgument(!recursive, "dir and recursive combination currently not supported");
      this.dir = checkNotNull(dir, "dir");
      checkArgument(!dir.equals("/"), "dir must not be a slash");
      return this;
   }

   /**
    * {@inheritDoc}
    */
   public ListContainerOptions afterMarker(String marker) {
      return (ListContainerOptions) super.afterMarker(marker);
   }

   /**
    * {@inheritDoc}
    */
   public ListContainerOptions maxResults(int maxKeys) {
      return (ListContainerOptions) super.maxResults(maxKeys);
   }

   /**
    * return a listing of all objects inside the store, recursively.
    */
   public ListContainerOptions recursive() {
      // checkArgument(path == null, "path and recursive combination currently not supported");
      this.recursive = true;
      return this;
   }

   public static class Builder {

      /**
       * @see ListContainerOptions#inDirectory(String)
       */
      public static ListContainerOptions inDirectory(String directory) {
         ListContainerOptions options = new ListContainerOptions();
         return options.inDirectory(directory);
      }

      /**
       * @see ListContainerOptions#afterMarker(String)
       */
      public static ListContainerOptions afterMarker(String marker) {
         ListContainerOptions options = new ListContainerOptions();
         return options.afterMarker(marker);
      }

      /**
       * @see ListContainerOptions#maxResults(int)
       */
      public static ListContainerOptions maxResults(int maxKeys) {
         ListContainerOptions options = new ListContainerOptions();
         return options.maxResults(maxKeys);
      }

      /**
       * @see ListContainerOptions#recursive()
       */
      public static ListContainerOptions recursive() {
         ListContainerOptions options = new ListContainerOptions();
         return options.recursive();
      }

   }
}
