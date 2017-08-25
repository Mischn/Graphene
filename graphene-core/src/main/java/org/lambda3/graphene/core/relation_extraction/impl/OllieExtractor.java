package org.lambda3.graphene.core.relation_extraction.impl;

/*-
 * ==========================License-Start=============================
 * OllieExtractor.java - Graphene Core - Lambda^3 - 2017
 * Graphene
 * %%
 * Copyright (C) 2017 Lambda^3
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * ==========================License-End===============================
 */


import edu.knowitall.ollie.Ollie;
import edu.knowitall.ollie.OllieExtraction;
import edu.knowitall.ollie.OllieExtractionInstance;
import edu.knowitall.tool.parse.MaltParser;
import edu.knowitall.tool.parse.graph.DependencyGraph;
import edu.stanford.nlp.trees.Tree;
import org.lambda3.graphene.core.relation_extraction.BinaryExtraction;
import org.lambda3.graphene.core.relation_extraction.RelationExtractor;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class OllieExtractor extends RelationExtractor {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Ollie ollie;
	private MaltParser maltParser;
	private static final String MALT_PARSER_FILENAME = "engmalt.linear-1.7.mco";

	public OllieExtractor() throws MalformedURLException {
		super();

		// initialize MaltParser
		maltParser = new MaltParser(new File(MALT_PARSER_FILENAME));

		// initialize Ollie
		ollie = new Ollie();
	}

	private Iterable<OllieExtractionInstance> extract(String sentence) {

		// parse the sentence
		DependencyGraph graph = maltParser.dependencyGraph(sentence);

		// run Ollie over the sentence and convert to a Java collection
		Iterable<OllieExtractionInstance> extrs = scala.collection.JavaConversions.asJavaIterable(ollie.extract(graph));
		return extrs;
	}

	public List<BinaryExtraction> doExtraction(String text) {
		List<BinaryExtraction> extractions = new ArrayList<>();

		Iterable<OllieExtractionInstance> extrs = extract(text);

		// create the extraction
		for (OllieExtractionInstance inst : extrs) {
			OllieExtraction extr = inst.extr();
			extractions.add(new BinaryExtraction(
				extr.openparseConfidence(),
				extr.rel().text(),
				extr.arg1().text(),
				extr.arg2().text()
			));
		}

		return extractions;
	}

	@Override
	public List<BinaryExtraction> doExtraction(Tree parseTree) {
		return doExtraction(WordsUtils.wordsToString(ParseTreeExtractionUtils.getContainingWords(parseTree)));
	}
}
