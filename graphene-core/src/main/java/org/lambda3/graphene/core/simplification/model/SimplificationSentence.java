/*
 * ==========================License-Start=============================
 * graphene-core : SimplificationSentence
 *
 * Copyright © 2017 Lambda³
 *
 * GNU General Public License 3
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 * ==========================License-End==============================
 */

package org.lambda3.graphene.core.simplification.model;

import java.util.List;

public class SimplificationSentence {
	private String originalSentence;
	private List<CoreSentence> coreSentences;

	// for deserialization
	public SimplificationSentence() {
	}

	public SimplificationSentence(String originalSentence, List<CoreSentence> coreSentences) {
		this.originalSentence = originalSentence;
		this.coreSentences = coreSentences;
	}

	public String getOriginalSentence() {
		return originalSentence;
	}

	public List<CoreSentence> getCoreSentences() {
		return coreSentences;
	}
}