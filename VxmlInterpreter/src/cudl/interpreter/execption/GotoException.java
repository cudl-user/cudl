package cudl.interpreter.execption;

public class GotoException extends InterpreterException {
	public String next;

	public GotoException(String string) {
		this.next = string;
	}
}