[Cudl](https://github.com/multimediabs/cudl) is a voice xml tester under development.

[Cudl](https://github.com/multimediabs/cudl) is named after the nicer combination resulted 
from this command during the first github import...

```sh
find src/ -name '*.java' -exec cat \{\} \; | sed -e 's:\(.\):\1\n:g'  |sort | uniq  -c | sort -n | less
```

[Cudl](https://github.com/multimediabs/cudl) is a VoiceXML testing tool. 

## Quick Start
1. git clone https://github.com/multimediabs/cudl
2. cd cudl/VoiceInterpreter
3. ant clean jar
4. Put the cudl[VERSION].jar in your java project and you just follow the example bellow.

## Example
This is a sample "hello world" in VoiceXML.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<vxml xmlns="http://www.w3.org/2001/vxml"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.w3.org/2001/vxml
   http://www.w3.org/TR/voicexml20/vxml.xsd"
   version="2.0">
  <form>
    <block>Hello Word !</block>

  </form>
</vxml>
```
The code bellow test the prompt "Hello world !" is heart when the above the file is exécuted.
```java
public void testDireBonjour() throws IOException, SAXException, ParserConfigurationException {
	List<Prompt> exceptedPrompts = new ArrayList<Prompt>();
	Prompt prompt = new Prompt();
	prompt.tts = "Hello world !";
	exceptedPrompts.add(prompt);

	interpreter = new Interpreter("file:hello.vxml");
	interpreter.start();

	assertEquals(exceptedPrompts, interpreter.getPrompts());
}

```
