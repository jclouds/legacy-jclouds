/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.binders.cloud;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.xml.XMLParser;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.LinksDto;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * Bind multiple objects to the payload of the request as a list of links.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class BindNetworkConfigurationRefToPayload extends BindToXMLPayload {
   @Inject
   public BindNetworkConfigurationRefToPayload(final XMLParser xmlParser) {
      super(xmlParser);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(final R request, final Object input) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest,
            "this binder is only valid for GeneratedHttpRequests");
      checkArgument(checkNotNull(input, "input") instanceof VLANNetworkDto,
            "this binder is only valid for VLANNetworkDto");
      GeneratedHttpRequest gRequest = (GeneratedHttpRequest) request;
      checkState(gRequest.getArgs() != null, "args should be initialized at this point");

      VLANNetworkDto network = (VLANNetworkDto) input;
      VirtualMachineDto vm = (VirtualMachineDto) Iterables.find(gRequest.getArgs(),
            Predicates.instanceOf(VirtualMachineDto.class));

      RESTLink configLink = checkNotNull(vm.searchLink("configurations"), "missing required link");

      LinksDto dto = new LinksDto();
      dto.addLink(new RESTLink("network_configuration", configLink.getHref() + "/" + network.getId()));

      return super.bindToRequest(request, dto);
   }
}
