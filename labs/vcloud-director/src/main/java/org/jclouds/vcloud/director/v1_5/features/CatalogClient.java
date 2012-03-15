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
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.CatalogItem;
import org.jclouds.vcloud.director.v1_5.domain.CatalogType;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;

/**
 * Provides synchronous access to {@link Catalog} objects.
 * 
 * @see CatalogAsyncClient
 * @author grkvlt@apache.org
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface CatalogClient {

   /**
    * Retrieves a catalog.
    *
    * <pre>
    * GET /catalog/{id}
    * </pre>
    *
    * @param catalogUri the reference for the catalog
    * @return a catalog
    */
   CatalogType getCatalog(URI catalogUri);

   /**
    * Creates a catalog item in a catalog.
    *
    * <pre>
    * POST /catalog/{id}/catalogItems
    * </pre>
    *
    * @param catalogUri the URI of the catalog
    * @param item the catalog item to create
    * @return the created catalog item
    */
   CatalogItem addCatalogItem(URI catalogUri, CatalogItem item);

   /**
    * Retrieves a catalog item.
    *
    * <pre>
    * GET /catalogItem/{id}
    * </pre>
    * 
    * @param catalogItemRef the reference for the catalog item
    * @return the catalog item
    */
   CatalogItem getCatalogItem(URI catalogItemRef);

   /**
    * Modifies a catalog item.
    *
    * <pre>
    * PUT /catalogItem/{id}
    * </pre>
    *
    * @param catalogItemRef the reference for the catalog item
    * @param catalogItem the catalog item
    * @return the updated catalog item
    */
   CatalogItem updateCatalogItem(URI catalogItemRef, CatalogItem catalogItem);

   /**
    * Deletes a catalog item.
    *
    * <pre>
    * DELETE /catalogItem/{id}
    * </pre>
    *
    * @param catalogItemRef the reference for the catalog item
    */
   void deleteCatalogItem(URI catalogItemRef);

   /**
    * @return synchronous access to {@link Metadata.Writeable} features
    */
   @Delegate
   MetadataClient.Writeable getMetadataClient();
}
