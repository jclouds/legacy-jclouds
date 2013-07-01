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
package org.jclouds.ec2.features;

import static org.jclouds.aws.reference.FormParameters.ACTION;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
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
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SinceApiVersion;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to EC2 Windows Features via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference" >doc</a>
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
@SinceApiVersion("2008-08-08")
public interface WindowsApi {

   /**
    * Bundles the Windows instance. This procedure is not applicable for Linux
    * and UNIX instances. For more information, go to the Amazon Elastic Compute
    * Cloud Developer Guide or Amazon Elastic Compute Cloud Getting Started
    * Guide.
    * 
    * @param region
    *           Bundles are tied to the Region where its files are located
    *           within Amazon S3.
    * 
    * @param instanceId
    *           The ID of the instance to bundle.
    * @param prefix
    *           Specifies the beginning of the file name of the AMI.
    * @param bucket
    *           The bucket in which to store the AMI. You can specify a bucket
    *           that you already own or a new bucket that Amazon EC2 creates on
    *           your behalf. If you specify a bucket that belongs to someone
    *           else, Amazon EC2 returns an error.
    * @param uploadPolicy
    *           An Amazon S3 upload policy that gives Amazon EC2 permission to
    *           upload items into Amazon S3 on the user's behalf.
    *           <p/>
    *           ex.
    * 
    *           <pre>
    * {"expiration": "2008-08-30T08:49:09Z","conditions": ["bucket": "my-bucket"},["starts-with", "$key", "my-new-image"]]}
    * </pre>
    * 
    * @param options
    *           if the bucket isn't owned by you, use this to set the bucket's
    *           accesskeyid
    * @return status of the work
    * 
    * @see #cancelBundleTaskInRegion
    * @see #describeBundleTasksInRegion
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-BundleInstance.html"
    *      />
    */
   @Named("BundleInstance")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "BundleInstance")
   @XMLResponseParser(BundleTaskHandler.class)
   BundleTask bundleInstanceInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("InstanceId") String instanceId, @FormParam("Storage.S3.Prefix") String prefix,
            @FormParam("Storage.S3.Bucket") String bucket,
            @BinderParam(BindS3UploadPolicyAndSignature.class) String uploadPolicy,
            BundleInstanceS3StorageOptions... options);

   /**
    * Cancels an Amazon EC2 bundling operation.
    * 
    * @param region
    *           The bundleTask ID is tied to the Region.
    * @param bundleId
    *           The ID of the bundle task to cancel.
    * @return task for the cancel.
    * 
    * @see #bundleInstanceInRegion
    * @see #describeBundleTasksInRegion
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CancelBundleTask.html"
    *      />
    */
   @Named("CancelBundleTask")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CancelBundleTask")
   @XMLResponseParser(BundleTaskHandler.class)
   BundleTask cancelBundleTaskInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("BundleId") String bundleId);

   /**
    * 
    * Describes current bundling tasks.
    * 
    * @param region
    *           The bundleTask ID is tied to the Region.
    * 
    * @see #cancelBundleTaskInRegion
    * @see #bundleInstanceInRegion
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeBundleTasks.html"
    *      />
    */
   @Named("DescribeBundleTasks")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeBundleTasks")
   @XMLResponseParser(DescribeBundleTasksResponseHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<BundleTask> describeBundleTasksInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindBundleIdsToIndexedFormParams.class) String... bundleTaskIds);

   /**
    *
    * Retrieves the encrypted administrator password for the instances running Windows.
    *
    * @param region The region where the instance is based
    * @param instanceId The ID of the instance to query
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-GetPasswordData.html" />
    */
   @Named("GetPasswordData")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "GetPasswordData")
   @XMLResponseParser(GetPasswordDataResponseHandler.class)
   PasswordData getPasswordDataInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("InstanceId") String instanceId);


   /**
    * 
    * Retrieves the encrypted administrator password for the instances running Windows. <h4>Note</h4>
    * 
    * The Windows password is only generated the first time an AMI is launched. It is not generated
    * for rebundled AMIs or after the password is changed on an instance.
    * 
    * The password is encrypted using the key pair that you provided.
    * 
    * @param instanceId
    *           The ID of the instance to query
    * @return password data or null if not available
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-GetPasswordData.html"
    *      />
    */
   @Named("GetPasswordData")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "GetPasswordData")
   @XMLResponseParser(GetPasswordDataResponseHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   PasswordData getPasswordDataForInstance(@FormParam("InstanceId") String instanceId);

}
