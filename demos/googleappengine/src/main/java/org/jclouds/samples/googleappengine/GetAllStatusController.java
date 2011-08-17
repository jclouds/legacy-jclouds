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

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.logging.Logger;
import org.jclouds.samples.googleappengine.functions.BlobStoreContextToStatusResult;
import org.jclouds.samples.googleappengine.functions.ComputeServiceContextToStatusResult;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Shows an example of how to use {@link BlobStoreContext} and {@link ComputeServiceContext}
 * injected with Guice.
 * 
 * @author Adrian Cole
 */
@Singleton
public class GetAllStatusController extends HttpServlet {
   private static final long serialVersionUID = 1L;

   private final Iterable<BlobStoreContext> blobsStoreContexts;
   private final Iterable<ComputeServiceContext> computeServiceContexts;
   private final BlobStoreContextToStatusResult blobStoreContextToContainerResult;
   private final ComputeServiceContextToStatusResult computeServiceContextToContainerResult;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   GetAllStatusController(Iterable<BlobStoreContext> blobsStoreContexts,
         Iterable<ComputeServiceContext> computeServiceContexts,
         BlobStoreContextToStatusResult blobStoreContextToContainerResult,
         ComputeServiceContextToStatusResult computeServiceContextToContainerResult) {
      this.blobsStoreContexts = blobsStoreContexts;
      this.computeServiceContexts = computeServiceContexts;
      this.blobStoreContextToContainerResult = blobStoreContextToContainerResult;
      this.computeServiceContextToContainerResult = computeServiceContextToContainerResult;
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {
         addStatusResultsToRequest(request);
         RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/jsp/status.jsp");
         dispatcher.forward(request, response);
      } catch (Exception e) {
         logger.error(e, "Error listing status");
         throw new ServletException(e);
      }
   }

   private void addStatusResultsToRequest(HttpServletRequest request) throws InterruptedException, ExecutionException,
         TimeoutException {
      request.setAttribute(
            "status",
            ImmutableSet.copyOf(Iterables.concat(
                  Iterables.transform(blobsStoreContexts, blobStoreContextToContainerResult),
                  Iterables.transform(computeServiceContexts, computeServiceContextToContainerResult))));
   }

}