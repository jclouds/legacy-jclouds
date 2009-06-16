package org.jclouds.aws.s3.xml;

import java.util.Properties;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.AccessControlList.CanonicalUserGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.Grant;
import org.jclouds.aws.s3.domain.AccessControlList.GroupGrantee;
import org.jclouds.aws.s3.reference.S3Constants;
import org.w3c.dom.Document;

import com.jamesmurty.utils.XMLBuilder;

public class AccessControlListBuilder {
   private final AccessControlList acl;
   
   public AccessControlListBuilder(AccessControlList acl) {
      this.acl = acl;
   }
   
   protected XMLBuilder generateBuilder() throws ParserConfigurationException, 
         FactoryConfigurationError 
   {
      XMLBuilder ownerBuilder = XMLBuilder
         .create("AccessControlPolicy").attr("xmlns", S3Constants.S3_REST_API_XML_NAMESPACE)
            .elem("Owner");
      if (acl.getOwner() != null) {
         ownerBuilder.elem("ID").text(acl.getOwner().getId()).up();
         if (acl.getOwner().getDisplayName() != null) {
            ownerBuilder.elem("DisplayName").text(acl.getOwner().getDisplayName()).up();
         }
      }
      XMLBuilder grantsBuilder = ownerBuilder.root().elem("AccessControlList");
      for (Grant grant : acl.getGrants()) {
         XMLBuilder grantBuilder = grantsBuilder.elem("Grant");
         XMLBuilder granteeBuilder = grantBuilder            
               .elem("Grantee").attr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
         
         if (grant.getGrantee() instanceof GroupGrantee) {
            granteeBuilder.attr("xsi:type", "Group")
               .elem("URI").text(grant.getGrantee().getIdentifier());
         } else if (grant.getGrantee() instanceof CanonicalUserGrantee) {
            CanonicalUserGrantee grantee = (CanonicalUserGrantee) grant.getGrantee();
            granteeBuilder.attr("xsi:type", "CanonicalUser")
               .elem("ID").text(grantee.getIdentifier()).up();
            if (grantee.getDisplayName() != null) {
               granteeBuilder.elem("DisplayName").text(grantee.getDisplayName());
            }
         } else if (grant.getGrantee() instanceof EmailAddressGrantee) {            
            granteeBuilder.attr("xsi:type", "AmazonCustomerByEmail")
               .elem("EmailAddress").text(grant.getGrantee().getIdentifier());            
         }
         grantBuilder.elem("Permission").text(grant.getPermission().toString());
      }
      return grantsBuilder;            
   }   
   
   public Document getDocument() throws ParserConfigurationException, FactoryConfigurationError {
      return generateBuilder().getDocument();
   }
   
   public String getXmlString() throws TransformerException, ParserConfigurationException, 
         FactoryConfigurationError 
   {
      Properties outputProperties = new Properties();
      outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
      return generateBuilder().asString(outputProperties);
   }

}
