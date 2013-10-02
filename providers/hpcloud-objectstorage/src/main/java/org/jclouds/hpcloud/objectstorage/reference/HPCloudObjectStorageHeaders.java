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
package org.jclouds.hpcloud.objectstorage.reference;

import org.jclouds.openstack.swift.reference.SwiftHeaders;


/**
 * Additional headers specified by HP Cloud Object Storage REST API.
 * 
 * @see <a href="https://manage.hpcloud.com/pages/build/docs/objectstorage-lvs/api" />
 * @author Jeremy Daggett
 * 
 */
public interface HPCloudObjectStorageHeaders extends SwiftHeaders {

   public static final String CDN_ENABLED = "X-Cdn-Enabled";
   public static final String CDN_LOG_RETENTION = "X-Log-Retention";
   public static final String CDN_REFERRER_ACL = "X-Referrer-ACL";
   public static final String CDN_TTL = "X-Ttl";
   public static final String CDN_URI = "X-Cdn-Uri";
   public static final String CDN_USER_AGENT_ACL = "X-User-Agent-ACL";

}
