/*
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

package org.jclouds.googlecompute.handlers;

import com.google.common.base.Function;
import org.jclouds.googlecompute.domain.InstanceTemplate;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author David Alves
 */
public class InstanceBinder implements MapBinder {

   @Inject
   private BindToJsonPayload jsonBinder;

   @Inject
   @Named("machineTypes")
   Function<String, URI> machineTypesToURI;

   @Inject
   @Named("networks")
   Function<String, URI> networksToURI;

   @Inject
   @Named("zones")
   Function<String, URI> zonesToURI;

   /**
    * {@inheritDoc}
    */
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      InstanceTemplate template = (InstanceTemplate) checkNotNull(postParams.get("template"), "template");
      template.name(checkNotNull(postParams.get("name"), "name").toString());
      template.zone(zonesToURI.apply((String) checkNotNull(postParams.get("zone"), "zone")));
      if (template.getNetworkName() != null) {
         template.network(networksToURI.apply(template.getNetworkName()));
      }

      if (template.getMachineTypeName() != null) {
         template.machineType(machineTypesToURI.apply(template.getMachineTypeName()));
      }
      template.zone((String) null);
      template.machineType((String) null);
      return bindToRequest(request, template);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }
}
