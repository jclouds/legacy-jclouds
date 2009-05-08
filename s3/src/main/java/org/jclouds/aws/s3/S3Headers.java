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
package org.jclouds.aws.s3;

public interface S3Headers {

    /**
     * The canned ACL to apply to the object. Options include private,
     * public-read, public-read-write, and authenticated-read. For more
     * information, see REST Access Control Policy.
     */
    public static final String CANNED_ACL = "x-amz-acl";
    /**
     * Any header starting with this prefix is considered user metadata. It will
     * be stored with the object and returned when you retrieve the object. The
     * total size of the HTTP request, not including the body, must be less than
     * 8 KB.
     */
    public static final String USER_METADATA_PREFIX = "x-amz-meta-";
    public static final String ETAG = "ETag";
    public static final String AMZ_MD5 = "x-amz-meta-object-md5";
    public static final String RANGE = "Range";
    public static final String OBJECT_IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String OBJECT_IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
    public static final String OBJECT_IF_MATCH = "If-Match";
    public static final String OBJECT_IF_NONE_MATCH = "If-None-Match";

}