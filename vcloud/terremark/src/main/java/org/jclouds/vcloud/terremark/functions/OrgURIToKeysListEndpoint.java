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

package org.jclouds.vcloud.terremark.functions;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.terremark.domain.TerremarkOrganization;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class OrgURIToKeysListEndpoint implements Function<Object, URI> {
   private final Supplier<Map<String, ? extends Organization>> orgMap;
   private final URI defaultOrg;

   @Inject
   public OrgURIToKeysListEndpoint(Supplier<Map<String, ? extends Organization>> orgMap, @Org URI defaultUri) {
      this.orgMap = orgMap;
      this.defaultOrg = defaultUri;
   }

   public URI apply(Object from) {
      Map<URI, ? extends Organization> uriToOrg = Maps.uniqueIndex(orgMap.get().values(),
            new Function<Organization, URI>() {

               @Override
               public URI apply(Organization from) {
                  return from.getId();
               }

            });
      try {
         return TerremarkOrganization.class.cast(uriToOrg.get(from == null ? defaultOrg : from)).getKeysList()
               .getId();
      } catch (NullPointerException e) {
         throw new ResourceNotFoundException("org " + from + " not found in " + uriToOrg);
      }
   }

}