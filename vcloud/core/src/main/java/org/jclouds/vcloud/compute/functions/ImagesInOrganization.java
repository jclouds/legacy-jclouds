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

import org.jclouds.compute.domain.Image;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.functions.AllCatalogItemsInOrganization;
import org.jclouds.vcloud.functions.VAppTemplatesForCatalogItems;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class ImagesInOrganization implements Function<Organization, Iterable<? extends Image>> {

   private final AllCatalogItemsInOrganization allCatalogItemsInOrganization;
   private final VAppTemplatesForCatalogItems vAppTemplatesForCatalogItems;
   private final Provider<ImageForVAppTemplate> imageForVAppTemplateProvider;

   @Inject
   ImagesInOrganization(AllCatalogItemsInOrganization allCatalogItemsInOrganization,
            Provider<ImageForVAppTemplate> imageForVAppTemplateProvider,
            VAppTemplatesForCatalogItems vAppTemplatesForCatalogItems) {
      this.imageForVAppTemplateProvider = imageForVAppTemplateProvider;
      this.allCatalogItemsInOrganization = allCatalogItemsInOrganization;
      this.vAppTemplatesForCatalogItems = vAppTemplatesForCatalogItems;
   }

   @Override
   public Iterable<? extends Image> apply(Organization from) {
      Iterable<? extends CatalogItem> catalogs = allCatalogItemsInOrganization.apply(from);
      Iterable<? extends VAppTemplate> vAppTemplates = vAppTemplatesForCatalogItems.apply(catalogs);
      return Iterables.transform(vAppTemplates, imageForVAppTemplateProvider.get().withParent(from));
   }

}