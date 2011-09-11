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
package org.jclouds.vcloud.compute.functions;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
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
public class HardwareInOrg implements Function<Org, Iterable<? extends Hardware>> {

   private final Function<Org, Iterable<? extends CatalogItem>> allCatalogItemsInOrg;
   private final Function<Iterable<? extends CatalogItem>, Iterable<? extends VAppTemplate>> vAppTemplatesForCatalogItems;
   private final Provider<HardwareForVAppTemplate> sizeForVAppTemplateProvider;

   @Inject
   HardwareInOrg(AllCatalogItemsInOrg allCatalogItemsInOrg,
            Provider<HardwareForVAppTemplate> sizeForVAppTemplateProvider,
            Function<Iterable<? extends CatalogItem>, Iterable<? extends VAppTemplate>> vAppTemplatesForCatalogItems) {
      this.sizeForVAppTemplateProvider = sizeForVAppTemplateProvider;
      this.allCatalogItemsInOrg = allCatalogItemsInOrg;
      this.vAppTemplatesForCatalogItems = vAppTemplatesForCatalogItems;
   }

   @Override
   public Iterable<? extends Hardware> apply(Org from) {
      Iterable<? extends CatalogItem> catalogs = allCatalogItemsInOrg.apply(from);
      Iterable<? extends VAppTemplate> vAppTemplates = vAppTemplatesForCatalogItems.apply(catalogs);
      return Iterables.transform(Iterables.filter(vAppTemplates, Predicates.notNull()), sizeForVAppTemplateProvider.get().withParent(from));
   }

}