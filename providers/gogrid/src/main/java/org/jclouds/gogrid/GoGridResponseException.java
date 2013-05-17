/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.gogrid;

import static java.lang.String.format;

import java.util.Set;

import org.jclouds.gogrid.domain.internal.ErrorResponse;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;

/**
 * @author Oleksiy Yarmula
 */
public class GoGridResponseException extends HttpResponseException {

    private Set<ErrorResponse> errors;

    public GoGridResponseException(HttpCommand command, HttpResponse response, Set<ErrorResponse> errors) {
        super(buildMessage(command, response, errors), command, response);
        this.setErrors(errors);
    }

    public Set<ErrorResponse> getError() {
        return errors;
    }

    public void setErrors(Set<ErrorResponse> errors) {
        this.errors = errors;
    }

    private static String buildMessage(HttpCommand command, HttpResponse response, Set<ErrorResponse> errors) {
        StringBuilder builder = new StringBuilder();
        builder.append(format("command %s failed with code %s. ", command.toString(),
                response.getStatusCode()));
        for(ErrorResponse error : errors) {
            builder.append(format("Error [%s]: %s. ", error.getErrorCode(), error.getMessage()));
        }
        return builder.toString();
    }
}
