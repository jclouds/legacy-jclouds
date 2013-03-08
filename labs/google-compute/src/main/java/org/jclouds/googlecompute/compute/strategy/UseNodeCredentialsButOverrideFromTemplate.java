package org.jclouds.googlecompute.compute.strategy;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.domain.LoginCredentials;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * GCE needs the credentials to create the node so the node credentials already take the Image credentials into account,
 * as such only overriding the TemplateOptions credentials is required.
 *
 * @author David Alves
 */
@Singleton
public class UseNodeCredentialsButOverrideFromTemplate extends PrioritizeCredentialsFromTemplate {


   @Inject
   public UseNodeCredentialsButOverrideFromTemplate(
           Function<Template, LoginCredentials> credentialsFromImageOrTemplateOptions) {
      super(credentialsFromImageOrTemplateOptions);
   }

   public LoginCredentials apply(Template template, LoginCredentials fromNode) {
      RunScriptOptions options = checkNotNull(template.getOptions(), "template options are required");
      LoginCredentials.Builder builder = LoginCredentials.builder(fromNode);
      if (options.getLoginUser() != null)
         builder.user(template.getOptions().getLoginUser());
      if (options.getLoginPassword() != null)
         builder.password(options.getLoginPassword());
      if (options.getLoginPrivateKey() != null)
         builder.privateKey(options.getLoginPrivateKey());
      if (options.shouldAuthenticateSudo() != null && options.shouldAuthenticateSudo())
         builder.authenticateSudo(true);
      return builder.build();
   }
}
