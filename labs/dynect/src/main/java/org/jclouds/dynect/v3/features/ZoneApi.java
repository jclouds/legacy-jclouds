/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, String 2.0 (the
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
package org.jclouds.dynect.v3.features;

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.dynect.v3.domain.Zone;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.FluentIterable;

/**
 * @see ZoneAsyncApi
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface ZoneApi {
   /**
    * Lists all zone ids.
    */
   FluentIterable<String> list();

   /**
    * Retrieves information about the specified zone, including its nameserver
    * configuration
    * 
    * @param name
    *           name of the zone to get information about. ex
    *           {@code Z1PA6795UKMFR9}
    * @return null if not found
    */
   @Nullable
   Zone get(String name);
}
