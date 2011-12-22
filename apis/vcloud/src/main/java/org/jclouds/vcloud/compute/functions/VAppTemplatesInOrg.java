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

import static org.jclouds.vcloud.compute.util.VCloudComputeUtils.getCredentialsFrom;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
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
public class VAppTemplatesInOrg implements Function<Org, Iterable<VAppTemplate>> {

   private final AllCatalogItemsInOrg allCatalogItemsInOrg;
   private final Function<Iterable<CatalogItem>, Iterable<VAppTemplate>> vAppTemplatesForCatalogItems;
   private final Map<String, Credentials> credentialStore;

   @Inject
   VAppTemplatesInOrg(AllCatalogItemsInOrg allCatalogItemsInOrg,
            Function<Iterable<CatalogItem>, Iterable<VAppTemplate>> vAppTemplatesForCatalogItems,
            Map<String, Credentials> credentialStore) {
      this.allCatalogItemsInOrg = allCatalogItemsInOrg;
      this.vAppTemplatesForCatalogItems = vAppTemplatesForCatalogItems;
      this.credentialStore = credentialStore;
   }

   @Override
   public Iterable<VAppTemplate> apply(Org from) {
      Iterable<CatalogItem> catalogs = allCatalogItemsInOrg.apply(from);
      Iterable<VAppTemplate> vAppTemplates = vAppTemplatesForCatalogItems.apply(catalogs);
      return Iterables.transform(Iterables.filter(vAppTemplates, Predicates.notNull()),
               new Function<VAppTemplate, VAppTemplate>() {

                  @Override
                  public VAppTemplate apply(VAppTemplate arg0) {
                     LoginCredentials creds = getCredentialsFrom(arg0);
                     if (creds == null)
                        credentialStore.remove("image#" + arg0.getHref().toASCIIString());
                     else
                        credentialStore.put("image#" + arg0.getHref().toASCIIString(), creds);
                     return arg0;
                  }

               });
   }

}