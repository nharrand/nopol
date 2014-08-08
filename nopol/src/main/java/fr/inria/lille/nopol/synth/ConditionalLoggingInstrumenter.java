/*
 * Copyright (C) 2013 INRIA
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.inria.lille.nopol.synth;

import java.util.Collection;
import java.util.List;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import fr.inria.lille.commons.spoon.collectable.CollectableValueFinder;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.commons.utils.Singleton;

/**
 * 
 * Adds basic logging before each conditionals (if, loops). Use basic scope inference (the real one is hard due to the
 * complex semantics of "static" and "final" (w.r.t. init, anonymous classes, etc.)
 * 
 */
public final class ConditionalLoggingInstrumenter extends AbstractProcessor<CtStatement> {

	public ConditionalLoggingInstrumenter(RuntimeValues runtimeValues) {
		this.runtimeValues = runtimeValues;
		this.collectableFinder = Singleton.of(CollectableValueFinder.class);
	}
	
	@Override
	public void process(CtStatement element) {
		Collection<String> collectables = collectablesOf(element);
		List<CtStatement> statements = runtimeValues().asCollectionStatements(collectables, element.getParent());
		for (CtStatement statement : statements) {
			element.insertBefore(statement);
		}
	}
	
	private Collection<String> collectablesOf(CtStatement element) {
		Collection<String> collectables;
		if (CtIf.class.isInstance(element)) {
			collectables = collectableFinder().findFromIf((CtIf) element);
		} else {
			collectables = collectableFinder().findFromStatement(element);
		}
		return collectables;
	}
	
	private CollectableValueFinder collectableFinder() {
		return collectableFinder;
	}
	
	private RuntimeValues runtimeValues() {
		return runtimeValues;
	}
	
	private RuntimeValues runtimeValues;
	private CollectableValueFinder collectableFinder;
}
