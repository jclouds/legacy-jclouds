/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.trmk.vcloud_0_8.domain.CatalogItem;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.endpoints.Catalog;
import org.jclouds.trmk.vcloud_0_8.endpoints.Org;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class OrgNameCatalogNameVAppTemplateNameToEndpoint implements MapBinder {
   private final Supplier<Map<String, Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.CatalogItem>>>> orgCatalogItemMap;
   private final Supplier<ReferenceType> defaultOrg;
   private final Supplier<ReferenceType> defaultCatalog;

   @Inject
   public OrgNameCatalogNameVAppTemplateNameToEndpoint(
            Supplier<Map<String, Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.CatalogItem>>>> orgCatalogItemMap,
            @Org Supplier<ReferenceType> defaultOrg, @Catalog Supplier<ReferenceType> defaultCatalog) {
      this.orgCatalogItemMap = orgCatalogItemMap;
      this.defaultOrg = defaultOrg;
      this.defaultCatalog = defaultCatalog;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      Object org = postParams.get("orgName");
      Object catalog = postParams.get("catalogName");
      Object catalogItem = postParams.get("itemName");
      if (org == null)
         org = defaultOrg.get().getName();
      if (catalog == null)
         catalog = defaultCatalog.get().getName();
      Map<String, Map<String, Map<String, ? extends CatalogItem>>> orgCatalogItemMap = this.orgCatalogItemMap.get();

      if (!orgCatalogItemMap.containsKey(org))
         throw new NoSuchElementException("org: " + org + " not found in " + orgCatalogItemMap.keySet());
      Map<String, Map<String, ? extends CatalogItem>> catalogs = orgCatalogItemMap.get(org);

      if (!catalogs.containsKey(catalog))
         throw new NoSuchElementException("catalog: " + org + "/" + catalog + " not found in " + catalogs.keySet());
      Map<String, ? extends CatalogItem> catalogMap = catalogs.get(catalog);

      if (!catalogMap.containsKey(catalogItem))
         throw new NoSuchElementException("item: " + org + "/" + catalog + "/" + catalogItem + " not found in "
                  + catalogMap.keySet());
      CatalogItem item = catalogMap.get(catalogItem);

      URI endpoint = checkNotNull(item.getEntity(),
            "item: " + org + "/" + catalog + "/" + catalogItem + " has no entity").getHref();
      return (R) request.toBuilder().endpoint(endpoint).build();
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new IllegalStateException(getClass() + " needs parameters");
   }
}
