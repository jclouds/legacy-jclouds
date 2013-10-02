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
package org.jclouds.blobstore.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

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
public class ListContainerOptions extends ListOptions implements Cloneable {

   public static final ImmutableListContainerOptions NONE = new ImmutableListContainerOptions(
            new ListContainerOptions());

   private String dir;
   private boolean recursive;
   private boolean detailed;

   public ListContainerOptions() {
   }

   ListContainerOptions(Integer maxKeys, String marker, String dir, boolean recursive,
            boolean detailed) {
      super(maxKeys, marker);
      this.dir = dir;
      this.recursive = recursive;
      this.detailed = detailed;
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
      public boolean isDetailed() {
         return delegate.isDetailed();
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

      @Override
      public String toString() {
         return delegate.toString();
      }

   }

   public String getDir() {
      return dir;
   }

   public boolean isRecursive() {
      return recursive;
   }

   public boolean isDetailed() {
      return detailed;
   }

   /**
    * This will list the contents of a virtual or real directory path.
    * 
    */
   public ListContainerOptions inDirectory(String dir) {
      checkNotNull(dir, "dir");
      checkArgument(!dir.equals("/"), "dir must not be a slash");
      this.dir = dir;
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

   /**
    * populate each result with detailed such as metadata even if it incurs extra requests to the
    * service.
    */
   public ListContainerOptions withDetails() {
      this.detailed = true;
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

      /**
       * @see ListContainerOptions#withDetails()
       */
      public static ListContainerOptions withDetails() {
         ListContainerOptions options = new ListContainerOptions();
         return options.withDetails();
      }
   }

   @Override
   public ListContainerOptions clone() {
      return new ListContainerOptions(getMaxResults(), getMarker(), dir, recursive, detailed);
   }

   @Override
   public String toString() {
      return "[dir=" + dir + ", recursive=" + recursive + ", detailed=" + detailed
               + ", maxResults=" + getMaxResults() + "]";
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(detailed, recursive, dir, getMarker(), getMaxResults());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ListContainerOptions other = (ListContainerOptions) obj;
      return (detailed == other.detailed) &&
               recursive == other.recursive &&
               Objects.equal(dir, other.dir) &&
               Objects.equal(getMarker(), other.getMarker()) &&
               Objects.equal(getMaxResults(), other.getMaxResults());
   }
   
   
}
