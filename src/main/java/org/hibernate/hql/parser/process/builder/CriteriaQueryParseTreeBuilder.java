/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.hql.parser.process.builder;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.hibernate.hql.parser.antlr.HqlLexer;
import org.hibernate.hql.parser.antlr.HqlParser;
import org.hibernate.hql.parser.criteria.ExpressionFactory;
import org.hibernate.hql.parser.process.HqlParseTreePrinter;
import org.hibernate.hql.parser.process.ParsingContext;
import org.hibernate.hql.parser.semantic.QuerySpec;
import org.hibernate.hql.parser.semantic.expression.Expression;
import org.hibernate.hql.parser.semantic.from.FromClause;
import org.hibernate.hql.parser.semantic.groupBy.GroupByClause;
import org.hibernate.hql.parser.semantic.predicate.HavingClause;
import org.hibernate.hql.parser.semantic.predicate.WhereClause;
import org.hibernate.hql.parser.semantic.select.SelectClause;
import org.hibernate.hql.parser.semantic.select.SelectList;
import org.hibernate.hql.parser.semantic.select.SelectListItem;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.text.ParseException;
import java.util.Set;

/**
 * @author Steve Ebersole
 */
public class CriteriaQueryParseTreeBuilder  {
	/**
	 * Singleton access
	 */
	public static final CriteriaQueryParseTreeBuilder INSTANCE = new CriteriaQueryParseTreeBuilder();

	private boolean debugEnabled = true;

	public HqlParser parseHql(String hql) {
		// Build the lexer
		HqlLexer hqlLexer = new HqlLexer( new ANTLRInputStream( hql ) );

		// Build the parser...
		final HqlParser parser = new HqlParser( new CommonTokenStream( hqlLexer ) );

		// this part would be protected by logging most likely.  Print the parse tree structure
		if ( debugEnabled ) {
			ParseTreeWalker.DEFAULT.walk( new HqlParseTreePrinter( parser ), parser.statement() );
			hqlLexer.reset();
			parser.reset();
		}

		return parser;
	}

	public QuerySpec parse(Object query) throws ParseException {

		if ( !(query instanceof CriteriaQuery) ) {
//			throw new ParseException( "Incorrect query type: " + query.getClass().getName() );
			return null;
		}

		CriteriaQuery criteriaQuery = (CriteriaQuery) query;

		ParsingContext parsingContext = null;

		Selection selection = criteriaQuery.getSelection();

		Set<Root> roots = criteriaQuery.getRoots();

//		criteriaQuery.getRestriction()

//		for( Root root: roots){
//			root.
//		}

//		selection.

//		final SelectStatement selectStatement = new SelectStatement( parsingContext );
//		selectStatement.applyQuerySpec( visitQuerySpec( ctx.querySpec() ) );
//		if ( ctx.orderByClause() != null ) {
//			selectStatement.applyOrderByClause( visitOrderByClause( ctx.orderByClause() ) );
//		}
//
//		return selectStatement;
//
//
		FromClause fromClause = new FromClause( parsingContext );


		SelectClause selectClause = new SelectClause( getSelection(criteriaQuery), criteriaQuery.isDistinct() );

		WhereClause whereClause = new WhereClause( null );
		GroupByClause groupByClause = new GroupByClause( null );
		HavingClause havingClause = new HavingClause( null );




		return new QuerySpec( null, fromClause,  selectClause, whereClause, groupByClause, havingClause);
	}

	private org.hibernate.hql.parser.semantic.select.Selection getSelection(CriteriaQuery criteriaQuery) {

		SelectList selectList = new SelectList();

		if(criteriaQuery.getSelection().isCompoundSelection()) {
			for (Object selectionObj : criteriaQuery.getSelection().getCompoundSelectionItems()) {
				if ( selectionObj instanceof Selection ) {
					Selection selection = (Selection) selectionObj;


					SelectListItem selectListItem = new SelectListItem( null );
					selectList.addSelectListItem( selectListItem );
				}

			}
		}
		else {
			Expression expression = ExpressionFactory.getExpression( criteriaQuery.getSelection() );

			SelectListItem selectListItem = new SelectListItem( expression, criteriaQuery.getSelection().getAlias());

			selectList.addSelectListItem( selectListItem );
		}

		return selectList;
	}

/*
	public Parser parse(Object query, final HibernateEntityManagerImplementor entityManager) throws ParseException {

		CriteriaCompiler criteriaCompiler = new CriteriaCompiler( entityManager );


		Query compiledQuery = criteriaCompiler.compile( (CompilableCriteria) query );


		QueryImpl hibernateQuery = (QueryImpl) ((CriteriaQueryTypeQueryAdapter) compiledQuery).getHibernateQuery();
		String jpql = hibernateQuery.getQueryString();

		//return jpql parser

		return null;
	}
*/
}
