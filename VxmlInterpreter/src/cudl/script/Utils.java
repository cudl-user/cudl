package cudl.script;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import cudl.node.VoiceXmlNode;

public class Utils {

	public static String scriptableObjectToString(Object scriptObject) {
		String objectName = scriptObject.getClass().getSimpleName();

		if (objectName.equals("Double") || scriptObject.getClass().getSimpleName().equals("Integer")) {
			return scriptObject.toString();
		}

		if (objectName.equals("NativeArray")) {
			return nativeArrayToString(scriptObject);
		}

		if (objectName.equals("NativeObject")) {
			return nativeObjectToString(scriptObject);
		}

		return javascriptScring(scriptObject.toString());
	}

	private static String nativeObjectToString(Object scriptObject) {
		String ret = "{";
		NativeObject nativeObject = (NativeObject) scriptObject;
		Set<Object> keySet = nativeObject.keySet();
		for (Object object : keySet) {
			ret += scriptableObjectToString(object) + " : " + scriptableObjectToString(nativeObject.get(object)) + ", ";
		}
		ret += "}";
		return ret.replaceAll("(,) *}", "}");
	}

	private static String nativeArrayToString(Object scriptObject) {
		List<Object> list = new ArrayList<Object>();
		NativeArray array = ((NativeArray) scriptObject);
		for (int i = 0; i < array.getLength(); i++) {
			Object object = array.get(i);
			list.add(scriptableObjectToString(object));
		}
		return list.toString();
	}

	private static String javascriptScring(String string) {
		return "'" + string + "'";
	}

	public static List<VoiceXmlNode> serachItems(VoiceXmlNode currentFormItem, String itemName) {
		ArrayList<VoiceXmlNode> searchResult = new ArrayList<VoiceXmlNode>();
		for (VoiceXmlNode voiceXmlNode : currentFormItem.getChilds()) {
			if(voiceXmlNode.getNodeName().equals(itemName)){
				searchResult.add(voiceXmlNode);
			}
		}
		return searchResult;
	}
}