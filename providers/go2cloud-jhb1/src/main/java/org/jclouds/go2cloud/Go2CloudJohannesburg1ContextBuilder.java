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
package org.jclouds.go2cloud;

import java.util.List;
import java.util.Properties;

import org.jclouds.elasticstack.ElasticStackContextBuilder;
import org.jclouds.go2cloud.config.Go2CloudJohannesburg1ComputeServiceContextModule;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class Go2CloudJohannesburg1ContextBuilder extends ElasticStackContextBuilder {

   public Go2CloudJohannesburg1ContextBuilder(Properties props) {
      super(props);
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new Go2CloudJohannesburg1ComputeServiceContextModule());
   }

}
