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
package org.jclouds.aws.s3.commands.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import org.jclouds.http.options.BaseHttpRequestOptions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Contains options supported in the REST API for the GET bucket operation. <h2>
 * Usage</h2> The recommended way to instantiate a GetBucketOptions object is to
 * statically import GetBucketOptions.Builder.* and invoke a static creation
 * method followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.aws.s3.commands.options.GetBucketOptions.Builder.*
 * <p/>
 * S3Connection connection = // get connection
 * Future<S3Bucket> bucket = connection.listBucket("bucketName",withPrefix("home/users").maxKeys(1000));
 * <code>
 *
 * @author Adrian Cole
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTBucketGET.html?"
 *      />
 */
public class ListBucketOptions extends BaseHttpRequestOptions {
    public static final ListBucketOptions NONE = new ListBucketOptions();

    /**
     * Limits the response to keys which begin with the indicated prefix. You
     * can use prefixes to separate a bucket into different sets of keys in a
     * way similar to how a file system uses folders.
     *
     * @throws UnsupportedEncodingException
     */
    public ListBucketOptions withPrefix(String prefix)
            throws UnsupportedEncodingException {
        parameters.put("prefix", URLEncoder.encode(checkNotNull(prefix, "prefix"),
                "UTF-8"));
        return this;
    }

    /**
     * @see ListBucketOptions#withPrefix(String)
     */
    public String getPrefix() {
        return parameters.get("prefix");
    }

    /**
     * Indicates where in the bucket to begin listing. The list will only
     * include keys that occur lexicographically after marker. This is
     * convenient for pagination: To get the next page of results use the last
     * key of the current page as the marker.
     *
     * @throws UnsupportedEncodingException
     */
    public ListBucketOptions afterMarker(String marker)
            throws UnsupportedEncodingException {
        parameters.put("marker", URLEncoder.encode(checkNotNull(marker, "marker"),
                "UTF-8"));
        return this;
    }

    /**
     * @see ListBucketOptions#afterMarker(String)
     */
    public String getMarker() {
        return parameters.get("marker");
    }

    /**
     * The maximum number of keys you'd like to see in the response body. The
     * server might return fewer than this many keys, but will not return more.
     */
    public ListBucketOptions maxResults(long maxKeys) {
        checkState(maxKeys >= 0, "maxKeys must be >= 0");
        parameters.put("max-keys", Long.toString(maxKeys));
        return this;
    }

    /**
     * @see ListBucketOptions#maxResults(long)
     */
    public String getMaxKeys() {
        return parameters.get("max-keys");
    }

    /**
     * Causes keys that contain the same string between the prefix and the first
     * occurrence of the delimiter to be rolled up into a single result element
     * in the CommonPrefixes collection. These rolled-up keys are not returned
     * elsewhere in the response.
     *
     * @throws UnsupportedEncodingException
     */
    public ListBucketOptions delimiter(String delimiter)
            throws UnsupportedEncodingException {
        parameters.put("delimiter", URLEncoder.encode(checkNotNull(delimiter,
                "delimiter"), "UTF-8"));
        return this;
    }

    /**
     * @see ListBucketOptions#delimiter(String)
     */
    public String getDelimiter() {
        return parameters.get("delimiter");
    }

    public static class Builder {

        /**
         * @throws UnsupportedEncodingException
         * @see ListBucketOptions#withPrefix(String)
         */
        public static ListBucketOptions withPrefix(String prefix)
                throws UnsupportedEncodingException {
            ListBucketOptions options = new ListBucketOptions();
            return options.withPrefix(prefix);
        }

        /**
         * @throws UnsupportedEncodingException
         * @see ListBucketOptions#afterMarker(String)
         */
        public static ListBucketOptions afterMarker(String marker)
                throws UnsupportedEncodingException {
            ListBucketOptions options = new ListBucketOptions();
            return options.afterMarker(marker);
        }

        /**
         * @see ListBucketOptions#maxResults(long)
         */
        public static ListBucketOptions maxResults(long maxKeys) {
            ListBucketOptions options = new ListBucketOptions();
            return options.maxResults(maxKeys);
        }

        /**
         * @throws UnsupportedEncodingException
         * @see ListBucketOptions#delimiter(String) 
         */
        public static ListBucketOptions delimiter(String delimiter)
                throws UnsupportedEncodingException {
            ListBucketOptions options = new ListBucketOptions();
            return options.delimiter(delimiter);
        }

    }
}
