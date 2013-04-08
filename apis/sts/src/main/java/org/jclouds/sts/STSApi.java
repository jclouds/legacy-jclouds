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

import org.jclouds.aws.domain.SessionCredentials;
import org.jclouds.sts.domain.User;
import org.jclouds.sts.domain.UserAndSessionCredentials;
import org.jclouds.sts.options.AssumeRoleOptions;
import org.jclouds.sts.options.FederatedUserOptions;
import org.jclouds.sts.options.SessionCredentialsOptions;

/**
 * Provides access to Amazon STS via the Query API
 * <p/>
 * 
 * @see STSAsyncApi
 * @see <a href="http://docs.amazonwebservices.com/STS/latest/APIReference" />
 * @author Adrian Cole
 */
public interface STSApi extends Closeable {
   /**
    * Returns a set of temporary credentials for an AWS account or IAM user,
    * with a default timeout
    */
   SessionCredentials createTemporaryCredentials();

   /**
    * like {@link #createTemporaryCredentials()}, except you can modify the
    * timeout and other parameters.
    */
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
   UserAndSessionCredentials assumeRole(String roleArn, String sessionName);
   
   /**
    * like {@link #assumeRole(String, String)}, except you can modify the
    * timeout and other parameters.
    */
   UserAndSessionCredentials assumeRole(String roleArn, String sessionName, AssumeRoleOptions options);
   
   /**
    * Returns a set of temporary credentials for a federated user with the user
    * name specified.
    * 
    * @param userName
    *           The name of the federated user, included as part of
    *           {@link User#getId}.
    */
   UserAndSessionCredentials createFederatedUser(String userName);
   
   /**
    * like {@link #createFederatedUser(String)}, except you can modify the
    * timeout and other parameters.
    */
   UserAndSessionCredentials createFederatedUser(String userName, FederatedUserOptions options);

}
