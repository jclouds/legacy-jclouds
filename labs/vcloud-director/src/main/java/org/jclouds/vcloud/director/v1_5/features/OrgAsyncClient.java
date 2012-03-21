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
package org.jclouds.vcloud.director.v1_5.features;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.CONTROL_ACCESS;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.vcloud.director.v1_5.domain.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.Org;
import org.jclouds.vcloud.director.v1_5.domain.OrgList;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationToRequest;
import org.jclouds.vcloud.director.v1_5.functions.ThrowVCloudErrorOn4xx;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see OrgClient
 * @author Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationToRequest.class)
public interface OrgAsyncClient {

   /**
    * @see OrgClient#getOrgList()
    */
   @GET
   @Path("/org/")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<OrgList> getOrgList();

   /**
    * @see OrgClient#getOrg(URI)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<? extends Org> getOrg(@EndpointParam URI orgUri);

   /**
    * @see OrgClient#modifyControlAccess(URI, URI, ControlAccessParams)
    */
   @POST
   @Path("/action/controlAccess")
   @Produces(CONTROL_ACCESS)
   @Consumes(CONTROL_ACCESS)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<ControlAccessParams> modifyControlAccess(@EndpointParam URI orgURI, @EndpointParam URI catalogURI,
                                                       @BinderParam(BindToXMLPayload.class) ControlAccessParams params);

   /**
    * @see OrgClient#getControlAccess(URI, URI, ControlAccessParams)
    */
   @POST
   @Path("/controlAccess")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<ControlAccessParams> getControlAccess(@EndpointParam URI orgURI, @EndpointParam URI catalogURI);
   
   /**
    * @return asynchronous access to {@link Metadata.Readable} features
    */
   @Delegate
   MetadataAsyncClient.Readable getMetadataClient();
}
