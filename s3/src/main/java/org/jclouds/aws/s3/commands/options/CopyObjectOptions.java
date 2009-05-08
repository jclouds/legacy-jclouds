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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.UnsupportedEncodingException;

import org.jclouds.aws.s3.DateService;
import org.jclouds.aws.s3.S3Utils;
import org.jclouds.http.options.BaseHttpRequestOptions;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Contains options supported in the REST API for the COPY object operation.
 * <p/>
 * <h2>Usage</h2> The recommended way to instantiate a CopyObjectOptions object
 * is to statically import CopyObjectOptions.Builder.* and invoke a static
 * creation method followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.aws.s3.commands.options.CopyObjectOptions.Builder.*
 * 
 * S3Connection connection = // get connection
 * 
 * Multimap<String,String> metadata = HashMultimap.create();
 * metadata.put("x-amz-meta-adrian", "foo");
 * 
 * // this will copy the object, provided it wasn't modified since yesterday.
 * // it will not use metadata from the source, and instead use what we pass in.
 * Future<S3Object.MetaData> object = connection.copyObject("sourceBucket", "objectName",
 *                                                          "destinationBucket", "destinationName",
 *                                                           overrideMetadataWith(meta).
 *                                                           ifSourceModifiedSince(new DateTime().minusDays(1))
 *                                                          );
 * <code>
 * 
 * Description of parameters taken from {@link http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTObjectCOPY.html}
 * 
 * @author Adrian Cole
 * 
 * 
 */
public class CopyObjectOptions extends BaseHttpRequestOptions {
    private final static DateService dateService = new DateService();

    public static final CopyObjectOptions NONE = new CopyObjectOptions();

    private Multimap<String, String> metadata;

    /**
     * For use in the header x-amz-copy-source-if-unmodified-since
     * <p />
     * Copies the object if it hasn't been modified since the specified time;
     * otherwise returns a 412 (precondition failed).
     * <p />
     * This header can be used with x-amz-copy-source-if-match, but cannot be
     * used with other conditional copy headers.
     * 
     * @return valid HTTP date (go to http://rfc.net/rfc2616.html#s3.3).
     * @see CopyObjectOptions#ifSourceModifiedSince(DateTime)
     */
    public String getIfModifiedSince() {
	return getFirstHeaderOrNull("x-amz-copy-source-if-modified-since");
    }

    /**
     * For use in the header x-amz-copy-source-if-modified-since
     * <p />
     * Copies the object if it has been modified since the specified time;
     * otherwise returns a 412 (failed condition).
     * <p/>
     * This header can be used with x-amz-copy-source-if-none-match, but cannot
     * be used with other conditional copy headers.
     * 
     * @return valid HTTP date (go to http://rfc.net/rfc2616.html#s3.3).
     * 
     * @see CopyObjectOptions#ifSourceUnmodifiedSince(DateTime)
     */
    public String getIfUnmodifiedSince() {
	return getFirstHeaderOrNull("x-amz-copy-source-if-unmodified-since");
    }

    /**
     * For use in the request header: x-amz-copy-source-if-match
     * <p />
     * Copies the object if its entity tag (ETag) matches the specified tag;
     * otherwise return a 412 (precondition failed).
     * <p/>
     * This header can be used with x-amz-copy-source-if-unmodified-since, but
     * cannot be used with other conditional copy headers.
     * 
     * @see CopyObjectOptions#ifSourceMd5Matches(String)
     */
    public String getIfMatch() {
	return getFirstHeaderOrNull("x-amz-copy-source-if-match");
    }

    /**
     * For use in the request header: x-amz-copy-source-if-none-match
     * <p />
     * Copies the object if its entity tag (ETag) is different than the
     * specified Etag; otherwise returns a 412 (failed condition).
     * <p/>
     * This header can be used with x-amz-copy-source-if-modified-since, but
     * cannot be used with other conditional copy headers.
     * 
     * @see CopyObjectOptions#ifSourceMd5DoesntMatch(String)
     */
    public String getIfNoneMatch() {
	return getFirstHeaderOrNull("x-amz-copy-source-if-none-match");
    }

    /**
     * When not null, contains the header
     * [x-amz-copy-source-if-unmodified-since] -> [REPLACE] and metadata headers
     * passed in from the users.
     * 
     * @see #overrideMetadataWith(Multimap)
     */
    public Multimap<String, String> getMetadata() {
	return metadata;
    }

    /**
     * Only return the object if it has changed since this time.
     * <p />
     * Not compatible with {@link #ifSourceMd5Matches(byte[])} or
     * {@link #ifSourceUnmodifiedSince(DateTime)}
     */
    public CopyObjectOptions ifSourceModifiedSince(DateTime ifModifiedSince) {
	checkState(getIfMatch() == null,
		"ifMd5Matches() is not compatible with ifModifiedSince()");
	checkState(getIfUnmodifiedSince() == null,
		"ifUnmodifiedSince() is not compatible with ifModifiedSince()");
	replaceHeader("x-amz-copy-source-if-modified-since",
		dateService.toHeaderString(checkNotNull(ifModifiedSince,
			"ifModifiedSince")));
	return this;
    }

    /**
     * Only return the object if it hasn't changed since this time.
     * <p />
     * Not compatible with {@link #ifSourceMd5DoesntMatch(byte[])} or
     * {@link #ifSourceModifiedSince(DateTime)}
     */
    public CopyObjectOptions ifSourceUnmodifiedSince(DateTime ifUnmodifiedSince) {
	checkState(getIfNoneMatch() == null,
		"ifMd5DoesntMatch() is not compatible with ifUnmodifiedSince()");
	checkState(getIfModifiedSince() == null,
		"ifModifiedSince() is not compatible with ifUnmodifiedSince()");
	replaceHeader("x-amz-copy-source-if-unmodified-since", dateService
		.toHeaderString(checkNotNull(ifUnmodifiedSince,
			"ifUnmodifiedSince")));
	return this;
    }

    /**
     * The object's md5 hash should match the parameter <code>md5</code>.
     * 
     * <p />
     * Not compatible with {@link #ifSourceMd5DoesntMatch(byte[])} or
     * {@link #ifSourceModifiedSince(DateTime)}
     * 
     * @param md5
     *            hash representing the entity
     * @throws UnsupportedEncodingException
     *             if there was a problem converting this into an S3 eTag string
     */
    public CopyObjectOptions ifSourceMd5Matches(byte[] md5)
	    throws UnsupportedEncodingException {
	checkState(getIfNoneMatch() == null,
		"ifMd5DoesntMatch() is not compatible with ifMd5Matches()");
	checkState(getIfModifiedSince() == null,
		"ifModifiedSince() is not compatible with ifMd5Matches()");
	replaceHeader("x-amz-copy-source-if-match", String.format("\"%1s\"",
		S3Utils.toHexString(checkNotNull(md5, "md5"))));
	return this;
    }

    /**
     * The object should not have a md5 hash corresponding with the parameter
     * <code>md5</code>.
     * <p />
     * Not compatible with {@link #ifSourceMd5Matches(byte[])} or
     * {@link #ifSourceUnmodifiedSince(DateTime)}
     * 
     * @param md5
     *            hash representing the entity
     * @throws UnsupportedEncodingException
     *             if there was a problem converting this into an S3 eTag string
     */
    public CopyObjectOptions ifSourceMd5DoesntMatch(byte[] md5)
	    throws UnsupportedEncodingException {
	checkState(getIfMatch() == null,
		"ifMd5Matches() is not compatible with ifMd5DoesntMatch()");
	Preconditions
		.checkState(getIfUnmodifiedSince() == null,
			"ifUnmodifiedSince() is not compatible with ifMd5DoesntMatch()");
	replaceHeader("x-amz-copy-source-if-none-match", String.format(
		"\"%1s\"", S3Utils.toHexString(checkNotNull(md5,
			"ifMd5DoesntMatch"))));
	return this;
    }

    @Override
    public Multimap<String, String> buildRequestHeaders() {
	Multimap<String, String> returnVal = HashMultimap.create();
	returnVal.putAll(headers);
	if (metadata != null)
	    returnVal.putAll(metadata);
	return returnVal;
    }

    /**
     * Use the provided metadata instead of what is on the source object.
     */
    public CopyObjectOptions overrideMetadataWith(
	    Multimap<String, String> metadata) {
	checkNotNull(metadata, "metadata");
	for (String header : metadata.keySet()) {
	    checkArgument(header.startsWith("x-amz-meta-"),
		    "Metadata keys must start with x-amz-meta-");
	}
	metadata.put("x-amz-copy-source-if-unmodified-since", "REPLACE");
	this.metadata = metadata;
	return this;
    }

    public static class Builder {

	/**
	 * @see CopyObjectOptions#getIfModifiedSince()
	 */
	public static CopyObjectOptions ifSourceModifiedSince(
		DateTime ifModifiedSince) {
	    CopyObjectOptions options = new CopyObjectOptions();
	    return options.ifSourceModifiedSince(ifModifiedSince);
	}

	/**
	 * @see CopyObjectOptions#ifSourceUnmodifiedSince(DateTime)
	 */
	public static CopyObjectOptions ifSourceUnmodifiedSince(
		DateTime ifUnmodifiedSince) {
	    CopyObjectOptions options = new CopyObjectOptions();
	    return options.ifSourceUnmodifiedSince(ifUnmodifiedSince);
	}

	/**
	 * @see CopyObjectOptions#ifSourceMd5Matches(byte[])
	 */
	public static CopyObjectOptions ifSourceMd5Matches(byte[] md5)
		throws UnsupportedEncodingException {
	    CopyObjectOptions options = new CopyObjectOptions();
	    return options.ifSourceMd5Matches(md5);
	}

	/**
	 * @see CopyObjectOptions#ifSourceMd5DoesntMatch(byte[])
	 */
	public static CopyObjectOptions ifSourceMd5DoesntMatch(byte[] md5)
		throws UnsupportedEncodingException {
	    CopyObjectOptions options = new CopyObjectOptions();
	    return options.ifSourceMd5DoesntMatch(md5);
	}

	/**
	 * @see #overrideMetadataWith(Multimap)
	 */
	public static CopyObjectOptions overrideMetadataWith(
		Multimap<String, String> metadata) {
	    CopyObjectOptions options = new CopyObjectOptions();
	    return options.overrideMetadataWith(metadata);
	}
    }
}
