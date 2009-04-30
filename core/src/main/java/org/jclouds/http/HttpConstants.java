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
package org.jclouds.http;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public interface HttpConstants {
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String HOST = "Host";
    public static final String DATE = "Date";
    public static final String BINARY = "application/octet-stream";
    public static final String PLAIN = "text/plain";
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String PROPERTY_HTTP_SECURE = "jclouds.http.secure";
    public static final String PROPERTY_HTTP_PORT = "jclouds.http.port";
    public static final String PROPERTY_HTTP_ADDRESS = "jclouds.http.address";
}
