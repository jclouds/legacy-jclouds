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
package org.jclouds.route53.features;

import java.util.concurrent.TimeUnit;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.concurrent.Timeout;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.route53.domain.Zone;
import org.jclouds.route53.domain.ZoneAndNameServers;
import org.jclouds.route53.options.ListZonesOptions;

/**
 * @see ZoneAsyncApi
 * @see <a href="http://docs.aws.amazon.com/Route53/latest/APIReference/ActionsOnHostedZones.html" />
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface ZoneApi {

   /**
    * Retrieves information about the specified zone, including its nameserver configuration
    * 
    * @param name
    *           Name of the zone to get information about.
    * @return null if not found
    */
   @Nullable
   ZoneAndNameServers get(String name);

   /**
    * Lists the zones that have the specified path prefix. If there are none, the action returns an
    * empty list.
    * 
    * <br/>
    * You can paginate the results using the {@link ListZonesOptions parameter}
    * 
    * @param options
    *           the options describing the zones query
    * 
    * @return the response object
    */
   IterableWithMarker<Zone> list(ListZonesOptions options);

   /**
    * Lists the zones that have the specified path prefix. If there are none, the action returns an
    * empty list.
    * 
    * @return the response object
    */
   PagedIterable<Zone> list();

}
