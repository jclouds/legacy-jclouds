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
package org.jclouds.vcloud.director.v1_5.features.admin;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.vcloud.director.v1_5.domain.AdminCatalog;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.PublishCatalogParams;
import org.jclouds.vcloud.director.v1_5.features.CatalogClient;
import org.jclouds.vcloud.director.v1_5.features.MetadataClient;

/**
 * Provides synchronous access to {@link AdminCatalog} objects.
 * 
 * @see AdminCatalogAsyncClient
 * @author danikov
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface AdminCatalogClient extends CatalogClient {
   
   
   /**
    * Creates a catalog in an organization. The catalog will always be created in unpublished state.
    *
    * <pre>
    * POST /admin/org/{id}/catalogs
    * </pre>
    *
    * @param orgRef the reference for the org
    * @return contains a , which will point to the running asynchronous creation operation.
    */
   AdminCatalog createCatalog(URI orgRef, AdminCatalog catalog);

   /**
    * Retrieves a catalog.
    *
    * <pre>
    * GET /admin/catalog/{id}
    * </pre>
    *
    * @param catalogRef the reference for the catalog
    * @return a catalog
    */
   @Override
   AdminCatalog getCatalog(URI catalogRef);
   
   /**
    * Modifies a catalog. A catalog could be published or unpublished. The IsPublished property is treated as a 
    * read only value by the server. In order to control publishing settings use the 'publish' action must be used.
    * 
    * <pre>
    * PUT /admin/catalog/{id}
    * </pre>
    * 
    * @return the updated catalog
    */
   AdminCatalog updateCatalog(URI catalogRef, AdminCatalog catalog);
   
   /**
    * Deletes a catalog. The catalog could be deleted if it is either published or unpublished.
    * 
    * <pre>
    * DELETE /admin/catalog/{id}
    * </pre>
    */
   void deleteCatalog(URI catalogRef);
   
   /**
    * Retrieves the owner of a catalog.
    * 
    * <pre>
    * GET /admin/catalog/{id}/owner
    * </pre>
    * 
    * @return the owner or null if not found
    */
   Owner getOwner(URI catalogRef);
   
   /**
    * Changes owner for catalog.
    * 
    * <pre>
    * PUT /admin/catalog/{id}/owner
    * </pre>
    */
   void setOwner(URI catalogRef, Owner newOwner);
   
   /**
    * Publish a catalog. Publishing a catalog makes the catalog visible to all organizations in a vCloud.
    */
   void publishCatalog(URI catalogRef, PublishCatalogParams params);
   
   //TODO: lot of work to pass in a single boolean, would like to polymorphically include something like:
   //void publishCatalog(URI catalogRef)

   /**
    * @return synchronous access to {@link Metadata.Writeable} features
    */
   @Override
   @Delegate
   MetadataClient.Writeable getMetadataClient();
}
