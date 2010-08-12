/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.aws.sqs.config;

import org.jclouds.aws.config.AWSFormSigningRestClientModule;
import org.jclouds.aws.sqs.SQSAsyncClient;
import org.jclouds.aws.sqs.SQSClient;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;

/**
 * Configures the SQS connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class SQSRestClientModule extends AWSFormSigningRestClientModule<SQSClient, SQSAsyncClient> {

   public SQSRestClientModule() {
      super(SQSClient.class, SQSAsyncClient.class);
   }

}