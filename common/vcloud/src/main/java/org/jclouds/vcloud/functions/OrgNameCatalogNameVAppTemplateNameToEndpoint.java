/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.vcloud.domain.CatalogItem;
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
   private final Supplier<Map<String, Map<String, Map<String, ? extends org.jclouds.vcloud.domain.CatalogItem>>>> orgCatalogItemMap;
   private final String defaultOrg;
   private final String defaultCatalog;

   @Inject
   public OrgNameCatalogNameVAppTemplateNameToEndpoint(
         Supplier<Map<String, Map<String, Map<String, ? extends org.jclouds.vcloud.domain.CatalogItem>>>> orgCatalogItemMap,
         @Org String defaultOrg, @Catalog String defaultCatalog) {
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
         org = defaultOrg;
      if (catalog == null)
         catalog = defaultCatalog;
      try {
         Map<String, Map<String, ? extends CatalogItem>> catalogs = checkNotNull(orgCatalogItemMap.get().get(org));
         return catalogs.get(catalog).get(catalogItem).getEntity().getHref();
      } catch (NullPointerException e) {
         throw new NoSuchElementException(org + "/" + catalog + "/" + catalogItem + " not found in "
               + orgCatalogItemMap.get());
      }
   }

}