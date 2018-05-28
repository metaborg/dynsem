package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;

import com.oracle.truffle.api.CallTarget;

public interface IRuleRegistry {

	DynSemLanguage getLanguage();

	public void setLanguage(DynSemLanguage language);

	void registerRule(String arrowName, Class<?> dispatchClass, CallTarget[] rules);

	CallTarget[] lookupRules(String arrowName, Class<?> dispatchClass);


}