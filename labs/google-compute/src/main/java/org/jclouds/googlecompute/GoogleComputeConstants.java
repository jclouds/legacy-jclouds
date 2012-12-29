/*
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

package org.jclouds.googlecompute;

/**
 * @author David Alves
 */
public interface GoogleComputeConstants {

   public static final String COMPUTE_SCOPE = "https://www.googleapis.com/auth/compute";

   public static final String COMPUTE_READONLY_SCOPE = "https://www.googleapis.com/auth/compute.readonly";

   /**
    * TODO storage scopes should be moved to the GCS api
    */

   public static final String STORAGE_WRITEONLY_SCOPE = "https://www.googleapis.com/auth/devstorage.write_only";

   public static final String STORAGE_READONLY_SCOPE = "https://www.googleapis.com/auth/devstorage.read_only";

}
