/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.mezeo.pcs2;

import java.net.URI;
import java.util.concurrent.Future;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.mezeo.pcs2.functions.AddEntryIntoMultiMap;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.binders.BindToStringEntity;

import com.google.common.collect.Multimap;

/**
 * Provides access to Mezeo PCS v2 via their REST API.
 * <p/>
 * 
 * @see <a href=
 *      "http://developer.mezeo.com/mezeo-developer-center/documentation/howto-using-curl-to-access-api"
 *      />
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(BasicAuthentication.class)
public interface PCSUtil {

   @PUT
   @Endpoint(PCS.class)
   @Path("/files/{fileResourceId}/metadata/{key}")
   Future<Void> putMetadata(@PathParam("fileResourceId") String resourceId,
            @PathParam("key") String key, @BinderParam(BindToStringEntity.class) String value);

   @GET
   @ResponseParser(AddEntryIntoMultiMap.class)
   Future<Void> addEntryToMultiMap(Multimap<String, String> map, String key, @Endpoint URI value);
}
