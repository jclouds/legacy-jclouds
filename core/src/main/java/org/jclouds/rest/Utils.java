/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.rest;

import java.util.concurrent.ExecutorService;

import org.jclouds.crypto.Crypto;
import org.jclouds.date.DateService;
import org.jclouds.json.Json;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.internal.UtilsImpl;
import org.jclouds.xml.XMLParser;

import com.google.common.annotations.Beta;
import com.google.common.eventbus.EventBus;
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

   Json getJson();

   /**
    * #see #getJson
    */
   Json json();

   HttpAsyncClient getHttpAsyncClient();

   /**
    * #see #getHttpAsyncClient
    */
   HttpAsyncClient asyncHttp();

   HttpClient getHttpClient();

   /**
    * #see #getHttpClient
    */
   HttpClient http();

   Crypto getCrypto();

   /**
    * #see #getCrypto
    */
   Crypto crypto();

   DateService getDateService();

   /**
    * #see #getDateService
    */
   DateService date();

   ExecutorService getUserExecutor();

   /**
    * #see #getUserExecutor
    */
   ExecutorService userExecutor();

   ExecutorService getIoExecutor();

   /**
    * #see #getIoExecutor
    */
   ExecutorService ioExecutor();

   @Beta
   EventBus getEventBus();

   EventBus eventBus();

   LoggerFactory getLoggerFactory();

   /**
    * #see #getLoggerFactory
    */
   LoggerFactory loggerFactory();


   @Beta
   Injector getInjector();

   /**
    * #see #getInjector
    */
   @Beta
   Injector injector();
   
   XMLParser getXml();

   /**
    * #see #getXml
    */
   XMLParser xml();

}
