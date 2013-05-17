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
package org.jclouds.gogrid.reference;

/**
 * @author Oleksiy Yarmula
 */
public interface GoGridQueryParams {

   public static final String ID_KEY = "id";
   public static final String NAME_KEY = "name";
   public static final String SERVER_ID_OR_NAME_KEY = "server";
   public static final String SERVER_TYPE_KEY = "server.type";

   public static final String DATACENTER_KEY = "datacenter";

   public static final String IS_SANDBOX_KEY = "isSandbox";
   public static final String IMAGE_KEY = "image";
   public static final String IP_KEY = "ip";

   public static final String SERVER_RAM_KEY = "server.ram";

   public static final String DESCRIPTION_KEY = "description";
   public static final String POWER_KEY = "power";

   public static final String MAX_NUMBER_KEY = "num_items";
   public static final String START_DATE_KEY = "startdate";
   public static final String END_DATE_KEY = "enddate";
   public static final String OWNER_KEY = "owner";

   public static final String JOB_STATE_KEY = "job.state";
   public static final String JOB_OBJECT_TYPE_KEY = "job.objecttype";

   public static final String OBJECT_KEY = "object";

   public static final String IP_STATE_KEY = "ip.state";
   public static final String IP_TYPE_KEY = "ip.type";

   public static final String LOAD_BALANCER_KEY = "loadbalancer";
   public static final String LOAD_BALANCER_TYPE_KEY = "loadbalancer.type";
   public static final String LOAD_BALANCER_PERSISTENCE_TYPE_KEY = "loadbalancer.persistence";
   public static final String VIRTUAL_IP_KEY = "virtualip.";
   public static final String REAL_IP_LIST_KEY = "realiplist.";

   public static final String IS_PUBLIC_KEY = "isPublic";
   public static final String IMAGE_TYPE_KEY = "image.type";
   public static final String IMAGE_STATE_KEY = "image.state";
   public static final String IMAGE_FRIENDLY_NAME_KEY = "friendlyName";
   public static final String IMAGE_DESCRIPTION_KEY = "description";

   public static final String LOOKUP_LIST_KEY = "lookup";
}
