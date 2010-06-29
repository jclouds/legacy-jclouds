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
package org.jclouds.compute.internal;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.Utils;
import org.jclouds.date.DateService;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.HttpAsyncClient;
import org.jclouds.rest.HttpClient;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshClient.Factory;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class UtilsImpl extends org.jclouds.rest.internal.UtilsImpl implements Utils {

   private final Factory sshFactory;

   @Inject
   UtilsImpl(HttpClient simpleClient, HttpAsyncClient simpleAsyncClient,
            EncryptionService encryption, DateService date,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userThreads,
            @Named(Constants.PROPERTY_IO_WORKER_THREADS) ExecutorService ioThreads,
            LoggerFactory loggerFactory, SshClient.Factory sshFactory) {
      super(simpleClient, simpleAsyncClient, encryption, date, userThreads, ioThreads,
               loggerFactory);
      this.sshFactory = sshFactory;
   }

   @Override
   public Factory getSshClientFactory() {
      return sshFactory;
   }

   @Override
   public Factory sshFactory() {
      return sshFactory;
   }

}
