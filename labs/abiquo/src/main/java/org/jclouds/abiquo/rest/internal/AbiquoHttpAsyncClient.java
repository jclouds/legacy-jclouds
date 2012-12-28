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

package org.jclouds.abiquo.rest.internal;

import javax.ws.rs.GET;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.abiquo.binders.BindLinkToPathAndAcceptHeader;
import org.jclouds.abiquo.http.filters.AbiquoAuthentication;
import org.jclouds.abiquo.http.filters.AppendApiVersionToMediaType;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;

import com.abiquo.model.rest.RESTLink;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Custom Rest methods to work with the Abiquo Api.
 * 
 * @author Ignasi Barrera
 */
@RequestFilters({ AbiquoAuthentication.class, AppendApiVersionToMediaType.class })
public interface AbiquoHttpAsyncClient {
   /**
    * @see AbiquoHttpClient#get(RESTLink)
    */
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   public ListenableFuture<HttpResponse> get(@BinderParam(BindLinkToPathAndAcceptHeader.class) final RESTLink link);
}
