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

import java.net.URI;
import java.util.SortedMap;
import java.util.concurrent.Future;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.vcloud.endpoints.VCloud;
import org.jclouds.vcloud.xml.SupportedVersionsHandler;

/**
 * Establishes a context with a VCloud endpoint.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx" />
 * @author Adrian Cole
 */
@Endpoint(VCloud.class)
public interface VCloudVersions {

   /**
    * Retrieve information for supported versions
    */
   @GET
   @XMLResponseParser(SupportedVersionsHandler.class)
   @Path("/versions")
   @Consumes(MediaType.APPLICATION_XML)
   Future<SortedMap<String, URI>> getSupportedVersions();
}
