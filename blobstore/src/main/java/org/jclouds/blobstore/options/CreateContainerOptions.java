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

/**
 * Contains options supported in the list container operation. <h2>
 * Usage</h2> The recommended way to instantiate a CreateOptions object is to statically import
 * CreateContainerOptions.* and invoke a static creation method followed by an instance mutator (if
 * needed):
 * <p/>
 * <code>
 * import static org.jclouds.blobstore.options.CreateContainerOptions.Builder.*
 * <p/>
 * BlobStore connection = // get connection
 * Future<CreateResponse<ResourceMetadata>> list = connection.list("container",inDirectory("home/users").maxResults(1000));
 * <code>
 * 
 * @author Adrian Cole
 */
public class CreateContainerOptions implements Cloneable {

   public static final ImmutableCreateContainerOptions NONE = new ImmutableCreateContainerOptions(
            new CreateContainerOptions());

   private boolean publicRead;

   public CreateContainerOptions() {
   }

   CreateContainerOptions(boolean publicRead) {
      this.publicRead = publicRead;
   }

   public static class ImmutableCreateContainerOptions extends CreateContainerOptions {
      private final CreateContainerOptions delegate;

      public ImmutableCreateContainerOptions(CreateContainerOptions delegate) {
         this.delegate = delegate;
      }

      @Override
      public boolean isPublicRead() {
         return delegate.isPublicRead();
      }

      @Override
      public CreateContainerOptions publicRead() {
         throw new UnsupportedOperationException();
      }

      @Override
      public CreateContainerOptions clone() {
         return delegate.clone();
      }

      @Override
      public String toString() {
         return delegate.toString();
      }

   }

   public boolean isPublicRead() {
      return publicRead;
   }

   /**
    * return a listing of all objects inside the store, publicReadly.
    */
   public CreateContainerOptions publicRead() {
      // checkArgument(path == null, "path and publicRead combination currently not supported");
      this.publicRead = true;
      return this;
   }

   public static class Builder {

      /**
       * @see CreateContainerOptions#publicRead()
       */
      public static CreateContainerOptions publicRead() {
         CreateContainerOptions options = new CreateContainerOptions();
         return options.publicRead();
      }

   }

   @Override
   public CreateContainerOptions clone() {
      return new CreateContainerOptions(publicRead);
   }

   @Override
   public String toString() {
      return "[publicRead=" + publicRead + "]";
   }
}
