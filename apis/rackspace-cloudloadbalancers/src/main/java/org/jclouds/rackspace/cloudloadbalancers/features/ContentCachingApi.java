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
package org.jclouds.rackspace.cloudloadbalancers.features;

/**
 * When content caching is enabled, recently-accessed files are stored on the load balancer for easy retrieval by web 
 * clients. Content caching improves the performance of high traffic web sites by temporarily storing data that was 
 * recently accessed. While it's cached, requests for that data will be served by the load balancer, which in turn 
 * reduces load off the back end nodes. The result is improved response times for those requests and less load on the 
 * web server.
 * <p/>
 * 
 * @see ContentCachingAsyncApi
 * @author Everett Toews
 */
public interface ContentCachingApi {
   /**
    * Determine if the load balancer is content caching.
    */
   boolean isContentCaching();
   
   /**
    * Enable content caching.
    */
   void enable();
   
   /**
    * Disable content caching.
    */
   void disable();
}