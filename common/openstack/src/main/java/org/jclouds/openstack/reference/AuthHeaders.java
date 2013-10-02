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
package org.jclouds.openstack.reference;

/**
 * Headers common to Rackspace apis.
 * 
 * @see <a href="http://docs.rackspacecloud.com/servers/api/cs-devguide-latest.pdf" />
 * @author Adrian Cole
 * 
 */
public interface AuthHeaders {

   public static final String AUTH_USER = "X-Auth-User";
   public static final String AUTH_KEY = "X-Auth-Key";
   public static final String STORAGE_USER = "X-Storage-User";
   public static final String STORAGE_PASS = "X-Storage-Pass";
   public static final String AUTH_TOKEN = "X-Auth-Token";
   public static final String URL_SUFFIX = "-Url";

   public static final String CDN_MANAGEMENT_URL = "X-CDN-Management" + URL_SUFFIX;
   public static final String SERVER_MANAGEMENT_URL = "X-Server-Management" + URL_SUFFIX;
   public static final String STORAGE_URL = "X-Storage" + URL_SUFFIX;

}
