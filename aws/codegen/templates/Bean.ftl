[#ftl]
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
[#list bean.parameters![] as field]
   /**
    *
    * ${field.desc} 
    * /
   private ${field.javaType} ${field.name?uncap_first};

[/#list]

[#-- Print get/set --]
[#list bean.parameters![] as field]
[#assign lowerName = field.name?uncap_first]
[#assign upperName = field.name?cap_first]
   /**
    *
    * @return ${field.desc} 
    * /
   public ${field.javaType} get${upperName}(){
      return this.${lowerName};
   }

   /**
    *
    * @param ${lowerName} 
    * ${field.desc} 
    * /
   public void set${upperName}(${field.javaType} ${lowerName}) {
      this.${lowerName} = ${lowerName};
   }

[/#list]
}
