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
package org.jclouds.http.commands.callables;

import org.apache.commons.io.IOUtils;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpFutureCommand;

import com.google.inject.Inject;

/**
 * Simply returns true when the http response code is in the range 200-299.
 * 
 * @author Adrian Cole
 */
public class ReturnTrueIf2xx extends
	HttpFutureCommand.ResponseCallable<Boolean> {

    @Inject
    public ReturnTrueIf2xx() {
	super();
    }

    public Boolean call() throws HttpException {
	checkCode();
	IOUtils.closeQuietly(getResponse().getContent());
	return true;
    }
}