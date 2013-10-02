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
package org.jclouds.proxy;

import java.net.Proxy.Type;

import org.jclouds.domain.Credentials;
import org.jclouds.proxy.internal.GuiceProxyConfig;

import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.google.inject.ImplementedBy;

/**
 * parameters needed to configure {@link java.net.Proxy}. Check presence of
 * {@link #getProxyHost()} to decide if proxy support should even be attempted.
 * 
 * @author Adrian Cole
 * 
 */
@ImplementedBy(GuiceProxyConfig.class)
public interface ProxyConfig {

   /**
    * @see org.jclouds.Constants#PROPERTY_PROXY_SYSTEM
    */
   boolean useSystem();
   
   /**
    * @see org.jclouds.Constants#PROPERTY_PROXY_TYPE
    */
   Type getType();
   
   /**
    * @see org.jclouds.Constants#PROPERTY_PROXY_HOST
    * @see org.jclouds.Constants#PROPERTY_PROXY_PORT
    */
   Optional<HostAndPort> getProxy();

   /**
    * @see org.jclouds.Constants#PROPERTY_PROXY_USER
    * @see org.jclouds.Constants#PROPERTY_PROXY_PASSWORD
    */
   Optional<Credentials> getCredentials();

}
