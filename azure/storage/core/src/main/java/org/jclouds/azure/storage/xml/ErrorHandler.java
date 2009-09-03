package org.jclouds.azure.storage.xml;

import org.jclouds.azure.storage.domain.AzureStorageError;
import org.jclouds.http.functions.ParseSax;

/**
 * Parses the error from the Amazon S3 REST API.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?UsingRESTError.html"
 *      />
 * @author Adrian Cole
 */
public class ErrorHandler extends ParseSax.HandlerWithResult<AzureStorageError> {

   private AzureStorageError error = new AzureStorageError();
   private StringBuilder currentText = new StringBuilder();

   public AzureStorageError getResult() {
      return error;
   }

   public void endElement(String uri, String name, String qName) {

      if (qName.equals("Code")) {
         error.setCode(currentText.toString());
      } else if (qName.equals("Message")) {
         error.setMessage(currentText.toString());
      } else if (!qName.equals("Error")) {
         error.getDetails().put(qName, currentText.toString());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
