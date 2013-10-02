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
package org.jclouds.vcloud.functions;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudApi;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.ReferenceType;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * @author Adrian Cole
 */
@Singleton
public class CatalogItemsInCatalog implements Function<Catalog, Iterable<CatalogItem>> {
   @Resource
   public Logger logger = Logger.NULL;

   private final VCloudApi aclient;

   @Inject
   CatalogItemsInCatalog(VCloudApi aclient) {
      this.aclient = aclient;
   }

   @Override
   public Iterable<CatalogItem> apply(Catalog from) {
      return transform(filter(from.values(), new Predicate<ReferenceType>() {
         public boolean apply(ReferenceType input) {
            return input.getType().equals(VCloudMediaType.CATALOGITEM_XML);
         }
      }), new Function<ReferenceType, CatalogItem>() {
         public CatalogItem apply(ReferenceType from) {
            return aclient.getCatalogApi().getCatalogItem(from.getHref());
         }
      });
   }

}
