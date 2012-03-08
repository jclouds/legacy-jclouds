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

   public static final String OK = "ERR-200: ok";

   /* Error codes from 100 to 199 reflect parsing and other errors in domain objects. */

   public static final String REF_REQ_LIVE = "ERR-101: %s reference required to perform live tests";

   public static final String OBJ_REQ_LIVE = "ERR-102: %s instance required to perform live tests";

   public static final String OBJ_FIELD_REQ_LIVE = "ERR-103: %s must have a non-null \"%s\" to perform live tests";

   public static final String OBJ_FIELD_REQ = "ERR-103: %s must always have a non-null field \"%s\"";

   public static final String OBJ_FIELD_ATTRB_REQ = "ERR-105: %s %s (%s) must always have a non-null field \"%s\"";

   public static final String OBJ_FIELD_EQ = "ERR-106: %s %s must have the value \"%s\" (%s)";

   public static final String OBJ_FIELD_CONTAINS = "ERR-107: %s %s must contain the values \"%s\" (%s)";

   public static final String OBJ_FIELD_GTE_0 = "ERR-108: %s field %s must be greater than to equal to 0 (%d)";

   public static final String GETTER_RETURNS_SAME_OBJ = "ERR-109: %s should return the same %s as %s (%s, %s)";

   public static final String OBJ_FIELD_UPDATABLE = "ERR-110: %s field %s should be updatable";

   public static final String OBJ_FIELD_ATTRB_DEL = "ERR-111: %s %s (%s) should have deleted field \"%s\" (%s)";

   public static final String OBJ_DEL = "ERR-112: %s (%s) should have been deleted";

   public static final String TASK_COMPLETE_TIMELY = "ERR-113: Task %s should complete in a timely fashion";

   public static final String NOT_NULL_OBJECT_FMT = "ERR-114: The %s field of the %s must not be null";
   
   public static final String NOT_EMPTY_OBJECT_FMT = "ERR-115: One or more %s fields of the %s must be present";
   
   public static final String REQUIRED_VALUE_OBJECT_FMT = "ERR-116: The %s field of the %s must not be '%s'; allowed values: %s";

   public static final String REQUIRED_VALUE_FMT = "ERR-117: The %s field must not be '%s'; allowed values: %s";

   public static final String MUST_BE_WELL_FORMED_FMT = "ERR-118: The %s field must be well formed: '%s'";

   public static final String MUST_EXIST_FMT = "ERR-119: The '%s' %s must exist";
   
   public static final String MUST_CONTAIN_FMT = "ERR-120: The %s field must contain '%s': '%s'";

   public static final String CONDITION_FMT = "ERR-121: The %s field must be %s: '%s'";

   public static final String CORRECT_VALUE_OBJECT_FMT = "ERR-122: The %s field of the %s must be '%s': '%s'";
   
   public static final String OBJ_FIELD_CLONE = "ERR-123: %s %s must be a clone of \"%s\" (%s)";
   
   public static final String OBJ_FIELD_EMPTY_TO_DELETE = "ERR-124: %s must have no %s to be deleted (%s)";

   /* Error codes from 300 to 399 reflect entities and their links and relationship errors. */ 
   
   public static final String ENTITY_NON_NULL = "ERR-301: The %s entity must not be null";

   public static final String ENTITY_EQUAL = "ERR-302: The two %s entities must be equal";

}
