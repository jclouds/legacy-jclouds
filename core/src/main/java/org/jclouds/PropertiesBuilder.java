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
package org.jclouds;

import static org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTION_REUSE;
import static org.jclouds.Constants.PROPERTY_MAX_REDIRECTS;
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.jclouds.Constants.PROPERTY_MAX_SESSION_FAILURES;
import static org.jclouds.Constants.PROPERTY_PROXY_SYSTEM;
import static org.jclouds.Constants.PROPERTY_RELAX_HOSTNAME;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;

import java.net.URI;
import java.util.Properties;

import com.google.common.annotations.VisibleForTesting;

/**
 * Builds properties used in Http engines
 * 
 * @author Adrian Cole, Andrew Newdigate
 */
public abstract class PropertiesBuilder {

   /**
    * @see org.jclouds.http.Constants.PROPERTY_RELAX_HOSTNAME
    */
   public PropertiesBuilder relaxSSLHostname(boolean relax) {
      properties.setProperty(PROPERTY_RELAX_HOSTNAME, relax + "");
      return this;
   }

   /**
    * @see org.jclouds.http.Constants.PROPERTY_PROXY_SYSTEM
    */
   public PropertiesBuilder useSystemProxies(boolean useSystemProxies) {
      properties.setProperty(PROPERTY_PROXY_SYSTEM, useSystemProxies + "");
      return this;
   }

   /**
    * @see org.jclouds.http.Constants.PROPERTY_MAX_RETRIES
    */
   public PropertiesBuilder withHttpMaxRetries(int httpMaxRetries) {
      properties.setProperty(PROPERTY_MAX_RETRIES, Integer.toString(httpMaxRetries));
      return this;
   }

   /**
    * @see org.jclouds.http.Constants.PROPERTY_MAX_REDIRECTS
    */
   public PropertiesBuilder withHttpMaxRedirects(int httpMaxRedirects) {
      properties.setProperty(PROPERTY_MAX_REDIRECTS, Integer.toString(httpMaxRedirects));
      return this;
   }

   /**
    * @see org.jclouds.http.Constants.PROPERTY_MAX_CONNECTION_REUSE
    */
   public PropertiesBuilder withMaxClientReuse(int poolMaxClientReuse) {
      properties.setProperty(PROPERTY_MAX_CONNECTION_REUSE, Integer.toString(poolMaxClientReuse));
      return this;
   }

   /**
    * @see org.jclouds.http.Constants.PROPERTY_MAX_SESSION_FAILURES
    */
   public PropertiesBuilder withMaxSessionFailures(int poolMaxSessionFailures) {
      properties.setProperty(PROPERTY_MAX_SESSION_FAILURES, Integer
               .toString(poolMaxSessionFailures));
      return this;

   }

   /**
    * @see org.jclouds.http.Constants.PROPERTY_IO_WORKER_THREADS
    */
   public PropertiesBuilder limitIoWorkerThreadsTo(int poolIoWorkerThreads) {
      properties.setProperty(PROPERTY_IO_WORKER_THREADS, Integer.toString(poolIoWorkerThreads));
      return this;
   }

   /**
    * @see org.jclouds.http.Constants.PROPERTY_IO_WORKER_THREADS
    */
   public PropertiesBuilder limitUserThreadsTo(int poolIoWorkerThreads) {
      properties.setProperty(PROPERTY_USER_THREADS, Integer.toString(poolIoWorkerThreads));
      return this;
   }

   /**
    * @see org.jclouds.http.Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT
    */
   public PropertiesBuilder limitConnectionsTo(int connectionLimit) {
      properties.setProperty(PROPERTY_MAX_CONNECTIONS_PER_CONTEXT, Integer
               .toString(connectionLimit));
      return this;
   }

   /**
    * @see org.jclouds.http.Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST
    */
   public PropertiesBuilder limitConnectionsPerHostTo(int connectionLimit) {
      properties.setProperty(PROPERTY_MAX_CONNECTIONS_PER_HOST, Integer.toString(connectionLimit));
      return this;
   }

   protected final Properties properties;

   public PropertiesBuilder() {
      this.properties = defaultProperties();
   }

   protected Properties defaultProperties() {
      Properties props = new Properties();
      props.setProperty(PROPERTY_MAX_CONNECTIONS_PER_CONTEXT, 20 + "");
      props.setProperty(PROPERTY_MAX_CONNECTIONS_PER_HOST, 12 + "");
      props.setProperty(PROPERTY_IO_WORKER_THREADS, 20 + "");
      props.setProperty(PROPERTY_USER_THREADS, 0 + "");
      props.setProperty(PROPERTY_MAX_CONNECTION_REUSE, 75 + "");
      props.setProperty(PROPERTY_MAX_SESSION_FAILURES, 2 + "");
      return props;
   }

   public PropertiesBuilder(Properties properties) {
      this();
      this.properties.putAll(properties);
   }

   public abstract PropertiesBuilder withEndpoint(URI endpoint);

   public abstract PropertiesBuilder withCredentials(String account, String key);

   @VisibleForTesting
   public Properties build() {
      return properties;
   }
}
