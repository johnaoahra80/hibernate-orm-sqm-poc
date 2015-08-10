/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.hql.parser.process.builder;

import org.antlr.v4.runtime.Parser;
import org.hibernate.hql.parser.process.ParseTreeBuilder;
import org.hibernate.internal.QueryImpl;
import org.hibernate.jpa.criteria.compile.CompilableCriteria;
import org.hibernate.jpa.criteria.compile.CriteriaCompiler;
import org.hibernate.jpa.criteria.compile.CriteriaQueryTypeQueryAdapter;
import org.hibernate.jpa.spi.HibernateEntityManagerImplementor;

import javax.persistence.Query;
import java.text.ParseException;

/**
 * @author John O'Hara
 */
public class CriteriaQueryParseTreeBuilder implements ParseTreeBuilder {
	/**
	 * Singleton access
	 */
	public static final CriteriaQueryParseTreeBuilder INSTANCE = new CriteriaQueryParseTreeBuilder();

	@Override
	public Parser parse(Object query) throws ParseException {
		return this.parse( query, null );
	}

	public Parser parse(Object query, final HibernateEntityManagerImplementor entityManager) throws ParseException {

		CriteriaCompiler criteriaCompiler = new CriteriaCompiler( entityManager );


		Query compiledQuery = criteriaCompiler.compile( (CompilableCriteria) query );


		QueryImpl hibernateQuery = (QueryImpl) ((CriteriaQueryTypeQueryAdapter) compiledQuery).getHibernateQuery();
		String jpql = hibernateQuery.getQueryString();

		//return jpql parser
		return JpqlParseTreeBuilder.INSTANCE.parse( jpql );
	}
}
