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
package org.jclouds.cloudstack.features;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.core.MediaType;

import org.jclouds.cloudstack.domain.StoragePool;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.ListStoragePoolsOptions;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

/**
 * Provides synchronous access to CloudStack storage pool features.
 * <p/>
 *
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html" />
 * @author Richard Downer
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface GlobalStoragePoolApi {

   @Named("listStoragePools")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listStoragePools", "true" })
   @SelectJson("storagepool")
   @Consumes(MediaType.APPLICATION_JSON)
   Set<StoragePool> listStoragePools(ListStoragePoolsOptions... options);

}
