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
package org.jclouds.nirvanix.sdn.reference;

/**
 * Query parameters common to SDN apis.
 * 
 * @see <a href="http://developer.nirvanix.com/sitefiles/1000/API.html" />
 * @author Adrian Cole
 * 
 */
public interface SDNQueryParams {
   public static final String USERNAME = "username";
   public static final String PASSWORD = "password";
   public static final String APPKEY = "appKey";
   public static final String OUTPUT = "output";
   public static final String SESSIONTOKEN = "sessionToken";
   public static final String DESTFOLDERPATH = "destFolderPath";
   public static final String PATH = "path";
   public static final String SIZEBYTES = "sizeBytes";
   public static final String UPLOADTOKEN = "uploadToken";
}
