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
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_DEFAULT_DIR;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_INSTALLATION_KEY_SEQUENCE;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_WORKINGDIR;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.compute.domain.Image;
import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.IsoSpec;
import org.jclouds.virtualbox.domain.MasterSpec;
import org.jclouds.virtualbox.domain.NatAdapter;
import org.jclouds.virtualbox.domain.NetworkSpec;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.domain.YamlImage;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.StorageBus;

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.cache.AbstractLoadingCache;
import com.google.common.collect.Iterables;

/**
 * Does most of the work wrt to creating the master image.
 * 
 * @author dralves
 * 
 */
public class MasterImages extends AbstractLoadingCache<Image, IMachine> {

  private final Map<Image, IMachine>           masters = Maps.newHashMap();
  private final Function<MasterSpec, IMachine> mastersLoader;
  private final Map<Image, YamlImage>          imageMapping;
  private final ValueOfConfigurationKeyOrNull  cfg;
  private final String                         workingDir;
  private final String                         adminDisk;
  private String                               guestAdditionsIso;

  @Inject
  public MasterImages(@Named(Constants.PROPERTY_BUILD_VERSION) String version,
      Function<MasterSpec, IMachine> masterLoader, ValueOfConfigurationKeyOrNull cfg,
      Supplier<Map<Image, YamlImage>> yamlMapper) {
    checkNotNull(version, "version");
    this.mastersLoader = masterLoader;
    this.cfg = cfg;
    this.workingDir = cfg.apply(VIRTUALBOX_WORKINGDIR) == null ? VIRTUALBOX_DEFAULT_DIR : cfg
        .apply(VIRTUALBOX_WORKINGDIR);
    File wdFile = new File(workingDir);
    if (!wdFile.exists()) {
      wdFile.mkdirs();
    }
    this.adminDisk = workingDir + "/testadmin.vdi";
    this.imageMapping = yamlMapper.get();
    this.guestAdditionsIso = String.format("%s/VBoxGuestAdditions_%s.iso", workingDir,
        Iterables.get(Splitter.on('r').split(version), 0));
  }

  @Override
  public IMachine get(Image key) throws ExecutionException {
    if (masters.containsKey(key)) {
      return masters.get(key);
    }

    checkState(new File(guestAdditionsIso).exists(), "guest additions iso does not exist at: " + guestAdditionsIso);

    YamlImage yamlImage = imageMapping.get(key);

    String vmName = VIRTUALBOX_IMAGE_PREFIX + yamlImage.id;

    HardDisk hardDisk = HardDisk.builder().diskpath(adminDisk).autoDelete(true).controllerPort(0).deviceSlot(1).build();

    StorageController ideController = StorageController.builder().name("IDE Controller").bus(StorageBus.IDE)
        .attachISO(0, 0, yamlImage.iso).attachHardDisk(hardDisk).attachISO(1, 1, guestAdditionsIso).build();

    VmSpec vmSpecification = VmSpec.builder().id(yamlImage.id).name(vmName).memoryMB(512).osTypeId("")
        .controller(ideController).forceOverwrite(true).cleanUpMode(CleanupMode.Full).build();

    MasterSpec masterSpec = MasterSpec
        .builder()
        .vm(vmSpecification)
        .iso(
            IsoSpec
                .builder()
                .sourcePath(yamlImage.iso)
                .installationScript(
                    cfg.apply(VIRTUALBOX_INSTALLATION_KEY_SEQUENCE).replace("HOSTNAME", vmSpecification.getVmName()))
                .build())
        .network(
            NetworkSpec.builder()
                .natNetworkAdapter(0, NatAdapter.builder().tcpRedirectRule("127.0.0.1", 2222, "", 22).build()).build())
        .build();

    IMachine masterMachine = mastersLoader.apply(masterSpec);
    masters.put(key, masterMachine);
    return masterMachine;
  }

  @Override
  public IMachine getIfPresent(Image key) {
    if (masters.containsKey(key)) {
      return masters.get(key);
    }
    return null;
  }

}
