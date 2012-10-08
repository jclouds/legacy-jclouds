package org.jclouds.ec2.xml;

import static org.jclouds.util.SaxUtils.currentOrNegative;
import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.ec2.domain.IpPermission;
import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.SAXException;

/**
 *
 * @author Adrian Cole
 */
public class IpPermissionHandler extends ParseSax.HandlerForGeneratedRequestWithResult<IpPermission> {

   private StringBuilder currentText = new StringBuilder();
   private IpPermission.Builder builder = IpPermission.builder();

   /**
    * {@inheritDoc}
    */
   @Override
   public IpPermission getResult() {
      try {
         return builder.build();
      } finally {
         builder = IpPermission.builder();
      }
   }

   private String userId;
   private String groupId;

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "ipProtocol")) {
         // Algorete: ipProtocol can be an empty tag on EC2 clone (e.g.
         // OpenStack EC2)
         builder.ipProtocol(IpProtocol.fromValue(currentOrNegative(currentText)));
      } else if (equalsOrSuffix(qName, "fromPort")) {
         // Algorete: fromPort can be an empty tag on EC2 clone (e.g. OpenStack
         // EC2)
         builder.fromPort(Integer.parseInt(currentOrNegative(currentText)));
      } else if (equalsOrSuffix(qName, "toPort")) {
         // Algorete: toPort can be an empty tag on EC2 clone (e.g. OpenStack
         // EC2)
         builder.toPort(Integer.parseInt(currentOrNegative(currentText)));
      } else if (equalsOrSuffix(qName, "cidrIp")) {
         builder.ipRange(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "userId")) {
         this.userId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "groupName") || equalsOrSuffix(qName, "groupId")) {
         this.groupId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "item")) {
         if (userId != null && groupId != null)
            builder.userIdGroupPair(userId, groupId);
         userId = groupId = null;
      }
      currentText = new StringBuilder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
