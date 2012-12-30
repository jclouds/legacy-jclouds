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
package org.jclouds.openstack.swift.v1.features;

import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.options.ListContainersOptions;

import com.google.common.collect.FluentIterable;

/**
 * Storage Container Services
 * 
 * @see ContainerAsyncApi
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-object-storage/1.0/content/storage-container-services.html"
 *      >api doc</a>
 */
public interface ContainerApi {

   /**
    * @see #list(ListContainersOptions)
    */
   FluentIterable<? extends Container> list();

   /**
    * retrieve a list of existing storage containers ordered by name. The sort order for the name is
    * based on a binary comparison, a single built-in collating sequence that compares string data
    * using SQLite's memcmp() function, regardless of text encoding.
    * 
    * @param options
    * @return a list of existing storage containers ordered by name.
    */
   FluentIterable<? extends Container> list(ListContainersOptions options);

}
