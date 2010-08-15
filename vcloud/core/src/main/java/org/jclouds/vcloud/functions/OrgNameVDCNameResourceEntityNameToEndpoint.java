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

package org.jclouds.vcloud.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.endpoints.VDC;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class OrgNameVDCNameResourceEntityNameToEndpoint implements Function<Object, URI> {
   private final Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>>> orgVDCMap;
   private final String defaultOrg;
   private final String defaultVDC;

   @Inject
   public OrgNameVDCNameResourceEntityNameToEndpoint(
         Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>>> orgVDCMap, @Org String defaultOrg,
         @VDC String defaultVDC) {
      this.orgVDCMap = orgVDCMap;
      this.defaultOrg = defaultOrg;
      this.defaultVDC = defaultVDC;
   }

   @SuppressWarnings("unchecked")
   public URI apply(Object from) {
      Iterable<Object> orgVDC = (Iterable<Object>) checkNotNull(from, "args");
      Object org = Iterables.get(orgVDC, 0);
      Object vDC = Iterables.get(orgVDC, 1);
      Object entityName = Iterables.get(orgVDC, 2);
      if (org == null)
         org = defaultOrg;
      if (vDC == null)
         vDC = defaultVDC;
      try {
         Map<String, ? extends org.jclouds.vcloud.domain.VDC> vDCs = checkNotNull(orgVDCMap.get().get(org));
         return vDCs.get(vDC).getResourceEntities().get(entityName).getId();
      } catch (NullPointerException e) {
         throw new NoSuchElementException(org + "/" + vDC + "/" + entityName + " not found in " + orgVDCMap.get());
      }
   }

}