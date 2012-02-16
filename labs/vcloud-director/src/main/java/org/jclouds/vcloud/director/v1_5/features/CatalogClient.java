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

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.CatalogItem;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.ReferenceType;
import org.jclouds.vcloud.director.v1_5.domain.Task;

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
    * @param catalogRef the reference for the catalog
    * @return a catalog
    */
   Catalog getCatalog(ReferenceType<?> catalogRef);

   /**
    * Creates a catalog item in a catalog.
    *
    * <pre>
    * POST /catalog/{id}/catalogItems
    * </pre>
    *
    * @param catalogRef the reference for the catalog
    * @param item the catalog item to create
    * @return the created catalog item
    */
   CatalogItem addCatalogItem(ReferenceType<?> catalogRef, CatalogItem item);

   /**
    * Returns the metadata associated with the catalog.
    *
    * <pre>
    * GET /catalog/{id}/metadata
    * </pre>
    *
    * @param catalogRef the reference for the catalog
    * @return the catalog metadata
    */
   Metadata getCatalogMetadata(ReferenceType<?> catalogRef);

   /**
    * Returns the metadata associated with the catalog for the specified key.
    *
    * <pre>
    * GET /catalog/{id}/metadata/{key}
    * </pre>
    *
    * @param catalogRef the reference for the catalog
    * @param key the metadata entry key
    * @return the catalog metadata value
    */
   MetadataValue getCatalogMetadataValue(ReferenceType<?> catalogRef, String key);

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
   CatalogItem getCatalogItem(ReferenceType<?> catalogItemRef);

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
   CatalogItem updateCatalogItem(ReferenceType<?> catalogItemRef, CatalogItem catalogItem);

   /**
    * Deletes a catalog item.
    *
    * <pre>
    * DELETE /catalogItem/{id}
    * </pre>
    *
    * @param catalogItemRef the reference for the catalog item
    */
   void deleteCatalogItem(ReferenceType<?> catalogItemRef);

   /**
    * Returns the metadata associated with the catalog item.
    *
    * <pre>
    * GET /catalogItem/{id}/metadata
    * </pre>
    *
    * @param catalogItemRef the reference for the catalog item
    * @return the catalog item metadata
    */
   Metadata getCatalogItemMetadata(ReferenceType<?> catalogItemRef);

   /**
    * Merges the metadata for a catalog item with the information provided.
    *
    * <pre>
    * POST /catalogItem/{id}/metadata
    * </pre>
    *
    * @param catalogItemRef the reference for the catalog item
    * @param catalogItemMetadata the metadata for the catalog item
    * @return a task for the merge operation
    */
   Task mergeCatalogItemMetadata(ReferenceType<?> catalogItemRef, Metadata catalogItemMetadata);

   /**
    * Returns the metadata associated with the catalog item for the specified key.
    *
    * <pre>
    * GET /catalog/{id}/metadata/{key}
    * </pre>
    *
    * @param catalogItemRef the reference for the catalog item
    * @param key the metadata entry key
    * @return the catalog item metadata value
    */
   MetadataValue getCatalogItemMetadataValue(ReferenceType<?> catalogItemRef, String key);

   /**
    * Sets the metadata for the particular key for the catalog item to the value provided.
    *
    * <pre>
    * PUT /catalog/{id}/metadata/{key}
    * </pre>
    *
    * @param catalogItemRef the reference for the catalog item
    * @param key the metadata entry key
    * @param value the metadata value
    * @return a task for the set operation
    */
   Task setCatalogItemMetadataValue(ReferenceType<?> catalogItemRef, String key, MetadataValue value);

   /**
    * Deletes the metadata for the particular key for the catalog item.
    *
    * <pre>
    * DELETE /catalog/{id}/metadata/{key}
    * </pre>
    *
    * @param catalogItemRef the reference for the catalog item
    * @param key the metadata entry key
    * @return a task for the delete operation
    */
   Task deleteCatalogItemMetadataValue(ReferenceType<?> catalogItemRef, String key);
}
