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
package org.jclouds.trmk.vcloud_0_8.binders;

import static com.google.common.base.Preconditions.checkState;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.VDC;
import org.jclouds.trmk.vcloud_0_8.endpoints.Org;

import com.google.common.base.Supplier;
/**
 * 
 * @author Adrian Cole
 */
public abstract class OrgNameVDCNameResourceNameToEndpoint implements MapBinder {

   protected final Supplier<Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>>> orgVDCMap;
   protected final Supplier<ReferenceType> defaultOrg;
   protected final Supplier<ReferenceType> defaultVDC;

   @Inject
   public OrgNameVDCNameResourceNameToEndpoint(
         Supplier<Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>>> orgVDCMap,
         @Org Supplier<ReferenceType> defaultOrg, @org.jclouds.trmk.vcloud_0_8.endpoints.VDC Supplier<ReferenceType> defaultVDC) {
      this.orgVDCMap = orgVDCMap;
      this.defaultOrg = defaultOrg;
      this.defaultVDC = defaultVDC;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      Object org = postParams.get("orgName");
      Object vDC = postParams.get("vdcName");
      Object resource = postParams.get("resourceName");
      if (org == null)
         org = defaultOrg.get().getName();
      if (vDC == null)
         vDC = defaultVDC.get().getName();
      Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>> orgToVDCs = orgVDCMap.get();
      checkState(orgToVDCs != null, "could not get map of org name to vdcs!");
      Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC> vDCs = orgToVDCs.get(org);
      if (vDCs == null)
         throw new NoSuchElementException("org " + org + " not found in " + orgToVDCs.keySet());
      org.jclouds.trmk.vcloud_0_8.domain.VDC vDCObject = vDCs.get(vDC);
      if (vDCObject == null)
         throw new NoSuchElementException("vdc " + vDC + " in org " + org + " not found in " + vDCs.keySet());
      URI endpoint = getEndpointOfResourceInVDC(org, vDC, resource, vDCObject);
      return (R) request.toBuilder().endpoint(endpoint).build();
   }

   protected abstract URI getEndpointOfResourceInVDC(Object org, Object vDC, Object resource, VDC vDCObject);

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new IllegalStateException(getClass() + " needs parameters");
   }
}
