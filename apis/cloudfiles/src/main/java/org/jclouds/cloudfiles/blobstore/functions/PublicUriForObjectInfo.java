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
package org.jclouds.cloudfiles.blobstore.functions;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.openstack.swift.domain.ObjectInfo;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class PublicUriForObjectInfo implements Function<ObjectInfo, URI> {
   private final Map<String, URI> cdnContainer;
   private final Provider<UriBuilder> uriBuilders;

   @Inject
   public PublicUriForObjectInfo(Map<String, URI> cdnContainer, Provider<UriBuilder> uriBuilders) {
      this.cdnContainer = cdnContainer;
      this.uriBuilders = uriBuilders;
   }

   public URI apply(ObjectInfo from) {
      if (from == null)
         return null;
      try {
         return uriBuilders.get().uri(cdnContainer.get(from.getContainer())).path(from.getName()).replaceQuery("")
                  .build();
      } catch (NullPointerException e) {
         // MapMaker constructed maps are not allowed to return null;
         return null;
      }
   }
}
