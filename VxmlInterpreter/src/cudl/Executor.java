package cudl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import cudl.node.Assign;
import cudl.node.Audio;
import cudl.node.Block;
import cudl.node.Clear;
import cudl.node.Disconnect;
import cudl.node.Else;
import cudl.node.Elseif;
import cudl.node.Exit;
import cudl.node.Filled;
import cudl.node.Form;
import cudl.node.Goto;
import cudl.node.If;
import cudl.node.Log;
import cudl.node.Prompt;
import cudl.node.Return;
import cudl.node.Submit;
import cudl.node.Text;
import cudl.node.Value;
import cudl.node.Var;
import cudl.node.VoiceXmlNode;
import cudl.script.Scripting;

public class Executor {
	private final Scripting scripting;
	private final SystemOutput voiceXTTOutPut;

	public Executor(Scripting scripting, SystemOutput voiceXTTOutPut) {
		this.scripting = scripting;
		this.voiceXTTOutPut = voiceXTTOutPut;
	}

	public Object execute(VoiceXmlNode child) throws InterpreterException {
		try {
			return Executor.class.getMethod("execute", child.getClass()).invoke(this, child);
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof InterpreterException) {
				System.err.println("blah");
				throw (InterpreterException) e.getCause();
			}
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("No implementation for Executor.execute("
					+ child.getClass().getSimpleName() + " " + child.getNodeName() + ")");
		}
		return null;
	}

	public Object execute(Goto goto1) throws InterpreterException {
		throw new GotoException(goto1);
	}

	public Object execute(Exit Exit) throws InterpreterException {
		throw new ExitException();
	}

	public Object execute(Log log) throws InterpreterException {
		String debug = "";
		String label = log.getLabel();
		String expr = log.getExpr();

		if (label != null) {
			debug += "[" + label + "] ";
		}

		if (expr != null) {
			debug += scripting.eval(expr);
		}

		for (VoiceXmlNode node : log.getChilds()) {
			debug += execute(node);
		}
		voiceXTTOutPut.addLog(debug.trim());
		return null;
	}

	public Object execute(Return return1) throws InterpreterException {
		throw new RuntimeException("implement Return executor");
	}

	public Object execute(Disconnect disconnect) throws InterpreterException {
		throw new RuntimeException("implement Disconnect executor");
	}

	public Object execute(Submit submit) throws InterpreterException {
		throw new RuntimeException("implement submit executor");
	}

	public Object execute(Audio audio) {
		cudl.Prompt p = new cudl.Prompt();
		String src = audio.getSrc();
		String expr = audio.getExpr();

		p.audio = audio.getSrc() == null ? scripting.eval(expr) + "" : src;
		p.tts = audio.getTextContent();

		if (!(audio.getParent() instanceof Prompt)) {
			voiceXTTOutPut.addPrompt(p);
		}

		return p;
	}

	public Object execute(Var var) {
		String name = var.getAttribute("name");
		String expr = var.getAttribute("expr");
		scripting.put(name, expr == null ? "undefined" : expr);
		return null;
	}

	public Object execute(Assign assign) {
		String name = assign.getAttribute("name");
		String expr = assign.getAttribute("expr");
		scripting.set(name, expr);
		return null;
	}

	public Object execute(Clear clear) {
		String nameList = clear.getAttribute("namelist");
		StringTokenizer tokenizer = new StringTokenizer(nameList, " ");
		if (!tokenizer.hasMoreElements()) {
			for (VoiceXmlNode voiceXmlNode : getEnclosingDialog(clear).getChilds()) {
				if (voiceXmlNode instanceof FormItem) {
					//scripting.set(((FormItem)voiceXmlNode).getName(), "undefined");
				}
			}
		}
		while (tokenizer.hasMoreElements()) {
			String name = (String) tokenizer.nextElement();
			scripting.set(name, "undefined");
		}
		return null;
	}

	private VoiceXmlNode getEnclosingDialog(VoiceXmlNode node) {
		VoiceXmlNode parent = node.getParent();

		while (!(parent instanceof Form)) {
			parent = parent.getParent();
		}
		return parent;
	}

	public Object execute(Text text) {
		String value = text.getValue().replaceAll("[\\s][\\s]+", " ");
		if (value.trim().isEmpty()) {
			return "";
		}
		if (value.contains(".")) {
			System.err.println("ok" + value);
		}
		if (text.getParent() instanceof Block) {
			cudl.Prompt p = new cudl.Prompt();
			p.tts += value.trim();
			voiceXTTOutPut.addPrompt(p);
		}
		return value;
	}

	public Object execute(Value value) {
		Object eval = null;
		eval = scripting.eval(value.getExpr());
		System.err.println("***" + eval + "***");
		if (value.getParent() instanceof Block /* catch */) {
			cudl.Prompt p = new cudl.Prompt();
			p.tts += eval;
			voiceXTTOutPut.addPrompt(p);
		}

		return eval;
	}

	public Object execute(Prompt prompt) throws InterpreterException {
		cudl.Prompt p = new cudl.Prompt();

		String timeout = prompt.getTimeout();
		p.timeout = timeout != null ? timeout : "";

		String bargein = prompt.getBargein();
		p.bargein = bargein != null ? bargein : "";

		String bargeinType = prompt.getBargeinType();
		p.bargeinType = bargeinType != null ? bargeinType : "";

		String tts = "";
		for (VoiceXmlNode voiceXmlNode : prompt.getChilds()) {
			if (voiceXmlNode instanceof Audio) {
				cudl.Prompt pa = (cudl.Prompt) execute(voiceXmlNode);
				p.tts += pa.tts + " ";
				p.audio += pa.audio + " ";
			} else {
				p.tts += execute(voiceXmlNode) + "";
			}
		}

		p.tts = p.tts.trim();
		p.audio = p.audio.trim();
		voiceXTTOutPut.addPrompt(p);
		return null;
	}

	public Object execute(Filled filled) throws InterpreterException {
		for (VoiceXmlNode voiceXmlNode : filled.getChilds()) {
			execute(voiceXmlNode);
		}
		return null;
	}

	public void execute(If if1) throws InterpreterException {
		String cond = if1.getCond();

		String string = scripting.eval(cond).toString();

		if (Boolean.parseBoolean(string)) {
			execute(getInTrueChilds(if1));
		} else {
			execute(getInFalseChilds(if1));
		}
	}

	private List<VoiceXmlNode> getInFalseChilds(If if1) {
		List<VoiceXmlNode> childsFalse = new ArrayList<VoiceXmlNode>();
		boolean inElse = false;
		String cond = "";
		for (VoiceXmlNode voiceXmlNode : if1.getChilds()) {
			if (voiceXmlNode instanceof Else || voiceXmlNode instanceof Elseif) {
				inElse = !inElse;
				cond = voiceXmlNode instanceof Elseif ? voiceXmlNode.getAttribute("cond") : null;
				if (!inElse) {
					return childsFalse;
				}
			}
			if (inElse) {
				if (cond == null || Boolean.parseBoolean(scripting.eval(cond).toString())) {
					childsFalse.add(voiceXmlNode);
				}
			}
		}

		return childsFalse;
	}

	private List<VoiceXmlNode> getInTrueChilds(If if1) {
		List<VoiceXmlNode> childsTrue = new ArrayList<VoiceXmlNode>();

		for (VoiceXmlNode voiceXmlNode : if1.getChilds()) {
			boolean el_se = voiceXmlNode instanceof Else;
			boolean elseif = voiceXmlNode instanceof Elseif;
			if (el_se || elseif)
				break;

			childsTrue.add(voiceXmlNode);
		}
		return childsTrue;
	}

	private void execute(List<VoiceXmlNode> voiceXmlNodes) throws InterpreterException {
		for (VoiceXmlNode voiceXmlNode : voiceXmlNodes) {
			try {
				execute(voiceXmlNode);
			} catch (Throwable e) {
				if (e instanceof InterpreterException) {
					throw (InterpreterException) e;
				}
			}
		}
	}
}
