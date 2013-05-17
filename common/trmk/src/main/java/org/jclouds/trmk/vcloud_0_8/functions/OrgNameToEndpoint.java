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
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.endpoints.Org;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class OrgNameToEndpoint implements Function<Object, URI> {
   private final Supplier<Map<String, ReferenceType>> orgNameToEndpointSupplier;
   private final Supplier<ReferenceType> defaultOrg;

   @Inject
   public OrgNameToEndpoint(@Org Supplier<Map<String, ReferenceType>> orgNameToEndpointSupplier,
         @Org Supplier<ReferenceType> defaultOrg) {
      this.orgNameToEndpointSupplier = orgNameToEndpointSupplier;
      this.defaultOrg = defaultOrg;
   }

   public URI apply(Object from) {
      try {
         Map<String, ReferenceType> orgNameToEndpoint = orgNameToEndpointSupplier.get();
         return from == null ? defaultOrg.get().getHref() : orgNameToEndpoint.get(from).getHref();
      } catch (NullPointerException e) {
         throw new NoSuchElementException("org " + from + " not found in " + orgNameToEndpointSupplier.get().keySet());
      }
   }

}
