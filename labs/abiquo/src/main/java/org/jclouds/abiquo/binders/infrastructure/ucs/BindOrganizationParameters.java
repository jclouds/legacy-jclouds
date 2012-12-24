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

package org.jclouds.abiquo.binders.infrastructure.ucs;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.abiquo.server.core.infrastructure.OrganizationDto;

/**
 * Binds organization query parameters to request. This method assumes that the
 * input object is a {@link OrganizationDto}.
 * 
 * @author Francesc Montserrat
 * @author Ignasi Barrera
 */
@Singleton
public class BindOrganizationParameters implements Binder {

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(final R request, final Object input) {
      checkArgument(checkNotNull(input, "input") instanceof OrganizationDto,
            "this binder is only valid for OrganizationDto objects");

      OrganizationDto org = (OrganizationDto) input;

      return (R) request.toBuilder().addQueryParam("org", checkNotNull(org.getDn(), "org.dn")).build();
   }
}
