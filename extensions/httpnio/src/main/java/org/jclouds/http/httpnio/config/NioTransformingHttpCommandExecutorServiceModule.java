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
package org.jclouds.http.httpnio.config;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.entity.BufferingNHttpEntity;
import org.apache.http.nio.entity.ConsumingNHttpEntity;
import org.apache.http.nio.protocol.AsyncNHttpClientHandler;
import org.apache.http.nio.protocol.NHttpRequestExecutionHandler;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.jclouds.http.HttpCommandRendezvous;
import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.http.httpnio.pool.NioHttpCommandConnectionPool;
import org.jclouds.http.httpnio.pool.NioHttpCommandExecutionHandler;
import org.jclouds.http.httpnio.pool.NioTransformingHttpCommandExecutorService;
import org.jclouds.http.httpnio.pool.NioHttpCommandExecutionHandler.ConsumingNHttpEntityFactory;
import org.jclouds.http.pool.config.ConnectionPoolCommandExecutorServiceModule;
import org.jclouds.lifecycle.Closer;

import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresHttpCommandExecutorService
public class NioTransformingHttpCommandExecutorServiceModule extends
         ConnectionPoolCommandExecutorServiceModule<NHttpConnection> {

   @Provides
   // @Singleton per uri...
   public AsyncNHttpClientHandler provideAsyncNttpConnectionHandler(
            BasicHttpProcessor httpProcessor, NHttpRequestExecutionHandler execHandler,
            ConnectionReuseStrategy connStrategy, ByteBufferAllocator allocator, HttpParams params) {
      return new AsyncNHttpClientHandler(httpProcessor, execHandler, connStrategy, allocator,
               params);

   }

   @Provides
   @Singleton
   public BasicHttpProcessor provideConnectionProcessor() {
      BasicHttpProcessor httpproc = new BasicHttpProcessor();
      httpproc.addInterceptor(new RequestContent());
      httpproc.addInterceptor(new RequestTargetHost());
      httpproc.addInterceptor(new RequestConnControl());
      httpproc.addInterceptor(new RequestUserAgent());
      httpproc.addInterceptor(new RequestExpectContinue());
      return httpproc;
   }

   @Provides
   @Singleton
   public HttpParams provideHttpParams() {
      HttpParams params = new BasicHttpParams();
      params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000).setIntParameter(
               CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024).setBooleanParameter(
               CoreConnectionPNames.STALE_CONNECTION_CHECK, false).setBooleanParameter(
               CoreConnectionPNames.TCP_NODELAY, true).setParameter(
               CoreProtocolPNames.ORIGIN_SERVER, "jclouds/1.0");
      return params;
   }

   protected void configure() {
      super.configure();
      bind(TransformingHttpCommandExecutorService.class).to(
               NioTransformingHttpCommandExecutorService.class);
      bind(new TypeLiteral<BlockingQueue<HttpCommandRendezvous<?>>>() {
      }).to(new TypeLiteral<LinkedBlockingQueue<HttpCommandRendezvous<?>>>() {
      }).in(Scopes.SINGLETON);
      bind(NioHttpCommandExecutionHandler.ConsumingNHttpEntityFactory.class).to(
               ConsumingNHttpEntityFactoryImpl.class).in(Scopes.SINGLETON);
      bind(NHttpRequestExecutionHandler.class).to(NioHttpCommandExecutionHandler.class).in(
               Scopes.SINGLETON);
      bind(ConnectionReuseStrategy.class).to(DefaultConnectionReuseStrategy.class).in(
               Scopes.SINGLETON);
      bind(ByteBufferAllocator.class).to(HeapByteBufferAllocator.class);
      bind(NioHttpCommandConnectionPool.Factory.class).to(Factory.class).in(Scopes.SINGLETON);
   }

   private static class Factory implements NioHttpCommandConnectionPool.Factory {

      @Inject
      Closer closer;
      @Inject
      ExecutorService executor;
      @Inject
      javax.inject.Provider<Semaphore> allConnections;
      @Inject
      javax.inject.Provider<BlockingQueue<HttpCommandRendezvous<?>>> commandQueue;
      @Inject
      javax.inject.Provider<BlockingQueue<NHttpConnection>> available;
      @Inject
      javax.inject.Provider<AsyncNHttpClientHandler> clientHandler;
      @Inject
      javax.inject.Provider<DefaultConnectingIOReactor> ioReactor;
      @Inject
      HttpParams params;

      public NioHttpCommandConnectionPool create(URI endPoint) {
         NioHttpCommandConnectionPool pool = new NioHttpCommandConnectionPool(executor,
                  allConnections.get(), commandQueue.get(), available.get(), clientHandler.get(),
                  ioReactor.get(), params, endPoint);
         pool.start();
         closer.addToClose(new PoolCloser(pool));
         return pool;
      }

      private static class PoolCloser implements Closeable {
         private final NioHttpCommandConnectionPool pool;

         protected PoolCloser(NioHttpCommandConnectionPool pool) {
            this.pool = pool;
         }

         public void close() throws IOException {
            pool.shutdown();
         }
      }

   }

   private static class ConsumingNHttpEntityFactoryImpl implements ConsumingNHttpEntityFactory {
      @Inject
      javax.inject.Provider<ByteBufferAllocator> allocatorProvider;

      public ConsumingNHttpEntity create(HttpEntity httpEntity) {
         return new BufferingNHttpEntity(httpEntity, allocatorProvider.get());
      }
   }

   @Override
   public BlockingQueue<NHttpConnection> provideAvailablePool() throws Exception {
      return new ArrayBlockingQueue<NHttpConnection>(maxConnections, true);
   }

   @Provides
   // uri scope
   public DefaultConnectingIOReactor provideDefaultConnectingIOReactor(HttpParams params)
            throws IOReactorException {
      return new DefaultConnectingIOReactor(maxWorkerThreads, params);
   }

}