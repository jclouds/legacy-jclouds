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
package org.jclouds.cloudfiles;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.cloudfiles.blobstore.config.CloudFilesBlobStoreContextModule;
import org.jclouds.cloudfiles.config.CloudFilesRestClientModule;
import org.jclouds.openstack.internal.TestOpenStackAuthenticationModule;
import org.jclouds.openstack.swift.CommonSwiftClientTest;
import org.jclouds.openstack.swift.SwiftApiMetadata;
import org.jclouds.openstack.swift.SwiftAsyncClient;
import org.jclouds.openstack.swift.config.SwiftRestClientModule;
import org.jclouds.rest.HttpClient;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import java.util.Properties;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

/**
 * @author Andrei Savu
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "CloudFilesClientTest")
public class CloudFilesClientTest extends CommonSwiftClientTest {

   public static final long UNIX_EPOCH_TIMESTAMP = 123456789L;
   public static final String TEMPORARY_URL_KEY = "get-or-set-X-Account-Meta-Temp-Url-Key";

   protected String provider = "cloudfiles";

   public static class FixedCloudFilesBlobStoreContextModule extends CloudFilesBlobStoreContextModule {
      @Override
      protected Long unixEpochTimestampProvider() {
         return UNIX_EPOCH_TIMESTAMP;
      }

      @Override
      protected String temporaryUrlKeyProvider(CloudFilesClient _) {
         return TEMPORARY_URL_KEY;
      }
   }

   @Override
   protected ApiMetadata createApiMetadata() {
      return new CloudFilesApiMetadata().toBuilder().defaultModules(
          ImmutableSet.<Class<? extends Module>>of(StorageEndpointModule.class,
              CloudFilesRestClientModule.class, FixedCloudFilesBlobStoreContextModule.class)).build();
   }
}
