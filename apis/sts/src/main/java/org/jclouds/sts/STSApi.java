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
import org.jclouds.sts.domain.User;
import org.jclouds.sts.domain.UserAndSessionCredentials;
import org.jclouds.sts.options.AssumeRoleOptions;
import org.jclouds.sts.options.FederatedUserOptions;
import org.jclouds.sts.options.SessionCredentialsOptions;
import org.jclouds.sts.xml.UserAndSessionCredentialsHandler;

/**
 * Provides access to Amazon STS via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/STS/latest/APIReference" />
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface STSApi extends Closeable {
   /**
    * Returns a set of temporary credentials for an AWS account or IAM user,
    * with a default timeout
    */
   @Named("GetSessionToken")
   @POST
   @Path("/")
   @XMLResponseParser(SessionCredentialsHandler.class)
   @FormParams(keys = "Action", values = "GetSessionToken")
   SessionCredentials createTemporaryCredentials();

   /**
    * like {@link #createTemporaryCredentials()}, except you can modify the
    * timeout and other parameters.
    */
   @Named("GetSessionToken")
   @POST
   @Path("/")
   @XMLResponseParser(SessionCredentialsHandler.class)
   @FormParams(keys = "Action", values = "GetSessionToken")
   SessionCredentials createTemporaryCredentials(SessionCredentialsOptions options);

   /**
    * Assumes a role for a specified session. Only IAM users can assume a role.
    * 
    * @param sessionName
    *           An identifier for the assumed role session, included as part of
    *           {@link User#getId}.
    * @param roleArn
    *           The Amazon Resource Name (ARN) of the role that the caller is
    *           assuming.
    */
   @Named("AssumeRole")
   @POST
   @Path("/")
   @XMLResponseParser(UserAndSessionCredentialsHandler.class)
   @FormParams(keys = "Action", values = "AssumeRole")
   UserAndSessionCredentials assumeRole(@FormParam("RoleArn") String roleArn,
         @FormParam("RoleSessionName") String sessionName);
   
   /**
    * like {@link #assumeRole(String, String)}, except you can modify the
    * timeout and other parameters.
    */
   @Named("AssumeRole")
   @POST
   @Path("/")
   @XMLResponseParser(UserAndSessionCredentialsHandler.class)
   @FormParams(keys = "Action", values = "AssumeRole")
   UserAndSessionCredentials assumeRole(@FormParam("RoleArn") String roleArn,
         @FormParam("RoleSessionName") String sessionName, AssumeRoleOptions options);
   
   /**
    * Returns a set of temporary credentials for a federated user with the user
    * name specified.
    * 
    * @param userName
    *           The name of the federated user, included as part of
    *           {@link User#getId}.
    */
   @Named("GetFederationToken")
   @POST
   @Path("/")
   @XMLResponseParser(UserAndSessionCredentialsHandler.class)
   @FormParams(keys = "Action", values = "GetFederationToken")
   UserAndSessionCredentials createFederatedUser(@FormParam("Name") String userName);
   
   /**
    * like {@link #createFederatedUser(String)}, except you can modify the
    * timeout and other parameters.
    */
   @Named("GetFederationToken")
   @POST
   @Path("/")
   @XMLResponseParser(UserAndSessionCredentialsHandler.class)
   @FormParams(keys = "Action", values = "GetFederationToken")
   UserAndSessionCredentials createFederatedUser(@FormParam("Name") String userName, FederatedUserOptions options);
}
