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
package org.jclouds.cloudstack.ec2.services;

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.cloudstack.ec2.options.CloudStackEC2RegisterImageOptions;
import org.jclouds.ec2.services.AMIAsyncClient;
import org.jclouds.ec2.xml.ImageIdHandler;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.*;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import static org.jclouds.aws.reference.FormParameters.ACTION;

/**
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface CloudStackAMIAsyncClient extends AMIAsyncClient {

    /**
    * @see CloudStackAMIClient#registerImageFromManifestInRegion
    */
    @POST
    @Path("/")
    @FormParams(keys = ACTION, values = "RegisterImage")
    @XMLResponseParser(ImageIdHandler.class)
    ListenableFuture<String> registerImageFromManifestInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("Name") String imageName, @FormParam("ImageLocation") String pathToManifest,
            CloudStackEC2RegisterImageOptions... options);
}
