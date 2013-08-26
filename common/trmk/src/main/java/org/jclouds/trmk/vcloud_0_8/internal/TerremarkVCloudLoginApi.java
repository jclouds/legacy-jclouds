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
package org.jclouds.trmk.vcloud_0_8.internal;

import java.io.Closeable;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType;
import org.jclouds.trmk.vcloud_0_8.domain.VCloudSession;
import org.jclouds.trmk.vcloud_0_8.functions.ParseLoginResponseFromHeaders;

@Endpoint(org.jclouds.trmk.vcloud_0_8.endpoints.VCloudLogin.class)
@RequestFilters(BasicAuthentication.class)
public interface TerremarkVCloudLoginApi extends Closeable {

   @POST
   @ResponseParser(ParseLoginResponseFromHeaders.class)
   @Consumes(TerremarkVCloudMediaType.ORGLIST_XML)
   VCloudSession login();
}
