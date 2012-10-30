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
package org.jclouds.vcloud.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.endpoints.Catalog;
import org.jclouds.vcloud.endpoints.Org;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class OrgNameCatalogNameVAppTemplateNameToEndpoint implements Function<Object, URI> {
   private final Supplier<Map<String, Map<String, Map<String, CatalogItem>>>> orgCatalogItemMap;
   private final Supplier<ReferenceType> defaultOrg;
   private final Supplier<ReferenceType> defaultCatalog;

   @Inject
   public OrgNameCatalogNameVAppTemplateNameToEndpoint(
            Supplier<Map<String, Map<String, Map<String, CatalogItem>>>> orgCatalogItemMap,
            @Org Supplier<ReferenceType> defaultOrg, @Catalog Supplier<ReferenceType> defaultCatalog) {
      this.orgCatalogItemMap = orgCatalogItemMap;
      this.defaultOrg = defaultOrg;
      this.defaultCatalog = defaultCatalog;
   }

   @SuppressWarnings("unchecked")
   public URI apply(Object from) {
      Iterable<Object> orgCatalog = (Iterable<Object>) checkNotNull(from, "args");
      Object org = Iterables.get(orgCatalog, 0);
      Object catalog = Iterables.get(orgCatalog, 1);
      Object catalogItem = Iterables.get(orgCatalog, 2);
      if (org == null)
         org = defaultOrg.get().getName();
      if (catalog == null)
         catalog = defaultCatalog.get().getName();
      Map<String, Map<String, Map<String, CatalogItem>>> orgCatalogItemMap = this.orgCatalogItemMap.get();

      if (!orgCatalogItemMap.containsKey(org))
         throw new NoSuchElementException("org: " + org + " not found in " + orgCatalogItemMap.keySet());
      Map<String, Map<String,  CatalogItem>> catalogs = orgCatalogItemMap.get(org);

      if (!catalogs.containsKey(catalog))
         throw new NoSuchElementException("catalog: " + org + "/" + catalog + " not found in " + catalogs.keySet());
      Map<String, CatalogItem> catalogMap = catalogs.get(catalog);

      if (!catalogMap.containsKey(catalogItem))
         throw new NoSuchElementException("item: " + org + "/" + catalog + "/" + catalogItem + " not found in "
                  + catalogMap.keySet());
      CatalogItem item = catalogMap.get(catalogItem);

      return checkNotNull(item.getEntity(), "item: " + org + "/" + catalog + "/" + catalogItem + " has no entity")
               .getHref();
   }

}
