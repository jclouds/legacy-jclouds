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

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.filter;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.util.Iterables2;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.VAppTemplate;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Singleton
public class VAppTemplatesInOrg implements Function<Org, Iterable<VAppTemplate>> {

   private final Function<Org, Iterable<CatalogItem>> allCatalogItemsInOrg;
   private final Function<Iterable<CatalogItem>, Iterable<VAppTemplate>> vAppTemplatesForCatalogItems;

   @Inject
   VAppTemplatesInOrg(Function<Org, Iterable<CatalogItem>> allCatalogItemsInOrg,
            Function<Iterable<CatalogItem>, Iterable<VAppTemplate>> vAppTemplatesForCatalogItems) {
      this.allCatalogItemsInOrg = allCatalogItemsInOrg;
      this.vAppTemplatesForCatalogItems = vAppTemplatesForCatalogItems;
   }

   @Override
   public Iterable<VAppTemplate> apply(Org from) {
      Iterable<CatalogItem> catalogs = allCatalogItemsInOrg.apply(from);
      Iterable<VAppTemplate> vAppTemplates = Iterables2.concreteCopy(vAppTemplatesForCatalogItems.apply(catalogs));
      return filter(vAppTemplates, and(notNull(), new Predicate<VAppTemplate>(){

         //TODO: test this
         @Override
         public boolean apply(VAppTemplate input) {
            if (input == null)
               return false;
            return ImmutableSet.of(Status.RESOLVED, Status.OFF).contains(input.getStatus());
         }
         
      }));
   }

}