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
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.terremark.endpoints.KeysList;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class OrgNameToKeysListEndpoint implements Function<Object, URI> {
   private final Supplier<Map<String, NamedResource>> orgNameToEndpoint;
   private final String defaultOrg;

   @Inject
   public OrgNameToKeysListEndpoint(@KeysList Supplier<Map<String, NamedResource>> orgNameToEndpoint,
         @Org String defaultUri) {
      this.orgNameToEndpoint = orgNameToEndpoint;
      this.defaultOrg = defaultUri;
   }

   public URI apply(Object from) {
      try {
         return orgNameToEndpoint.get().get(from == null ? defaultOrg : from).getLocation();
      } catch (NullPointerException e) {
         throw new ResourceNotFoundException("org " + from + " not found in " + orgNameToEndpoint.get().keySet());
      }
   }

}