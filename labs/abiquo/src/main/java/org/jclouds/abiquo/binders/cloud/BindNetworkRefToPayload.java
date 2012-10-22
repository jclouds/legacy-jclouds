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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.xml.XMLParser;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.LinksDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;

/**
 * Bind the link reference to an {@link VLANNetworkDto} object into the payload.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class BindNetworkRefToPayload extends BindToXMLPayload {
   @Inject
   public BindNetworkRefToPayload(final XMLParser xmlParser) {
      super(xmlParser);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(final R request, final Object input) {
      checkArgument(checkNotNull(input, "input") instanceof VLANNetworkDto,
            "this binder is only valid for VLANNetworkDto objects");

      VLANNetworkDto network = (VLANNetworkDto) input;
      RESTLink editLink = checkNotNull(network.getEditLink(), "VLANNetworkDto must have an edit link");

      LinksDto refs = new LinksDto();
      switch (network.getType()) {
         case INTERNAL:
            refs.addLink(new RESTLink("internalnetwork", editLink.getHref()));
            break;
         case EXTERNAL:
            refs.addLink(new RESTLink("externalnetwork", editLink.getHref()));
            break;
         case PUBLIC:
            refs.addLink(new RESTLink("publicnetwork", editLink.getHref()));
            break;
         case UNMANAGED:
            refs.addLink(new RESTLink("unmanagednetwork", editLink.getHref()));
            break;
         default:
            // TODO: EXTERNAL_UNMANAGED network type
            throw new IllegalArgumentException("Unsupported network type");
      }

      return super.bindToRequest(request, refs);
   }

}
