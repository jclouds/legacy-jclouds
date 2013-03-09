/*
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

package org.jclouds.googlecompute;

import com.google.common.annotations.Beta;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;

/**
 * @author David Alves
 */
public interface GoogleComputeConstants {

   public static final String GOOGLE_PROVIDER_NAME = "google-compute";

   /**
    * The name of the project that keeps public resources.
    */
   public static final String GOOGLE_PROJECT = "google";

   public static final String COMPUTE_SCOPE = "https://www.googleapis.com/auth/compute";

   public static final String COMPUTE_READONLY_SCOPE = "https://www.googleapis.com/auth/compute.readonly";

   /**
    * The total time, in msecs, to wait for an operation to complete.
    */
   @Beta
   public static final String OPERATION_COMPLETE_TIMEOUT = "jclouds.google-compute.operation-complete-timeout";

   /**
    * The interval, in msecs, between calls to check whether an operation has completed.
    */
   @Beta
   public static final String OPERATION_COMPLETE_INTERVAL = "jclouds.google-compute.operation-complete-interval";

   public static final Location GOOGLE_PROVIDER_LOCATION = new LocationBuilder().scope(LocationScope.PROVIDER).id
           (GOOGLE_PROVIDER_NAME).description(GOOGLE_PROVIDER_NAME).build();

}
