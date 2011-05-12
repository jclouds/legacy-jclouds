/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.providers;

import java.net.URI;

/**
 * Implementation of {@ link org.jclouds.types.ProviderMetadata} for testing.
 *
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
public class JcloudsTestBlobStoreProviderMetadata extends BaseProviderMetadata {

   /**
    * {@ see org.jclouds.types.ProviderMetadata#getId()}
    */
   @Override
   public String getId() {
      return "test-blobstore-provider";
   }

   /**
    * {@ see org.jclouds.types.ProviderMetadata#getType()}
    */
   @Override
   public String getType() {
      return ProviderMetadata.BLOBSTORE_TYPE;
   }

   /**
    * {@ see org.jclouds.types.ProviderMetadata#getName()}
    */
   @Override
   public String getName() {
      return "Test Blobstore Provider";
   }

   /**
    * {@ see org.jclouds.types.ProviderMetadata#getHomepage()}
    */
   @Override
   public URI getHomepage() {
      return URI.create("http://jclouds.org");
   }

   /**
    * {@ see org.jclouds.types.ProviderMetadata#getConsole()}
    */
   @Override
   public URI getConsole() {
      return URI.create("http://jclouds.org/console");
   }

}