/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
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
import java.io.Writer;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jclouds.aws.s3.S3Context;
import org.jclouds.aws.s3.S3ResponseException;
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

    @Inject
    S3Context context;

    @Resource
    protected Logger logger = Logger.NULL;

    @Override
    protected void doGet(HttpServletRequest httpServletRequest,
	    HttpServletResponse httpServletResponse) throws ServletException,
	    IOException {
	httpServletResponse.setContentType("text/plain");
	Writer writer = httpServletResponse.getWriter();
	try {
	    List<S3Bucket.Metadata> myBuckets = context.getConnection()
		    .listOwnedBuckets().get(10, TimeUnit.SECONDS);
	    writer.write("List:\n");
	    for (S3Bucket.Metadata bucket : myBuckets) {
		writer.write(String.format("  %1$s", bucket));
		try {
		    writer.write(String.format(": %1$s entries%n", context
			    .createInputStreamMap(bucket.getName()).size()));
		} catch (S3ResponseException e) {
		    String message = String.format(
			    ": unable to list entries due to: %1$s%n", e
				    .getError().getCode());
		    writer.write(message);
		    logger.warn(e, "message");
		}

	    }
	} catch (Exception e) {
	    throw new ServletException(e);
	}
	writer.flush();
	writer.close();
    }
}