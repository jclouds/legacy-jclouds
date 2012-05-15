/**
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
package org.jclouds.joyent.sdc.v6_5.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.joyent.sdc.v6_5.domain.Machine;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Machine via their REST API.
 * <p/>
 * 
 * @author Gerald Pereira
 * @see MachineClient
 * @see <a href="http://apidocs.joyent.com/sdcapidoc/cloudapi">api doc</a>
 */
@SkipEncoding( { '/', '=' })
@Headers(keys = "X-Api-Version", values = "{jclouds.api-version}")
@RequestFilters(BasicAuthentication.class)
public interface MachineAsyncClient {

	/**
	 * @see MachineClient#listServers
	 */
	@GET
	@Path("/my/machines")
	@Consumes(MediaType.APPLICATION_JSON)
	@ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
	ListenableFuture<Set<Machine>> listMachines();

	/**
	 * @see MachineClient#getServerDetails
	 */
	@GET
	@Path("/my/machines/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@ExceptionParser(ReturnNullOnNotFoundOr404.class)
	ListenableFuture<Machine> getMachine(@PathParam("id") String id);

}
