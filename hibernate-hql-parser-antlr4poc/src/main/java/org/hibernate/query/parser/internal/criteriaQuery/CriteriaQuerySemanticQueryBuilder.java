/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.query.parser.internal.criteriaQuery;

import org.hibernate.query.parser.internal.ParsingContext;
import org.hibernate.sqm.query.QuerySpec;
import org.hibernate.sqm.query.SelectStatement;
import org.hibernate.sqm.query.expression.Expression;
import org.hibernate.sqm.query.from.FromClause;
import org.hibernate.sqm.query.predicate.WhereClause;
import org.hibernate.sqm.query.select.SelectClause;
import org.hibernate.sqm.query.select.SelectList;
import org.hibernate.sqm.query.select.SelectListItem;
import org.hibernate.sqm.query.select.Selection;
import org.jboss.logging.Logger;

import javax.persistence.criteria.CriteriaQuery;

/**
 * @author Steve Ebersole
 */
//public class CriteriaQuerySemanticQueryBuilder extends AbstractHqlParseTreeVisitor {
public class CriteriaQuerySemanticQueryBuilder {
	private static final Logger log = Logger.getLogger( CriteriaQuerySemanticQueryBuilder.class );

//	private final FromClauseProcessor fromClauseProcessor;

	private FromClause currentFromClause;

	private final CriteriaQuery query;

	public CriteriaQuerySemanticQueryBuilder(ParsingContext parsingContext, CriteriaQuery query) {
		this.query = query;
/*
		super( parsingContext, fromClauseProcessor.getFromElementBuilder(), fromClauseProcessor.getFromClauseIndex() );
		this.fromClauseProcessor = fromClauseProcessor;

		if ( fromClauseProcessor.getStatementType() == Statement.Type.INSERT ) {
			throw new NotYetImplementedException();
			// set currentFromClause
		}
		else if ( fromClauseProcessor.getStatementType() == Statement.Type.UPDATE ) {
			throw new NotYetImplementedException();
			// set currentFromClause
		}
		else if ( fromClauseProcessor.getStatementType() == Statement.Type.DELETE ) {
			throw new NotYetImplementedException();
			// set currentFromClause
		}
*/
	}

//	@Override
	public FromClause getCurrentFromClause() {
		return currentFromClause;
	}

	public SelectStatement visitSelectStatement() {
		// for the moment, only selectStatements are valid...
//		return visitSelectStatement( ctx.selectStatement() );

//		throw new NotYetImplementedException();

		final SelectStatement selectStatement = new SelectStatement();
		selectStatement.applyQuerySpec( visitQuerySpec( ) );
/*
//TODO: implement order by
		if ( ctx.orderByClause() != null ) {
			selectStatement.applyOrderByClause( visitOrderByClause( ctx.orderByClause() ) );
		}
*/

		return selectStatement;

	}


	public QuerySpec visitQuerySpec() {
		final SelectClause selectClause;
		if ( query.getSelection() != null ) {
			selectClause = visitSelectClause();
		}
		else {
			log.info( "Encountered implicit select clause which is a deprecated feature" );
//			log.info( "Encountered implicit select clause which is a deprecated feature : " + ctx.toString() );
//			selectClause = buildInferredSelectClause( getCurrentFromClause() );
			selectClause = null;

		}

		final WhereClause whereClause = null;
/*
		if ( ctx.whereClause() != null ) {
			whereClause = visitWhereClause( ctx.whereClause() );
		}
		else {
			whereClause = null;
		}
*/
		return new QuerySpec( getCurrentFromClause(), selectClause, whereClause );
	}


	private SelectClause visitSelectClause(){
		return new SelectClause( getSelection( query ), query.isDistinct() );
	}

	private Selection getSelection(CriteriaQuery query) {

		SelectList selectList = new SelectList();

		if(query.getSelection().isCompoundSelection()) {
			for (Object selectionObj : query.getSelection().getCompoundSelectionItems()) {
				if ( selectionObj instanceof Selection ) {
					Selection selection = (Selection) selectionObj;


					SelectListItem selectListItem = new SelectListItem( null );
					selectList.addSelectListItem( selectListItem );
				}

			}
		}
		else {
			Expression expression = ExpressionFactory.getExpression( query.getSelection() );

			SelectListItem selectListItem = new SelectListItem( expression, query.getSelection().getAlias());

			selectList.addSelectListItem( selectListItem );
		}

		return selectList;
	}

/*
	@Override
	public QuerySpec visitQuerySpec(HqlParser.QuerySpecContext ctx) {
		final FromClause fromClause = fromClauseProcessor.findFromClauseForQuerySpec( ctx );
		if ( fromClause == null ) {
			throw new ParsingException( "Could not resolve FromClause by QuerySpecContext" );
		}
		FromClause originalCurrentFromClause = currentFromClause;
		currentFromClause = fromClause;
		attributePathResolverStack.push(
				new BasicAttributePathResolverImpl(
						fromClauseProcessor.getFromElementBuilder(),
						fromClauseProcessor.getFromClauseIndex(),
						getParsingContext(),
						currentFromClause
				)
		);
		try {
			return super.visitQuerySpec( ctx );
		}
		finally {
			attributePathResolverStack.pop();
			currentFromClause = originalCurrentFromClause;
		}
	}

	@Override
	public AttributePathPart visitIndexedPath(HqlParser.IndexedPathContext ctx) {
		return super.visitIndexedPath( ctx );
	}
*/
}
