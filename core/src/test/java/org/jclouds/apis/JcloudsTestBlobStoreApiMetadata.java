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
package org.jclouds.apis;

import java.net.URI;

/**
 * Implementation of @ link org.jclouds.types.ApiMetadata} for testing.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>, Adrian Cole
 */
public class JcloudsTestBlobStoreApiMetadata extends BaseApiMetadata {
   
   public JcloudsTestBlobStoreApiMetadata() {
      this(builder()
            .id("test-blobstore-api")
            .type(ApiType.BLOBSTORE)
            .name("Test Blobstore Api")
            .identityName("user")
            .credentialName("password")
            .documentation(URI.create("http://jclouds.org/documentation")));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected JcloudsTestBlobStoreApiMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public JcloudsTestBlobStoreApiMetadata build() {
         return new JcloudsTestBlobStoreApiMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   @Override
   public ConcreteBuilder toBuilder() {
      return builder().fromApiMetadata(this);
   }
}