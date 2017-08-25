package org.lambda3.graphene.core.relation_extraction.impl;

/*-
 * ==========================License-Start=============================
 * StanfordOpenIEExtractor.java - Graphene Core - Lambda^3 - 2017
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


import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.lambda3.graphene.core.relation_extraction.BinaryExtraction;
import org.lambda3.graphene.core.relation_extraction.RelationExtractor;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 *
 */
public class StanfordOpenIEExtractor extends RelationExtractor {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final StanfordCoreNLP PIPELINE;
	static {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie");
		PIPELINE = new StanfordCoreNLP(props);
	}

	public List<BinaryExtraction> doExtraction(String text) {
		List<BinaryExtraction> extractions = new ArrayList<>();

		// Annotate document.
		Annotation doc = new Annotation(text);
		PIPELINE.annotate(doc);

		// Loop over sentences in the document
		for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
			// Get the OpenIE triples for the sentence
			Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
			// Print the triples
			for (RelationTriple triple : triples) {
				extractions.add(new BinaryExtraction(
					triple.confidence,
					triple.relationGloss(),
					triple.subjectGloss(),
					triple.objectGloss()
				));
			}
		}

		return extractions;
	}


	@Override
	public List<BinaryExtraction> doExtraction(Tree parseTree) {
		return doExtraction(WordsUtils.wordsToString(ParseTreeExtractionUtils.getContainingWords(parseTree)));
	}
}
