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
package org.jclouds.trmk.vcloud_0_8.compute.functions;

import static com.google.common.base.Predicates.notNull;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.trmk.vcloud_0_8.domain.CatalogItem;
import org.jclouds.trmk.vcloud_0_8.domain.Org;
import org.jclouds.trmk.vcloud_0_8.domain.VAppTemplate;
import org.jclouds.trmk.vcloud_0_8.functions.AllCatalogItemsInOrg;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * @author Adrian Cole
 */
@Singleton
public class ImagesInVCloudExpressOrg implements Function<Org, Iterable<? extends Image>> {

   private final AllCatalogItemsInOrg allCatalogItemsInOrg;
   private final Function<Iterable<? extends CatalogItem>, Iterable<? extends VAppTemplate>> vAppTemplatesForCatalogItems;
   private final Provider<ImageForVCloudExpressVAppTemplate> imageForVAppTemplateProvider;

   @Inject
   ImagesInVCloudExpressOrg(AllCatalogItemsInOrg allCatalogItemsInOrg,
            Provider<ImageForVCloudExpressVAppTemplate> imageForVAppTemplateProvider,
            Function<Iterable<? extends CatalogItem>, Iterable<? extends VAppTemplate>> vAppTemplatesForCatalogItems) {
      this.imageForVAppTemplateProvider = imageForVAppTemplateProvider;
      this.allCatalogItemsInOrg = allCatalogItemsInOrg;
      this.vAppTemplatesForCatalogItems = vAppTemplatesForCatalogItems;
   }

   @Override
   public Iterable<? extends Image> apply(Org from) {
      Iterable<? extends CatalogItem> catalogs = allCatalogItemsInOrg.apply(from);
      Iterable<? extends VAppTemplate> vAppTemplates = vAppTemplatesForCatalogItems.apply(catalogs);
      return FluentIterable.from(vAppTemplates)
                           .transform(imageForVAppTemplateProvider.get().withParent(from))
                           .filter(notNull());
   }

}
