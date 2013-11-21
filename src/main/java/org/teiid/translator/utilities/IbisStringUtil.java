package org.teiid.translator.utilities;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class IbisStringUtil {

	public static String trimSingleQuotes(String value) {
		if (value == null)
			return value;

		value = value.trim();
		if (value.startsWith("\'") && value.endsWith("\'")) {
			return value.substring(1, value.length() - 1);
		}

		return value;

	}
	
	public static String getShortName(String elementName) {
		int lastDot = elementName.lastIndexOf("."); //$NON-NLS-1$
		if (lastDot >= 0) {
			elementName = elementName.substring(lastDot + 1);
		}
		return elementName;
	}
	
	public static String encode(String url) throws UnsupportedEncodingException{
	return	url.replace(" ", "%20");
	}
	
	public static String addDoubleQuotes(String uri){
		uri = trimSingleQuotes(uri);
		uri = "\"" + uri + "\"";
		return uri;
	}

}
