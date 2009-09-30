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

import javax.ws.rs.GET;
import javax.ws.rs.PUT;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ReturnFalseOn404;
import org.jclouds.rest.Endpoint;
import org.jclouds.rest.EntityParam;
import org.jclouds.rest.ExceptionParser;
import org.jclouds.rest.RequestFilters;
import org.jclouds.rest.SkipEncoding;

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
   @ExceptionParser(ReturnFalseOn404.class)
   boolean put(@Endpoint URI resource, @EntityParam String value);

   @GET
   String get(@Endpoint URI resource);

}
