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

package org.jclouds.vcloud.config;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.get;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED;

import java.net.URI;
import java.util.Map;

import javax.inject.Named;

import org.jclouds.domain.Location;
import org.jclouds.http.RequiresHttp;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.vcloud.VCloudExpressAsyncClient;
import org.jclouds.vcloud.VCloudExpressClient;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.functions.VCloudExpressAllCatalogItemsInCatalog;
import org.jclouds.vcloud.functions.VCloudExpressAllCatalogsInOrganization;
import org.jclouds.vcloud.functions.VCloudExpressAllVDCsInOrganization;
import org.jclouds.vcloud.functions.VCloudExpressOrganizationsForNames;
import org.jclouds.vcloud.functions.VCloudExpressOrganizatonsForLocations;
import org.jclouds.vcloud.functions.VCloudExpressVAppTemplatesForCatalogItems;
import org.jclouds.vcloud.predicates.VCloudExpressTaskSuccess;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * Configures the VCloud authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public abstract class BaseVCloudExpressRestClientModule<S extends VCloudExpressClient, A extends VCloudExpressAsyncClient>
         extends CommonVCloudRestClientModule<S, A> {

   public BaseVCloudExpressRestClientModule(Class<S> syncClientType, Class<A> asyncClientType) {
      super(syncClientType, asyncClientType);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<Function<Catalog, Iterable<? extends CatalogItem>>>() {
      }).to(new TypeLiteral<VCloudExpressAllCatalogItemsInCatalog>() {
      });
      bind(new TypeLiteral<Function<Organization, Iterable<? extends Catalog>>>() {
      }).to(new TypeLiteral<VCloudExpressAllCatalogsInOrganization>() {
      });
      bind(new TypeLiteral<Function<Organization, Iterable<? extends VDC>>>() {
      }).to(new TypeLiteral<VCloudExpressAllVDCsInOrganization>() {
      });
      bind(new TypeLiteral<Function<Iterable<String>, Iterable<? extends Organization>>>() {
      }).to(new TypeLiteral<VCloudExpressOrganizationsForNames>() {
      });
      bind(new TypeLiteral<Function<Iterable<? extends Location>, Iterable<? extends Organization>>>() {
      }).to(new TypeLiteral<VCloudExpressOrganizatonsForLocations>() {
      });
      bind(new TypeLiteral<Function<Iterable<? extends CatalogItem>, Iterable<? extends VAppTemplate>>>() {
      }).to(new TypeLiteral<VCloudExpressVAppTemplatesForCatalogItems>() {
      });
   }

   @Override
   protected Organization provideOrganization(VCloudExpressClient discovery) {
      if (authException.get() != null)
         throw authException.get();
      try {
         return discovery.findOrganizationNamed(null);
      } catch (AuthorizationException e) {
         authException.set(e);
         throw e;
      }
   }

   @Override
   protected URI provideDefaultNetwork(VCloudExpressClient client) {
      if (authException.get() != null)
         throw authException.get();
      try {
         org.jclouds.vcloud.domain.VDC vDC = client.findVDCInOrgNamed(null, null);
         Map<String, NamedResource> networks = vDC.getAvailableNetworks();
         checkState(networks.size() > 0, "No networks present in vDC: " + vDC.getName());
         return get(networks.values(), 0).getId();
      } catch (AuthorizationException e) {
         authException.set(e);
         throw e;
      }
   }

   @Override
   protected Predicate<URI> successTester(Injector injector,
            @Named(PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED) long completed) {
      return new RetryablePredicate<URI>(injector.getInstance(VCloudExpressTaskSuccess.class), completed);
   }
}
