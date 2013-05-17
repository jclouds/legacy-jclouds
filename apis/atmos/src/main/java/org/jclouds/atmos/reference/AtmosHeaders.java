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
package org.jclouds.atmos.reference;

/**
 * Query parameters common to Atmos apis.
 * 
 * @see <a href="https://community.emc.com/community/labs/atmos_online" />
 * @author Adrian Cole
 * 
 */
public interface AtmosHeaders {

   public static final String SIGNATURE = "x-emc-signature";
   public static final String LISTABLE_META = "x-emc-listable-meta";
   public static final String META = "x-emc-meta";
   public static final String LISTABLE_TAGS = "x-emc-listable-tags";
   public static final String TAGS = "x-emc-tags";
   public static final String USER_ACL = "x-emc-useracl";
   public static final String DATE = "x-emc-date";
   public static final String GROUP_ACL = "x-emc-groupacl";
   public static final String UID = "x-emc-uid";
   public static final String TOKEN = "x-emc-token";
   
}
