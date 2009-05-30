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
package org.jclouds.http.commands;

import org.jclouds.http.commands.callables.xml.ParseSax;

import com.google.inject.Inject;

/**
 * temporary factory until guice can do multi-type assisted inject
 * @see <a href="http://code.google.com/p/google-guice/issues/detail?id=346" />
 * 
 * @author Adrian Cole
 */
public class CommandFactory {

    @Inject
    private ParseSaxFactory parseSaxFactory;

    public static interface ParseSaxFactory {
	ParseSax<?> create(ParseSax.HandlerWithResult<?> handler);
    }

    @SuppressWarnings("unchecked")
    public GetAndParseSax<?> createGetAndParseSax(String uri,
	    ParseSax.HandlerWithResult<?> handler) {
	return new GetAndParseSax(uri, parseSaxFactory.create(handler));
    }

    @Inject
    private GetStringFactory getStringFactory;

    public static interface GetStringFactory {
	GetString create(String uri);
    }

    public GetString createGetString(String uri) {
	return getStringFactory.create(uri);
    }

    @Inject
    private HeadFactory headFactory;

    public static interface HeadFactory {
	Head create(String uri);
    }

    public Head createHead(String uri) {
	return headFactory.create(uri);
    }

}
