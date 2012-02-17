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

   public static final String NOT_NULL_OBJECT_FMT = "ERR-01: The %s field of the %s must not be null";
   
   public static final String NOT_EMPTY_OBJECT_FMT = "ERR-02: One or more %s fields of the %s must be present";
   
   public static final String REQUIRED_VALUE_OBJECT_FMT = "ERR-03: The %s field of the %s must not be '%s'; allowed values: %s";

   public static final String REQUIRED_VALUE_FMT = "ERR-04: The %s field must not be '%s'; allowed values: %s";

   public static final String MUST_BE_WELL_FORMED_FMT = "ERR-05: The %s field must be well formed: '%s'";

   public static final String MUST_EXIST_FMT = "ERR-06: The '%s' %s must exist";
   
   public static final String MUST_CONTAIN_FMT = "ERR-07: The %s field must contain '%s': '%s'";

   public static final String CONDITION_FMT = "ERR-08: The %s field must be %s: '%s'";

   public static final String CORRECT_VALUE_OBJECT_FMT = "ERR-09: The %s field of the %s must be '%s': '%s'";
   
   public static final String TASK_COMPLETE_TIMELY = "Task %s should complete in a timely fashion";

}
