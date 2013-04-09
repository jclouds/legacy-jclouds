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
package org.jclouds.dynect.v3;

import java.io.Closeable;

import javax.ws.rs.PathParam;

import org.jclouds.dynect.v3.domain.Job;
import org.jclouds.dynect.v3.features.RecordApi;
import org.jclouds.dynect.v3.features.SessionApi;
import org.jclouds.dynect.v3.features.ZoneApi;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides access to DynECT Managed DNS through the API2 api
 * <p/>
 * 
 * @see DynECTAsyncApi
 * @see <a href="https://manage.dynect.net/help/docs/api2/rest/"
 *      />
 * @author Adrian Cole
 */
public interface DynECTApi extends Closeable {
   /**
    * returns the current status of a job.
    * 
    * @param jobId
    *           The ID of the job
    * @return null, if not found
    */
   @Nullable
   Job getJob(long jobId);

   /**
    * Provides synchronous access to Session features.
    */
   @Delegate
   SessionApi getSessionApi();

   /**
    * Provides synchronous access to Zone features.
    */
   @Delegate
   ZoneApi getZoneApi();
   
   /**
    * Provides synchronous access to Record features
    */
   @Delegate
   RecordApi getRecordApiForZone(@PathParam("zone") String zone);
}