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
package org.jclouds.vcloud.config;

import java.net.URI;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.endpoints.Org;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class DefaultVCloudReferencesModule extends AbstractModule {
   
   @Override
   protected void configure() {
      bind(new TypeLiteral<Supplier<ReferenceType>>() {
      }).annotatedWith(Org.class).to(DefaultOrgForUser.class);
   }

   @Provides
   @Singleton
   @org.jclouds.vcloud.endpoints.Org
   protected Predicate<ReferenceType> provideDefaultOrgSelector(Injector i) {
      return Predicates.alwaysTrue();
   }

   @Provides
   @org.jclouds.vcloud.endpoints.TasksList
   @Singleton
   protected Supplier<ReferenceType> provideDefaultTasksList(DefaultTasksListForOrg defaultTasksListURIForOrg,
            @org.jclouds.vcloud.endpoints.Org Supplier<ReferenceType> defaultOrg) {
      return Suppliers.compose(defaultTasksListURIForOrg, defaultOrg);
   }

   @Provides
   @org.jclouds.vcloud.endpoints.Catalog
   @Singleton
   protected Supplier<ReferenceType> provideDefaultCatalog(DefaultCatalogForOrg defaultCatalogURIForOrg,
            @org.jclouds.vcloud.endpoints.Org Supplier<ReferenceType> defaultOrg) {
      return Suppliers.compose(defaultCatalogURIForOrg, defaultOrg);
   }

   @Provides
   @Singleton
   @org.jclouds.vcloud.endpoints.Catalog
   protected Predicate<ReferenceType> provideDefaultCatalogSelector(Injector i) {
      return i.getInstance(WriteableCatalog.class);
   }

   @Provides
   @Singleton
   protected Supplier<Map<URI, org.jclouds.vcloud.domain.Catalog>> provideCatalogsById(
            Supplier<Map<String, Map<String, org.jclouds.vcloud.domain.Catalog>>> supplier) {
      return Suppliers
               .compose(
                        new Function<Map<String, Map<String, org.jclouds.vcloud.domain.Catalog>>, Map<URI, org.jclouds.vcloud.domain.Catalog>>() {

                           @Override
                           public Map<URI, Catalog> apply(Map<String, Map<String, Catalog>> arg0) {
                              Builder<URI, Catalog> builder = ImmutableMap.builder();
                              for (Map<String, Catalog> v1 : arg0.values()) {
                                 for (Catalog v2 : v1.values()) {
                                    builder.put(v2.getHref(), v2);
                                 }
                              }
                              return builder.build();
                           }

                        }, supplier);
   }

   @Singleton
   public static class WriteableCatalog implements Predicate<ReferenceType> {

      @Resource
      protected Logger logger = Logger.NULL;

      private final Supplier<Map<URI, org.jclouds.vcloud.domain.Catalog>> catalogsByIdSupplier;

      @Inject
      public WriteableCatalog(Supplier<Map<URI, org.jclouds.vcloud.domain.Catalog>> catalogsByIdSupplier) {
         this.catalogsByIdSupplier = catalogsByIdSupplier;
      }

      @Override
      public boolean apply(ReferenceType arg0) {
         // TODO: this is inefficient, calculating the index each time, but
         // shouldn't be added to constructor as the supplier is an expensive
         // call
         Map<URI, Catalog> index = catalogsByIdSupplier.get();
         Catalog catalog = index.get(arg0.getHref());
         if (catalog == null) {
            if (logger.isTraceEnabled())
               logger.trace("didn't find catalog %s", arg0);
            return false;
         } else
            return !catalog.isReadOnly();
      }
   }

   @Provides
   @org.jclouds.vcloud.endpoints.VDC
   @Singleton
   protected Supplier<ReferenceType> provideDefaultVDC(DefaultVDCForOrg defaultVDCURIForOrg,
            @org.jclouds.vcloud.endpoints.Org Supplier<ReferenceType> defaultOrg) {
      return Suppliers.compose(defaultVDCURIForOrg, defaultOrg);
   }

   @Provides
   @Singleton
   @org.jclouds.vcloud.endpoints.VDC
   protected Predicate<ReferenceType> provideDefaultVDCSelector(Injector i) {
      return Predicates.alwaysTrue();
   }

   @Provides
   @org.jclouds.vcloud.endpoints.Network
   @Singleton
   protected Supplier<ReferenceType> provideDefaultNetwork(DefaultNetworkForVDC defaultNetworkURIForVDC,
            @org.jclouds.vcloud.endpoints.VDC Supplier<ReferenceType> defaultVDC) {
      return Suppliers.compose(defaultNetworkURIForVDC, defaultVDC);
   }

   @Provides
   @Singleton
   @org.jclouds.vcloud.endpoints.Network
   protected Predicate<ReferenceType> provideDefaultNetworkSelector(Injector i) {
      return Predicates.alwaysTrue();
   }
}
