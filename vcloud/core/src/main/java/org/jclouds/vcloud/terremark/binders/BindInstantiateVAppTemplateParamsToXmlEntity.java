package org.jclouds.vcloud.terremark.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringEntity;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindInstantiateVAppTemplateParamsToXmlEntity implements MapBinder {
   @Inject
   @Named("InstantiateVAppTemplateParams")
   String xmlTemplate;
   @Inject
   BindToStringEntity stringBinder;

   public void bindToRequest(HttpRequest request, Map<String, String> postParams) {

      String name = checkNotNull(postParams.get("name"), "name parameter not present");
      String template = checkNotNull(postParams.get("template"), "template parameter not present");
      String count = checkNotNull(postParams.get("count"), "count parameter not present");
      String megabytes = checkNotNull(postParams.get("megabytes"),
               "megabytes parameter not present");
      String network = checkNotNull(postParams.get("network"), "network parameter not present");

      String entity = xmlTemplate.replaceAll("\\{name\\}", name);
      entity = entity.replaceAll("\\{template\\}", template);
      entity = entity.replaceAll("\\{count\\}", count);
      entity = entity.replaceAll("\\{megabytes\\}", megabytes);
      entity = entity.replaceAll("\\{network\\}", network);

      stringBinder.bindToRequest(request, entity);
   }

   public void bindToRequest(HttpRequest request, Object input) {
      throw new IllegalStateException("InstantiateVAppTemplateParams is needs parameters");

   }

}
