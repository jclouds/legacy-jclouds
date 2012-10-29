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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.Org;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class CatalogItemsInOrg implements Function<Org, Iterable<CatalogItem>> {

   private final Function<Org, Iterable<Catalog>> allCatalogsInOrg;

   private final Function<Catalog, Iterable<CatalogItem>> allCatalogItemsInCatalog;

   @Inject
   CatalogItemsInOrg(Function<Org, Iterable<Catalog>> allCatalogsInOrg,
            Function<Catalog, Iterable<CatalogItem>> allCatalogItemsInCatalog) {
      this.allCatalogsInOrg = allCatalogsInOrg;
      this.allCatalogItemsInCatalog = allCatalogItemsInCatalog;
   }

   @Override
   public Iterable<CatalogItem> apply(Org from) {
      return Iterables.concat(Iterables.transform(allCatalogsInOrg.apply(from),
               new Function<Catalog, Iterable<? extends CatalogItem>>() {
                  @Override
                  public Iterable<? extends CatalogItem> apply(Catalog from) {
                     return allCatalogItemsInCatalog.apply(from);
                  }

               }));
   }
}
