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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.endpoints.Catalog;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class OrgNameAndCatalogNameToEndpoint implements Function<Object, URI> {
   private final Supplier<Map<String, Org>> orgMap;
   private final Supplier<ReferenceType> defaultOrg;
   private final Supplier<ReferenceType> defaultCatalog;

   @Inject
   public OrgNameAndCatalogNameToEndpoint(Supplier<Map<String, Org>> orgMap,
         @org.jclouds.vcloud.endpoints.Org Supplier<ReferenceType> defaultOrg,
         @Catalog Supplier<ReferenceType> defaultCatalog) {
      this.orgMap = orgMap;
      this.defaultOrg = defaultOrg;
      this.defaultCatalog = defaultCatalog;
   }

   @SuppressWarnings("unchecked")
   public URI apply(Object from) {
      Iterable<Object> orgCatalog = (Iterable<Object>) checkNotNull(from, "args");
      Object org = Iterables.get(orgCatalog, 0);
      Object catalog = Iterables.get(orgCatalog, 1);
      if (org == null && catalog == null)
         return defaultCatalog.get().getHref();
      else if (org == null)
         org = defaultOrg.get().getName();

      try {
         Map<String, ReferenceType> catalogs = checkNotNull(orgMap.get().get(org)).getCatalogs();
         return catalog == null ? Iterables.getLast(catalogs.values()).getHref() : catalogs.get(catalog).getHref();
      } catch (NullPointerException e) {
         throw new NoSuchElementException(org + "/" + catalog + " not found in " + orgMap.get());
      }
   }

}
