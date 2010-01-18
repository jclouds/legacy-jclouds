/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.http.pool;

/**
 * Properties used in pooling http engines
 * 
 * @author Adrian Cole
 */
public interface PoolConstants {
   /**
    * Integer property. default (12)
    * <p/>
    * Limits the amount of connections per host.
    */
   public static final String PROPERTY_POOL_MAX_CONNECTIONS = "jclouds.pool.max_connections";
   /**
    * Integer property. default (12)
    * <p/>
    * Amount of threads servicing the I/O of http connections
    */
   public static final String PROPERTY_POOL_IO_WORKER_THREADS = "jclouds.http.pool.io_worker_threads";

   /**
    * Integer property. default (2)
    * <p/>
    * Maximum amount of http session failures before a pool is disabled.
    */
   public static final String PROPERTY_POOL_MAX_SESSION_FAILURES = "jclouds.http.pool.max_session_failures";
   /**
    * Integer property. default (75)
    * <p/>
    * Maximum amount of times to re-use an http connection. Services like Amazon S3 throw errors if
    * connections are reused too many times.
    */
   public static final String PROPERTY_POOL_MAX_CONNECTION_REUSE = "jclouds.http.pool.max_connection_reuse";
}
