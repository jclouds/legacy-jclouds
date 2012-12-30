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

/**
 * Storage Object Services An object represents the data and any metadata for the files stored in
 * the system. Through the ReST interface, metadata for an object can be included by adding custom
 * HTTP headers to the request and the data payload as the request body. Objects cannot exceed 5GB
 * and must have names that do not exceed 1024 bytes after URL encoding. However, objects larger
 * than 5GB can be segmented and then concatenated together so that you can upload 5 GB segments and
 * download a single concatenated object. You can work with the segments and manifests directly with
 * HTTP requests.
 * 
 * @see ObjectAsyncApi
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-object-storage/1.0/content/storage-object-services.html"
 *      >api doc</a>
 */
public interface ObjectApi {

}
