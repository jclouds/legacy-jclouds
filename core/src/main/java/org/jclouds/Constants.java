/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE_2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds;

/**
 * Constants used in jclouds services.
 * 
 * @author Adrian Cole
 */
public interface Constants {
   /**
    * Integer property. default (0)
    * <p/>
    * Amount of threads servicing the user requests and transformations
    */
   public static final String PROPERTY_USER_THREADS = "jclouds.user_threads";

   /**
    * Integer property. default (20)
    * <p/>
    * Amount of threads servicing the I/O of http connections.
    */
   public static final String PROPERTY_IO_WORKER_THREADS = "jclouds.io_worker_threads";

   /**
    * Integer property. default (20)
    * <p/>
    * Limits the amount of connections per context.
    */
   public static final String PROPERTY_MAX_CONNECTIONS_PER_CONTEXT = "jclouds.max_connections_per_context";

   /**
    * Integer property. default (0)
    * <p/>
    * Limits the amount of connections per host. 0 means indirectly limited by
    * {@link #PROPERTY_MAX_CONNECTIONS_PER_CONTEXT}.
    */
   public static final String PROPERTY_MAX_CONNECTIONS_PER_HOST = "jclouds.max_connections_per_host";

   /**
    * Integer property. default (2)
    * <p/>
    * Maximum amount of http session failures before a pool is disabled.
    */
   public static final String PROPERTY_MAX_SESSION_FAILURES = "jclouds.max_session_failures";
   /**
    * Integer property. default (75)
    * <p/>
    * Maximum amount of times to re_use an http connection. Services like Amazon S3 throw errors if
    * connections are reused too many times.
    */
   public static final String PROPERTY_MAX_CONNECTION_REUSE = "jclouds.max_connection_reuse";

   /**
    * int property. default (60000)
    * <p/>
    * How many milliseconds to wait before a socket connection times out. 0 means infinity.
    */
   public static final String PROPERTY_SO_TIMEOUT = "jclouds.so_timeout";

   /**
    * Long property. default (60000)
    * <p/>
    * How many milliseconds to wait before a connection times out. 0 means infinity.
    */
   public static final String PROPERTY_CONNECTION_TIMEOUT = "jclouds.connection_timeout";

   /**
    * Boolean property.
    * <p/>
    * Whether or not to use the proxy setup from the underlying operating system.
    */
   public static final String PROPERTY_PROXY_SYSTEM = "jclouds.use_system_proxy";
   /**
    * Integer property.
    * <p/>
    * Commands are retried, if the problem on the server side was a resolvable conflict. However,
    * the maximum tries of a single command is bounded.
    */
   public static final String PROPERTY_MAX_RETRIES = "jclouds.max_retries";
   /**
    * Integer property.
    * <p/>
    * Commands are limited to only a certain amount of redirects.
    */
   public static final String PROPERTY_MAX_REDIRECTS = "jclouds.max_redirects";
   /**
    * Long property.
    * <p/>
    * longest time a single request can take before throwing an exception.
    */
   public static final String PROPERTY_HTTP_REQUEST_TIMEOUT = "jclouds.request_timeout";
   /**
    * Boolean property.
    * <p/>
    * allow mismatch between hostname and ssl cerificate. Set to true in DNS_based services like
    * Amazon S3.
    */
   public static final String PROPERTY_RELAX_HOSTNAME = "jclouds.relax_hostname";
   /**
    * Name of the logger that records all http headers from the client and the server.
    */
   public static final String LOGGER_HTTP_HEADERS = "jclouds.headers";
   /**
    * Name of the logger that records the content sent to and from the server.
    */
   public static final String LOGGER_HTTP_WIRE = "jclouds.wire";
   /**
    * Name of the logger that records the steps of the request signing process of the HTTP_service.
    */
   public static final String LOGGER_SIGNATURE = "jclouds.signature";
    /**
     * Name of the custom adapter bindings map for Gson
     */
   public static final String PROPERTY_GSON_ADAPTERS = "jclouds.gson.adapters";

}
