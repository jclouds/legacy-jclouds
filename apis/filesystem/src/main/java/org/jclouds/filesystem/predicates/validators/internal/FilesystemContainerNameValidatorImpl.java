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
package org.jclouds.filesystem.predicates.validators.internal;

import java.io.File;

import org.jclouds.filesystem.predicates.validators.FilesystemContainerNameValidator;

import com.google.inject.Singleton;

/**
 * Validates container name for filesystem provider implementation
 *
 * @see org.jclouds.rest.InputParamValidator
 * @see org.jclouds.predicates.Validator
 *
 * @author Alfredo "Rainbowbreeze" Morresi
 */
@Singleton
public class FilesystemContainerNameValidatorImpl extends FilesystemContainerNameValidator {

    @Override
    public void validate(String name) throws IllegalArgumentException {
        //container name cannot be null or empty
        if (name == null || name.length() < 1)
            throw new IllegalArgumentException("Container name can't be null or empty");

        //container name cannot contains / (or \ in Windows) character
        if (name.contains(File.separator))
            throw new IllegalArgumentException(String.format(
                    "Container name '%s' cannot contain character %s", name, File.separator));
    }

}
