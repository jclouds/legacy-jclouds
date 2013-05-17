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
package org.jclouds.cloudfiles.reference;

import org.jclouds.openstack.swift.reference.SwiftHeaders;


/**
 * Additional headers specified by Rackspace Cloud Files REST API.
 * 
 * @see <a href="http://www.rackspacecloud.com/cf-devguide-20090311.pdf" />
 * @author Adrian Cole
 * 
 */
public interface CloudFilesHeaders extends SwiftHeaders {

   public static final String CDN_ENABLED = "X-CDN-Enabled";
   public static final String CDN_LOG_RETENTION = "X-Log-Retention";
   public static final String CDN_TTL = "X-TTL";
   public static final String CDN_URI = "X-CDN-URI";
   public static final String CDN_SSL_URI = "X-Cdn-Ssl-Uri";
   public static final String CDN_STREAMING_URI = "X-Cdn-Streaming-Uri";
   public static final String CDN_REFERRER_ACL = "X-Referrer-ACL ";
   public static final String CDN_USER_AGENT_ACL = "X-User-Agent-ACL";

   public static final String CDN_CONTAINER_PURGE_OBJECT_EMAIL = "X-Purge-Email";
   public static final String CDN_WEBSITE_INDEX = "X-Container-Meta-Web-Index";
   public static final String CDN_WEBSITE_ERROR = "X-Container-Meta-Web-Error";
}
