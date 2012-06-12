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
package org.jclouds.openstack.nova.v2_0.functions;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.openstack.nova.v2_0.domain.Extension;

import com.google.common.base.Function;

@Singleton
public class ExtensionToNameSpace implements Function<Extension, URI> {
   private final Provider<UriBuilder> uriBuilders;

   @Inject
   public ExtensionToNameSpace(Provider<UriBuilder> uriBuilders) {
      this.uriBuilders = uriBuilders;
   }

   @Override
   public URI apply(Extension input) {
      return uriBuilders.get().uri(input.getNamespace()).scheme("http").build();
   }

   public String toString() {
      return "extensionToURI()";
   }

}