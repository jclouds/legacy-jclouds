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
package org.jclouds.http.config;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.http.TransformingHttpCommandExecutorServiceImpl;
import org.jclouds.http.internal.JavaUrlHttpCommandExecutorService;
import org.jclouds.logging.Logger;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * Configures {@link JavaUrlHttpCommandExecutorService}.
 * 
 * Note that this uses threads
 * 
 * @author Adrian Cole
 */
@ConfiguresHttpCommandExecutorService
public class JavaUrlHttpCommandExecutorServiceModule extends AbstractModule {

   @Override
   protected void configure() {
      bindClient();
   }

   protected void bindClient() {
      bind(HttpCommandExecutorService.class).to(JavaUrlHttpCommandExecutorService.class).in(
               Scopes.SINGLETON);
      bind(HostnameVerifier.class).to(LogToMapHostnameVerifier.class);
      bind(TransformingHttpCommandExecutorService.class).to(
               TransformingHttpCommandExecutorServiceImpl.class).in(Scopes.SINGLETON);
   }

   /**
    * 
    * Used to get more information about HTTPS hostname wrong errors.
    * 
    * @author Adrian Cole
    */
   @Singleton
   static class LogToMapHostnameVerifier implements HostnameVerifier {
      @Resource
      private Logger logger = Logger.NULL;
      private final Map<String, String> sslMap = Maps.newHashMap();;

      public boolean verify(String hostname, SSLSession session) {
         logger.warn("hostname was %s while session was %s", hostname, session.getPeerHost());
         sslMap.put(hostname, session.getPeerHost());
         return true;
      }
   }
}