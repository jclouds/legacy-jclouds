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

import org.jclouds.aws.s3.DateService;
import org.jclouds.aws.s3.S3Utils;
import org.joda.time.DateTime;

/**
 * Contains options supported in the REST API for the GET object operation. <h2>
 * Usage</h2> The recommended way to instantiate a GetObjectOptions object is to
 * statically import GetObjectOptions.Builder.* and invoke a static creation
 * method followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.aws.s3.commands.options.GetObjectOptions.Builder.*
 * 
 * S3Connection connection = // get connection
 * 
 * // this will get the first megabyte of an object, provided it wasn't modified since yesterday
 * Future<S3Object> object = connection.getObject("bucket","objectName",range(0,1024).ifUnmodifiedSince(new DateTime().minusDays(1)));
 * <code>
 * 
 * Description of parameters taken from {@link http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTObjectGET.html}
 * 
 * @author Adrian Cole
 * 
 * 
 */
public class GetObjectOptions {
    private final static DateService dateService = new DateService();

    private String range;
    private String ifModifiedSince;
    private String ifUnmodifiedSince;
    private String ifMatch;
    private String ifNoneMatch;

    /**
     * Only download the specified range of the object.
     */
    public GetObjectOptions range(long start, long end) {
	checkState(start >= 0, "start must be >= 0");
	checkState(end >= 0, "end must be >= 0");
	this.range = String.format("bytes=%1d-%2d", start, end);
	return this;
    }

    /**
     * For use in the header Range
     * <p />
     * 
     * @see GetObjectOptions#range(long, long)
     */
    public String getRange() {
	return range;
    }

    /**
     * Only return the object if it has changed since this time.
     * <p />
     * Not compatible with {@link #ifMd5Matches(byte[])} or
     * {@link #ifUnmodifiedSince(DateTime)}
     */
    public GetObjectOptions ifModifiedSince(DateTime ifModifiedSince) {
	checkState(ifMatch == null,
		"ifMd5Matches() is not compatible with ifModifiedSince()");
	checkState(ifUnmodifiedSince == null,
		"ifUnmodifiedSince() is not compatible with ifModifiedSince()");
	this.ifModifiedSince = dateService.toHeaderString(checkNotNull(
		ifModifiedSince, "ifModifiedSince"));
	return this;
    }

    /**
     * For use in the header If-Modified-Since
     * <p />
     * Return the object only if it has been modified since the specified time,
     * otherwise return a 304 (not modified).
     * 
     * @see GetObjectOptions#ifModifiedSince(DateTime)
     */
    public String getIfModifiedSince() {
	return ifModifiedSince;
    }

    /**
     * Only return the object if it hasn't changed since this time.
     * <p />
     * Not compatible with {@link #ifMd5DoesntMatch(byte[])} or
     * {@link #ifModifiedSince(DateTime)}
     */
    public GetObjectOptions ifUnmodifiedSince(DateTime ifUnmodifiedSince) {
	checkState(ifNoneMatch == null,
		"ifMd5DoesntMatch() is not compatible with ifUnmodifiedSince()");
	checkState(ifModifiedSince == null,
		"ifModifiedSince() is not compatible with ifUnmodifiedSince()");
	this.ifUnmodifiedSince = dateService.toHeaderString(checkNotNull(
		ifUnmodifiedSince, "ifUnmodifiedSince"));
	return this;
    }

    /**
     * For use in the header If-Unmodified-Since
     * <p />
     * Return the object only if it has not been modified since the specified
     * time, otherwise return a 412 (precondition failed).
     * 
     * @see GetObjectOptions#ifUnmodifiedSince(DateTime)
     */
    public String getIfUnmodifiedSince() {
	return ifUnmodifiedSince;
    }

    /**
     * The object's md5 hash should match the parameter <code>md5</code>.
     * 
     * <p />
     * Not compatible with {@link #ifMd5DoesntMatch(byte[])} or
     * {@link #ifModifiedSince(DateTime)}
     * 
     * @param md5
     *            hash representing the entity
     * @throws UnsupportedEncodingException
     *             if there was a problem converting this into an S3 eTag string
     */
    public GetObjectOptions ifMd5Matches(byte[] md5)
	    throws UnsupportedEncodingException {
	checkState(ifNoneMatch == null,
		"ifMd5DoesntMatch() is not compatible with ifMd5Matches()");
	checkState(ifModifiedSince == null,
		"ifModifiedSince() is not compatible with ifMd5Matches()");
	this.ifMatch = String.format("\"%1s\"", S3Utils
		.toHexString(checkNotNull(md5, "md5")));
	return this;
    }

    /**
     * For use in the request header: If-Match
     * <p />
     * Return the object only if its entity tag (ETag) is the same as the md5
     * specified, otherwise return a 412 (precondition failed).
     * 
     * @see GetObjectOptions#ifMd5Matches(String)
     */
    public String getIfMatch() {
	return ifMatch;
    }

    /**
     * The object should not have a md5 hash corresponding with the parameter
     * <code>md5</code>.
     * <p />
     * Not compatible with {@link #ifMd5Matches(byte[])} or
     * {@link #ifUnmodifiedSince(DateTime)}
     * 
     * @param md5
     *            hash representing the entity
     * @throws UnsupportedEncodingException
     *             if there was a problem converting this into an S3 eTag string
     */
    public GetObjectOptions ifMd5DoesntMatch(byte[] md5)
	    throws UnsupportedEncodingException {
	checkState(ifMatch == null,
		"ifMd5Matches() is not compatible with ifMd5DoesntMatch()");
	checkState(ifUnmodifiedSince == null,
		"ifUnmodifiedSince() is not compatible with ifMd5DoesntMatch()");
	this.ifNoneMatch = String.format("\"%1s\"", S3Utils
		.toHexString(checkNotNull(md5, "ifMd5DoesntMatch")));
	return this;
    }

    /**
     * For use in the request header: If-None-Match
     * <p />
     * Return the object only if its entity tag (ETag) is different from the one
     * specified, otherwise return a 304 (not modified).
     * 
     * @see GetObjectOptions#ifMd5DoesntMatch(String)
     */
    public String getIfNoneMatch() {
	return ifNoneMatch;
    }

    public static class Builder {

	/**
	 * @see GetObjectOptions#range(long, long)
	 */
	public static GetObjectOptions range(long start, long end) {
	    GetObjectOptions options = new GetObjectOptions();
	    return options.range(start, end);
	}

	/**
	 * @see GetObjectOptions#getIfModifiedSince()
	 */
	public static GetObjectOptions ifModifiedSince(DateTime ifModifiedSince) {
	    GetObjectOptions options = new GetObjectOptions();
	    return options.ifModifiedSince(ifModifiedSince);
	}

	/**
	 * @see GetObjectOptions#ifUnmodifiedSince(DateTime)
	 */
	public static GetObjectOptions ifUnmodifiedSince(
		DateTime ifUnmodifiedSince) {
	    GetObjectOptions options = new GetObjectOptions();
	    return options.ifUnmodifiedSince(ifUnmodifiedSince);
	}

	/**
	 * @see GetObjectOptions#ifMd5Matches(byte[])
	 */
	public static GetObjectOptions ifMd5Matches(byte[] md5)
		throws UnsupportedEncodingException {
	    GetObjectOptions options = new GetObjectOptions();
	    return options.ifMd5Matches(md5);
	}

	/**
	 * @see GetObjectOptions#ifMd5DoesntMatch(byte[])
	 */
	public static GetObjectOptions ifMd5DoesntMatch(byte[] md5)
		throws UnsupportedEncodingException {
	    GetObjectOptions options = new GetObjectOptions();
	    return options.ifMd5DoesntMatch(md5);
	}

    }
}
