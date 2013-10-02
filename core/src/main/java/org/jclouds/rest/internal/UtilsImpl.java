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
package org.jclouds.rest.internal;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.crypto.Crypto;
import org.jclouds.date.DateService;
import org.jclouds.domain.Credentials;
import org.jclouds.json.Json;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.HttpClient;
import org.jclouds.rest.Utils;
import org.jclouds.xml.XMLParser;

import com.google.common.annotations.Beta;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * @author Adrian Cole
 */
@Singleton
public class UtilsImpl implements Utils {

   private final Json json;
   private final HttpClient simpleClient;
   private final org.jclouds.rest.HttpAsyncClient simpleAsyncClient;
   private final Crypto encryption;
   private final DateService date;
   private final ListeningExecutorService userExecutor;
   private final ListeningExecutorService ioExecutor;
   private final EventBus eventBus;
   private final Map<String, Credentials> credentialStore;
   private final LoggerFactory loggerFactory;
   private Injector injector;
   private XMLParser xml;

   @Inject
   protected UtilsImpl(Injector injector, Json json, XMLParser xml, HttpClient simpleClient, org.jclouds.rest.HttpAsyncClient simpleAsyncClient,
         Crypto encryption, DateService date, @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
            @Named(Constants.PROPERTY_IO_WORKER_THREADS) ListeningExecutorService ioExecutor, EventBus eventBus,
            Map<String, Credentials> credentialStore, LoggerFactory loggerFactory) {
      this.injector = injector;
      this.json = json;
      this.simpleClient = simpleClient;
      this.simpleAsyncClient = simpleAsyncClient;
      this.encryption = encryption;
      this.date = date;
      this.userExecutor = userExecutor;
      this.ioExecutor = ioExecutor;
      this.eventBus = eventBus;
      this.credentialStore = credentialStore;
      this.loggerFactory = loggerFactory;
      this.xml = xml;
   }

   @Override
   @Deprecated
   public org.jclouds.rest.HttpAsyncClient asyncHttp() {
      return simpleAsyncClient;
   }

   @Override
   public DateService date() {
      return date;
   }

   @Override
   public Crypto crypto() {
      return encryption;
   }

   @Override
   public HttpClient http() {
      return simpleClient;
   }

   @Override
   public ListeningExecutorService ioExecutor() {
      return ioExecutor;
   }

   @Override
   public ListeningExecutorService userExecutor() {
      return userExecutor;
   }
   
   @Override
   public EventBus eventBus() {
      return eventBus;
   }

   @Override
   public LoggerFactory loggerFactory() {
      return loggerFactory;
   }

   @Override
   public Json json() {
      return json;
   }

   @Override
   @Beta
   public Injector injector() {
      return injector;
   }

   @Override
   public XMLParser xml() {
      return xml;
   }

   @Override
   public Map<String, Credentials> credentialStore() {
      return credentialStore;
   }

}
