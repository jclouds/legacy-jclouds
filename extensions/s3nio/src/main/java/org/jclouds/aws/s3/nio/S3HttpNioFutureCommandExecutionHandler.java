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
package org.jclouds.aws.s3.nio;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.entity.InputStreamEntity;
import org.jclouds.http.httpnio.pool.HttpNioFutureCommandConnectionRetry;
import org.jclouds.http.httpnio.pool.HttpNioFutureCommandExecutionHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
@Singleton
public class S3HttpNioFutureCommandExecutionHandler extends HttpNioFutureCommandExecutionHandler {

    @Inject
    public S3HttpNioFutureCommandExecutionHandler(java.util.logging.Logger logger, ConsumingNHttpEntityFactory entityFactory, ExecutorService executor, HttpNioFutureCommandConnectionRetry futureOperationRetry) {
        super(logger, entityFactory, executor, futureOperationRetry);
    }

    @Override
    protected boolean isRetryable(HttpResponse response) throws IOException {
        if (super.isRetryable(response))
            return true;
        int code = response.getStatusLine().getStatusCode();
        if (code == 409) {
            return true;
        } else if (code == 400) {
            if (response.getEntity() != null) {
                InputStream input = response.getEntity().getContent();
                if (input != null) {
                    String reason = null;
                    try {
                        reason = IOUtils.toString(input);
                    } finally {
                        IOUtils.closeQuietly(input);
                    }
                    if (reason != null) {
                        try {
                            if (reason.indexOf("RequestTime") >= 0) return true;
                        } finally {
                            IOUtils.closeQuietly(input);
                            response.setEntity(new NStringEntity(reason));
                        }
                    }

                }

            }
        }
        return false;
    }

}
