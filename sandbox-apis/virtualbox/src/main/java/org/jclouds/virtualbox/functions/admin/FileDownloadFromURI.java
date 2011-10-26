/*
 * *
 *  * Licensed to jclouds, Inc. (jclouds) under one or more
 *  * contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  jclouds licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.jclouds.virtualbox.functions.admin;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.ByteStreams.copy;
import static com.google.common.io.Closeables.closeQuietly;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_WORKINGDIR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.config.VirtualBoxConstants;

import com.google.common.base.Function;

/**
 * @author Mattias Holmqvist
 */
public class FileDownloadFromURI implements Function<URI, File> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ComputeServiceContext context;
   private final String workingDir;
   private final String isoFile;

   @Inject
   public FileDownloadFromURI(final ComputeServiceContext context,
                              @Named(VIRTUALBOX_WORKINGDIR) final String workingDir,
                              @Named(VirtualBoxConstants.VIRTUALBOX_ISOFILE) final String isoFile) {
      this.context = context;
      this.workingDir = workingDir;
      this.isoFile = isoFile;
   }

   @Override
   public File apply(@Nullable URI input) {

      final File file = new File(workingDir, isoFile);

      if (!file.exists()) {
         final InputStream inputStream = context.utils().http().get(input);
         checkNotNull(inputStream, "%s not found", input);
         try {
            copy(inputStream, new FileOutputStream(file));
            return file;
         } catch (FileNotFoundException e) {
            logger.error(e, "File %s could not be found", file);
         } catch (IOException e) {
            logger.error(e, "Error when downloading file %s", input);
         } finally {
            closeQuietly(inputStream);
         }
         return null;
      } else {
         logger.debug("File %s already exists. Skipping download", file);
         return file;
      }
   }
}
