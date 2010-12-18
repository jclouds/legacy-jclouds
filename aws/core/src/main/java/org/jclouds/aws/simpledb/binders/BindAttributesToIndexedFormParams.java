package org.jclouds.aws.simpledb.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static org.jclouds.http.HttpUtils.addFormParamTo;

import java.util.Collection;
import java.util.Iterator;

import java.util.List;
import org.jclouds.aws.simpledb.domain.Item;
import org.jclouds.aws.simpledb.domain.AttributePair;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

/**
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class BindAttributesToIndexedFormParams implements Binder {

   private final String attributeName = "Attribute.%d.Name";
   private final String attributeValue = "Attribute.%d.Value";
   private final String attributeReplace = "Attribute.%d.Replace";

   public void bindToRequest(HttpRequest request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Item,
               "this binder is only valid for AttributeMap");
      Item attributeMap = (Item) input;

      int amazonOneBasedIndex = 1; // according to docs, counters must start with 1
      for (String itemName : attributeMap.getAttributes().keySet())
      {
       
    	    Collection<AttributePair> c = attributeMap.getAttributes().get(itemName);
            Iterator<AttributePair> it = c.iterator();
            while(it.hasNext())
            {
                AttributePair attr = it.next();
                // not null by contract

                String value = attr.getValue();

//                    System.out.println(format(attributeName, amazonOneBasedIndex)+ "" + attr.getKey());
//                    System.out.println(format(attributeValue, amazonOneBasedIndex)+ "" + value);
//                    System.out.println(format(attributeReplace, amazonOneBasedIndex) +""+ String
//                            .valueOf(attr.isReplace()) );

                if (value != null) {
                    addFormParamTo(request, format(attributeName, amazonOneBasedIndex), attr.getKey() );
                    addFormParamTo(request, format(attributeValue, amazonOneBasedIndex),
                               value);
                    addFormParamTo(request, format(attributeReplace, amazonOneBasedIndex), String
                         .valueOf(attr.isReplace()));


                }
                amazonOneBasedIndex++;
            }
            
       
      }

   }

}
