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
package org.jclouds.trmk.vcloud_0_8.functions;

import java.net.URI;
import java.util.Map;

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.trmk.vcloud_0_8.domain.Org;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 */
public abstract class OrgURIToEndpoint implements Function<Object, URI> {

   protected final Supplier<Map<String, ? extends Org>> orgMap;
   protected final Supplier<ReferenceType> defaultOrg;

   public OrgURIToEndpoint(Supplier<Map<String, ? extends Org>> orgMap,
         @org.jclouds.trmk.vcloud_0_8.endpoints.Org Supplier<ReferenceType> defaultUri) {
      this.orgMap = orgMap;
      this.defaultOrg = defaultUri;
   }

   public URI apply(Object from) {
      Map<URI, ? extends Org> uriToOrg = Maps.uniqueIndex(orgMap.get().values(), new Function<Org, URI>() {

         @Override
         public URI apply(Org from) {
            return from.getHref();
         }

      });
      try {
         Org org = uriToOrg.get(from == null ? defaultOrg.get().getHref() : from);
         return getUriFromOrg(org);
      } catch (NullPointerException e) {
         throw new ResourceNotFoundException("org " + from + " not found in: " + uriToOrg, e);
      }
   }

   protected abstract URI getUriFromOrg(Org org);

}
