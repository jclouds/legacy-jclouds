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
package org.jclouds.cloudstack.domain;

import com.google.common.base.CaseFormat;

/**
 * @author Adrian Cole
 * @see TemplateClient#listTemplates
 */
public enum TemplateFilter {
   /**
    * templates that are featured and are public
    */
   FEATURED,
   /**
    * templates that have been registered/created by the owner
    */
   SELF,
   /**
    * templates that have been registered/created by the owner that can be used
    * to deploy a new VM
    */
   SELF_EXECUTABLE,
   /**
    * templates that have been registered/created by the owner that can be used
    * to deploy a new VM - 3.x filter value.
    */
   SELFEXECUTABLE,
   /**
    * templates that have been granted to the calling user by another user
    */
   SHAREDEXECUTABLE,
   /**
    * all templates that can be used to deploy a new VM
    */
   EXECUTABLE,
   /**
    * templates that are public
    */
   COMMUNITY,
   /**
    * All templates
    */
   ALL;

   @Override
   public String toString() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name());
   }
}
