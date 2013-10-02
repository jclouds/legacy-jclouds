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

/**
 * @author Oleksiy Yarmula
 */
public enum IpType {
    PRIVATE("Private"),
    PRIVATE_2("Private 2"),
    PUBLIC("Public"),
    PUBLIC_2("Public 2"),
    PUBLIC_3("Public 3"),
    PUBLIC_4("Public 4");

    final String name;

    IpType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
