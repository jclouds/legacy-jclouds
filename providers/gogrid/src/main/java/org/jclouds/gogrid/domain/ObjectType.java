/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.gogrid.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.CaseFormat;

/**
 * @author Oleksiy Yarmula
 */
public enum ObjectType {

    VIRTUAL_SERVER,
    LOAD_BALANCER,
    CLOUD_STORAGE,
    STORAGE_DNS,
    SERVER_IMAGE,
    DEDICATED_SERVER,
    UNRECOGNIZED;

    public String value() {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
    }

    @Override
    public String toString() {
        return value();
    }

    public static ObjectType fromValue(String type) {
        if("StorageDNS".equals(type)) return STORAGE_DNS;
        try {
            return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(type, "type")));
        } catch(IllegalArgumentException e) {
            return UNRECOGNIZED;
        }
    }

}
