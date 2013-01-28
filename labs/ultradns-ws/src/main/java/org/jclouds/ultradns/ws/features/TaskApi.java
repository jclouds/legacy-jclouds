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
package org.jclouds.ultradns.ws.features;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.ultradns.ws.domain.Task;

import com.google.common.collect.FluentIterable;

/**
 * @see TaskAsyncApi
 * @author Adrian Cole
 */
public interface TaskApi {
   /**
    * Runs a test task
    * 
    * @return guid of the task created
    */
   String runTest(String value);

   /**
    * Retrieves information about the specified task
    * 
    * @param guid
    *           guid of the task to get information about.
    * @return null if not found
    */
   @Nullable
   Task get(String guid);

   /**
    * Lists all tasks.
    */
   FluentIterable<Task> list();
   
   /**
    * clears a background task in either a COMPLETE or ERROR state. 
    * 
    * @param guid
    *           guid of the task to clear.
    */
   void clear(String guid);
}
