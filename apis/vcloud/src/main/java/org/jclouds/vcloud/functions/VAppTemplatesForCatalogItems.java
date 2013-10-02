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
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudApi;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.VAppTemplate;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * @author Adrian Cole
 */
@Singleton
public class VAppTemplatesForCatalogItems implements Function<Iterable<CatalogItem>, Iterable<VAppTemplate>> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   private Logger logger = Logger.NULL;
   private final VCloudApi aclient;

   @Inject
   VAppTemplatesForCatalogItems(VCloudApi aclient) {
      this.aclient = aclient;
   }

   @Override
   public Iterable<VAppTemplate> apply(Iterable<CatalogItem> from) {
      return filter(transform(filter(from, new Predicate<CatalogItem>() {
         public boolean apply(CatalogItem input) {
            return input.getEntity().getType().equals(VCloudMediaType.VAPPTEMPLATE_XML);
         }
      }), new Function<CatalogItem, VAppTemplate>() {
         public VAppTemplate apply(CatalogItem from) {
            return aclient.getVAppTemplateApi().getVAppTemplate(from.getEntity().getHref());
         }
      }), Predicates.notNull());
   }

}
