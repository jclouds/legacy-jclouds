/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud;

import static org.jclouds.vcloud.VCloudMediaType.CATALOG_XML;
import static org.jclouds.vcloud.VCloudMediaType.TASKSLIST_XML;
import static org.jclouds.vcloud.VCloudMediaType.VDC_XML;

import java.util.concurrent.Future;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;

import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.endpoints.TasksList;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.xml.CatalogHandler;
import org.jclouds.vcloud.xml.TasksListHandler;
import org.jclouds.vcloud.xml.VDCHandler;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx" />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface VCloudClient {

   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.Catalog.class)
   @Consumes(CATALOG_XML)
   @XMLResponseParser(CatalogHandler.class)
   Future<? extends Catalog> getCatalog();

   @GET
   @Endpoint(org.jclouds.vcloud.endpoints.VDC.class)
   @XMLResponseParser(VDCHandler.class)
   @Consumes(VDC_XML)
   Future<? extends VDC> getDefaultVDC();

   @GET
   @Endpoint(TasksList.class)
   @Consumes(TASKSLIST_XML)
   @XMLResponseParser(TasksListHandler.class)
   Future<? extends org.jclouds.vcloud.domain.TasksList> getDefaultTasksList();

   //   
   // @GET
   // @Endpoint(vDC.class)
   // public Set<String> getvDCs();
   //

}
