/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.http.pool;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public interface PoolConstants {
    public static final String PROPERTY_POOL_MAX_CONNECTIONS = "jclouds.pool.max_connections";
    public static final String PROPERTY_POOL_IO_WORKER_THREADS = "jclouds.http.pool.io_worker_threads";
    public static final String PROPERTY_POOL_REQUEST_INVOKER_THREADS = "jclouds.http.pool.request_invoker_threads";
    public static final String PROPERTY_POOL_MAX_SESSION_FAILURES = "jclouds.http.pool.max_session_failures";
    public static final String PROPERTY_POOL_MAX_CONNECTION_REUSE = "jclouds.http.pool.max_connection_reuse";
}
