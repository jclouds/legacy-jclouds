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

   /* danikov */

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
   
   /* grkvlt */

   public static final String NOT_NULL_OBJECT_FMT = "ERR-01: The %s field of the %s must not be null";
   
   public static final String NOT_EMPTY_OBJECT_FMT = "ERR-02: One or more %s fields of the %s must be present";
   
   public static final String REQUIRED_VALUE_OBJECT_FMT = "ERR-03: The %s field of the %s must not be '%s'; allowed values: %s";

   public static final String REQUIRED_VALUE_FMT = "ERR-04: The %s field must not be '%s'; allowed values: %s";

   public static final String MUST_BE_WELL_FORMED_FMT = "ERR-05: The %s field must be well formed: '%s'";

   public static final String MUST_EXIST_FMT = "ERR-06: The '%s' %s must exist";
   
   public static final String MUST_CONTAIN_FMT = "ERR-07: The %s field must contain '%s': '%s'";

   public static final String CONDITION_FMT = "ERR-08: The %s field must be %s: '%s'";

   public static final String CORRECT_VALUE_OBJECT_FMT = "ERR-09: The %s field of the %s must be '%s': '%s'";

}
