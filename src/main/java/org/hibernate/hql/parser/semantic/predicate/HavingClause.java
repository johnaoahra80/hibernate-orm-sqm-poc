/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.hql.parser.semantic.predicate;

import org.hibernate.hql.parser.process.ParsingContext;

/**
 * @author Steve Ebersole
 */
public class HavingClause {
	private final ParsingContext parsingContext;

	private Predicate predicate;

	public HavingClause(ParsingContext parsingContext) {
		this.parsingContext = parsingContext;
	}

	public HavingClause(ParsingContext parsingContext, Predicate predicate) {
		this.parsingContext = parsingContext;
		this.predicate = predicate;
	}

	public Predicate getPredicate() {
		return predicate;
	}

	public void setPredicate(Predicate predicate) {
		this.predicate = predicate;
	}
}
