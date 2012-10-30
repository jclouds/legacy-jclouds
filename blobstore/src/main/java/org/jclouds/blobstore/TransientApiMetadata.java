/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.blobstore;

import java.net.URI;

import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.blobstore.config.TransientBlobStoreContextModule;

/**
 * Implementation of {@link ApiMetadata} for jclouds in-memory (Transient) API
 * 
 * @author Adrian Cole
 */
public class TransientApiMetadata extends BaseApiMetadata {

   /** The serialVersionUID */
   private static final long serialVersionUID = -6541485071006878726L;

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return Builder.class.cast(builder().fromApiMetadata(this));
   }

   public TransientApiMetadata() {
      super(builder());
   }

   protected TransientApiMetadata(Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseApiMetadata.Builder {

      protected Builder() {
         id("transient")
         .name("in-memory (Transient) API")
         .identityName("Unused")
         .defaultEndpoint("http://localhost")
         .defaultIdentity(System.getProperty("user.name"))
         .defaultCredential("bar")
         .version("1")
         .view(BlobStoreContext.class)
         .defaultModule(TransientBlobStoreContextModule.class)
         .documentation(URI.create("http://www.jclouds.org/documentation/userguide/blobstore-guide"));
      }

      @Override
      public TransientApiMetadata build() {
         return new TransientApiMetadata(this);
      }

   }

}