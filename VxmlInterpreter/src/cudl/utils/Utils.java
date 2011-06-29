package cudl.utils;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.lang.String;
import java.net.MalformedURLException;
import java.net.URL;

public class Utils {
	public static Node lastNodeVisited;

	public static Node searchDialogByName(NodeList nodeList, String id) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			NamedNodeMap attributes = node.getAttributes();
			Node namedItem = null;
			if (null != attributes) {
				namedItem = attributes.getNamedItem("id");
			}
			if (namedItem != null && namedItem.getNodeValue().equals(id)) {
				return node;
			}
		}
		return null;
	}

	// FIXME: search all occurrence of searchNode
	public static Node serachItem(Node parent, String searchNodeName) {
		NodeList nodeList = parent.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals(searchNodeName)) {
				return node;
			}
		}
		return null;
	}

	public static boolean checkCond(Node node) {
		NamedNodeMap attribute = node.getAttributes();
		Node cond = (attribute.getLength() == 0) ? null : attribute
				.getNamedItem("cond");
		return cond == null || cond.getNodeValue().equals("true");
	}

	public static String tackWeelFormedUrl(String location, String relativePath)
			throws MalformedURLException {
		URL target = new URL(new URL(location), relativePath);
		return target.toString();
	}
	
	public static String getNodeAttributeValue(Node node, String attributeName) {
		NamedNodeMap attributes = node.getAttributes();
		if (attributes == null)
			return null;

		Node attributeValue = attributes.getNamedItem(attributeName);
		return attributeValue == null ? null : attributeValue.getNodeValue();
	}
}
