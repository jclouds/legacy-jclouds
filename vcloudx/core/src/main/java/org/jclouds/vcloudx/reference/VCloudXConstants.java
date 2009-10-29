/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloudx.reference;

/**
 * Configuration properties and constants used in VCloudX connections.
 * 
 * @author Adrian Cole
 */
public interface VCloudXConstants {
   public static final String PROPERTY_VCLOUDX_ENDPOINT = "jclouds.vcloudx.endpoint";
   public static final String PROPERTY_VCLOUDX_USER = "jclouds.vcloudx.user";
   public static final String PROPERTY_VCLOUDX_KEY = "jclouds.vcloudx.key";
   /**
    * automatically renew vcloud token before this interval expires.
    */
   public static final String PROPERTY_VCLOUDX_SESSIONINTERVAL = "jclouds.vcloudx.sessioninterval";
}
