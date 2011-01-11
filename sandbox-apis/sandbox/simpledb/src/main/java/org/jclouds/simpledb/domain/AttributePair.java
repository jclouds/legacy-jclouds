package org.jclouds.simpledb.domain;



/**
 * AttributePair is a class to support the attributes to put in the SimpleDB
 * <p/>
 * 
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class AttributePair 
{

	private String key;
	private String value;
	private boolean replace;
	
	
	/**
	 * 
	 * Default constructor to represent an attribute in a domain in SimpleDB
	 * 
	 * <p/>
	 * 
	 * @param key Name of Attribute
	 * @param value Value of Attribute
	 * @param replace Replace value if it already exists in domain
	 */
	public AttributePair(String key, String value, boolean replace)
	{
		this.key = key;
                
		this.value = value ;
		this.replace = replace;
	}
	
	
	public String getKey() {
		return key;
	}


	public String getValue() {
		return value;
	}

        public void setValue(String value)
        {
            this.value = value;
        }


	public boolean isReplace() {
		return replace;
	}

	
	
	
}
