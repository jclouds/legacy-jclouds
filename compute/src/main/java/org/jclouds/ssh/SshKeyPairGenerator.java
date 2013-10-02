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
package org.jclouds.ssh;

import java.util.Map;

import org.jclouds.ssh.internal.RsaSshKeyPairGenerator;

import com.google.common.base.Supplier;
import com.google.inject.ImplementedBy;

/**
 * Creates a unique keypair without a passphrase. The resulting map has the following keys
 * <ul>
 * <li>public</li>
 * <li>private</li>
 * </ul>
 */
@ImplementedBy(RsaSshKeyPairGenerator.class)
public interface SshKeyPairGenerator extends Supplier<Map<String, String>> {

}
