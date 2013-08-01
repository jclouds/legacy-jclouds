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
package org.jclouds.softlayer.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.softlayer.domain.ProductPackage;

/**
 * Provides synchronous access to Account.
 * <p/>
 * 
 * @see <a href="http://sldn.softlayer.com/article/REST" />
 * @author Jason King
 */
@RequestFilters(BasicAuthentication.class)
@Path("/v{jclouds.api-version}")
public interface AccountApi {

   /**
    * 
    * @return Gets all the active packages.
    * This will give you a basic description of the packages that are currently
    * active and from which you can order a server or additional services.
    *
    * Calling ProductPackage.getItems() will return an empty set.
    * Use ProductPackageApi.getProductPackage(long id) to obtain items data.
    * @see ProductPackageApi#getProductPackage
    */
   @GET
   @Path("/SoftLayer_Account/ActivePackages.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Set<ProductPackage> getActivePackages();

}
