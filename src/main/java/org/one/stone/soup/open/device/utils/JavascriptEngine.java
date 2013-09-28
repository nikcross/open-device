package org.one.stone.soup.open.device.utils;

import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;

public class JavascriptEngine {
	private static final class JsContext extends Context {
		long startTime;
		long totalInstructions = 0;
		boolean stop = false;

		JsContext(ContextFactory contextFactory) {
			super(contextFactory);
		}

		public void stop() {
			stop = true;
		}
	}
	
	class JsContextFactory extends ContextFactory {
		JsContext createContext() {
			JsContext context = new JsContext(this);
			context.setOptimizationLevel(-1);
			context.setInstructionObserverThreshold(1000);
			
			return context;
		}
	}
		
	public Object runScript(String scriptName,
			String script, Map<String, Object> parameters)
			throws Throwable {
		JsContext context = new JsContextFactory().createContext();
		Context.enter();
		Scriptable scope = prepareScope( context, parameters, scriptName);

		context.startTime = System.currentTimeMillis();
		try {
			Object result = context.evaluateString(scope,
					convertScriptToFunction(script), scriptName, 0, null);
			return result;
		} catch (JavaScriptException je) {
			throw new JavascriptException(je.details() + "\n\n"
					+ je.getScriptStackTrace()+ "\n\n"
					+ "in "+scriptName
					, je.lineNumber(),
					je.columnNumber());		
		} catch (EcmaError error) {
			throw new JavascriptException(error.details() + "\n\n"
					+ error.getScriptStackTrace()+ "\n\n"
					+ "in "+scriptName
					, error.lineNumber(),
					error.columnNumber());
		} catch(WrappedException wex) {
			throw wex.getCause();
		} catch (EvaluatorException evaluatorException) {
			
			throw new JavascriptException(evaluatorException.details() + "\n\n"
					+ evaluatorException.getScriptStackTrace(),
					evaluatorException.lineNumber(),
					evaluatorException.columnNumber());
		}
	}
	private Scriptable prepareScope(JsContext context,
			Map<String, Object> parameters, String scriptName)
			throws JavascriptException {
		Scriptable scope = context.initStandardObjects();

		if (parameters == null) {
			parameters = new HashMap<String, Object>();
		}

		for (String key : parameters.keySet()) {
			Object value = parameters.get(key);
			mountObject(scope, key, value);
		}
		return scope;
	}
	private void mountObject(Scriptable scope,String key,Object value) {
		if(value!=null) {
			Object wrappedOut = Context.toObject(value, scope);
			ScriptableObject.putProperty(scope, key, wrappedOut);
		}
		
	}
	private String convertScriptToFunction(String script) {
		return "function fn() {\n" + script + "\n}; fn();";
	}
}
