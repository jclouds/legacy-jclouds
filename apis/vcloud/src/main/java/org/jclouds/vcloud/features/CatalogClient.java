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
package org.jclouds.vcloud.features;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.options.CatalogItemOptions;

/**
 * Provides access to Catalog functionality in vCloud
 * <p/>
 * 
 * @see <a href="http://communities.vmware.com/community/developer/forums/vcloudapi" />
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface CatalogClient {

   Catalog getCatalog(URI catalogId);

   /**
    * returns the catalog in the organization associated with the specified name. Note that both
    * parameters can be null to choose default.
    * 
    * @param orgName
    *           organization name, or null for the default
    * @param catalogName
    *           catalog name, or null for the default
    * @throws NoSuchElementException
    *            if you specified an org or catalog name that isn't present
    */
   Catalog findCatalogInOrgNamed(@Nullable String orgName, @Nullable String catalogName);

   CatalogItem getCatalogItem(URI catalogItem);

   /**
    * returns the catalog item in the catalog associated with the specified name. Note that the org
    * and catalog parameters can be null to choose default.
    * 
    * @param orgName
    *           organization name, or null for the default
    * @param catalogName
    *           catalog name, or null for the default
    * @param itemName
    *           item you wish to lookup
    * 
    * @throws NoSuchElementException
    *            if you specified an org, catalog, or catalog item name that isn't present
    */
   CatalogItem findCatalogItemInOrgCatalogNamed(@Nullable String orgName, @Nullable String catalogName, String itemName);

   /**
    * A catalog can contain references to vApp templates and media images that have been uploaded to
    * any vDC in an organization. A vApp template or media image can be listed in at most one
    * catalog.
    * 
    * @param entity
    *           the reference to the vApp templates and media image
    * @param catalog
    *           URI of the catalog to add the resourceEntity from
    * @param name
    *           name of the entry in the catalog
    * 
    * @param options
    *           options such as description or properties
    * @return the new catalog item
    */
   CatalogItem addVAppTemplateOrMediaImageToCatalogAndNameItem(URI entity, URI catalog, String name, CatalogItemOptions... options);

   void deleteCatalogItem(URI href);
}
