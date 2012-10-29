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
package org.jclouds.vcloud.director.v1_5.features.admin;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.CONTROL_ACCESS;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.AdminCatalog;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.params.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.params.PublishCatalogParams;
import org.jclouds.vcloud.director.v1_5.features.CatalogAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.MetadataAsyncApi;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.href.CatalogURNToAdminHref;
import org.jclouds.vcloud.director.v1_5.functions.href.OrgURNToAdminHref;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see AdminCatalogApi
 * @author danikov, Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface AdminCatalogAsyncApi extends CatalogAsyncApi {

   /**
    * @see AdminCatalogApi#addCatalogToOrg(AdminCatalog, String)
    */
   @POST
   @Path("/catalogs")
   @Consumes(VCloudDirectorMediaType.ADMIN_CATALOG)
   @Produces(VCloudDirectorMediaType.ADMIN_CATALOG)
   @JAXBResponseParser
   ListenableFuture<AdminCatalog> addCatalogToOrg(@BinderParam(BindToXMLPayload.class) AdminCatalog catalog,
            @EndpointParam(parser = OrgURNToAdminHref.class) String orgUrn);

   /**
    * @see AdminCatalogApi#addCatalogToOrg(AdminCatalog, URI)
    */
   @POST
   @Path("/catalogs")
   @Consumes(VCloudDirectorMediaType.ADMIN_CATALOG)
   @Produces(VCloudDirectorMediaType.ADMIN_CATALOG)
   @JAXBResponseParser
   ListenableFuture<AdminCatalog> addCatalogToOrg(@BinderParam(BindToXMLPayload.class) AdminCatalog catalog,
            @EndpointParam URI orgHref);

   /**
    * @see AdminCatalogApi#get(String)
    */
   @Override
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<AdminCatalog> get(@EndpointParam(parser = CatalogURNToAdminHref.class) String catalogUrn);

   /**
    * @see AdminCatalogApi#get(URI)
    */
   @Override
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<AdminCatalog> get(@EndpointParam URI orgHref);

   /**
    * @see AdminCatalogApi#edit(String, AdminCatalog)
    */
   @PUT
   @Consumes(VCloudDirectorMediaType.ADMIN_CATALOG)
   @Produces(VCloudDirectorMediaType.ADMIN_CATALOG)
   @JAXBResponseParser
   ListenableFuture<AdminCatalog> edit(@EndpointParam(parser = CatalogURNToAdminHref.class) String catalogUrn,
            @BinderParam(BindToXMLPayload.class) AdminCatalog catalog);

   /**
    * @see AdminCatalogApi#edit(URI, AdminCatalog)
    */
   @PUT
   @Consumes(VCloudDirectorMediaType.ADMIN_CATALOG)
   @Produces(VCloudDirectorMediaType.ADMIN_CATALOG)
   @JAXBResponseParser
   ListenableFuture<AdminCatalog> edit(@EndpointParam URI catalogAdminHref,
            @BinderParam(BindToXMLPayload.class) AdminCatalog catalog);

   /**
    * @see AdminCatalogApi#remove(String)
    */
   @DELETE
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> remove(@EndpointParam(parser = CatalogURNToAdminHref.class) String catalogUrn);

   /**
    * @see AdminCatalogApi#remove(URI)
    */
   @DELETE
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> remove(@EndpointParam URI catalogAdminHref);

   /**
    * @see AdminCatalogApi#getOwner(String)
    */
   @GET
   @Path("/owner")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Owner> getOwner(@EndpointParam(parser = CatalogURNToAdminHref.class) String catalogUrn);

   /**
    * @see AdminCatalogApi#getOwner(URI)
    */
   @GET
   @Path("/owner")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Owner> getOwner(@EndpointParam URI catalogAdminHref);

   /**
    * @see AdminCatalogApi#setOwner(String, Owner)
    */
   @PUT
   @Path("/owner")
   @Consumes
   @Produces(VCloudDirectorMediaType.OWNER)
   @JAXBResponseParser
   ListenableFuture<Void> setOwner(@EndpointParam(parser = CatalogURNToAdminHref.class) String catalogUrn,
            @BinderParam(BindToXMLPayload.class) Owner newOwner);

   /**
    * @see AdminCatalogApi#setOwner(URI, Owner)
    */
   @PUT
   @Path("/owner")
   @Consumes
   @Produces(VCloudDirectorMediaType.OWNER)
   @JAXBResponseParser
   ListenableFuture<Void> setOwner(@EndpointParam URI catalogAdminHref,
            @BinderParam(BindToXMLPayload.class) Owner newOwner);

   /**
    * @see AdminCatalogApi#publish(String, PublishCatalogParams)
    */
   @POST
   @Path("/action/publish")
   @Consumes
   @Produces(VCloudDirectorMediaType.PUBLISH_CATALOG_PARAMS)
   @JAXBResponseParser
   ListenableFuture<Void> publish(@EndpointParam(parser = CatalogURNToAdminHref.class) String catalogUrn,
            @BinderParam(BindToXMLPayload.class) PublishCatalogParams params);

   /**
    * @see AdminCatalogApi#publish(URI, PublishCatalogParams)
    */
   @POST
   @Path("/action/publish")
   @Consumes
   @Produces(VCloudDirectorMediaType.PUBLISH_CATALOG_PARAMS)
   @JAXBResponseParser
   ListenableFuture<Void> publish(@EndpointParam URI catalogAdminHref,
            @BinderParam(BindToXMLPayload.class) PublishCatalogParams params);

   /**
    * @see AdminCatalogApi#editAccessControl(String, ControlAccessParams)
    */
   @POST
   @Path("/action/controlAccess")
   @Produces(CONTROL_ACCESS)
   @Consumes(CONTROL_ACCESS)
   @JAXBResponseParser
   ListenableFuture<ControlAccessParams> editAccessControl(@EndpointParam(parser = CatalogURNToAdminHref.class) String catalogUrn,
      @BinderParam(BindToXMLPayload.class) ControlAccessParams params);

   /**
    * @see AdminCatalogApi#editAccessControl(URI, ControlAccessParams)
    */
   @POST
   @Path("/action/controlAccess")
   @Produces(CONTROL_ACCESS)
   @Consumes(CONTROL_ACCESS)
   @JAXBResponseParser
   ListenableFuture<ControlAccessParams> editAccessControl(@EndpointParam URI catalogAdminHref,
      @BinderParam(BindToXMLPayload.class) ControlAccessParams params);
   
   /**
    * @see AdminCatalogApi#getAccessControl(String)
    */
   @GET
   @Path("/controlAccess")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ControlAccessParams> getAccessControl(@EndpointParam(parser = CatalogURNToAdminHref.class) String catalogUrn);
   
   /**
    * @see AdminCatalogApi#getAccessControl(URI)
    */
   @GET
   @Path("/controlAccess")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ControlAccessParams> getAccessControl(@EndpointParam URI catalogAdminHref);
   
   /**
    * @return synchronous access to {@link Metadata.Writeable} features
    */
   @Override
   @Delegate
   MetadataAsyncApi.Writeable getMetadataApi(@EndpointParam(parser = CatalogURNToAdminHref.class) String catalogUrn);

   @Override
   @Delegate
   MetadataAsyncApi.Writeable getMetadataApi(@EndpointParam URI catalogAdminHref);

}
