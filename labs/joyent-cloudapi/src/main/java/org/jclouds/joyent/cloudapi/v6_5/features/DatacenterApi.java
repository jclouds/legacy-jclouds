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
package org.jclouds.joyent.cloudapi.v6_5.features;

import java.net.URI;
import java.util.Map;
/**
 * Datacenter Services
 * 
 * @see DatacenterAsyncApi
 * @author Adrian Cole
 * @see <a href="http://apidocs.joyent.com/sdcapidoc/cloudapi/index.html#datacenters">api doc</a>
 */
public interface DatacenterApi {

   /**
    * Provides a list of all datacenters this cloud is aware of.
    * 
    * @return keys are the datacenter name, and the value is the URL.
    */
   Map<String, URI> getDatacenters();
}
