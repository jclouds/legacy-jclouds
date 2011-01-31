/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.byon.suppliers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.annotation.Resource;

import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;
import org.jclouds.util.Strings2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author Adrian Cole
 */
public class SupplyFromProviderURIOrNodesProperty implements Supplier<InputStream>, Function<URI, InputStream> {
   @Resource
   protected Logger logger = Logger.NULL;
   private final URI url;

   @Inject(optional = true)
   @Named("byon.nodes")
   @VisibleForTesting
   String nodes;

   @Inject
   public SupplyFromProviderURIOrNodesProperty(@Provider URI url) {
      this.url = checkNotNull(url, "url");
   }

   @Override
   public InputStream get() {
      if (nodes != null)
         return Strings2.toInputStream(nodes);
      return apply(url);
   }

   @Override
   public String toString() {
      return "[url=" + url + "]";
   }

   @Override
   public InputStream apply(URI input) {
      try {
         if (input.getScheme() != null && input.getScheme().equals("classpath"))
            return getClass().getResourceAsStream(input.getPath());
         return input.toURL().openStream();
      } catch (IOException e) {
         logger.error(e, "URI could not be read: %s", url);
         Throwables.propagate(e);
         return null;
      }
   }

}