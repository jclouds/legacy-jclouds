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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jclouds.aws.s3.S3Context;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Shows an example of how to use @{link S3Connection} injected with Guice.
 * 
 * @author Adrian Cole
 */
@Singleton
public class JCloudsServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;

   private final static String className;

   /**
    * Tests google's behaviour in static context
    */
   static {
      StackTraceElement[] sTrace = new Exception().getStackTrace();
      // sTrace[0] will be always there
      className = sTrace[0].getClassName();
   }

   @Inject
   S3Context context;

   @Resource
   protected Logger logger = Logger.NULL;

   public static class BucketResult {
      private String name;
      private String size = "unknown";
      private String status = "ok";

      public void setName(String name) {
         this.name = name;
      }

      public String getName() {
         return name;
      }

      public void setSize(String size) {
         this.size = size;
      }

      public String getSize() {
         return size;
      }

      public void setStatus(String status) {
         this.status = status;
      }

      public String getStatus() {
         return status;
      }
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      try {
         List<S3Bucket.Metadata> myBucketMetadata = context.getConnection().listOwnedBuckets().get(
                  25, TimeUnit.SECONDS);
         List<BucketResult> myBuckets = new ArrayList<BucketResult>();
         for (S3Bucket.Metadata metadata : myBucketMetadata) {
            BucketResult result = new BucketResult();
            result.setName(metadata.getName());
            try {
               S3Bucket bucket = context.getConnection().listBucket(metadata.getName()).get(10,
                        TimeUnit.SECONDS);
               if (bucket == S3Bucket.NOT_FOUND) {
                  result.setStatus("not found");
               } else {
                  result.setSize(bucket.getSize() + "");
               }
            } catch (Exception e) {
               logger.error(e, "Error listing bucket %1$s", result.getName());
               result.setStatus(e.getMessage());
            }
            myBuckets.add(result);
         }
         request.setAttribute("buckets", myBuckets);
         request.setAttribute("className", className);
         String nextJSP = "/WEB-INF/jsp/buckets.jsp";
         RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
         dispatcher.forward(request, response);
      } catch (Exception e) {
         logger.error(e, "Error listing buckets");
         throw new ServletException(e);
      }
   }
}