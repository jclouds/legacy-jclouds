[#ftl]
[#--


    Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>

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

import ${rootPackageName}.domain.*;
[#if bean.response??]
import ${bean.response.packageName}.*;
[/#if]
[#if bean.options??]
import ${bean.options.packageName}.*;
[/#if]
import org.jclouds.aws.reference.AWSConstants;
import java.util.Set;
import java.util.List;

import org.jclouds.http.commands.callables.xml.ParseSax;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

import org.jclouds.http.HttpFutureCommand;

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
[#assign RT = bean.response.javaType]
public class ${shortClassName} extends HttpFutureCommand<${RT}> {
   @Inject
   public ${shortClassName}(@Named(AWSConstants.PROPERTY_AWS_ACCESSKEYID) String awsAccessKeyId, 
             @Named(AWSConstants.PROPERTY_AWS_SECRETACCESSKEY) String awsSecretAccessKey, 
             ParseSax<${RT}> callable, 
[#if bean.options.javaType?? ]
             @Assisted ${bean.options.javaType} options
[#else]
             @Assisted BaseEC2RequestOptions<EC2RequestOptions> options
[/#if]
[#list bean.parameters![] as param]
             ,@Assisted ${param.javaType} ${param.javaName?uncap_first}[#rt]
[/#list]) {
      super("GET", 
         "/" + options
[#list bean.parameters![] as param]
            .with${param.javaName?cap_first}(${param.javaName?uncap_first})
[/#list]
            .signWith(awsAccessKeyId,awsSecretAccessKey).buildQueryString(), callable);
   }
}