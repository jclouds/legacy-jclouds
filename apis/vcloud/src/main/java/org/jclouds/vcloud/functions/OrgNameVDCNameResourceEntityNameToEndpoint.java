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

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.endpoints.VDC;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class OrgNameVDCNameResourceEntityNameToEndpoint extends OrgNameVDCNameResourceNameToEndpoint {
   @Inject
   public OrgNameVDCNameResourceEntityNameToEndpoint(
         Supplier<Map<String, Map<String, org.jclouds.vcloud.domain.VDC>>> orgVDCMap,
         @Org Supplier<ReferenceType> defaultOrg, @VDC Supplier<ReferenceType> defaultVDC) {
      super(orgVDCMap, defaultOrg, defaultVDC);
   }

   protected URI getEndpointOfResourceInVDC(Object org, Object vDC, Object resource,
         org.jclouds.vcloud.domain.VDC vDCObject) {
      ReferenceType resourceEntity = vDCObject.getResourceEntities().get(resource);
      if (resourceEntity == null)
         throw new NoSuchElementException("entity " + resource + " in vdc " + vDC + ", org " + org + " not found in "
               + vDCObject.getResourceEntities().keySet());
      return resourceEntity.getHref();
   }

}
