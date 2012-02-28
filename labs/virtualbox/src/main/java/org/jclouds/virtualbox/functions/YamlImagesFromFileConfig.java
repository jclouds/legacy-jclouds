/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */

package org.jclouds.virtualbox.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.jclouds.virtualbox.config.VirtualBoxConstants;

import com.google.common.base.Supplier;

/**
 * A supplier for vbox yaml config that reads a yaml whose path is stored under
 * VirtualBoxConstants.VIRTUALBOX_IMAGES_DESCRIPTOR.
 * 
 * @author dralves
 * 
 */
public class YamlImagesFromFileConfig implements Supplier<String> {

  private String yamlFilePath;

  @Inject
  public YamlImagesFromFileConfig(@Named(VirtualBoxConstants.VIRTUALBOX_IMAGES_DESCRIPTOR) String yamlFilePath) {
    this.yamlFilePath = yamlFilePath;
  }

  @Override
  public String get() {
    checkNotNull(yamlFilePath, "yaml file path");
    File yamlFile = new File(yamlFilePath);
    checkState(yamlFile.exists(), "yaml file does not exist at: " + yamlFilePath);
    try {
      return IOUtils.toString(new FileInputStream(yamlFile));
    } catch (IOException e) {
      throw new RuntimeException("error reading yaml file");
    }
  }
}
