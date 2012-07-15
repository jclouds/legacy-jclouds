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
import static com.google.common.base.Preconditions.checkState;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;

import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.endpoints.Org;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
/**
 * 
 * @author danikov
 */
public abstract class OrgNameVdcNameResourceNameToEndpoint  implements Function<Object, URI>{

   protected final Supplier<Map<String, Map<String, org.jclouds.vcloud.director.v1_5.domain.Vdc>>> orgVdcMap;
   protected final Supplier<Reference> defaultOrg;
   protected final Supplier<Reference> defaultVdc;

   @Inject
   public OrgNameVdcNameResourceNameToEndpoint(
         Supplier<Map<String, Map<String, org.jclouds.vcloud.director.v1_5.domain.Vdc>>> orgVdcMap,
         @Org Supplier<Reference> defaultOrg, @org.jclouds.vcloud.director.v1_5.endpoints.Vdc Supplier<Reference> defaultVdc) {
      this.orgVdcMap = orgVdcMap;
      this.defaultOrg = defaultOrg;
      this.defaultVdc = defaultVdc;
   }

   @SuppressWarnings("unchecked")
   public URI apply(Object from) {
      Iterable<Object> orgVdc = (Iterable<Object>) checkNotNull(from, "args");
      Object org = Iterables.get(orgVdc, 0);
      Object Vdc = Iterables.get(orgVdc, 1);
      Object resource = Iterables.get(orgVdc, 2);
      if (org == null)
         org = defaultOrg.get().getName();
      if (Vdc == null)
         Vdc = defaultVdc.get().getName();
      Map<String, Map<String, org.jclouds.vcloud.director.v1_5.domain.Vdc>> orgToVdcs = orgVdcMap.get();
      checkState(orgToVdcs != null, "could not get map of org name to Vdcs!");
      Map<String, org.jclouds.vcloud.director.v1_5.domain.Vdc> Vdcs = orgToVdcs.get(org);
      if (Vdcs == null)
         throw new NoSuchElementException("org " + org + " not found in " + orgToVdcs.keySet());
      org.jclouds.vcloud.director.v1_5.domain.Vdc VdcObject = Vdcs.get(Vdc);
      if (VdcObject == null)
         throw new NoSuchElementException("Vdc " + Vdc + " in org " + org + " not found in " + Vdcs.keySet());
      return getEndpointOfResourceInVdc(org, Vdc, resource, VdcObject);
   }

   protected abstract URI getEndpointOfResourceInVdc(Object org, Object Vdc, Object resource, Vdc VdcObject);

}
