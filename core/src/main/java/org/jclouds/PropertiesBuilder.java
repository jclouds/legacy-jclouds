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

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_CONNECTION_TIMEOUT;
import static org.jclouds.Constants.PROPERTY_CREDENTIAL;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTION_REUSE;
import static org.jclouds.Constants.PROPERTY_MAX_REDIRECTS;
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.jclouds.Constants.PROPERTY_MAX_SESSION_FAILURES;
import static org.jclouds.Constants.PROPERTY_PROVIDER;
import static org.jclouds.Constants.PROPERTY_PROXY_HOST;
import static org.jclouds.Constants.PROPERTY_PROXY_PASSWORD;
import static org.jclouds.Constants.PROPERTY_PROXY_PORT;
import static org.jclouds.Constants.PROPERTY_PROXY_SYSTEM;
import static org.jclouds.Constants.PROPERTY_PROXY_USER;
import static org.jclouds.Constants.PROPERTY_RELAX_HOSTNAME;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.Constants.PROPERTY_SO_TIMEOUT;
import static org.jclouds.Constants.PROPERTY_TRUST_ALL_CERTS;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;

import java.util.Properties;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;

/**
 * Builds properties used in Http engines
 * 
 * @author Adrian Cole, Andrew Newdigate
 */
public class PropertiesBuilder {

   /**
    * @see org.jclouds.Constants.PROPERTY_RELAX_HOSTNAME
    */
   public PropertiesBuilder relaxSSLHostname(boolean relax) {
      properties.setProperty(PROPERTY_RELAX_HOSTNAME, relax + "");
      return this;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_TRUST_ALL_CERTS
    */
   public PropertiesBuilder trustAllCerts(boolean trust) {
      properties.setProperty(PROPERTY_TRUST_ALL_CERTS, trust + "");
      return this;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_PROXY_SYSTEM
    */
   public PropertiesBuilder useSystemProxies(boolean useSystemProxies) {
      properties.setProperty(PROPERTY_PROXY_SYSTEM, useSystemProxies + "");
      return this;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_PROXY_HOST
    */
   public PropertiesBuilder withProxyHost(String proxyHost) {
      properties.setProperty(PROPERTY_PROXY_HOST, proxyHost);
      return this;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_PROXY_PORT
    */
   public PropertiesBuilder withProxyPort(int proxyPort) {
      properties.setProperty(PROPERTY_PROXY_PORT, Integer.toString(proxyPort));
      return this;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_PROXY_USER
    */
   public PropertiesBuilder withProxyUser(String proxyUser) {
      properties.setProperty(PROPERTY_PROXY_USER, proxyUser);
      return this;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_PROXY_PASSWORD
    */
   public PropertiesBuilder withProxyPassword(String proxyPassword) {
      properties.setProperty(PROPERTY_PROXY_PASSWORD, proxyPassword);
      return this;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_SO_TIMEOUT
    */
   public PropertiesBuilder withSOTimeout(long soTimeout) {
      properties.setProperty(PROPERTY_SO_TIMEOUT, Long.toString(soTimeout));
      return this;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_CONNECTION_TIMEOUT
    */
   public PropertiesBuilder withConnectionTimeout(long connectionTimeout) {
      properties.setProperty(PROPERTY_CONNECTION_TIMEOUT, Long.toString(connectionTimeout));
      return this;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_MAX_RETRIES
    */
   public PropertiesBuilder withMaxRetries(int httpMaxRetries) {
      properties.setProperty(PROPERTY_MAX_RETRIES, Integer.toString(httpMaxRetries));
      return this;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_MAX_REDIRECTS
    */
   public PropertiesBuilder withMaxRedirects(int httpMaxRedirects) {
      properties.setProperty(PROPERTY_MAX_REDIRECTS, Integer.toString(httpMaxRedirects));
      return this;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_MAX_CONNECTION_REUSE
    */
   public PropertiesBuilder withMaxClientReuse(int poolMaxClientReuse) {
      properties.setProperty(PROPERTY_MAX_CONNECTION_REUSE, Integer.toString(poolMaxClientReuse));
      return this;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_MAX_SESSION_FAILURES
    */
   public PropertiesBuilder withMaxSessionFailures(int poolMaxSessionFailures) {
      properties.setProperty(PROPERTY_MAX_SESSION_FAILURES, Integer.toString(poolMaxSessionFailures));
      return this;

   }

   /**
    * @see org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS
    */
   public PropertiesBuilder limitIoWorkerThreadsTo(int poolIoWorkerThreads) {
      properties.setProperty(PROPERTY_IO_WORKER_THREADS, Integer.toString(poolIoWorkerThreads));
      return this;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS
    */
   public PropertiesBuilder limitUserThreadsTo(int poolIoWorkerThreads) {
      properties.setProperty(PROPERTY_USER_THREADS, Integer.toString(poolIoWorkerThreads));
      return this;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT
    */
   public PropertiesBuilder limitConnectionsTo(int connectionLimit) {
      properties.setProperty(PROPERTY_MAX_CONNECTIONS_PER_CONTEXT, Integer.toString(connectionLimit));
      return this;
   }

   /**
    * @see org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST
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
      props.setProperty(PROPERTY_ISO3166_CODES, "");
      props.setProperty(PROPERTY_MAX_CONNECTIONS_PER_CONTEXT, 20 + "");
      props.setProperty(PROPERTY_MAX_CONNECTIONS_PER_HOST, 0 + "");
      props.setProperty(PROPERTY_SO_TIMEOUT, 60000 + "");
      props.setProperty(PROPERTY_CONNECTION_TIMEOUT, 60000 + "");
      props.setProperty(PROPERTY_IO_WORKER_THREADS, 20 + "");
      props.setProperty(PROPERTY_USER_THREADS, 0 + "");
      props.setProperty(PROPERTY_MAX_CONNECTION_REUSE, 75 + "");
      props.setProperty(PROPERTY_MAX_SESSION_FAILURES, 2 + "");
      props.setProperty(PROPERTY_SESSION_INTERVAL, 60 + "");
      return props;
   }

   public PropertiesBuilder(Properties properties) {
      this();
      this.properties.putAll(properties);
   }

   public PropertiesBuilder provider(String providerName) {
      properties.setProperty(PROPERTY_PROVIDER, providerName);
      return this;
   }

   public PropertiesBuilder endpoint(String endpoint) {
      properties.setProperty(PROPERTY_ENDPOINT, endpoint);
      return this;
   }

   public PropertiesBuilder iso3166Codes(Iterable<String> codes) {
      properties.setProperty(PROPERTY_ISO3166_CODES, Joiner.on(',').join(codes));
      return this;
   }

   public PropertiesBuilder apiVersion(String apiVersion) {
      properties.setProperty(PROPERTY_API_VERSION, apiVersion);
      return this;
   }

   public PropertiesBuilder credentials(String identity, @Nullable String credential) {
      properties.setProperty(PROPERTY_IDENTITY, identity);
      if (credential != null)
         properties.setProperty(PROPERTY_CREDENTIAL, credential);
      return this;
   }

   public PropertiesBuilder sessionInterval(long seconds) {
      properties.setProperty(PROPERTY_SESSION_INTERVAL, seconds + "");
      return this;
   }

   @VisibleForTesting
   public Properties build() {
      return properties;
   }
}
