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
package org.jclouds.trmk.ecloud.features;

import static org.jclouds.trmk.ecloud.TerremarkECloudMediaType.DATACENTERSLIST_XML;

import java.net.URI;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.trmk.ecloud.functions.OrgURIToDataCentersListEndpoint;
import org.jclouds.trmk.vcloud_0_8.domain.DataCenter;
import org.jclouds.trmk.vcloud_0_8.domain.KeyPair;
import org.jclouds.trmk.vcloud_0_8.filters.SetVCloudTokenCookie;
import org.jclouds.trmk.vcloud_0_8.xml.DataCentersHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @see <a href=
 *      "http://support.theenterprisecloud.com/kb/default.asp?id=960&Lang=1&SID="
 *      />
 * @see DataCenterOperationsClient
 * 
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface DataCenterOperationsAsyncClient {

   /**
    * @see DataCenterOperationsClient#listDataCentersInOrg
    */
   @GET
   @Consumes(DATACENTERSLIST_XML)
   @XMLResponseParser(DataCentersHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<KeyPair>> listDataCentersInOrg(
         @Nullable @EndpointParam(parser = OrgURIToDataCentersListEndpoint.class) URI org);

   /**
    * @see DataCenterOperationsClient#listDataCenters
    */
   @GET
   @Consumes(DATACENTERSLIST_XML)
   @XMLResponseParser(DataCentersHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<DataCenter>> listDataCenters(@EndpointParam URI dataCenters);
}
