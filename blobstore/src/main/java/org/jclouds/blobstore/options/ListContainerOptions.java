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
 * ListenableFuture<ListResponse<ResourceMetadata>> list = connection.list("container",inDirectory("home/users").maxResults(1000));
 * <code>
 * 
 * @author Adrian Cole
 */
public class ListContainerOptions extends ListOptions implements Cloneable {
   public ListContainerOptions() {
   }

   ListContainerOptions(Integer maxKeys, String marker, String dir, boolean recursive) {
      super(maxKeys, marker);
      this.dir = dir;
      this.recursive = recursive;
   }

   public static class ImmutableListContainerOptions extends ListContainerOptions {
      private final ListContainerOptions delegate;

      @Override
      public ListContainerOptions afterMarker(String marker) {
         throw new UnsupportedOperationException();
      }

      public ImmutableListContainerOptions(ListContainerOptions delegate) {
         this.delegate = delegate;
      }

      @Override
      public String getDir() {
         return delegate.getDir();
      }

      @Override
      public ListContainerOptions inDirectory(String dir) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isRecursive() {
         return delegate.isRecursive();
      }

      @Override
      public ListContainerOptions maxResults(int maxKeys) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ListContainerOptions recursive() {
         throw new UnsupportedOperationException();

      }

      @Override
      public String getMarker() {
         return delegate.getMarker();
      }

      @Override
      public Integer getMaxResults() {
         return delegate.getMaxResults();
      }

      @Override
      public ListContainerOptions clone() {
         return delegate.clone();
      }

   }

   public static final ImmutableListContainerOptions NONE = new ImmutableListContainerOptions(
            new ListContainerOptions());

   private String dir;

   private boolean recursive;

   public String getDir() {
      return dir;
   }

   public boolean isRecursive() {
      return recursive;
   }

   /**
    * This will list the contents of a virtual or real directory path.
    * 
    */
   public ListContainerOptions inDirectory(String dir) {
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

   @Override
   public ListContainerOptions clone() {
      return new ListContainerOptions(getMaxResults(), getMarker(), dir, recursive);
   }

   @Override
   public String toString() {
      return "[dir=" + dir + ", recursive=" + recursive + ", maxResults=" + getMaxResults() + "]";
   }
}
