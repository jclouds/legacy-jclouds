/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.gogrid.services;

import com.google.common.util.concurrent.ListenableFuture;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jclouds.gogrid.GoGrid;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.filters.SharedKeyLiteAuthentication;
import org.jclouds.gogrid.functions.ParseServerListFromJsonResponse;
import org.jclouds.rest.annotations.*;

import java.util.Set;

import static org.jclouds.gogrid.reference.GoGridHeaders.VERSION;

/**
 * Provides asynchronous access to GoGrid via their REST API.
 * <p/>
 *
 * @see GridServerClient
 * @see <a href="http://wiki.gogrid.com/wiki/index.php/API" />
 * @author Adrian Cole
 * @author Oleksiy Yarmula
 */
@Endpoint(GoGrid.class)
@RequestFilters(SharedKeyLiteAuthentication.class)
@QueryParams(keys = VERSION, values = "1.3")
public interface GridServerAsyncClient {

   @GET
   @ResponseParser(ParseServerListFromJsonResponse.class)
   @Path("/grid/server/list")
   ListenableFuture<Set<Server>> getServerList();

}
