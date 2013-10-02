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
package org.jclouds.vcloud.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.endpoints.VDC;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class OrgNameAndVDCNameToEndpoint implements MapBinder {
   private final Supplier<Map<String, Org>> orgNameToVDCEndpoint;
   private final Supplier<ReferenceType> defaultOrg;
   private final Supplier<ReferenceType> defaultVDC;

   @Inject
   public OrgNameAndVDCNameToEndpoint(Supplier<Map<String, Org>> orgNameToVDCEndpoint,
         @org.jclouds.vcloud.endpoints.Org Supplier<ReferenceType> defaultOrg, @VDC Supplier<ReferenceType> defaultVDC) {
      this.orgNameToVDCEndpoint = orgNameToVDCEndpoint;
      this.defaultOrg = defaultOrg;
      this.defaultVDC = defaultVDC;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      Object org = postParams.get("orgName");
      Object vdc = postParams.get("vdcName");
      if (org == null && vdc == null)
         return (R) request.toBuilder().endpoint(defaultVDC.get().getHref()).build();
      else if (org == null)
         org = defaultOrg.get().getName();

      try {
         Map<String, ReferenceType> vdcs = checkNotNull(orgNameToVDCEndpoint.get().get(org)).getVDCs();
         URI endpoint = vdc == null ? Iterables.getLast(vdcs.values()).getHref() : vdcs.get(vdc).getHref();
         return (R) request.toBuilder().endpoint(endpoint).build();
      } catch (NullPointerException e) {
         throw new NoSuchElementException(org + "/" + vdc + " not found in " + orgNameToVDCEndpoint.get());
      }
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new IllegalStateException(getClass() + " needs parameters");
   }
}
