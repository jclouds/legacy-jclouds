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

import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_MAX_REDIRECTS;
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_MAX_RETRIES;
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_PROXY_SYSTEM;
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_RELAX_HOSTNAME;
import static org.jclouds.http.pool.PoolConstants.PROPERTY_POOL_IO_WORKER_THREADS;
import static org.jclouds.http.pool.PoolConstants.PROPERTY_POOL_MAX_CONNECTIONS;
import static org.jclouds.http.pool.PoolConstants.PROPERTY_POOL_MAX_CONNECTION_REUSE;
import static org.jclouds.http.pool.PoolConstants.PROPERTY_POOL_MAX_SESSION_FAILURES;

import java.util.Properties;

import com.google.common.annotations.VisibleForTesting;

/**
 * Builds properties used in Http engines
 * 
 * @author Adrian Cole, Andrew Newdigate
 */
public class HttpPropertiesBuilder {

   /**
    * @see org.jclouds.http.HttpConstants.PROPERTY_HTTP_RELAX_HOSTNAME
    */
   public HttpPropertiesBuilder relaxSSLHostname(boolean relax) {
      properties.setProperty(PROPERTY_HTTP_RELAX_HOSTNAME, relax + "");
      return this;
   }

   /**
    * @see org.jclouds.http.HttpConstants.PROPERTY_HTTP_PROXY_SYSTEM
    */
   public HttpPropertiesBuilder useSystemProxies(boolean useSystemProxies) {
      properties.setProperty(PROPERTY_HTTP_PROXY_SYSTEM, useSystemProxies + "");
      return this;
   }

   /**
    * @see org.jclouds.http.HttpConstants.PROPERTY_HTTP_MAX_RETRIES
    */
   public HttpPropertiesBuilder withHttpMaxRetries(int httpMaxRetries) {
      properties.setProperty(PROPERTY_HTTP_MAX_RETRIES, Integer.toString(httpMaxRetries));
      return this;
   }

   /**
    * @see org.jclouds.http.HttpConstants.PROPERTY_HTTP_MAX_REDIRECTS
    */
   public HttpPropertiesBuilder withHttpMaxRedirects(int httpMaxRedirects) {
      properties.setProperty(PROPERTY_HTTP_MAX_REDIRECTS, Integer.toString(httpMaxRedirects));
      return this;
   }

   /**
    * @see org.jclouds.http.HttpConstants.pool.PoolConstants.PROPERTY_POOL_MAX_CONNECTION_REUSE
    */
   public HttpPropertiesBuilder withPoolMaxClientReuse(int poolMaxClientReuse) {
      properties.setProperty(PROPERTY_POOL_MAX_CONNECTION_REUSE, Integer
               .toString(poolMaxClientReuse));
      return this;
   }

   /**
    * @see org.jclouds.http.HttpConstants.pool.PoolConstants.PROPERTY_POOL_MAX_SESSION_FAILURES
    */
   public HttpPropertiesBuilder withPoolMaxSessionFailures(int poolMaxSessionFailures) {
      properties.setProperty(PROPERTY_POOL_MAX_SESSION_FAILURES, Integer
               .toString(poolMaxSessionFailures));
      return this;

   }

   /**
    * @see org.jclouds.http.HttpConstants.pool.PoolConstants.PROPERTY_POOL_IO_WORKER_THREADS
    */
   public HttpPropertiesBuilder withPoolIoWorkerThreads(int poolIoWorkerThreads) {
      properties
               .setProperty(PROPERTY_POOL_IO_WORKER_THREADS, Integer.toString(poolIoWorkerThreads));
      return this;

   }

   /**
    * @see org.jclouds.http.HttpConstants.pool.PoolConstants.PROPERTY_POOL_MAX_CONNECTIONS
    */
   public HttpPropertiesBuilder withPoolMaxClients(int poolMaxClients) {
      properties.setProperty(PROPERTY_POOL_MAX_CONNECTIONS, Integer.toString(poolMaxClients));
      return this;
   }

   protected final Properties properties;

   public HttpPropertiesBuilder() {
      this.properties = defaultProperties();
   }

   protected Properties defaultProperties() {
      return new Properties();
   }

   public HttpPropertiesBuilder(Properties properties) {
      this();
      this.properties.putAll(properties);
   }

   public HttpPropertiesBuilder withCredentials(String account, String key) {
      return this;
   }

   @VisibleForTesting
   public Properties build() {
      return properties;
   }
}
