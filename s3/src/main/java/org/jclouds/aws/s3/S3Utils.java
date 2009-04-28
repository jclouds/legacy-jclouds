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
package org.jclouds.aws.s3;

import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Base64;
import org.jclouds.Utils;
import org.jclouds.aws.s3.domain.S3Object;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class S3Utils extends Utils {


    public static String digest(String toEncode, byte[] key) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        HMac hmac = new HMac(new SHA1Digest());
        byte[] resBuf = new byte[hmac.getMacSize()];
        byte[] plainBytes = toEncode.getBytes();
        byte[] keyBytes = key;
        hmac.init(new KeyParameter(keyBytes));
        hmac.update(plainBytes, 0, plainBytes.length);
        hmac.doFinal(resBuf, 0);
        return new String(Base64.encode(resBuf));
    }


    public static String getContentAsStringAndClose(S3Object object) throws IOException {
        Object o = object.getContent();

        if (o instanceof InputStream) {
            String returnVal = toStringAndClose((InputStream) o);
            if (object.getContentType().indexOf("xml") >= 0) {

            }
            return returnVal;
        } else {
            throw new IllegalArgumentException("Object type not supported: " + o.getClass().getName());
        }
    }
}