/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.servermanager.compute.suppliers;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.TransformingSetSupplier;
import org.jclouds.compute.domain.Image;
import org.jclouds.servermanager.ServerManager;
import org.jclouds.servermanager.compute.functions.ServerManagerImageToImage;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ServerManagerImageSupplier extends TransformingSetSupplier<org.jclouds.servermanager.Image, Image> {
   private final ServerManager client;

   @Inject
   protected ServerManagerImageSupplier(ServerManager client, ServerManagerImageToImage serverManagerImageToImage) {
      super(serverManagerImageToImage);
      this.client = client;
   }

   public Iterable<org.jclouds.servermanager.Image> supplyFrom() {
      return client.listImages();
   }
}
