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

import static org.jclouds.vcloud.director.v1_5.predicates.ReferencePredicates.nameEquals;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.endpoints.Org;
import org.jclouds.vcloud.director.v1_5.endpoints.Vdc;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * 
 * @author danikov
 */
@Singleton
public class OrgNameVdcNameResourceEntityNameToEndpoint extends OrgNameVdcNameResourceNameToEndpoint {
   @Inject
   public OrgNameVdcNameResourceEntityNameToEndpoint(
         Supplier<Map<String, Map<String, org.jclouds.vcloud.director.v1_5.domain.Vdc>>> orgVdcMap,
         @Org Supplier<Reference> defaultOrg, @Vdc Supplier<Reference> defaultVdc) {
      super(orgVdcMap, defaultOrg, defaultVdc);
   }

   protected URI getEndpointOfResourceInVdc(Object org, Object Vdc, Object resource,
         org.jclouds.vcloud.director.v1_5.domain.Vdc VdcObject) {
      Reference resourceEntity = Iterables.find(VdcObject.getResourceEntities(), nameEquals((String)resource));
      if (resourceEntity == null)
         throw new NoSuchElementException("entity " + resource + " in Vdc " + Vdc + ", org " + org + " not found in "
               + VdcObject.getResourceEntities());
      return resourceEntity.getHref();
   }

}
