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
package org.jclouds.http;

/**
 * Constants used in http services.
 * 
 * @author Adrian Cole
 */
public interface HttpConstants {
   /**
    * Boolean property.
    * <p/>
    * Whether or not to use the proxy setup from the underlying operating system.
    */
   public static final String PROPERTY_HTTP_PROXY_SYSTEM = "jclouds.http.proxy.system";
   /**
    * Integer property.
    * <p/>
    * Commands are retried, if the problem on the server side was a resolvable conflict. However,
    * the maximum tries of a single command is bounded.
    */
   public static final String PROPERTY_HTTP_MAX_RETRIES = "jclouds.http.max-retries";
   /**
    * Integer property.
    * <p/>
    * Commands are limited to only a certain amount of redirects.
    */
   public static final String PROPERTY_HTTP_MAX_REDIRECTS = "jclouds.http.max-redirects";
   /**
    * Long property.
    * <p/>
    * longest time a single request can take before throwing an exception.
    */
   public static final String PROPERTY_HTTP_REQUEST_TIMEOUT = "jclouds.http.request.timeout";
   /**
    * Boolean property.
    * <p/>
    * allow mismatch between hostname and ssl cerificate. Set to true in DNS-based services like
    * Amazon S3.
    */
   public static final String PROPERTY_HTTP_RELAX_HOSTNAME = "jclouds.http.relax-hostname";
   /**
    * Name of the logger that records all http headers from the client and the server.
    */
   public static final String LOGGER_HTTP_HEADERS = "jclouds.http.headers";
   /**
    * Name of the logger that records the content sent to and from the server.
    */
   public static final String LOGGER_HTTP_WIRE = "jclouds.http.wire";
   /**
    * Name of the logger that records the steps of the request signing process of the HTTP-service.
    */
   public static final String LOGGER_SIGNATURE = "jclouds.signature";

}
