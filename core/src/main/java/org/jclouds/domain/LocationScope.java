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
package org.jclouds.domain;

/**
 * The scope of the location
 * 
 * @author Adrian Cole
 */
public enum LocationScope {

    PROVIDER,
    REGION,
    ZONE,
    /**
     * @see <a href="http://dmtf.org/standards/cloud">cimi system docs</a>
     */
    SYSTEM,
    /**
     * E.g. the DMZ segment, secure segment. 
     */
    NETWORK,
    RACK,
    HOST;

}
