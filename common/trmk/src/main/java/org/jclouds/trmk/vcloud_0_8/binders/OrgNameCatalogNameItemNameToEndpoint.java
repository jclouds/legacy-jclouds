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

import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.endpoints.Catalog;
import org.jclouds.trmk.vcloud_0_8.endpoints.Org;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class OrgNameCatalogNameItemNameToEndpoint implements MapBinder {
   private final Supplier<Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog>>> orgCatalogMap;
   private final Supplier<ReferenceType> defaultOrg;
   private final Supplier<ReferenceType> defaultCatalog;

   @Inject
   public OrgNameCatalogNameItemNameToEndpoint(
         Supplier<Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog>>> orgCatalogMap,
         @Org Supplier<ReferenceType> defaultOrg, @Catalog Supplier<ReferenceType> defaultCatalog) {
      this.orgCatalogMap = orgCatalogMap;
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
      try {
         Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog> catalogs = checkNotNull(orgCatalogMap.get().get(org));
         return (R) request.toBuilder().endpoint(catalogs.get(catalog).get(catalogItem).getHref()).build();
      } catch (NullPointerException e) {
         throw new NoSuchElementException(org + "/" + catalog + "/" + catalogItem + " not found in "
               + orgCatalogMap.get());
      }
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new IllegalStateException(getClass() + " needs parameters");
   }
}
