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
package org.jclouds.sts;

import java.io.Closeable;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.domain.SessionCredentials;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.aws.xml.SessionCredentialsHandler;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.sts.domain.UserAndSessionCredentials;
import org.jclouds.sts.options.AssumeRoleOptions;
import org.jclouds.sts.options.FederatedUserOptions;
import org.jclouds.sts.options.SessionCredentialsOptions;
import org.jclouds.sts.xml.UserAndSessionCredentialsHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Amazon STS via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/STS/latest/APIReference" />
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface STSAsyncApi extends Closeable {

   /**
    * @see STSApi#createTemporaryCredentials()
    */
   @Named("GetSessionToken")
   @POST
   @Path("/")
   @XMLResponseParser(SessionCredentialsHandler.class)
   @FormParams(keys = "Action", values = "GetSessionToken")
   ListenableFuture<SessionCredentials> createTemporaryCredentials();

   /**
    * @see STSApi#createTemporaryCredentials(SessionCredentialsOptions)
    */
   @Named("GetSessionToken")
   @POST
   @Path("/")
   @XMLResponseParser(SessionCredentialsHandler.class)
   @FormParams(keys = "Action", values = "GetSessionToken")
   ListenableFuture<SessionCredentials> createTemporaryCredentials(SessionCredentialsOptions options);

   /**
    * @see STSApi#assumeRole(String, String)
    */
   @Named("AssumeRole")
   @POST
   @Path("/")
   @XMLResponseParser(UserAndSessionCredentialsHandler.class)
   @FormParams(keys = "Action", values = "AssumeRole")
   ListenableFuture<UserAndSessionCredentials> assumeRole(@FormParam("RoleArn") String roleArn,
         @FormParam("RoleSessionName") String sessionName);

   /**
    * @see STSApi#assumeRole(String, String, AssumeRoleOptions)
    */
   @Named("AssumeRole")
   @POST
   @Path("/")
   @XMLResponseParser(UserAndSessionCredentialsHandler.class)
   @FormParams(keys = "Action", values = "AssumeRole")
   ListenableFuture<UserAndSessionCredentials> assumeRole(@FormParam("RoleArn") String roleArn,
         @FormParam("RoleSessionName") String sessionName, AssumeRoleOptions options);
   
   /**
    * @see STSApi#createFederatedUser(String)
    */
   @Named("GetFederationToken")
   @POST
   @Path("/")
   @XMLResponseParser(UserAndSessionCredentialsHandler.class)
   @FormParams(keys = "Action", values = "GetFederationToken")
   ListenableFuture<UserAndSessionCredentials> createFederatedUser(@FormParam("Name") String userName);

   /**
    * @see STSApi#createFederatedUser(FederatedUserOptions)
    */
   @Named("GetFederationToken")
   @POST
   @Path("/")
   @XMLResponseParser(UserAndSessionCredentialsHandler.class)
   @FormParams(keys = "Action", values = "GetFederationToken")
   ListenableFuture<UserAndSessionCredentials> createFederatedUser(@FormParam("Name") String userName, FederatedUserOptions options);
}
