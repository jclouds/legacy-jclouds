[#ftl]
package ${bean.packageName};

import ${rootPackageName}.domain.*;
import ${rootPackageName}.response.*;
[#if bean.options??]
import ${bean.options.packageName}.*;
[/#if]

import org.jclouds.http.commands.callables.xml.ParseSax;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
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
[#if bean.options?? && bean.options.awsType??]
             @Assisted ${bean.options.awsType} options,
[#else]
             @Assisted BaseEC2RequestOptions<EC2RequestOptions> options,
[/#if]
[#list bean.parameters![] as param]
             @Assisted ${param.javaType} ${param.name?uncap_first}[#rt]
[#if param_has_next],[#else])[/#if]
[/#list]
{
   super("GET", 
      "/" + options.buildQueryString()
[#list bean.parameters![] as param]
         .with${param.name?cap_first}(${param.name?uncap_first})
[/#list]
         .signWith(awsAccessKeyId,awsSecretAccessKey), 
      callable);
}
