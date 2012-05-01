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
package org.jclouds.aws.ec2.services;

import static org.jclouds.aws.reference.FormParameters.ACTION;

import java.util.Set;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.ec2.xml.ProductCodesHandler;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.ec2.binders.BindProductCodesToIndexedFormParams;
import org.jclouds.ec2.services.AMIAsyncClient;
import org.jclouds.ec2.services.AMIClient;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to AMI Services.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface AWSAMIAsyncClient extends AMIAsyncClient {
   // TODO make AWSImage as it has product codes...

   /**
    * @see AMIClient#getProductCodesForImageInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeImageAttribute", "productCodes" })
   @XMLResponseParser(ProductCodesHandler.class)
   ListenableFuture<Set<String>> getProductCodesForImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#addProductCodesToImageInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifyImageAttribute", "add",
            "productCodes" })
   ListenableFuture<Void> addProductCodesToImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindProductCodesToIndexedFormParams.class) Iterable<String> productCodes,
            @FormParam("ImageId") String imageId);

   /**
    * @see AMIClient#removeProductCodesToImageInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifyImageAttribute", "remove",
            "productCodes" })
   ListenableFuture<Void> removeProductCodesFromImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindProductCodesToIndexedFormParams.class) Iterable<String> productCodes,
            @FormParam("ImageId") String imageId);
}
