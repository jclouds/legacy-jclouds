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
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.vcloud.director.v1_5.domain.AdminCatalog;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.params.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.params.PublishCatalogParams;
import org.jclouds.vcloud.director.v1_5.features.CatalogApi;
import org.jclouds.vcloud.director.v1_5.features.MetadataApi;
import org.jclouds.vcloud.director.v1_5.functions.href.CatalogURNToAdminHref;

/**
 * Provides synchronous access to {@link AdminCatalog} objects.
 * 
 * @see AdminCatalogAsyncApi
 * @author danikov, Adrian Cole
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface AdminCatalogApi extends CatalogApi {

   /**
    * Creates a catalog in an organization. The catalog will always be addd in unpublished state.
    * 
    * <pre>
    * POST /admin/org/{id}/catalogs
    * </pre>
    * 
    * @param orgUrn
    *           the urn for the org
    * @return contains a , which will point to the running asynchronous creation operation.
    */
   AdminCatalog addCatalogToOrg(AdminCatalog catalog, String orgUrn);

   AdminCatalog addCatalogToOrg(AdminCatalog catalog, URI catalogAdminHref);

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
   AdminCatalog get(URI catalogAdminHref);

   /**
    * Modifies a catalog. A catalog could be published or unpublished. The IsPublished property is
    * treated as a read only value by the server. In order to control publishing settings use the
    * 'publish' action must be used.
    * 
    * <pre>
    * PUT /admin/catalog/{id}
    * </pre>
    * 
    * @return the edited catalog
    */
   AdminCatalog edit(String catalogUrn, AdminCatalog catalog);

   AdminCatalog edit(URI catalogAdminHref, AdminCatalog catalog);

   /**
    * Deletes a catalog. The catalog could be removed if it is either published or unpublished.
    * 
    * <pre>
    * DELETE /admin/catalog/{id}
    * </pre>
    */
   void remove(String catalogUrn);

   void remove(URI catalogAdminHref);

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

   Owner getOwner(URI catalogAdminHref);

   /**
    * Changes owner for catalog.
    * 
    * <pre>
    * PUT /admin/catalog/{id}/owner
    * </pre>
    */
   void setOwner(String catalogUrn, Owner newOwner);

   void setOwner(URI catalogAdminHref, Owner newOwner);

   // TODO: lot of work to pass in a single boolean, would like to polymorphically include something
   // like:
   // void publishCatalog(String catalogUrn)
   
   /**
    * Publish a catalog. Publishing a catalog makes the catalog visible to all organizations in a
    * vCloud.
    * @param orgUrn
    */
   void publish(String catalogUrn, PublishCatalogParams params);

   void publish(URI catalogAdminHref, PublishCatalogParams params);

   /**
    * Modifies a catalog control access.
    *
    * <pre>
    * POST /org/{id}/catalog/{catalogId}/action/controlAccess
    * </pre>
    *
    * @return the control access information
    */
   ControlAccessParams editAccessControl(String catalogUrn, ControlAccessParams params);

   ControlAccessParams editAccessControl(URI catalogAdminHref, ControlAccessParams params);

   /**
    * Retrieves the catalog control access information.
    *
    * <pre>
    * GET /org/{id}/catalog/{catalogId}/controlAccess
    * </pre>
    *
    * @return the control access information
    */
   ControlAccessParams getAccessControl(String catalogUrn);
   
   ControlAccessParams getAccessControl(URI catalogAdminHref);

   /**
    * @return synchronous access to {@link Metadata.Writeable} features
    */
   @Override
   @Delegate
   MetadataApi.Writeable getMetadataApi(@EndpointParam(parser = CatalogURNToAdminHref.class) String catalogUrn);

   @Override
   @Delegate
   MetadataApi.Writeable getMetadataApi(@EndpointParam URI catalogAdminHref);

}
