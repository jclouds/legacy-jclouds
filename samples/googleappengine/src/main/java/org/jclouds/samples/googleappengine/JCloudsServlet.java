/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jclouds.aws.s3.S3Context;
import org.jclouds.aws.s3.domain.S3Bucket;

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

    @Override
    protected void doGet(HttpServletRequest httpServletRequest,
	    HttpServletResponse httpServletResponse) throws ServletException,
	    IOException {
	Writer writer = httpServletResponse.getWriter();
	try {
	    List<S3Bucket> myBuckets = context.getConnection().getBuckets()
		    .get(10, TimeUnit.SECONDS);
	    writer.write("List:\n");
	    for (S3Bucket bucket : myBuckets) {
		writer.write(String.format("  %1s: %2s entries%n", bucket
			.getName(), context.createInputStreamMap(bucket)
			.size()));
	    }
	} catch (Exception e) {
	    throw new ServletException(e);
	}
	writer.flush();
	writer.close();
    }

}