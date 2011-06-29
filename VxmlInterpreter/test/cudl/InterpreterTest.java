package cudl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.script.ScriptException;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import cudl.utils.Prompt;

public class InterpreterTest {
	private InterpreterContext interpreterContext;
	private String url;

	@Before
	public void setUp() throws IOException {
		url = "file://" + new File(".").getCanonicalPath() + "/test/docVxml/";
	}

	@Test
	public void testLogTrace() throws IOException, ScriptException,
			ParserConfigurationException, SAXException {
		List<String> traceLog = new ArrayList<String>();
		traceLog.add("LOG Hello");
		traceLog.add("LOG Hello 1");
		traceLog.add("LOG Hello 2");
		traceLog.add("LOG Hello 3");

		List<String> traceStat = new ArrayList<String>();
		traceStat.add("LOG Hello");

		interpreterContext = new InterpreterContext(url + "shello2.vxml");
		interpreterContext.launchInterpreter();

		assertEquals(traceStat, interpreterContext.interpreter
				.getTracetWithLabel("stats"));
		assertEquals(traceLog, interpreterContext.interpreter.getTraceLog());
		assertTrue(interpreterContext.interpreter.raccrochage());
	}

	@Test
	public void testLogTraceWithExit() throws IOException, ScriptException,
			ParserConfigurationException, SAXException {
		List<String> traceLog = new ArrayList<String>();
		traceLog.add("LOG Hello");
		traceLog.add("LOG Hello 1");

		List<String> traceStat = new ArrayList<String>();
		traceStat.add("LOG Hello");

		interpreterContext = new InterpreterContext(url + "shelloExit.vxml");
		interpreterContext.launchInterpreter();

		assertEquals(traceLog, interpreterContext.interpreter.getTraceLog());
		assertEquals(traceStat, interpreterContext.interpreter
				.getTracetWithLabel("stats"));
		assertTrue(interpreterContext.interpreter.raccrochage());
	}

	@Test
	public void testLogTraceWhithVariable() throws IOException,
			ScriptException, ParserConfigurationException, SAXException {
		List<String> traceLog = new ArrayList<String>();
		traceLog.add("LOG Hello");
		traceLog.add("LOG Hello 2");
		traceLog.add("LOG Hello 2");
		traceLog.add("LOG Hello 3");

		List<String> traceStat = new ArrayList<String>();
		traceStat.add("LOG Hello");

		interpreterContext = new InterpreterContext(url + "shelloVar.vxml");
		interpreterContext.launchInterpreter();

		assertEquals(traceStat, interpreterContext.interpreter
				.getTracetWithLabel("stats"));

		assertEquals(traceLog, interpreterContext.interpreter.getTraceLog());
		assertTrue(interpreterContext.interpreter.raccrochage());
	}

	// @Test
	// public void testLogTraceWhithVariableWithClear() throws
	// IOException {
	// List<String> traceLog = new ArrayList<String>();
	// traceLog.add("LOG Hello");
	// traceLog.add("LOG Hello 1");
	// traceLog.add("LOG Hello 2");
	// traceLog.add("LOG Hello 3");
	//
	// List<String> traceStat = new ArrayList<String>();
	// traceStat.add("[label:stats] LOG Hello");
	//
	// varExcepted = new TreeMap<String, String>();
	// varExcepted.put("block_0", "defined");
	// varExcepted.put("block_1", "defined");
	// varExcepted.put("block_2", "defined");
	// varExcepted.put("block_3", "defined");
	// varExcepted.put("telephone", "undefined");
	// varExcepted.put("telephone1", "undefined");
	//
	// interpreterContext = new InterpreterContext(url+"shelloVarClear.vxml");
	// interpreterContext.launchInterpreter();
	//
	// assertEquals(traceLog, interpreterContext.interpreter.getTraceLog());
	// assertEquals(traceStat, interpreterContext.interpreter.getTraceStat());
	// assertEquals(varExcepted, interpreterContext.interpreter.getVar());
	// }

	@Test
	public void testLogTraceWithIfElseifAndElse() throws IOException,
			ScriptException, ParserConfigurationException, SAXException {
		List<String> traceLog = new ArrayList<String>();
		traceLog.add("LOG Hello");
		traceLog.add("LOG Hello 1");
		traceLog.add("LOG Hello 2");
		traceLog.add("LOG Hello A");
		traceLog.add("LOG Hello B");
		traceLog.add("LOG Hello 3");
		traceLog.add("LOG Hello 4");
		traceLog.add("LOG Hello 5");

		interpreterContext = new InterpreterContext(url
				+ "shelloWith_if_elseif_and_else.vxml");
		interpreterContext.launchInterpreter();

		assertTrue(interpreterContext.interpreter.getTracetWithLabel("stats")
				.isEmpty());

		System.err.println(traceLog);
		System.err.println(interpreterContext.interpreter.getTraceLog());
		assertEquals(traceLog, interpreterContext.interpreter.getTraceLog());
		assertTrue(interpreterContext.interpreter.raccrochage());
	}

	@Test
	public void testLogManyBlock() throws IOException, ScriptException,
			ParserConfigurationException, SAXException {
		List<String> traceLog = new ArrayList<String>();
		traceLog.add("LOG Hello");
		traceLog.add("LOG Hello 1");
		traceLog.add("LOG Hello 2");

		interpreterContext = new InterpreterContext(url + "manyBlocks.vxml");
		interpreterContext.launchInterpreter();

		assertTrue(interpreterContext.interpreter.getTracetWithLabel("stats")
				.isEmpty());
		assertEquals(traceLog, interpreterContext.interpreter.getTraceLog());
	}

	@Test
	public void testLogGotoInASameDialog() throws IOException, ScriptException,
			ParserConfigurationException, SAXException {
		List<String> traceLog = new ArrayList<String>();
		traceLog.add("LOG Hello");
		traceLog.add("LOG Hello 1");
		traceLog.add("LOG Hello 2");
		traceLog.add("LOG Hello 3");
		traceLog.add("LOG Hello 4");

		interpreterContext = new InterpreterContext(url + "goto.vxml");
		interpreterContext.launchInterpreter();

		assertTrue(interpreterContext.interpreter.getTracetWithLabel("stats")
				.isEmpty());
		assertEquals(traceLog, interpreterContext.interpreter.getTraceLog());
	}

	@Test
	public void testCollectPrompt() throws IOException, ScriptException,
			ParserConfigurationException, SAXException {
		List<Prompt> prompts = new ArrayList<Prompt>();
		Prompt promptExecepeted;

		promptExecepeted = new Prompt();
		promptExecepeted.timeout = "200ms";
		promptExecepeted.bargein = "true";
		promptExecepeted.audio = "/ROOT/prompts/WAV/ACCUEIL_CHOIX_TARIFS.wav ";
		promptExecepeted.tts = "Pour avoir des informations détaillées sur ce tarif, dites : « tarif ».";
		prompts.add(promptExecepeted);

		promptExecepeted = new Prompt();
		promptExecepeted.tts = "dites tarif.";
		prompts.add(promptExecepeted);

		promptExecepeted = new Prompt();
		promptExecepeted.timeout = "300ms";
		promptExecepeted.bargein = "true";
		promptExecepeted.audio = "/ROOT/prompts/WAV/ACCUEIL_CHOIX_TARIFS.wav ";
		promptExecepeted.tts = "Pour avoir des informations détaillées sur ce tarif, dites : « tarif ».";
		prompts.add(promptExecepeted);

		promptExecepeted = new Prompt();
		promptExecepeted.tts = "dites tarif.";
		prompts.add(promptExecepeted);

		interpreterContext = new InterpreterContext(url + "prompt.vxml");
		interpreterContext.launchInterpreter();

		assertEquals(prompts, interpreterContext.interpreter.getPrompts());
	}

	@Test
	public void testCombinattionPromptTrace() throws IOException,
			ScriptException, ParserConfigurationException, SAXException {
		List<Prompt> prompts = new ArrayList<Prompt>();
		Prompt promptExecepeted;

		promptExecepeted = new Prompt();
		promptExecepeted.audio = "rtsp://www.piafcauserie.example.com/grive.wav http://www.piafcauserie.example.com/tourterelle.wav ";
		promptExecepeted.tts = "Bienvenue chez Graines a gogo. Ce mois-ci nous proposons le barril de 250 kg de graines de chardon a 299.95€ frais d'expédition et de transport compris.";
		prompts.add(promptExecepeted);

		interpreterContext = new InterpreterContext(url + "prompt1.vxml");
		interpreterContext.launchInterpreter();

		assertTrue(interpreterContext.interpreter.getPrompts().size() == 1);
		assertEquals(prompts, interpreterContext.interpreter.getPrompts());
	}

	@Test()
	public void testNodeValue() throws IOException, ScriptException,
			ParserConfigurationException, SAXException {
		List<Prompt> prompts = new ArrayList<Prompt>();
		Prompt promptExecepeted;

		promptExecepeted = new Prompt();
		promptExecepeted.tts = "cette variable est defini partout dans le formulaire";
		prompts.add(promptExecepeted);

		promptExecepeted = new Prompt();
		promptExecepeted.tts = "ha ok: cette variable est defini partout dans le formulaire. c'est compris";
		prompts.add(promptExecepeted);

		interpreterContext = new InterpreterContext(url + "root.vxml");
		interpreterContext.launchInterpreter();

		assertEquals(prompts, interpreterContext.interpreter.getPrompts());
	}

	@Test
	public void sideEffectInScript() throws IOException, ScriptException,
			ParserConfigurationException, SAXException {
		List<String> traceLog = new ArrayList<String>();
		traceLog.add("bla bla");
		traceLog.add("un exemple de variable de global avec effet de bord");

		interpreterContext = new InterpreterContext(url
				+ "sideEffectInScope.vxml");
		interpreterContext.launchInterpreter();

		assertEquals(traceLog, interpreterContext.interpreter.getTraceLog());
	}

	@Test(expected = ScriptException.class)
	public void anonymeScopeVariable() throws IOException, ScriptException,
			ParserConfigurationException, SAXException {

		interpreterContext = new InterpreterContext(url
				+ "anonymeScopeVariable.vxml");
		interpreterContext.launchInterpreter();
	}

	@Test(expected = ScriptException.class)
	public void dialogScopeVariable() throws IOException, ScriptException,
			ParserConfigurationException, SAXException {
		interpreterContext = new InterpreterContext(url
				+ "dialogScopeVariable.vxml");
		interpreterContext.launchInterpreter();
	}

	@Test
	public void documentScopeVariableIsVisibleInAllDocument()
			throws IOException, ScriptException, URISyntaxException,
			ParserConfigurationException, SAXException {
		List<Prompt> prompts = new ArrayList<Prompt>();
		Prompt promptExecepeted;

		promptExecepeted = new Prompt();
		promptExecepeted.tts = "variable du document1";
		prompts.add(promptExecepeted);
		promptExecepeted = new Prompt();
		promptExecepeted.tts = "variable du document2";
		prompts.add(promptExecepeted);
		promptExecepeted = new Prompt();
		promptExecepeted.tts = "variable du document3";
		prompts.add(promptExecepeted);

		interpreterContext = new InterpreterContext(url
				+ "documentScopeVariable.vxml");
		interpreterContext.launchInterpreter();

		assertEquals(prompts, interpreterContext.interpreter.getPrompts());
	}

	@Test
	public void RootVariableIsAllWaysVisible() throws IOException,
			ScriptException, ParserConfigurationException, SAXException {
		List<Prompt> prompts = new ArrayList<Prompt>();
		Prompt promptExecepeted;

		promptExecepeted = new Prompt();
		promptExecepeted.tts = "variable application";
		prompts.add(promptExecepeted);
		promptExecepeted = new Prompt();
		promptExecepeted.tts = "variable du document";
		prompts.add(promptExecepeted);

		interpreterContext = new InterpreterContext(url + "rootVariable.vxml");
		interpreterContext.launchInterpreter();

		assertEquals(prompts, interpreterContext.interpreter.getPrompts());
	}

	@Test(expected = ScriptException.class)
	public void WhenRootChangeLastRootVariableIsNotLongerAccessible()
			throws IOException, ScriptException, ParserConfigurationException,
			SAXException {
		interpreterContext = new InterpreterContext(url
				+ "rootChangeVariable.vxml");
		interpreterContext.launchInterpreter();
	}

	@Test
	public void comparerTwoVariableInDifferentScope() throws IOException,
			ScriptException, ParserConfigurationException, SAXException {

		interpreterContext = new InterpreterContext(url
				+ "compareTwoVariableDeclaredIndifferentScope");
		interpreterContext.launchInterpreter();
	}
}