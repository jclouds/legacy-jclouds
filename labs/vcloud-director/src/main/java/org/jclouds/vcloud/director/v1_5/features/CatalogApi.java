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
package org.jclouds.vcloud.director.v1_5.features;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.CatalogItem;
import org.jclouds.vcloud.director.v1_5.functions.href.CatalogItemURNToHref;
import org.jclouds.vcloud.director.v1_5.functions.href.CatalogURNToHref;

/**
 * Provides synchronous access to {@link Catalog} objects.
 * 
 * @see CatalogAsyncApi
 * @author grkvlt@apache.org, Adrian Cole
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface CatalogApi {

   /**
    * Retrieves a catalog.
    * 
    * <pre>
    * GET /catalog/{id}
    * </pre>
    * 
    * @param catalogUri
    *           the reference for the catalog
    * @return a catalog
    */
   Catalog get(String catalogUrn);

   Catalog get(URI catalogHref);

   /**
    * Creates a catalog item in a catalog.
    * 
    * <pre>
    * POST /catalog/{id}/catalogItems
    * </pre>
    * 
    * @param catalogUri
    *           the URI of the catalog
    * @param item
    *           the catalog item to add
    * @return the addd catalog item
    */
   CatalogItem addItem(String catalogUrn, CatalogItem item);

   CatalogItem addItem(URI catalogHref, CatalogItem item);

   /**
    * Retrieves a catalog item.
    * 
    * <pre>
    * GET /catalogItem/{id}
    * </pre>
    * 
    * @param catalogItemRef
    *           the reference for the catalog item
    * @return the catalog item
    */
   CatalogItem getItem(String catalogItemUrn);

   CatalogItem getItem(URI catalogItemHref);

   /**
    * Modifies a catalog item.
    * 
    * <pre>
    * PUT /catalogItem/{id}
    * </pre>
    * 
    * @param catalogItemRef
    *           the reference for the catalog item
    * @param catalogItem
    *           the catalog item
    * @return the edited catalog item
    */
   CatalogItem editItem(String catalogItemUrn, CatalogItem catalogItem);

   CatalogItem editItem(URI catalogItemHref, CatalogItem catalogItem);

   /**
    * Deletes a catalog item.
    * 
    * <pre>
    * DELETE /catalogItem/{id}
    * </pre>
    * 
    * @param catalogItemRef
    *           the reference for the catalog item
    */
   void removeItem(String catalogItemUrn);

   void removeItem(URI catalogItemHref);

   /**
    * @return synchronous access to {@link Metadata.Readable} features
    */
   @Delegate
   MetadataApi.Readable getMetadataApi(@EndpointParam(parser = CatalogURNToHref.class) String catalogUrn);

   @Delegate
   MetadataApi.Readable getMetadataApi(@EndpointParam URI catalogItemHref);

   /**
    * @return synchronous access to {@link Metadata.Writeable} features for CatalogItems
    */
   @Delegate
   MetadataApi.Writeable getItemMetadataApi(@EndpointParam(parser = CatalogItemURNToHref.class) String catalogItemUrn);

   @Delegate
   MetadataApi.Writeable getItemMetadataApi(@EndpointParam URI catalogItemHref);

}
