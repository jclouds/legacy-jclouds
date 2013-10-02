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
package org.jclouds.aws.ec2.features;

import static org.jclouds.aws.reference.FormParameters.ACTION;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.ec2.xml.ProductCodesHandler;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.ec2.binders.BindProductCodesToIndexedFormParams;
import org.jclouds.ec2.features.AMIApi;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to AMI Services.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface AWSAMIApi extends AMIApi {
   // TODO make AWSImage as it has product codes...

   /**
    * Returns the Product Codes of an image.
    * 
    * @param region
    *           AMIs are tied to the Region where its files are located within Amazon S3.
    * @param imageId
    *           The ID of the AMI for which an attribute will be described
    * @see #describeImages
    * @see #modifyImageAttribute
    * @see #resetImageAttribute
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeImageAttribute.html"
    *      />
    * @see DescribeImagesOptions
    */
   @Named("DescribeImageAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "Attribute" }, values = { "DescribeImageAttribute", "productCodes" })
   @XMLResponseParser(ProductCodesHandler.class)
   Set<String> getProductCodesForImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("ImageId") String imageId);

   /**
    * Adds {@code productCode}s to an AMI.
    * 
    * @param region
    *           AMIs are tied to the Region where its files are located within Amazon S3.
    * @param productCodes
    *           Product Codes
    * @param imageId
    *           The AMI ID.
    * 
    * @see #removeProductCodesFromImage
    * @see #describeImageAttribute
    * @see #resetImageAttribute
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-ModifyImageAttribute.html"
    *      />
    */
   @Named("ModifyImageAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifyImageAttribute", "add",
            "productCodes" })
   void addProductCodesToImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindProductCodesToIndexedFormParams.class) Iterable<String> productCodes,
            @FormParam("ImageId") String imageId);

   /**
    * Removes {@code productCode}s from an AMI.
    * 
    * @param region
    *           AMIs are tied to the Region where its files are located within Amazon S3.
    * @param productCodes
    *           Product Codes
    * @param imageId
    *           The AMI ID.
    * 
    * @see #addProductCodesToImage
    * @see #describeImageAttribute
    * @see #resetImageAttribute
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-ModifyImageAttribute.html"
    *      />
    */
   @Named("ModifyImageAttribute")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "OperationType", "Attribute" }, values = { "ModifyImageAttribute", "remove",
            "productCodes" })
   void removeProductCodesFromImageInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindProductCodesToIndexedFormParams.class) Iterable<String> productCodes,
            @FormParam("ImageId") String imageId);
}
