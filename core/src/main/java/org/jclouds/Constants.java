/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds;

import org.jclouds.domain.Location;
import org.jclouds.location.reference.LocationConstants;

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
   public static final String PROPERTY_USER_THREADS = "jclouds.user-threads";

   /**
    * Integer property. default (20)
    * <p/>
    * Amount of threads servicing the I/O of http connections.
    */
   public static final String PROPERTY_IO_WORKER_THREADS = "jclouds.io-worker-threads";

   /**
    * Integer property. default (20)
    * <p/>
    * Limits the amount of connections per context.
    */
   public static final String PROPERTY_MAX_CONNECTIONS_PER_CONTEXT = "jclouds.max-connections-per_context";

   /**
    * Integer property. default (0)
    * <p/>
    * Limits the amount of connections per host. 0 means indirectly limited by
    * {@link #PROPERTY_MAX_CONNECTIONS_PER_CONTEXT}.
    */
   public static final String PROPERTY_MAX_CONNECTIONS_PER_HOST = "jclouds.max-connections-per-host";

   /**
    * Integer property. default (2)
    * <p/>
    * Maximum amount of http session failures before a pool is disabled.
    */
   public static final String PROPERTY_MAX_SESSION_FAILURES = "jclouds.max-session-failures";
   /**
    * Integer property. default (75)
    * <p/>
    * Maximum amount of times to re_use an http connection. Services like Amazon S3 throw errors if
    * connections are reused too many times.
    */
   public static final String PROPERTY_MAX_CONNECTION_REUSE = "jclouds.max-connection-reuse";

   /**
    * int property. default (60000)
    * <p/>
    * How many milliseconds to wait before a socket connection times out. 0 means infinity.
    */
   public static final String PROPERTY_SO_TIMEOUT = "jclouds.so-timeout";

   /**
    * Long property. default (60000)
    * <p/>
    * How many milliseconds to wait before a connection times out. 0 means infinity.
    */
   public static final String PROPERTY_CONNECTION_TIMEOUT = "jclouds.connection-timeout";

   /**
    * Long property. default (60)
    * <p/>
    * How many seconds to wait before creating a new session
    */
   public static final String PROPERTY_SESSION_INTERVAL = "jclouds.session-interval";

   /**
    * Boolean property.
    * <p/>
    * Whether or not to use the proxy setup from the underlying operating system.
    */
   public static final String PROPERTY_PROXY_SYSTEM = "jclouds.use-system-proxy";
   /**
    * String property.
    * <p/>
    *Explicitly sets the host name of a HTTP proxy server.
    */
   public static final String PROPERTY_PROXY_HOST = "jclouds.proxy-host";
   /**
    * Integer property.
    * <p/>
    * Explicitly sets the port number of a HTTP proxy server.
    */
   public static final String PROPERTY_PROXY_PORT = "jclouds.proxy-port";
   /**
    * String property.
    * <p/>
    * Explicitly sets the user name credential for proxy authentication.
    */
   public static final String PROPERTY_PROXY_USER = "jclouds.proxy-user";
   /**
    * String property.
    * <p/>
    * Explicitly sets the password credential for proxy authentication.
    */
   public static final String PROPERTY_PROXY_PASSWORD = "jclouds.proxy-password";

   /**
    * Integer property.
    * <p/>
    * Commands are retried, if the problem on the server side was a resolvable conflict. However,
    * the maximum tries of a single command is bounded.
    */
   public static final String PROPERTY_MAX_RETRIES = "jclouds.max-retries";
   /**
    * Integer property.
    * <p/>
    * Commands are limited to only a certain amount of redirects.
    */
   public static final String PROPERTY_MAX_REDIRECTS = "jclouds.max-redirects";
   /**
    * Long property.
    * <p/>
    * longest time a single request can take before throwing an exception.
    */
   public static final String PROPERTY_REQUEST_TIMEOUT = "jclouds.request-timeout";
   /**
    * Boolean property.
    * <p/>
    * allow mismatch between hostname and ssl cerificate. Set to true in DNS_based services like
    * Amazon S3.
    */
   public static final String PROPERTY_RELAX_HOSTNAME = "jclouds.relax-hostname";
   /**
    * Boolean property.
    * <p/>
    * trust self-signed certs
    */
   public static final String PROPERTY_TRUST_ALL_CERTS = "jclouds.trust-all-certs";
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
    * String property.
    * <p/>
    * Explicitly identifies a provider of an api
    */
   public static final String PROPERTY_PROVIDER = "jclouds.provider";

   /**
    * String property.
    * <p/>
    * Explicitly identifies the name of a product that a provider may run
    */
   public static final String PROPERTY_API = "jclouds.api";

   /**
    * String property.
    * <p/>
    * Explicitly identifies the version of an api.
    */
   public static final String PROPERTY_API_VERSION = "jclouds.api-version";

   /**
    * String property.
    * <p/>
    * Explicitly identifies the most top-level endpoint to a service provider. This helps
    * differentiate two providers of the same api, or a different environments providing the same
    * api.
    */
   public static final String PROPERTY_ENDPOINT = "jclouds.endpoint";

   /**
    * String property.
    * <p/>
    * Explicitly sets the login identity into a provider
    */
   public static final String PROPERTY_IDENTITY = "jclouds.identity";

   /**
    * String property. default("")
    * <p/>
    * comma-delimited iso 3166 codes; ex. US-CA,US
    * 
    * @see Location#getIso3166Codes
    */
   public static final String PROPERTY_ISO3166_CODES = "jclouds." + LocationConstants.ISO3166_CODES;

   /**
    * String property.
    * <p/>
    * Explicitly sets the secret, which when combined with the identity, will create an
    * authenticated subject or session
    */
   public static final String PROPERTY_CREDENTIAL = "jclouds.credential";

}
