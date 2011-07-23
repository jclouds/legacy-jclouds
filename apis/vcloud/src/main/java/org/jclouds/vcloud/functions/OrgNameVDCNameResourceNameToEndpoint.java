/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import static com.google.common.base.Preconditions.checkState;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;

import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.endpoints.Org;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
/**
 * 
 * @author Adrian Cole
 */
public abstract class OrgNameVDCNameResourceNameToEndpoint  implements Function<Object, URI>{

   protected final Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>>> orgVDCMap;
   protected final ReferenceType defaultOrg;
   protected final ReferenceType defaultVDC;

   @Inject
   public OrgNameVDCNameResourceNameToEndpoint(
         Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>>> orgVDCMap,
         @Org ReferenceType defaultOrg, @org.jclouds.vcloud.endpoints.VDC ReferenceType defaultVDC) {
      this.orgVDCMap = orgVDCMap;
      this.defaultOrg = defaultOrg;
      this.defaultVDC = defaultVDC;
   }

   @SuppressWarnings("unchecked")
   public URI apply(Object from) {
      Iterable<Object> orgVDC = (Iterable<Object>) checkNotNull(from, "args");
      Object org = Iterables.get(orgVDC, 0);
      Object vDC = Iterables.get(orgVDC, 1);
      Object resource = Iterables.get(orgVDC, 2);
      if (org == null)
         org = defaultOrg.getName();
      if (vDC == null)
         vDC = defaultVDC.getName();
      Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>> orgToVDCs = orgVDCMap.get();
      checkState(orgToVDCs != null, "could not get map of org name to vdcs!");
      Map<String, ? extends org.jclouds.vcloud.domain.VDC> vDCs = orgToVDCs.get(org);
      if (vDCs == null)
         throw new NoSuchElementException("org " + org + " not found in " + orgToVDCs.keySet());
      org.jclouds.vcloud.domain.VDC vDCObject = vDCs.get(vDC);
      if (vDCObject == null)
         throw new NoSuchElementException("vdc " + vDC + " in org " + org + " not found in " + vDCs.keySet());
      return getEndpointOfResourceInVDC(org, vDC, resource, vDCObject);
   }

   protected abstract URI getEndpointOfResourceInVDC(Object org, Object vDC, Object resource, VDC vDCObject);

}