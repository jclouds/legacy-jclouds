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
 * An error page is the html file that is shown to an end user who is attempting to access a load balancer node that 
 * is offline/unavailable. During provisioning, every load balancer is configured with a default error page that gets 
 * displayed when traffic is requested for an offline node. A single custom error page may be added per account load 
 * balancer with an HTTP protocol. Page updates will override existing content.
 * <p/>
 * 
 * @see ErrorPageAsyncApi
 * @author Everett Toews
 */
public interface ErrorPageApi {
   /**
    * Specify the HTML content for the custom error page. Must be 65536 characters or less.
    */
   void create(String content);
   
   /**
    * Get the error page HTML content.
    */
   String get();
   
   /**
    * If a custom error page is deleted, or the load balancer is changed to a non-HTTP protocol, the default error 
    * page will be restored.
    */
   boolean delete();
}