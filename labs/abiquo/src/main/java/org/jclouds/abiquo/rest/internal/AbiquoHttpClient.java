/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.rest.internal;

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.http.HttpResponse;

import com.abiquo.model.rest.RESTLink;

/**
 * Custom Rest methods to work with the Abiquo Api.
 * 
 * @author Ignasi Barrera
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface AbiquoHttpClient {
   /**
    * Perform a GET request to the given link.
    * 
    * @param link
    *           The link to get.
    * @return The response.
    */
   public HttpResponse get(final RESTLink link);
}
