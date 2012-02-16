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
package org.jclouds.vcloud.director.v1_5;

/**
 * @author grkvlt@apache.org
 */
public class VCloudDirectorLiveTestConstants {

   public static final String REF_REQ_LIVE = "%s reference required to perform live tests";
   public static final String OBJ_REQ_LIVE = "%s instance required to perform live tests";
   public static final String OBJ_FIELD_REQ_LIVE = "%s must have a non-null \"%s\" to perform live tests";
   public static final String OBJ_FIELD_REQ = "%s must always have a non-null field \"%s\"";
   public static final String OBJ_FIELD_ATTRB_REQ = "%s %s (%s) must always have a non-null field \"%s\"";
   public static final String OBJ_FIELD_EQ = "%s %s must have the value \"%s\" (%s)";
   public static final String OBJ_FIELD_CONTAINS = "%s %s must contain the values \"%s\" (%s)";
   public static final String OBJ_FIELD_GTE_0 = "%s field %s must be greater than to equal to 0 (%d)";
   public static final String GETTER_RETURNS_SAME_OBJ = "%s should return the same %s as %s (%s, %s)";
   public static final String OBJ_FIELD_UPDATABLE = "%s field %s should be updatable";
   public static final String OBJ_FIELD_ATTRB_DEL = "%s %s (%s) should have deleted field \"%s\" (%s)";
   public static final String OBJ_DEL = "%s (%s) should have been deleted";
   public static final String TASK_COMPLETE_TIMELY = "Task %s should complete in a timely fashion";
   
   @Deprecated
   public static final String FIELD_NOT_NULL_FMT = "The %s field of the %s must not be null";

}
