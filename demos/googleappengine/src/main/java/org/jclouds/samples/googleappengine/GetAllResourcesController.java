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
package org.jclouds.samples.googleappengine;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.transform;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jclouds.View;
import org.jclouds.domain.ResourceMetadata;
import org.jclouds.logging.Logger;
import org.jclouds.samples.googleappengine.domain.ResourceResult;
import org.jclouds.samples.googleappengine.functions.ResourceMetadataToResourceResult;
import org.jclouds.samples.googleappengine.functions.ViewToAsyncResources;
import org.jclouds.samples.googleappengine.functions.ViewToId;

import com.google.common.base.Stopwatch;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * Shows an example of how to list all resources from all views!
 * 
 * @author Adrian Cole
 */
@Singleton
public class GetAllResourcesController extends HttpServlet {
   private static final long serialVersionUID = 1L;

   private final ListeningExecutorService currentRequestExecutorService;
   private final Iterable<View> views;
   private final ViewToAsyncResources viewToAsyncResources;
   private final ResourceMetadataToResourceResult resourceMetadataToStatusResult;
   private final Provider<Long> remainingMillis;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   GetAllResourcesController(ListeningExecutorService currentRequestExecutorService, Iterable<View> views,
         ViewToAsyncResources viewToAsyncResources, ResourceMetadataToResourceResult resourceMetadataToStatusResult,
         Provider<Long> remainingMillis) {
      this.currentRequestExecutorService = currentRequestExecutorService;
      this.views = views;
      this.viewToAsyncResources = viewToAsyncResources;
      this.resourceMetadataToStatusResult = resourceMetadataToStatusResult;
      this.remainingMillis = remainingMillis;
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {
         addResourcesToRequest(request);
         RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/jsp/resources.jsp");
         dispatcher.forward(request, response);
      } catch (Exception e) {
         logger.error(e, "Error listing resources");
         throw new ServletException(e);
      }
   }

   private void addResourcesToRequest(HttpServletRequest request) {
      Stopwatch watch = new Stopwatch().start();
      logger.info("ready to list views: %s", transform(views, ViewToId.INSTANCE));
      Iterable<ListenableFuture<? extends Iterable<? extends ResourceMetadata<?>>>> asyncResources = transform(views,
            viewToAsyncResources);
      logger.info("launched %s tasks with %sms remaining", size(asyncResources), remainingMillis.get());

      Set<Iterable<? extends ResourceMetadata<?>>> done = allResourcesWithinDeadline(asyncResources);
      logger.info("%s tasks completed in %sms with %sms remaining", size(done), watch.stop().elapsedMillis(),
            remainingMillis.get());

      Iterable<ResourceMetadata<?>> flattened = concat(done);

      Set<ResourceResult> results = FluentIterable.from(flattened).transform(resourceMetadataToStatusResult)
            .toImmutableSet();

      request.setAttribute("resources", results);
   }

   private <T> Set<T> allResourcesWithinDeadline(Iterable<ListenableFuture<? extends T>> asyncResources) {
      Builder<T> resourcesWeCanList = addToBuilderOnComplete(asyncResources);

      // only serve resources that made it by the timeout
      blockUntilAllDoneOrCancelOnTimeout(asyncResources);

      return resourcesWeCanList.build();
   }

   private <T> Builder<T> addToBuilderOnComplete(Iterable<ListenableFuture<? extends T>> asyncResources) {

      final Builder<T> resourcesWeCanList = ImmutableSet.<T> builder();

      for (final ListenableFuture<? extends T> asyncResource : asyncResources) {
         Futures.addCallback(asyncResource, new FutureCallback<T>() {
            public void onSuccess(T result) {
               if (result != null)
                  resourcesWeCanList.add(result);
            }

            public void onFailure(Throwable t) {
               if (!(t instanceof CancellationException))
                  logger.error(t, "exception getting resource %s: %s", asyncResource, t.getMessage());
            }
         }, currentRequestExecutorService);

      }
      return resourcesWeCanList;
   }

   // ensure we don't violate our request timeouts.
   private void blockUntilAllDoneOrCancelOnTimeout(Iterable<? extends ListenableFuture<?>> asyncResources) {
      try {
         for (ListenableFuture<?> asyncResource : asyncResources) {
            if (remainingMillis.get() > 0) {
               try {
                  asyncResource.get(remainingMillis.get(), TimeUnit.MILLISECONDS);
               } catch (Exception e) {
                  logger.info("exception getting resource %s: %s", asyncResource, e.getMessage());
               }
            }
         }
      } finally {
         if (remainingMillis.get() < 0) {
            for (ListenableFuture<?> asyncResource : asyncResources) {
               if (!asyncResource.isDone())
                  asyncResource.cancel(true);
            }
         }
      }

   }

}
