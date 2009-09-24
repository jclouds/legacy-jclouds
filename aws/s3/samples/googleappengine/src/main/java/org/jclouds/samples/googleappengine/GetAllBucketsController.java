/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.samples.googleappengine;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jclouds.aws.s3.S3Context;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.logging.Logger;
import org.jclouds.samples.googleappengine.domain.BucketResult;
import org.jclouds.samples.googleappengine.functions.MetadataToBucketResult;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Shows an example of how to use @{link S3Connection} injected with Guice.
 * 
 * @author Adrian Cole
 */
@Singleton
public class GetAllBucketsController extends HttpServlet {
   private static final long serialVersionUID = 1L;

   private final S3Context context;
   private final Provider<MetadataToBucketResult> metadataToBucketResultProvider;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public GetAllBucketsController(S3Context context,
            Provider<MetadataToBucketResult> metadataToBucketResultProvider) {
      this.context = context;
      this.metadataToBucketResultProvider = metadataToBucketResultProvider;
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      try {
         addMyBucketsToRequest(request);
         RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(
                  "/WEB-INF/jsp/buckets.jsp");
         dispatcher.forward(request, response);
      } catch (Exception e) {
         logger.error(e, "Error listing buckets");
         throw new ServletException(e);
      }
   }

   private void addMyBucketsToRequest(HttpServletRequest request) throws InterruptedException,
            ExecutionException, TimeoutException {
      System.err.println(context.getAccount() + ":" + context.getEndPoint());
      List<BucketMetadata> myBucketMetadata = context.getApi().listContainers();
      List<BucketResult> myBuckets = Lists.transform(myBucketMetadata,
               metadataToBucketResultProvider.get());
      request.setAttribute("buckets", myBuckets);
   }
}