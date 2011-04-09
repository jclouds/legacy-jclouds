/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.Utils;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.crypto.Crypto;
import org.jclouds.date.DateService;
import org.jclouds.json.Json;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.HttpAsyncClient;
import org.jclouds.rest.HttpClient;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshClient.Factory;

import com.google.common.base.Function;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class UtilsImpl extends org.jclouds.rest.internal.UtilsImpl implements Utils {
   @Inject(optional = true)
   private Factory sshFactory;
   private final Function<NodeMetadata, SshClient> sshForNode;

   @Inject
   UtilsImpl(Json json, HttpClient simpleClient, HttpAsyncClient simpleAsyncClient, Crypto encryption,
         DateService date, @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userThreads,
         @Named(Constants.PROPERTY_IO_WORKER_THREADS) ExecutorService ioThreads, LoggerFactory loggerFactory,
         Function<NodeMetadata, SshClient> sshForNode) {
      super(json, simpleClient, simpleAsyncClient, encryption, date, userThreads, ioThreads, loggerFactory);
      this.sshForNode = sshForNode;
   }

   @Override
   public Factory getSshClientFactory() {
      return sshFactory;
   }

   @Override
   public Factory sshFactory() {
      return sshFactory;
   }

   @Override
   public Function<NodeMetadata, SshClient> sshForNode() {
      return sshForNode;
   }

}
