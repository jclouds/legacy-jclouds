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
package org.jclouds.googlecompute.internal;

import com.google.common.reflect.TypeToken;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.googlecompute.GoogleComputeApi;
import org.jclouds.googlecompute.GoogleComputeApiMetadata;
import org.jclouds.googlecompute.GoogleComputeAsyncApi;
import org.jclouds.googlecompute.config.UserProject;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.oauth.v2.OAuthTestUtils;
import org.jclouds.predicates.PredicateWithResult;
import org.jclouds.predicates.Retryables;
import org.jclouds.rest.RestContext;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;


/**
 * @author David Alves
 */
public class BaseGoogleComputeApiLiveTest extends BaseContextLiveTest<RestContext<GoogleComputeApi,
        GoogleComputeAsyncApi>> {

   protected static final String API_URL_PREFIX = "https://www.googleapis.com/compute/v1beta13/projects/";
   protected static final String ZONE_API_URL_SUFFIX = "/zones/";
   protected static final String DEFAULT_ZONE_NAME = "us-east1-a";

   protected static final String NETWORK_API_URL_SUFFIX = "/networks/";
   protected static final String DEFAULT_NETWORK_NAME = "default";

   protected static final String MACHINE_TYPE_API_URL_SUFFIX = "/machineTypes/";
   protected static final String DEFAULT_MACHINE_TYPE_NAME = "n1-standard-1";

   protected static final String GOOGLE_PROJECT = "google";


   public BaseGoogleComputeApiLiveTest() {
      provider = "google-compute";
   }

   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      OAuthTestUtils.setCredentialFromPemFile(properties, "google-compute.credential");
      return properties;
   }

   @Override
   protected TypeToken<RestContext<GoogleComputeApi, GoogleComputeAsyncApi>> contextType() {
      return GoogleComputeApiMetadata.CONTEXT_TOKEN;
   }

   protected String getUserProject() {
      return context.utils().injector().getInstance(Key.get(String.class, UserProject.class));
   }

   protected PredicateWithResult<Operation, Operation> getOperationDonePredicate() {
      return context.utils().getInjector().getInstance(Key.get(new TypeLiteral<PredicateWithResult<Operation,
              Operation>>() {}));
   }

   protected Operation assertOperationDoneSucessfully(Operation operation, long maxWaitSeconds) {
      operation = waitOperationDone(operation, maxWaitSeconds);
      assertEquals(operation.getStatus(), Operation.Status.DONE);
      assertNull(operation.getError());
      return operation;
   }

   protected Operation waitOperationDone(Operation operation, long maxWaitSeconds) {
      return waitOperationDone(getOperationDonePredicate(), operation, maxWaitSeconds);
   }

   protected String getDefaultZoneUrl(String project) {
      return getZoneUrl(project, DEFAULT_ZONE_NAME);
   }

   protected String getZoneUrl(String project, String zone) {
      return API_URL_PREFIX + project + ZONE_API_URL_SUFFIX + zone;
   }

   protected String getDefaultNetworkUrl(String project) {
      return gettNetworkUrl(project, DEFAULT_NETWORK_NAME);
   }

   protected String gettNetworkUrl(String project, String network) {
      return API_URL_PREFIX + project + NETWORK_API_URL_SUFFIX + network;
   }

   protected String getDefaultMachineTypekUrl(String project) {
      return gettMachineTypeUrl(project, DEFAULT_MACHINE_TYPE_NAME);
   }

   protected String gettMachineTypeUrl(String project, String machineType) {
      return API_URL_PREFIX + project + MACHINE_TYPE_API_URL_SUFFIX + machineType;
   }

   protected static Operation waitOperationDone(PredicateWithResult<Operation,
           Operation> operationDonePredicate, Operation operation, long maxWaitSeconds) {
      return Retryables.retryGettingResultOrFailing(operationDonePredicate, operation, maxWaitSeconds, 1,
              TimeUnit.SECONDS, "Operation timeout: " + operation.getName());
   }


}

