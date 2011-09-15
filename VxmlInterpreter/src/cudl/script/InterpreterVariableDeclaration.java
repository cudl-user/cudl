package cudl.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import cudl.utils.CudlSession;
import cudl.utils.Utils;

public class InterpreterVariableDeclaration {
	private static final String APPLICATION_VARIABLES = "lastresult$ = new Array(); "
			+ "lastresult$[0] = new Object(); " + "lastresult$[0].confidence = 1; "
			+ "lastresult$[0].utterance = undefined;" + "lastresult$[0].inputmode = undefined;"
			+ "lastresult$[0].interpretation = undefined;";

	public static final int SESSION_SCOPE = 90;
	public static final int APPLICATION_SCOPE = 80;
	public static final int DOCUMENT_SCOPE = 70;
	public static final int DIALOG_SCOPE = 60;
	public static final int ANONYME_SCOPE = 50;
	private ScriptableObject sharedScope;
	private ScriptableObject anonymeScope;
	private ScriptableObject dialogScope;
	private ScriptableObject documentScope;
	private ScriptableObject applicationScope;
	private ScriptableObject sessionScope;

	public InterpreterVariableDeclaration() throws IOException {
		Context context = new ContextFactory().enterContext();

		sharedScope = context.initStandardObjects();
		sessionScope = (ScriptableObject) context.newObject(sharedScope);
		sessionScope.put("session", sessionScope, sessionScope);
		sessionScope.setPrototype(sharedScope);

		applicationScope = (ScriptableObject) context.newObject(sessionScope);
		applicationScope.put("application", applicationScope, applicationScope);
		applicationScope.setPrototype(sessionScope);

		documentScope = (ScriptableObject) context.newObject(applicationScope);
		documentScope.put("document", documentScope, documentScope);
		documentScope.setPrototype(applicationScope);

		dialogScope = (ScriptableObject) context.newObject(documentScope);
		dialogScope.put("dialog", dialogScope, dialogScope);
		dialogScope.setPrototype(documentScope);

		anonymeScope = (ScriptableObject) context.newObject(dialogScope);
		anonymeScope.setPrototype(dialogScope);

		declareNormalizedSessionVariables();
		declarareNormalizedApplicationVariables();
	}

	public void declareVariable(String name, String value, int scope) {
		ScriptableObject scope2 = getScope(scope);
		scope2.put(name, scope2, evaluateScript(value, scope));
	}

	public Object evaluateScript(String script, int scope) {
		Context ctxt = Context.enter();
		return ctxt.evaluateString(getScope(scope), script, script + " " + scope, 1, null);
	}

	public void setValue(String name, String value) {
		Context ctxt = Context.enter();

		System.err.println(name + "     =      " + value);
		String[] split = name.split("\\.");
		if (Utils.scopeNames().contains(split[0])) {
			ctxt.evaluateString(getScopeByName(split[0]), split[1] + "=" + value, name + "=" + value,
					1, null);
		} else {
			ScriptableObject start = getScope(ANONYME_SCOPE);
			while (start != null) {
				if (start.has(name, start)) {
					ctxt.evaluateString(start, split[0] + "=" + value, name + "=" + value, 1, null);
					break;
				}
				start = (ScriptableObject) start.getPrototype();
			}
		}

	}

	public Object getValue(String name) {
		return Context.enter().evaluateString(anonymeScope, name, name, 1, null);
	}

	public void resetScopeBinding(int scope) {
		Context context = Context.enter();
		switch (scope) {
		case APPLICATION_SCOPE:
			applicationScope = (ScriptableObject) context.newObject(sessionScope);
			applicationScope.put("application", applicationScope, applicationScope);
			applicationScope.setPrototype(sessionScope);
			declarareNormalizedApplicationVariables();
		case DOCUMENT_SCOPE:
			documentScope = (ScriptableObject) context.newObject(applicationScope);
			documentScope.put("document", documentScope, documentScope);
			documentScope.setPrototype(applicationScope);
		case DIALOG_SCOPE:
			dialogScope = (ScriptableObject) context.newObject(documentScope);
			dialogScope.put("dialog", dialogScope, dialogScope);
			dialogScope.setPrototype(documentScope);
		case ANONYME_SCOPE:
			anonymeScope = (ScriptableObject) context.newObject(dialogScope);
			anonymeScope.setPrototype(dialogScope);
			break;
		default:
			break;
		}
	}

	public Object evaluateFileScript(String fileName, int scope) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new URL(fileName).openStream()));
		try {
			Context ctxt = Context.enter();
			return ctxt.evaluateReader(getScope(scope), in, fileName, 1, null);
		} finally {
			in.close();
		}
	}

	private void declareNormalizedSessionVariables() throws IOException {
		Context ctxt = new ContextFactory().enterContext();
		try {
			Class<?> cudlSessionFile = Class.forName("test.Session");
			CudlSession cudlSession = (CudlSession) cudlSessionFile.newInstance();
			String sessionScript = cudlSession.getSessionScript();
			ctxt.evaluateString(sessionScope	, sessionScript, sessionScript, 1, null);			
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (ClassNotFoundException e) {
			System.out.println("WARNING: You do not define session file. It name will be Session and placed in package test");
		}
	}

	private void declarareNormalizedApplicationVariables() {
		Context.enter().evaluateString(applicationScope, APPLICATION_VARIABLES,
				APPLICATION_VARIABLES, 1, null);
	}

	private Scriptable getScopeByName(String name) {
		return new HashMap<String, Scriptable>() {
			{
				put("application", applicationScope);
				put("document", documentScope);
				put("dialog", dialogScope);
			}
		}.get(name);
	}

	private ScriptableObject getScope(int scope) {
		switch (scope) {
		case ANONYME_SCOPE:
			return anonymeScope;
		case DIALOG_SCOPE:
			return dialogScope;
		case DOCUMENT_SCOPE:
			return documentScope;
		case APPLICATION_SCOPE:
			return applicationScope;
		case SESSION_SCOPE:
			return sessionScope;
		default:
			throw new IllegalArgumentException("Scope " + scope + " Undefined");
		}
	}
}
