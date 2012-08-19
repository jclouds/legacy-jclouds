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
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.params.PublishCatalogParams;
import org.jclouds.vcloud.director.v1_5.features.CatalogApi;
import org.jclouds.vcloud.director.v1_5.features.MetadataApi;

/**
 * Provides synchronous access to {@link AdminCatalog} objects.
 * 
 * @see AdminCatalogAsyncApi
 * @author danikov, Adrian Cole
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface AdminCatalogApi extends CatalogApi {

   /**
    * Creates a catalog in an organization. The catalog will always be created in unpublished state.
    * 
    * <pre>
    * POST /admin/org/{id}/catalogs
    * </pre>
    * 
    * @param orgUrn
    *           the urn for the org
    * @return contains a , which will point to the running asynchronous creation operation.
    */
   AdminCatalog createCatalogInOrg(AdminCatalog catalog, String orgUrn);

   AdminCatalog createCatalogInOrg(AdminCatalog catalog, URI adminCatalogHref);

   /**
    * Retrieves a catalog.
    * 
    * <pre>
    * GET /admin/catalog/{id}
    * </pre>
    * 
    * @param catalogUrn
    *           the urn for the catalog
    * @return a catalog
    */
   @Override
   AdminCatalog get(String catalogUrn);
   
   @Override
   AdminCatalog get(URI adminCatalogHref);

   /**
    * Modifies a catalog. A catalog could be published or unpublished. The IsPublished property is
    * treated as a read only value by the server. In order to control publishing settings use the
    * 'publish' action must be used.
    * 
    * <pre>
    * PUT /admin/catalog/{id}
    * </pre>
    * 
    * @return the updated catalog
    */
   AdminCatalog update(String catalogUrn, AdminCatalog catalog);

   AdminCatalog update(URI adminCatalogHref, AdminCatalog catalog);

   /**
    * Deletes a catalog. The catalog could be deleted if it is either published or unpublished.
    * 
    * <pre>
    * DELETE /admin/catalog/{id}
    * </pre>
    */
   void delete(String catalogUrn);

   void delete(URI adminCatalogHref);

   /**
    * Retrieves the owner of a catalog.
    * 
    * <pre>
    * GET /admin/catalog/{id}/owner
    * </pre>
    * 
    * @return the owner or null if not found
    */
   Owner getOwner(String catalogUrn);

   Owner getOwner(URI adminCatalogHref);

   /**
    * Changes owner for catalog.
    * 
    * <pre>
    * PUT /admin/catalog/{id}/owner
    * </pre>
    */
   void setOwner(String catalogUrn, Owner newOwner);

   void setOwner(URI adminCatalogHref, Owner newOwner);

   /**
    * Publish a catalog. Publishing a catalog makes the catalog visible to all organizations in a
    * vCloud.
    * @param orgUrn
    */
   void publish(String catalogUrn, PublishCatalogParams params);

   void publish(URI adminCatalogHref, PublishCatalogParams params);

   // TODO: lot of work to pass in a single boolean, would like to polymorphically include something
   // like:
   // void publishCatalog(String catalogUrn)

   /**
    * @return synchronous access to {@link Metadata.Writeable} features
    */
   @Override
   @Delegate
   MetadataApi.Writeable getMetadataApi();
}
