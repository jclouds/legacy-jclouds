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
package org.jclouds.blobstore.integration.internal;

import java.util.SortedSet;

import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.internal.MutableResourceMetadataImpl;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
public class BaseServiceIntegrationTest<A, S> extends BaseBlobStoreIntegrationTest<A, S> {

   @Test(groups = { "integration", "live" })
   void containerDoesntExist() {
      SortedSet<? extends ResourceMetadata> list = context.getBlobStore().list();
      assert !list.contains(new MutableResourceMetadataImpl());
   }

}