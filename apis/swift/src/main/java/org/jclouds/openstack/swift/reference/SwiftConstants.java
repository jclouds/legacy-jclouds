/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.swift.reference;


/**
 * Configuration properties and constants used in Cloud Files connections.
 * 
 * @author Adrian Cole
 */
public interface SwiftConstants {
   /**
    * For an integer value N, limits the number of results to at most N values.
    */
   String LIMIT = "limit";
   /**
    * Given a string value X, return Object names greater in value than the specified marker.
    */
   String MARKER = "marker";
   /**
    * For a string value X, causes the results to be limited to Object names beginning with the
    * substring X.
    */
   String PREFIX = "prefix";
   /**
    * For a string value X, return the Object names nested in the pseudo path.
    */
   String PATH = "path";

}
