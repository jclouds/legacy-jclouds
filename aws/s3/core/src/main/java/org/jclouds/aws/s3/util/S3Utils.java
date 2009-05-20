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
package org.jclouds.aws.s3.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Base64;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.util.Utils;

/**
 * Encryption, Hashing, and IO Utilities needed to sign and verify S3 requests
 * and responses.
 * 
 * @author Adrian Cole
 * 
 */
public class S3Utils extends Utils {

    private static final Pattern IP_PATTERN = Pattern
	    .compile("b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).)"
		    + "{3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)b");

    public static String validateBucketName(String bucketName) {
	checkNotNull(bucketName, "bucketName");
	checkArgument(bucketName.matches("^[a-z0-9].*"),
		"bucket name must start with a number or letter");
	checkArgument(
		bucketName.matches("^[-_.a-z0-9]+"),
		"bucket name can only contain lowercase letters, numbers, periods (.), underscores (_), and dashes (-)");
	checkArgument(bucketName.length() > 2 && bucketName.length() < 256,
		"bucket name must be between 3 and 255 characters long");
	checkArgument(!IP_PATTERN.matcher(bucketName).matches(),
		"bucket name cannot be ip address style");
	return bucketName;
    }

    static final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1', (byte) '2',
	    (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7',
	    (byte) '8', (byte) '9', (byte) 'a', (byte) 'b', (byte) 'c',
	    (byte) 'd', (byte) 'e', (byte) 'f' };

    public static String toHexString(byte[] raw)
	    throws UnsupportedEncodingException {
	byte[] hex = new byte[2 * raw.length];
	int index = 0;

	for (byte b : raw) {
	    int v = b & 0xFF;
	    hex[index++] = HEX_CHAR_TABLE[v >>> 4];
	    hex[index++] = HEX_CHAR_TABLE[v & 0xF];
	}
	return new String(hex, "ASCII");
    }

    public static long calculateSize(Object data) {
	long size = -1;
	if (data instanceof byte[]) {
	    size = ((byte[]) data).length;
	} else if (data instanceof String) {
	    size = ((String) data).length();
	} else if (data instanceof File) {
	    size = ((File) data).length();
	}
	return size;
    }

    /**
     * 
     * @throws IOException
     */
    public static byte[] md5(Object data) throws IOException {
	checkNotNull(data, "data must be set before calling generateMd5()");
	byte[] md5 = null;
	if (data == null || data instanceof byte[]) {
	    md5 = S3Utils.md5((byte[]) data);
	} else if (data instanceof String) {
	    md5 = S3Utils.md5(((String) data).getBytes());
	} else if (data instanceof File) {
	    md5 = S3Utils.md5(((File) data));
	} else {
	    throw new UnsupportedOperationException("Content not supported "
		    + data.getClass());
	}
	return md5;

    }

    public static byte[] fromHexString(String hex) {
	byte[] bytes = new byte[hex.length() / 2];
	for (int i = 0; i < bytes.length; i++) {
	    bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2),
		    16);
	}
	return bytes;
    }

    public static String hmacSha1Base64(String toEncode, byte[] key)
	    throws NoSuchAlgorithmException, NoSuchProviderException,
	    InvalidKeyException {
	HMac hmac = new HMac(new SHA1Digest());
	byte[] resBuf = new byte[hmac.getMacSize()];
	byte[] plainBytes = toEncode.getBytes();
	byte[] keyBytes = key;
	hmac.init(new KeyParameter(keyBytes));
	hmac.update(plainBytes, 0, plainBytes.length);
	hmac.doFinal(resBuf, 0);
	return toBase64String(resBuf);
    }

    public static String md5Hex(byte[] toEncode)
	    throws NoSuchAlgorithmException, NoSuchProviderException,
	    InvalidKeyException, UnsupportedEncodingException {
	byte[] resBuf = md5(toEncode);
	return toHexString(resBuf);
    }

    public static String md5Base64(byte[] toEncode)
	    throws NoSuchAlgorithmException, NoSuchProviderException,
	    InvalidKeyException {
	byte[] resBuf = md5(toEncode);
	return toBase64String(resBuf);
    }

    public static String toBase64String(byte[] resBuf) {
	return new String(Base64.encode(resBuf));
    }

    public static byte[] md5(byte[] plainBytes) {
	MD5Digest md5 = new MD5Digest();
	byte[] resBuf = new byte[md5.getDigestSize()];
	md5.update(plainBytes, 0, plainBytes.length);
	md5.doFinal(resBuf, 0);
	return resBuf;
    }

    public static byte[] md5(File toEncode) throws IOException {
	MD5Digest md5 = new MD5Digest();
	byte[] resBuf = new byte[md5.getDigestSize()];
	byte[] buffer = new byte[1024];
	int numRead = -1;
	InputStream i = new FileInputStream(toEncode);
	try {
	    do {
		numRead = i.read(buffer);
		if (numRead > 0) {
		    md5.update(buffer, 0, numRead);
		}
	    } while (numRead != -1);
	} finally {
	    IOUtils.closeQuietly(i);
	}
	md5.doFinal(resBuf, 0);
	return resBuf;
    }

    public static Md5InputStreamResult generateMd5Result(InputStream toEncode)
	    throws IOException {
	MD5Digest md5 = new MD5Digest();
	byte[] resBuf = new byte[md5.getDigestSize()];
	byte[] buffer = new byte[1024];
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	long length = 0;
	int numRead = -1;
	try {
	    do {
		numRead = toEncode.read(buffer);
		if (numRead > 0) {
		    length += numRead;
		    md5.update(buffer, 0, numRead);
		    out.write(buffer, 0, numRead);
		}
	    } while (numRead != -1);
	} finally {
	    IOUtils.closeQuietly(toEncode);
	}
	md5.doFinal(resBuf, 0);
	return new Md5InputStreamResult(out.toByteArray(), resBuf, length);
    }

    public static class Md5InputStreamResult {
	public final byte[] data;
	public final byte[] md5;
	public final long length;

	Md5InputStreamResult(byte[] data, byte[] md5, long length) {
	    this.data = checkNotNull(data, "data");
	    this.md5 = checkNotNull(md5, "md5");
	    checkArgument(length >= 0, "length cannot me negative");
	    this.length = length;
	}

    }

    public static String getContentAsStringAndClose(S3Object object)
	    throws IOException {
	checkNotNull(object, "s3Object");
	checkNotNull(object.getData(), "s3Object.content");
	Object o = object.getData();

	if (o instanceof InputStream) {
	    String returnVal = toStringAndClose((InputStream) o);
	    if (object.getMetadata().getContentType().indexOf("xml") >= 0) {

	    }
	    return returnVal;
	} else {
	    throw new IllegalArgumentException("Object type not supported: "
		    + o.getClass().getName());
	}
    }
}