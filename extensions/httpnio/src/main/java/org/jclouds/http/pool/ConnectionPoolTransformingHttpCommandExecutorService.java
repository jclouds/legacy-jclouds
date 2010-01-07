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
package org.jclouds.http.pool;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.util.concurrent.Futures.makeListenable;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandRendezvous;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.lifecycle.BaseLifeCycle;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class ConnectionPoolTransformingHttpCommandExecutorService<C> extends BaseLifeCycle
         implements TransformingHttpCommandExecutorService {

   private final ConcurrentMap<URI, HttpCommandConnectionPool<C>> poolMap;
   private final BlockingQueue<HttpCommandRendezvous<?>> commandQueue;
   private final HttpCommandConnectionPool.Factory<C> poolFactory;
   private final LoggerFactory logFactory;

   @Inject
   public ConnectionPoolTransformingHttpCommandExecutorService(ExecutorService executor,
            HttpCommandConnectionPool.Factory<C> pf,
            BlockingQueue<HttpCommandRendezvous<?>> commandQueue, LoggerFactory logFactory) {
      super(executor);
      this.poolFactory = pf;
      // TODO inject this.
      poolMap = new MapMaker().makeComputingMap(new Function<URI, HttpCommandConnectionPool<C>>() {
         public HttpCommandConnectionPool<C> apply(URI endPoint) {
            checkArgument(endPoint.getHost() != null, String.format(
                     "endPoint.getHost() is null for %s", endPoint));
            try {
               HttpCommandConnectionPool<C> pool = poolFactory.create(endPoint);
               addDependency(pool);
               return pool;
            } catch (RuntimeException e) {
               logger.error(e, "error creating entry for %s", endPoint);
               throw e;
            }
         }
      });
      this.commandQueue = commandQueue;
      this.logFactory = logFactory;
   }

   /**
    * {@inheritDoc}
    * 
    * If the reason we are shutting down is due an exception, we set that exception on all pending
    * commands. Otherwise, we cancel the pending commands.
    */
   @Override
   protected void doShutdown() {
      exception.compareAndSet(null, getExceptionFromDependenciesOrNull());
      while (!commandQueue.isEmpty()) {
         HttpCommandRendezvous<?> rendezvous = (HttpCommandRendezvous<?>) commandQueue.remove();
         if (rendezvous != null) {
            try {
               if (exception.get() != null)
                  rendezvous.setException(exception.get());
               else
                  rendezvous.setException(new CancellationException("shutdown"));
            } catch (InterruptedException e) {
               logger.error(e, "Error cancelling command %s", rendezvous.getCommand());
            }
         }
      }
   }

   @Override
   protected void doWork() throws InterruptedException {
      takeACommandOffTheQueueAndInvokeIt();
   }

   private void takeACommandOffTheQueueAndInvokeIt() throws InterruptedException {
      HttpCommandRendezvous<?> rendezvous = commandQueue.poll(1, TimeUnit.SECONDS);
      if (rendezvous != null) {
         try {
            invoke(rendezvous);
         } catch (Exception e) {
            Utils.<InterruptedException> rethrowIfRuntimeOrSameType(e);
            logger.error(e, "Error processing command %s", rendezvous.getCommand());
         }
      }
   }

   /**
    * This is an asynchronous operation that puts the <code>command</code> onto a queue. Later, it
    * will be processed via the {@link #invoke(TransformingHttpCommandExecutorService) invoke}
    * method.
    */
   public <T> ListenableFuture<T> submit(HttpCommand command,
            final Function<HttpResponse, T> responseTransformer) {
      exceptionIfNotActive();
      final SynchronousQueue<?> channel = new SynchronousQueue<Object>();
      // should block and immediately parse the response on exit.
      Future<T> future = executorService.submit(new Callable<T>() {
         Logger transformerLogger = logFactory.getLogger(responseTransformer.getClass().getName());

         public T call() throws Exception {
            Object o = channel.take();
            if (o instanceof Exception) {
               throw (Exception) o;
            }
            transformerLogger.debug("Processing intermediate result for: %s", o);
            T result = responseTransformer.apply((HttpResponse) o);
            transformerLogger.debug("Processed intermediate result for: %s", o);
            return result;
         }
      });

      HttpCommandRendezvous<T> rendezvous = new HttpCommandRendezvous<T>(command, channel,
               makeListenable(future));
      commandQueue.add(rendezvous);
      return rendezvous.getFuture();
   }

   /**
    * Invoke binds a command with a connection from the pool. This binding is called a
    * {@link HttpCommandConnectionHandle handle}. The handle will keep this binding until the
    * command's response is parsed or an exception is set on the Command object.
    * 
    * @param command
    */
   protected void invoke(HttpCommandRendezvous<?> command) {
      exceptionIfNotActive();

      URI endpoint = createBaseEndpointFor(command);

      HttpCommandConnectionPool<C> pool = poolMap.get(endpoint);
      if (pool == null) {
         // TODO limit;
         logger.warn("pool not available for command %s; retrying", command.getCommand());
         commandQueue.add(command);
         return;
      }

      HttpCommandConnectionHandle<C> connectionHandle = null;
      try {
         connectionHandle = pool.getHandle(command);
      } catch (InterruptedException e) {
         logger.warn(e, "Interrupted getting a connection for command %s; retrying", command
                  .getCommand());
         commandQueue.add(command);
         return;
      } catch (TimeoutException e) {
         logger.warn(e, "Timeout getting a connection for command %s on pool %s; retrying", command
                  .getCommand(), pool);
         commandQueue.add(command);
         return;
      } catch (RuntimeException e) {
         logger.warn(e, "Error getting a connection for command %s on pool %s; retrying", command
                  .getCommand(), pool);
         discardPool(endpoint, pool);
         commandQueue.add(command);
         return;
      }

      if (connectionHandle == null) {
         logger.error("Failed to obtain connection for command %s; retrying", command.getCommand());
         commandQueue.add(command);
         return;
      }
      connectionHandle.startConnection();
   }

   private void discardPool(URI endpoint, HttpCommandConnectionPool<C> pool) {
      poolMap.remove(endpoint, pool);
      pool.shutdown();
      this.dependencies.remove(pool);
   }

   /**
    * keys to the map are only used for socket information, not path. In this case, you should
    * remove any path or query details from the URI.
    */
   private URI createBaseEndpointFor(HttpCommandRendezvous<?> command) {
      URI endpoint = command.getCommand().getRequest().getEndpoint();
      if (endpoint.getPort() == -1) {
         return URI.create(String.format("%s://%s", endpoint.getScheme(), endpoint.getHost()));
      } else {
         return URI.create(String.format("%s://%s:%d", endpoint.getScheme(), endpoint.getHost(),
                  endpoint.getPort()));
      }
   }

}
