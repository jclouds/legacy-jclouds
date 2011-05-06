/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.providers;

import java.net.URI;

/**
 * The ProviderMetadata interface allows jclouds to provide a plugin framework
 * for gathering cloud provider metadata.
 *
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
public interface ProviderMetadata {

   public static final String BLOBSTORE_TYPE = "blobstore";
   public static final String COMPUTE_TYPE = "compute";

  /**
   * Returns an identifier unique to the provider.
   *
   * @return the provider's unique identifier
   */
  public String getId();

  /**
   * Returns the provider type.
   *
   * @return the provider's type
   */
  public String getType();

  /**
   * Returns the name of the provider.
   *
   * @return the name (display name) of the provider
   */
  public String getName();

  /**
   * Returns the URI to the provider's homepage.
   *
   * @return the url for the provider's homepage
   */
  public URI getHomepage();

  /**
   * Returns the URI to the provider's console.
   *
   * @return the url for the provider's console
   */
  public URI getConsole();

}