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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Contains options supported in the REST API for the GET bucket operation. <h2>
 * Usage</h2> The recommended way to instantiate a GetBucketOptions object is to
 * statically import GetBucketOptions.Builder.* and invoke a static creation
 * method followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.aws.s3.commands.options.GetBucketOptions.Builder.*
 * 
 * S3Connection connection = // get connection
 * Future<S3Bucket> bucket = connection.getBucket("bucketName",prefix("/home/users").maxKeys(1000));
 * <code>
 * 
 * Description of parameters taken from {@link http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTBucketGET.html}
 * 
 * @author Adrian Cole
 * 
 * 
 */
public class GetBucketOptions {
    private Map<String, String> options = new HashMap<String, String>();

    /**
     * Builds a query string, ex. ?marker=toast
     * 
     * @return an http query string representing these options, or empty string
     *         if none are present.
     */
    public String toQueryString() {
	StringBuilder builder = new StringBuilder("");
	if (options.size() > 0) {
	    builder.append("?");
	    for (Iterator<Entry<String, String>> i = options.entrySet()
		    .iterator(); i.hasNext();) {
		Entry<String, String> entry = i.next();
		builder.append(entry.getKey()).append("=").append(
			entry.getValue());
		if (i.hasNext())
		    builder.append("&");
	    }
	}
	String returnVal =  builder.toString();
	return returnVal;
    }

    /**
     * Limits the response to keys which begin with the indicated prefix. You
     * can use prefixes to separate a bucket into different sets of keys in a
     * way similar to how a file system uses folders.
     * 
     * @throws UnsupportedEncodingException
     */
    public GetBucketOptions prefix(String prefix)
	    throws UnsupportedEncodingException {
	options.put("prefix", URLEncoder.encode(checkNotNull(prefix, "prefix"),
		"UTF-8"));
	return this;
    }

    /**
     * @see GetBucketOptions#prefix(String)
     */
    public String getPrefix() {
	return options.get("prefix");
    }

    /**
     * Indicates where in the bucket to begin listing. The list will only
     * include keys that occur lexicographically after marker. This is
     * convenient for pagination: To get the next page of results use the last
     * key of the current page as the marker.
     * 
     * @throws UnsupportedEncodingException
     */
    public GetBucketOptions marker(String marker)
	    throws UnsupportedEncodingException {
	options.put("marker", URLEncoder.encode(checkNotNull(marker, "marker"),
		"UTF-8"));
	return this;
    }

    /**
     * @see GetBucketOptions#marker(String)
     */
    public String getMarker() {
	return options.get("marker");
    }

    /**
     * The maximum number of keys you'd like to see in the response body. The
     * server might return fewer than this many keys, but will not return more.
     */
    public GetBucketOptions maxKeys(long maxKeys) {
	checkState(maxKeys >= 0, "maxKeys must be >= 0");
	options.put("max-keys", Long.toString(maxKeys));
	return this;
    }

    /**
     * @see GetBucketOptions#maxKeys(String)
     */
    public String getMaxKeys() {
	return options.get("max-keys");
    }

    /**
     * Causes keys that contain the same string between the prefix and the first
     * occurrence of the delimiter to be rolled up into a single result element
     * in the CommonPrefixes collection. These rolled-up keys are not returned
     * elsewhere in the response.
     * 
     * @throws UnsupportedEncodingException
     */
    public GetBucketOptions delimiter(String delimiter)
	    throws UnsupportedEncodingException {
	options.put("delimiter", URLEncoder.encode(checkNotNull(delimiter,
		"delimiter"), "UTF-8"));
	return this;
    }

    /**
     * @see GetBucketOptions#delimiter(String)
     */
    public String getDelimiter() {
	return options.get("delimiter");
    }

    public static class Builder {

	/**
	 * @throws UnsupportedEncodingException
	 * @see GetBucketOptions#prefix
	 */
	public static GetBucketOptions prefix(String prefix)
		throws UnsupportedEncodingException {
	    GetBucketOptions options = new GetBucketOptions();
	    return options.prefix(prefix);
	}

	/**
	 * @throws UnsupportedEncodingException
	 * @see GetBucketOptions#marker
	 */
	public static GetBucketOptions marker(String marker)
		throws UnsupportedEncodingException {
	    GetBucketOptions options = new GetBucketOptions();
	    return options.marker(marker);
	}

	/**
	 * @see GetBucketOptions#maxKeys
	 */
	public static GetBucketOptions maxKeys(long maxKeys) {
	    GetBucketOptions options = new GetBucketOptions();
	    return options.maxKeys(maxKeys);
	}

	/**
	 * @throws UnsupportedEncodingException
	 * @see GetBucketOptions#delimiter
	 */
	public static GetBucketOptions delimiter(String delimiter)
		throws UnsupportedEncodingException {
	    GetBucketOptions options = new GetBucketOptions();
	    return options.delimiter(delimiter);
	}

    }
}
