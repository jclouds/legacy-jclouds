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
package org.jclouds.http.internal;

import java.net.Proxy;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.jclouds.Constants;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.reflect.Invokable;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Records all http commands submitted, storing them in the given List<HttpCommand>.
 * 
 * @author Adrian Cole
 */
@Singleton
public class TrackingJavaUrlHttpCommandExecutorService extends JavaUrlHttpCommandExecutorService {

   private final List<HttpCommand> commandsInvoked;

   /**
    * Creates a guice module, which will bind in TrackingJavaUrlHttpCommandExecutorService and also bind
    * the given list so that it is used by the tracker.
    */
   public static Module newTrackingModule(final List<HttpCommand> commandsInvoked) {
      return new AbstractModule() {

         @Override
         protected void configure() {
            bind(JavaUrlHttpCommandExecutorService.class).to(
                     TrackingJavaUrlHttpCommandExecutorService.class);
            bind(new TypeLiteral<List<HttpCommand>>() {
            }).toInstance(commandsInvoked);
         }
      };
   }
   
   public static Invokable<?, ?> getInvokerOfRequestAtIndex(final Collection<HttpCommand> commandsInvoked, int index) {
      return getInvokerOfRequest(Iterables.get(commandsInvoked, index));
   }

   public static Invokable<?, ?> getInvokerOfRequest(HttpCommand commandInvoked) {
      return GeneratedHttpRequest.class.cast(commandInvoked.getCurrentRequest()).getInvocation().getInvokable();
   }

   public static List<Object> getArgsForRequestAtIndex(final Collection<HttpCommand> commandsInvoked, int index) {
      return GeneratedHttpRequest.class.cast(Iterables.get(commandsInvoked, index).getCurrentRequest()).getInvocation()
            .getArgs();
   }

   @Inject
   public TrackingJavaUrlHttpCommandExecutorService(HttpUtils utils, ContentMetadataCodec contentMetadataCodec,
            @Named(Constants.PROPERTY_IO_WORKER_THREADS) ListeningExecutorService ioExecutor,
            DelegatingRetryHandler retryHandler, IOExceptionRetryHandler ioRetryHandler,
            DelegatingErrorHandler errorHandler, HttpWire wire, @Named("untrusted") HostnameVerifier verifier,
            @Named("untrusted") Supplier<SSLContext> untrustedSSLContextProvider, Function<URI, Proxy> proxyForURI,
            List<HttpCommand> commandsInvoked) throws SecurityException, NoSuchFieldException {
      super(utils, contentMetadataCodec, ioExecutor, retryHandler, ioRetryHandler, errorHandler, wire, verifier,
               untrustedSSLContextProvider, proxyForURI);
      this.commandsInvoked = commandsInvoked;
   }

   @Override
   public ListenableFuture<HttpResponse> submit(HttpCommand command) {
      commandsInvoked.add(command);
      return super.submit(command);
   }

}
