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

package org.jclouds.vcloud.compute.functions;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Size;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.functions.AllCatalogItemsInOrg;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class SizesInOrg implements Function<Org, Iterable<? extends Size>> {

   private final AllCatalogItemsInOrg allCatalogItemsInOrg;
   private final Function<Iterable<? extends CatalogItem>, Iterable<? extends VAppTemplate>> vAppTemplatesForCatalogItems;
   private final Provider<SizeForVAppTemplate> sizeForVAppTemplateProvider;

   @Inject
   SizesInOrg(AllCatalogItemsInOrg allCatalogItemsInOrg,
            Provider<SizeForVAppTemplate> sizeForVAppTemplateProvider,
            Function<Iterable<? extends CatalogItem>, Iterable<? extends VAppTemplate>> vAppTemplatesForCatalogItems) {
      this.sizeForVAppTemplateProvider = sizeForVAppTemplateProvider;
      this.allCatalogItemsInOrg = allCatalogItemsInOrg;
      this.vAppTemplatesForCatalogItems = vAppTemplatesForCatalogItems;
   }

   @Override
   public Iterable<? extends Size> apply(Org from) {
      Iterable<? extends CatalogItem> catalogs = allCatalogItemsInOrg.apply(from);
      Iterable<? extends VAppTemplate> vAppTemplates = vAppTemplatesForCatalogItems.apply(catalogs);
      return Iterables.transform(Iterables.filter(vAppTemplates, Predicates.notNull()), sizeForVAppTemplateProvider.get().withParent(from));
   }

}