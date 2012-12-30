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
package org.jclouds.azure.management.features;

import java.util.Set;
import org.jclouds.azure.management.domain.Location;

/**
 * The Service Management API includes operations for listing the available data center locations
 * for a hosted service in your subscription.
 * <p/>
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg441299" />
 * @see LocationAsyncApi
 * @author Adrian Cole
 */
public interface LocationApi {

   /**
    * The List Locations operation lists all of the data center locations that are valid for your
    * subscription.
    * 
    * @return the response object
    */
   Set<Location> list();

}
