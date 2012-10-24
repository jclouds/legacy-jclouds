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

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpRequest;

import com.abiquo.model.rest.RESTLink;
import com.google.common.annotations.VisibleForTesting;

/**
 * Binds the given link to the uri and the Accept header.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class BindLinkToPathAndAcceptHeader extends BindLinkToPath {
   @Override
   public <R extends HttpRequest> R bindToRequest(final R request, final Object input) {
      R updatedRequest = super.bindToRequest(request, input);
      return addHeader(updatedRequest, HttpHeaders.ACCEPT, ((RESTLink) input).getType());
   }

   @SuppressWarnings("unchecked")
   @VisibleForTesting
   <R extends HttpRequest> R addHeader(final R request, final String header, final String value) {
      return (R) request.toBuilder().replaceHeader(HttpHeaders.ACCEPT, checkNotNull(value, "value")).build();
   }
}
