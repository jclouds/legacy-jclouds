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

package org.jclouds.abiquo.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.xml.XMLParser;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.LinksDto;
import com.abiquo.model.transport.SingleResourceTransportDto;

/**
 * Bind multiple objects to the payload of the request as a list of links.
 * 
 * @author Ignasi Barrera
 */
public abstract class BindRefsToPayload extends BindToXMLPayload {
   @Inject
   public BindRefsToPayload(final XMLParser xmlParser) {
      super(xmlParser);
   }

   protected abstract String getRelToUse(final Object input);

   @Override
   public <R extends HttpRequest> R bindToRequest(final R request, final Object input) {
      checkArgument(checkNotNull(input, "input") instanceof SingleResourceTransportDto[],
            "this binder is only valid for SingleResourceTransportDto arrays");

      SingleResourceTransportDto[] dtos = (SingleResourceTransportDto[]) input;
      LinksDto refs = new LinksDto();

      for (SingleResourceTransportDto dto : dtos) {
         RESTLink editLink = checkNotNull(dto.getEditLink(), "entity must have an edit link");

         // Do not add repeated references
         if (refs.searchLinkByHref(editLink.getHref()) == null) {
            refs.addLink(new RESTLink(getRelToUse(input), editLink.getHref()));
         }
      }

      return super.bindToRequest(request, refs);
   }

}
