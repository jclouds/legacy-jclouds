/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.gogrid;

import org.jclouds.gogrid.domain.internal.ErrorResponse;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;

import static java.lang.String.format;

/**
 * @author Oleksiy Yarmula
 */
public class GoGridResponseException extends HttpResponseException {

    private static final long serialVersionUID = 1924589L;

    private ErrorResponse error;

    public GoGridResponseException(HttpCommand command, HttpResponse response, ErrorResponse error) {
        super(format("command %s failed with code %s, error [%s]: %s",
                    command.toString(), response.getStatusCode(), error.getErrorCode(),
                    error.getMessage()),
              command, response);
        this.setError(error);
    }


    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }
}
