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
package org.jclouds.oauth.v2.internal;

import com.google.common.reflect.TypeToken;
import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.oauth.v2.OAuthApiMetadata;
import org.jclouds.oauth.v2.OAuthAsyncClient;
import org.jclouds.oauth.v2.OAuthClient;
import org.jclouds.rest.RestContext;


/**
 * @author Adrian Cole
 */
public class BaseOAuthApiLiveTest extends BaseContextLiveTest<RestContext<OAuthClient, OAuthAsyncClient>> {

   public BaseOAuthApiLiveTest() {
      provider = "oauth";
   }
   
   @Override
   protected TypeToken<RestContext<OAuthClient, OAuthAsyncClient>> contextType() {
      return OAuthApiMetadata.CONTEXT_TOKEN;
   }

}

