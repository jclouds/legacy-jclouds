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
package org.jclouds.ec2.features;

import static org.jclouds.aws.reference.FormParameters.ACTION;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.ec2.domain.PasswordData;
import org.jclouds.ec2.xml.GetPasswordDataResponseHandler;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SinceApiVersion;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to EC2 Windows Features via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference" >doc</a>
 * @see WindowsAsyncApi
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
@Beta
@SinceApiVersion("2008-08-08")
public interface WindowsAsyncApi {

   /**
    * @see WindowsApi#getPasswordDataForInstance
    */
   @Named("GetPasswordData")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "GetPasswordData")
   @XMLResponseParser(GetPasswordDataResponseHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<PasswordData> getPasswordDataForInstance(@FormParam("InstanceId") String instanceId);

}
