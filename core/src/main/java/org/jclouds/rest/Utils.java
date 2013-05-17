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
package org.jclouds.rest;

import java.util.Map;

import org.jclouds.crypto.Crypto;
import org.jclouds.date.DateService;
import org.jclouds.domain.Credentials;
import org.jclouds.json.Json;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.internal.UtilsImpl;
import org.jclouds.xml.XMLParser;

import com.google.common.annotations.Beta;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.ImplementedBy;
import com.google.inject.Injector;

/**
 * 
 * 
 * @author Adrian Cole
 * 
 */
@ImplementedBy(UtilsImpl.class)
public interface Utils {

   /**
    * retrieves a list of credentials for resources created within this context, keyed on {@code id}
    * of the resource with a namespace prefix (ex. {@code node#}. We are testing this approach for
    * resources such as compute nodes, where you could access this externally.
    * <p/>
    * <h4>accessing credentials for a node</h4>
    * <p/>
    * the key is in the form {@code node#id}.
    * <ul>
    * <li>if the node id is {@code 8}, then the key will be {@code node#8}</li>
    * <li>if the node id is {@code us-east-1/i-asdfdas}, then the key will be {@code
    * node#us-east-1/i-asdfdas}</li>
    * <li>if the node id is {@code http://cloud/instances/1}, then the key will be {@code
    * node#http://cloud/instances/1}</li>
    * </ul>
    */
   @Beta
   Map<String, Credentials> credentialStore();

   Json json();

   /**
    * 
    * @deprecated will be removed in jclouds 1.7, as async interfaces are no
    *             longer supported.
    */
   @Deprecated
   HttpAsyncClient asyncHttp();

   HttpClient http();

   Crypto crypto();

   DateService date();

   /**
    * @deprecated will be removed in jclouds 1.7, as async interfaces are no
    *             longer supported.
    */
   @Deprecated
   ListeningExecutorService userExecutor();

   /**
    * @deprecated will be removed in jclouds 1.7, as async interfaces are no
    *             longer supported.
    */
   @Deprecated
   ListeningExecutorService ioExecutor();

   EventBus eventBus();

   LoggerFactory loggerFactory();

   @Beta
   Injector injector();

   XMLParser xml();

}
