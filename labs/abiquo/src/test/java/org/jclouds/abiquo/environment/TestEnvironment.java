/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.environment;

/**
 * Base class fot test environment populators.
 * <p>
 * This class should be used to populate and clean the test environment used in
 * live tests.
 * 
 * @author Ignasi Barrera
 */
public interface TestEnvironment {
   /**
    * Builds the test environment.
    */
   public void setup() throws Exception;

   /**
    * Cleans the test environment.
    */
   public void tearDown() throws Exception;
}
