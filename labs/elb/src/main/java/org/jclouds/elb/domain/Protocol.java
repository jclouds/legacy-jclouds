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

package org.jclouds.elb.domain;

/**
 * Specifies transport protocol to use for routing or the protocol to use for routing traffic to
 * back-end instances.
 * 
 * <h3>Note</h3> If the front-end protocol is HTTP or HTTPS, InstanceProtocol has to be at the same
 * protocol layer, i.e., HTTP or HTTPS. Likewise, if the front-end protocol is TCP or SSL,
 * InstanceProtocol has to be TCP or SSL.
 * 
 * <h3>Note</h3> If there is another listener with the same InstancePort whose InstanceProtocol is
 * secure, i.e., HTTPS or SSL, the listener's InstanceProtocol has to be secure, i.e., HTTPS or SSL.
 * If there is another listener with the same InstancePort whose InstanceProtocol is HTTP or TCP,
 * the listener's InstanceProtocol must be either HTTP or TCP.
 * 
 * @author Adrian Cole
 * @see <a href =
 *      "http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference/API_Listener.html">
 *      docs</a>
 */
public enum Protocol {

   HTTP, HTTPS, TCP, SSL,
   /**
    * The protocol was returned unrecognized.
    */
   UNRECOGNIZED;

}
