package de.ilume.western.union.extractor.util;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class XmlUtil {
	
	public static boolean contains(final String label, final String text) {
		if (label == null) {
			return false;
		}
		return label.contains(text);
	}
	
	public static String getText(final Elements elements) {
		if (elements.size() > 0) {
			return getText(elements.get(0));
		}
		return "";
	}
	
	public static String getText(final Element element) {
		Element textTag = element;
		
		while(!textTag.hasText() && textTag.children().size() > 0) {
			textTag = textTag.children().get(0);
		}
		
		return textTag.text();
	}
	
	public static String textFromChildAt(final Element element, final int index) {
		return textFromChildAt(element.children(), index);
	}
	
	public static String textFromChildAt(final Elements elements, final int index) {
		if (elements.size() > index) {
			String result = elements.get(index).text().trim();
			if (result == null) {
				result = "";
			}
			return result;
		} else {
			return "";
		}
	}

}
