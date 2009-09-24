/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.http;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public interface HttpConstants {
   public static final String PROPERTY_HTTP_MAX_RETRIES = "jclouds.http.max-retries";
   public static final String PROPERTY_HTTP_MAX_REDIRECTS = "jclouds.http.max-redirects";
   public static final String PROPERTY_SAX_DEBUG = "jclouds.http.sax.debug";
   public static final String PROPERTY_JSON_DEBUG = "jclouds.http.json.debug";

   /**
    * longest time a single request can take before throwing an exception.
    */
   public static final String PROPERTY_HTTP_REQUEST_TIMEOUT = "jclouds.http.request.timeout";
   /**
    * allow mismatch between hostname and ssl cerificate.
    */
   public static final String PROPERTY_HTTP_RELAX_HOSTNAME = "jclouds.http.relax-hostname";
}
