package org.lambda3.graphene.core.relation_extraction.impl;

/*-
 * ==========================License-Start=============================
 * ReverbExtractor.java - Graphene Core - Lambda^3 - 2017
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


import edu.stanford.nlp.trees.Tree;
import edu.washington.cs.knowitall.extractor.ReVerbExtractor;
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunction;
import edu.washington.cs.knowitall.extractor.conf.ReVerbOpenNlpConfFunction;
import edu.washington.cs.knowitall.nlp.ChunkedSentence;
import edu.washington.cs.knowitall.nlp.OpenNlpSentenceChunker;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;
import org.lambda3.graphene.core.relation_extraction.BinaryExtraction;
import org.lambda3.graphene.core.relation_extraction.RelationExtractor;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.words.WordsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ReverbExtractor extends RelationExtractor {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private OpenNlpSentenceChunker chunker;
	private ReVerbExtractor reverb;
	private ConfidenceFunction confFunc;

	public ReverbExtractor() {
		super();

		try {
			this.chunker = new OpenNlpSentenceChunker();
		} catch (IOException e) {
			throw new AssertionError("Could not initialize OpenNlpSentenceChunker");
		}
		this.reverb = new ReVerbExtractor();
		try {
			this.confFunc = new ReVerbOpenNlpConfFunction();
		} catch (IOException e) {
			throw new AssertionError("Could not initialize ReVerbOpenNlpConfFunction");
		}
	}

	public List<BinaryExtraction> doExtraction(String text) {
		List<BinaryExtraction> extractions = new ArrayList<>();

		ChunkedSentence sent = chunker.chunkSentence(text);

		// Prints out the (token, tag, chunk-tag) for the sentence
//		System.out.println(text);
//		for (int i = 0; i < sent.getLength(); i++) {
//			String token = sent.getToken(i);
//			String posTag = sent.getPosTag(i);
//			String chunkTag = sent.getChunkTag(i);
//			System.out.println(token + " " + posTag + " " + chunkTag);
//		}

		// get extractions from the sentence.
		for (ChunkedBinaryExtraction extr : reverb.extract(sent)) {
			double conf = confFunc.getConf(extr);

			BinaryExtraction e = new BinaryExtraction(
				conf,
				extr.getRelation().getText(),
				extr.getArgument1().getText(),
				extr.getArgument2().getText()
			);
			e.setConfidence(conf);

			extractions.add(e);
		}

		return extractions;
	}

	@Override
	public List<BinaryExtraction> doExtraction(Tree parseTree) {
		return doExtraction(WordsUtils.wordsToString(ParseTreeExtractionUtils.getContainingWords(parseTree)));
	}
}
