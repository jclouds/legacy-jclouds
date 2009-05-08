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
package org.jclouds.http;

public interface HttpHeaders {

    /**
     * Can be used to specify caching behavior along the request/reply chain. Go
     * to http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.
     */
    public static final String CACHE_CONTROL = "Cache-Control";
    /**
     * Specifies presentational information for the object. Go to
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html#sec19.5.1.
     */
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    /**
     * Specifies what content encodings have been applied to the object and thus
     * what decoding mechanisms must be applied in order to obtain the
     * media-type referenced by the Content-Type header field. Go to
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.11.
     */
    public static final String CONTENT_ENCODING = "Content-Encoding";
    /**
     * The size of the object, in bytes. This is required. Go to
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.13.
     */
    public static final String CONTENT_LENGTH = "Content-Length";
    /**
     * A standard MIME type describing the format of the contents. If none is
     * provided, the default is binary/octet-stream. Go to
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17.
     */
    public static final String CONTENT_TYPE = "Content-Type";
    /**
     * The base64 encoded 128-bit MD5 digest of the message (without the
     * headers) according to RFC 1864. This header can be used as a message
     * integrity check to verify that the data is the same data that was
     * originally sent.
     */
    public static final String CONTENT_MD5 = "Content-MD5";
    /**
     * A user agent that wishes to authenticate itself with a server-- usually,
     * but not necessarily, after receiving a 401 response--does so by including
     * an Authorization request-header field with the request. The Authorization
     * field value consists of credentials containing the authentication
     * information of the user agent for the realm of the resource being
     * requested.
     * 
     * Authorization = "Authorization" ":" credentials
     * 
     * @see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
     */
    public static final String AUTHORIZATION = "Authorization";
    public static final String HOST = "Host";
    public static final String DATE = "Date";
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String LAST_MODIFIED = "Last-Modified";
    public static final String SERVER = "Server";

}