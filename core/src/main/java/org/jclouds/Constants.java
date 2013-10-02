/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds;

import java.net.Proxy;

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
    * Integer property. default (10)
    * <p/>
    * Amount of threads servicing scheduled tasks.
    */
   public static final String PROPERTY_SCHEDULER_THREADS = "jclouds.scheduler-threads";

   /**
    * Integer property. default (20)
    * <p/>
    * Limits the amount of connections per context.
    */
   public static final String PROPERTY_MAX_CONNECTIONS_PER_CONTEXT = "jclouds.max-connections-per-context";

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
    *Explicitly sets the host name of a proxy server.
    */
   public static final String PROPERTY_PROXY_HOST = "jclouds.proxy-host";

   /**
    * Integer property. default is 80 when {@link #PROPERTY_PROXY_TYPE} is
    * {@code HTTP}, and 1080 when {@link #PROPERTY_PROXY_TYPE} is {@code SOCKS}.
    * <p/>
    * Explicitly sets the port number of a proxy server.
    */
   public static final String PROPERTY_PROXY_PORT = "jclouds.proxy-port";

   /**
    * String property. default {@code HTTP}, valid options: {@code HTTP}, {@code SOCKS}.
    * <p/>
    * Explicitly sets the type of a proxy server.
    * 
    * @see Proxy.Type
    */
   public static final String PROPERTY_PROXY_TYPE = "jclouds.proxy-type";
   
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
    * Boolean property. Default true.
    * <p/>
    * If a proxy server is configured, it will be used for all types of schemes.
    * Set to false to not use a proxy server for sockets (such as ssh access).
    */
   public static final String PROPERTY_PROXY_FOR_SOCKETS = "jclouds.proxy-for-sockets";

   /**
    * Integer property.
    * <p/>
    * Commands are retried, if the problem on the server side was a resolvable conflict. However,
    * the maximum tries of a single command is bounded.
    */
   public static final String PROPERTY_MAX_RETRIES = "jclouds.max-retries";
   /**
    * Long property.
    * <p/>
    * Commands are retried, if the problem on the server side was a resolvable conflict. However,
    * the maximum tries of a single command is bounded. If {@link #PROPERTY_MAX_RETRIES} is greater
    * than zero, this property is used to determine the start delay. The delay is based on exponential
    * backoff algorithm. Default value for this property is 50 milliseconds.
    */
   public static final String PROPERTY_RETRY_DELAY_START = "jclouds.retries-delay-start";
   /**
    * Integer property.
    * <p/>
    * Commands are limited to only a certain amount of redirects.
    */
   public static final String PROPERTY_MAX_REDIRECTS = "jclouds.max-redirects";
   /**
    * Long property.
    * <p/>
    * Maximum duration in milliseconds a single request can take before throwing an exception.
    */
   public static final String PROPERTY_REQUEST_TIMEOUT = "jclouds.request-timeout";
   /**
    * Boolean property.
    * <p/>
    * allow mismatch between hostname and ssl certificate. Set to true in DNS_based services like
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
    * String property. default empty string
    * <p/>
    * Explicitly identifies the version of an api.
    */
   public static final String PROPERTY_API_VERSION = "jclouds.api-version";

   /**
    * String property.
    * <p/>
    * Explicitly identifies the build that the server jclouds connects to is running.
    * 
    * For example, for virtualbox, the api version may be {@code 4.1.8} while the build version is
    * {@code 4.1.8r75467}. Or a vcloud endpoint may be api version {@code 1.0} while the build is
    * {@code 1.5.0.0.124312}
    */
   public static final String PROPERTY_BUILD_VERSION = "jclouds.build-version";
   
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

   /**
    * Long properties
    * <p/>
    * Overrides timeouts on sync interfaces. Timeout value is in ms.
    * Here's an example of an override for a single method:
    * <p/>
    * <code>
    * #10 seconds <br/>
    * jclouds.timeouts.S3Client.bucketExists=10000
    * </code>
    * <p/>
    * Or for all methods:
    * <p/>
    * <code>
    * jclouds.timeouts.GridServerClient = 350000
    * </code>
    */
   public static final String PROPERTY_TIMEOUTS_PREFIX = "jclouds.timeouts.";
   
   /**
    * Boolean property. Default (true).
    * <p/>
    * Configures the response parsers to pretty print the payload when possible. 
    */
   public static final String PROPERTY_PRETTY_PRINT_PAYLOADS = "jclouds.payloads.pretty-print";

   /**
    * When true, strip the Expect: 100-continue header. Useful when interacting with
    * providers that don't properly support Expect headers. Defaults to false.
    */
   public static final String PROPERTY_STRIP_EXPECT_HEADER = "jclouds.strip-expect-header";
}
