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
package org.jclouds.cloudstack.features;

import org.jclouds.cloudstack.domain.Cluster;
import org.jclouds.cloudstack.domain.Host;
import org.jclouds.cloudstack.options.ListClustersOptions;
import org.jclouds.cloudstack.options.ListHostsOptions;
import org.jclouds.concurrent.Timeout;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Provides synchronous access to CloudStack host features.
 * <p/>
 *
 * @see org.jclouds.cloudstack.features.GlobalOfferingAsyncClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html" />
 * @author Andrei Savu
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface GlobalHostClient {

   /**
    * Lists hosts
    *
    * @param options
    *           if present, how to constrain the list.
    * @return hosts matching query, or empty set, if no service
    *         offerings are found
    */
   Set<Host> listHosts(ListHostsOptions... options);

   /**
    * Lists clusters
    *
    * @param options if present, how to constrain the list
    * @return clusters matching query, or empty set if no clusters match
    */
   Set<Cluster> listClusters(ListClustersOptions... options);
}
