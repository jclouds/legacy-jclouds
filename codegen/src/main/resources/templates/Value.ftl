[#ftl]
[#--


    Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>

    ====================================================================
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
    ====================================================================

--]
package ${bean.packageName};

[#if bean.packageName != rootPackageName]
import ${rootPackageName}.*;
[/#if]

/**
 *
[#list bean.see as see]
[#if see?contains(".html")]
 * @see <a href='${see}' />
[#else]
 * @see ${see}
[/#if]
[/#list]
 * @author Generated 
 */
public class ${shortClassName} {

[#-- Print fields --]
[#list bean.fields![] as field]

   private ${field.javaType} ${field.name?uncap_first};

[/#list]

[#-- Print get/set --]
[#list bean.fields![] as field]
[#assign lowerName = field.name?uncap_first]
[#assign upperName = field.name?cap_first]

   public ${field.javaType} get${upperName}(){
      return this.${lowerName};
   }

   public void set${upperName}(${field.javaType} ${lowerName}) {
      this.${lowerName} = ${lowerName};
   }

[/#list]
}
