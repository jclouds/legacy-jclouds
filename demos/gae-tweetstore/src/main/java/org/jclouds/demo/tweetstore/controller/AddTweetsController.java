/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.demo.tweetstore.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.jclouds.demo.tweetstore.domain.StoredTweetStatus;
import org.jclouds.demo.tweetstore.functions.ServiceToStoredTweetStatuses;
import org.jclouds.logging.Logger;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * Shows an example of how to use @{link BlobStoreContext} injected with Guice.
 * 
 * @author Adrian Cole
 */
@Singleton
public class AddTweetsController extends HttpServlet implements
         Function<Set<String>, List<StoredTweetStatus>> {

   /** The serialVersionUID */
   private static final long serialVersionUID = 3888348023150822683L;
   private final Map<String, BlobStoreContext<?, ?>> contexts;
   private final ServiceToStoredTweetStatuses blobStoreContextToContainerResult;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   AddTweetsController(Map<String, BlobStoreContext<?, ?>> contexts,
            ServiceToStoredTweetStatuses blobStoreContextToContainerResult) {
      this.contexts = contexts;
      this.blobStoreContextToContainerResult = blobStoreContextToContainerResult;
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      try {
         addMyTweetsToRequest(request);
         RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/tweets.jsp");
         dispatcher.forward(request, response);
      } catch (Exception e) {
         logger.error(e, "Error listing containers");
         throw new ServletException(e);
      }
   }

   void addMyTweetsToRequest(HttpServletRequest request) throws InterruptedException,
            ExecutionException, TimeoutException {
      request.setAttribute("tweets", apply(contexts.keySet()));
   }

   public List<StoredTweetStatus> apply(Set<String> in) {
      List<StoredTweetStatus> statuses = Lists.newArrayList();
      for (Iterable<StoredTweetStatus> list : Iterables.transform(in,
               blobStoreContextToContainerResult)) {
         Iterables.addAll(statuses, list);
      }
      return statuses;
   }
}