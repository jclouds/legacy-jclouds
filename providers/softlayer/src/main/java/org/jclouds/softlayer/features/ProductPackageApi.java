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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.softlayer.domain.ProductPackage;

/**
 * Provides synchronous access to ProductPackage.
 * <p/>
 * 
 * @see <a href="http://sldn.softlayer.com/article/REST" />
 * @author Adrian Cole
 */
@RequestFilters(BasicAuthentication.class)
@Path("/v{jclouds.api-version}")
public interface ProductPackageApi {
   public static String PRODUCT_MASK = "items.prices;items.categories;locations.locationAddress;locations.regions";

   /**
    * 
    * @param id
    *           id of the product package
    * @return product package or null if not found
    */
   @GET
   @Path("/SoftLayer_Product_Package/{id}.json")
   @QueryParams(keys = "objectMask", values = PRODUCT_MASK)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   ProductPackage getProductPackage(@PathParam("id") long id);

}
