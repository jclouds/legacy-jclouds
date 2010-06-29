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
package org.jclouds.rest.internal;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.date.DateService;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.HttpAsyncClient;
import org.jclouds.rest.HttpClient;
import org.jclouds.rest.Utils;

import com.google.inject.Singleton;

/**
 * @author Adrian Cole
 */
@Singleton
public class UtilsImpl implements Utils {

   private final HttpClient simpleClient;
   private final HttpAsyncClient simpleAsyncClient;
   private final EncryptionService encryption;
   private final DateService date;
   private final ExecutorService userExecutor;
   private final ExecutorService ioExecutor;
   private final LoggerFactory loggerFactory;

   @Inject
   protected UtilsImpl(HttpClient simpleClient, HttpAsyncClient simpleAsyncClient,
            EncryptionService encryption, DateService date,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userThreads,
            @Named(Constants.PROPERTY_IO_WORKER_THREADS) ExecutorService ioThreads,
            LoggerFactory loggerFactory) {
      this.simpleClient = simpleClient;
      this.simpleAsyncClient = simpleAsyncClient;
      this.encryption = encryption;
      this.date = date;
      this.userExecutor = userThreads;
      this.ioExecutor = ioThreads;
      this.loggerFactory = loggerFactory;
   }

   @Override
   public HttpAsyncClient asyncHttp() {
      return simpleAsyncClient;
   }

   @Override
   public DateService date() {
      return date;
   }

   @Override
   public EncryptionService encryption() {
      return encryption;
   }

   @Override
   public DateService getDateService() {
      return date;
   }

   @Override
   public EncryptionService getEncryptionService() {
      return encryption;
   }

   @Override
   public HttpAsyncClient getHttpAsyncClient() {
      return simpleAsyncClient;
   }

   @Override
   public HttpClient getHttpClient() {
      return simpleClient;
   }

   @Override
   public HttpClient http() {
      return simpleClient;
   }

   @Override
   public ExecutorService getIoExecutor() {
      return ioExecutor;
   }

   @Override
   public ExecutorService getUserExecutor() {
      return userExecutor;
   }

   @Override
   public ExecutorService ioExecutor() {
      return ioExecutor;
   }

   @Override
   public ExecutorService userExecutor() {
      return userExecutor;
   }

   @Override
   public LoggerFactory getLoggerFactory() {
      return loggerFactory;
   }

   @Override
   public LoggerFactory loggerFactory() {
      return loggerFactory;
   }

}
