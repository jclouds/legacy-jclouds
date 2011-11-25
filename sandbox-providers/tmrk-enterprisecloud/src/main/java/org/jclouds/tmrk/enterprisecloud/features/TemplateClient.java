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
package org.jclouds.tmrk.enterprisecloud.features;

import org.jclouds.concurrent.Timeout;
import org.jclouds.tmrk.enterprisecloud.domain.template.Template;
import org.jclouds.tmrk.enterprisecloud.domain.template.Templates;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Provides synchronous access to Template(s).
 * <p/>
 * 
 * @see org.jclouds.tmrk.enterprisecloud.features.TemplateAsyncClient
 * @see <a href=
 *      "http://support.theenterprisecloud.com/kb/default.asp?id=984&Lang=1&SID="
 *      />
 * @author Jason King
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface TemplateClient {

   /**
    * The Get Templates call returns information regarding templates defined in a compute pool.
    * Note that Templates are not a simple wrapper around template objects, so if not found returns null.
    * Once the desired template is located getTemplate must be called to retrieve all the attached information
    * @param uri compute pool identifier
    * @return the templates
    */
   Templates getTemplates(URI uri);

   /**
    * The Get Templates by ID call returns information regarding a specified template defined in a compute pool
    * @param uri the uri of the template
    * @return the template
    */
   Template getTemplate(URI uri);

}
