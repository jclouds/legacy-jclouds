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
package org.jclouds.ec2.services;

import static org.jclouds.aws.reference.FormParameters.ACTION;

import java.util.Set;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.ec2.binders.BindBundleIdsToIndexedFormParams;
import org.jclouds.ec2.binders.BindS3UploadPolicyAndSignature;
import org.jclouds.ec2.domain.BundleTask;
import org.jclouds.ec2.domain.PasswordData;
import org.jclouds.ec2.options.BundleInstanceS3StorageOptions;
import org.jclouds.ec2.xml.BundleTaskHandler;
import org.jclouds.ec2.xml.DescribeBundleTasksResponseHandler;
import org.jclouds.ec2.xml.GetPasswordDataResponseHandler;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to EC2 Windows via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface WindowsAsyncClient {

   /**
    * @see WindowsClient#bundleInstanceInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "BundleInstance")
   @XMLResponseParser(BundleTaskHandler.class)
   ListenableFuture<BundleTask> bundleInstanceInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("InstanceId") String instanceId, @FormParam("Storage.S3.Prefix") String prefix,
            @FormParam("Storage.S3.Bucket") String bucket,
            @BinderParam(BindS3UploadPolicyAndSignature.class) String uploadPolicy,
            BundleInstanceS3StorageOptions... options);

   /**
    * @see WindowsClient#cancelBundleTaskInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CancelBundleTask")
   @XMLResponseParser(BundleTaskHandler.class)
   ListenableFuture<BundleTask> cancelBundleTaskInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("BundleId") String bundleId);

   /**
    * @see BundleTaskClient#describeBundleTasksInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeBundleTasks")
   @XMLResponseParser(DescribeBundleTasksResponseHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<BundleTask>> describeBundleTasksInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindBundleIdsToIndexedFormParams.class) String... bundleTaskIds);

   /**
    * @see WindowsClient#getPasswordDataInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "GetPasswordData")
   @XMLResponseParser(GetPasswordDataResponseHandler.class)
   ListenableFuture<PasswordData> getPasswordDataInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId);

}
