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
package org.jclouds.iam;

import org.jclouds.iam.domain.User;
import org.jclouds.iam.features.UserApi;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides access to Amazon IAM via the Query API
 * <p/>
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/IAM/latest/APIReference"
 *      />
 * @author Adrian Cole
 */
public interface IAMApi {
   /**
    * Retrieves information about the current user, including the user's path, GUID, and ARN.
    */
   User getCurrentUser();

   /**
    * Provides synchronous access to User features.
    */
   @Delegate
   UserApi getUserApi();
}
