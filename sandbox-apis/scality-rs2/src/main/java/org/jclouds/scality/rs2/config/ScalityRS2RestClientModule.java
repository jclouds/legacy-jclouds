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
package org.jclouds.scality.rs2.config;

import javax.inject.Singleton;

import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.config.S3RestClientModule;
import org.jclouds.scality.rs2.ScalityRS2AsyncClient;

import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
@RequiresHttp
public class ScalityRS2RestClientModule extends S3RestClientModule<S3Client, ScalityRS2AsyncClient> {

   public ScalityRS2RestClientModule() {
      super(S3Client.class, ScalityRS2AsyncClient.class);
   }

   @Provides
   @Singleton
   S3AsyncClient provideS3AsyncClient(ScalityRS2AsyncClient in) {
      return in;
   }

}
