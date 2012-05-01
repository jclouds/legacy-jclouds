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
package org.jclouds.vcloud.director.v1_5.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.director.v1_5.predicates.ReferencePredicates.nameEquals;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.org.AdminOrg;
import org.jclouds.vcloud.director.v1_5.endpoints.Vdc;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * 
 * @author danikov
 */
@Singleton
public class OrgNameAndVdcNameToEndpoint implements Function<Object, URI> {
   private final Supplier<Map<String, AdminOrg>> orgNameToVdcEndpoint;
   private final Supplier<Reference> defaultOrg;
   private final Supplier<Reference> defaultVdc;

   @Inject
   public OrgNameAndVdcNameToEndpoint(Supplier<Map<String, AdminOrg>> orgNameToVDCEndpoint,
         @org.jclouds.vcloud.director.v1_5.endpoints.Org Supplier<Reference> defaultOrg, @Vdc Supplier<Reference> defaultVdc) {
      this.orgNameToVdcEndpoint = orgNameToVDCEndpoint;
      this.defaultOrg = defaultOrg;
      this.defaultVdc = defaultVdc;
   }

   @SuppressWarnings("unchecked")
   public URI apply(Object from) {
      Iterable<Object> orgVdc = (Iterable<Object>) checkNotNull(from, "args");
      Object org = Iterables.get(orgVdc, 0);
      Object vdc = Iterables.get(orgVdc, 1);
      if (org == null && vdc == null)
         return defaultVdc.get().getHref();
      else if (org == null)
         org = defaultOrg.get().getName();

      try {
        Set<Reference> vdcs = checkNotNull(orgNameToVdcEndpoint.get().get(org)).getVdcs();
         return vdc == null ? Iterables.getLast(vdcs).getHref() : 
            Iterables.find(vdcs, nameEquals((String)vdc)).getHref();
      } catch (NullPointerException e) {
         throw new NoSuchElementException(org + "/" + vdc + " not found in " + orgNameToVdcEndpoint.get());
      }
   }

}