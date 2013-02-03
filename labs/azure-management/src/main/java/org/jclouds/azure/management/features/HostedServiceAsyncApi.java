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
package org.jclouds.azure.management.features;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.azure.management.binders.BindCreateHostedServiceToXmlPayload;
import org.jclouds.azure.management.domain.Deployment;
import org.jclouds.azure.management.domain.HostedService;
import org.jclouds.azure.management.domain.HostedServiceWithDetailedProperties;
import org.jclouds.azure.management.functions.ParseRequestIdHeader;
import org.jclouds.azure.management.options.CreateHostedServiceOptions;
import org.jclouds.azure.management.xml.DeploymentHandler;
import org.jclouds.azure.management.xml.HostedServiceHandler;
import org.jclouds.azure.management.xml.HostedServiceWithDetailedPropertiesHandler;
import org.jclouds.azure.management.xml.ListHostedServicesHandler;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * The Service Management API includes operations for managing the hosted services beneath your
 * subscription.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460812">docs</a>
 * @see HostedServiceApi
 * @author Gerald Pereira, Adrian Cole
 */
@Headers(keys = "x-ms-version", values = "2012-03-01")
public interface HostedServiceAsyncApi {

   /**
    * @see HostedServiceApi#list()
    */
   @Named("ListHostedServices")
   @GET
   @Path("/services/hostedservices")
   @XMLResponseParser(ListHostedServicesHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   @Consumes(MediaType.APPLICATION_XML)
   ListenableFuture<Set<HostedServiceWithDetailedProperties>> list();

   /**
    * @see HostedServiceApi#createServiceWithLabelInLocation(String, String, String)
    */
   @Named("CreateHostedService")
   @POST
   @Path("/services/hostedservices")
   @MapBinder(BindCreateHostedServiceToXmlPayload.class)
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   ListenableFuture<String> createServiceWithLabelInLocation(@PayloadParam("serviceName") String serviceName,
            @PayloadParam("label") String label, @PayloadParam("location") String location);

   /**
    * @see HostedServiceApi#createServiceWithLabelInLocation(String, String, String,
    *      CreateHostedServiceOptions)
    */
   @Named("CreateHostedService")
   @POST
   @Path("/services/hostedservices")
   @MapBinder(BindCreateHostedServiceToXmlPayload.class)
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   ListenableFuture<String> createServiceWithLabelInLocation(@PayloadParam("serviceName") String serviceName,
            @PayloadParam("label") String label, @PayloadParam("location") String location,
            @PayloadParam("options") CreateHostedServiceOptions options);

   /**
    * @see HostedServiceApi#get(String)
    */
   @Named("GetHostedServiceProperties")
   @GET
   @Path("/services/hostedservices/{serviceName}")
   @XMLResponseParser(HostedServiceHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(MediaType.APPLICATION_XML)
   ListenableFuture<HostedService> get(@PathParam("serviceName") String serviceName);

   /**
    * @see HostedServiceApi#getDetails(String)
    */
   @Named("GetHostedServiceProperties")
   @GET
   @Path("/services/hostedservices/{serviceName}")
   @QueryParams(keys = "embed-detail", values = "true")
   @XMLResponseParser(HostedServiceWithDetailedPropertiesHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(MediaType.APPLICATION_XML)
   ListenableFuture<HostedServiceWithDetailedProperties> getDetails(@PathParam("serviceName") String serviceName);

   /**
    * @see HostedServiceApi#delete(String)
    */
   @Named("DeleteHostedService")
   @DELETE
   @Path("/services/hostedservices/{serviceName}")
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(ParseRequestIdHeader.class)
   ListenableFuture<String> delete(@PathParam("serviceName") String serviceName);

   /**
    * @see HostedServiceApi#deleteDeployment(String, String)
    */
   @Named("DeleteDeployment")
   @DELETE
   @Path("/services/hostedservices/{serviceName}/deployments/{deploymentName}")
   @Fallback(VoidOnNotFoundOr404.class)
   @ResponseParser(ParseRequestIdHeader.class)
   ListenableFuture<String> deleteDeployment(@PathParam("serviceName") String serviceName,
            @PathParam("deploymentName") String deploymentName);

   /**
    * @see HostedServiceApi#get(String,String)
    */
   @Named("GetDeployment")
   @GET
   @Path("/services/hostedservices/{serviceName}/deployments/{deploymentName}")
   @XMLResponseParser(DeploymentHandler.class)
   @Fallback(VoidOnNotFoundOr404.class)
   @Consumes(MediaType.APPLICATION_XML)
   ListenableFuture<Deployment> getDeployment(@PathParam("serviceName") String serviceName, @PathParam("deploymentName") String deploymentName);
}
