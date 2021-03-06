package org.hibernate.sql.gen.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.AssertionFailure;
import org.hibernate.loader.plan.spi.Return;
import org.hibernate.sql.ast.SelectQuery;
import org.hibernate.sql.ast.from.EntityTableSpecificationGroup;
import org.hibernate.sql.ast.from.TableSpace;
import org.hibernate.sql.gen.Callback;
import org.hibernate.sql.gen.JdbcSelectPlan;
import org.hibernate.sql.gen.NotYetImplementedException;
import org.hibernate.sql.gen.ParameterBinder;
import org.hibernate.sql.gen.QueryOptionBinder;
import org.hibernate.sql.orm.QueryOptions;
import org.hibernate.sql.orm.internal.mapping.ImprovedEntityPersister;
import org.hibernate.sql.orm.internal.sqm.model.EntityTypeDescriptorImpl;
import org.hibernate.sqm.SemanticQueryWalker;
import org.hibernate.sqm.query.DeleteStatement;
import org.hibernate.sqm.query.QuerySpec;
import org.hibernate.sqm.query.SelectStatement;
import org.hibernate.sqm.query.Statement;
import org.hibernate.sqm.query.UpdateStatement;
import org.hibernate.sqm.query.expression.AttributeReferenceExpression;
import org.hibernate.sqm.query.expression.AvgFunction;
import org.hibernate.sqm.query.expression.BinaryArithmeticExpression;
import org.hibernate.sqm.query.expression.ConcatExpression;
import org.hibernate.sqm.query.expression.ConstantEnumExpression;
import org.hibernate.sqm.query.expression.ConstantFieldExpression;
import org.hibernate.sqm.query.expression.CountFunction;
import org.hibernate.sqm.query.expression.CountStarFunction;
import org.hibernate.sqm.query.expression.EntityTypeExpression;
import org.hibernate.sqm.query.expression.FromElementReferenceExpression;
import org.hibernate.sqm.query.expression.FunctionExpression;
import org.hibernate.sqm.query.expression.LiteralBigDecimalExpression;
import org.hibernate.sqm.query.expression.LiteralBigIntegerExpression;
import org.hibernate.sqm.query.expression.LiteralCharacterExpression;
import org.hibernate.sqm.query.expression.LiteralDoubleExpression;
import org.hibernate.sqm.query.expression.LiteralFalseExpression;
import org.hibernate.sqm.query.expression.LiteralFloatExpression;
import org.hibernate.sqm.query.expression.LiteralIntegerExpression;
import org.hibernate.sqm.query.expression.LiteralLongExpression;
import org.hibernate.sqm.query.expression.LiteralNullExpression;
import org.hibernate.sqm.query.expression.LiteralStringExpression;
import org.hibernate.sqm.query.expression.LiteralTrueExpression;
import org.hibernate.sqm.query.expression.MaxFunction;
import org.hibernate.sqm.query.expression.MinFunction;
import org.hibernate.sqm.query.expression.NamedParameterExpression;
import org.hibernate.sqm.query.expression.PositionalParameterExpression;
import org.hibernate.sqm.query.expression.SubQueryExpression;
import org.hibernate.sqm.query.expression.SumFunction;
import org.hibernate.sqm.query.expression.UnaryOperationExpression;
import org.hibernate.sqm.query.from.CrossJoinedFromElement;
import org.hibernate.sqm.query.from.FromClause;
import org.hibernate.sqm.query.from.FromElementSpace;
import org.hibernate.sqm.query.from.JoinedFromElement;
import org.hibernate.sqm.query.from.QualifiedAttributeJoinFromElement;
import org.hibernate.sqm.query.from.QualifiedEntityJoinFromElement;
import org.hibernate.sqm.query.from.RootEntityFromElement;
import org.hibernate.sqm.query.from.TreatedJoinedFromElement;
import org.hibernate.sqm.query.order.OrderByClause;
import org.hibernate.sqm.query.order.SortSpecification;
import org.hibernate.sqm.query.predicate.AndPredicate;
import org.hibernate.sqm.query.predicate.BetweenPredicate;
import org.hibernate.sqm.query.predicate.GroupedPredicate;
import org.hibernate.sqm.query.predicate.InSubQueryPredicate;
import org.hibernate.sqm.query.predicate.InTupleListPredicate;
import org.hibernate.sqm.query.predicate.IsEmptyPredicate;
import org.hibernate.sqm.query.predicate.IsNullPredicate;
import org.hibernate.sqm.query.predicate.LikePredicate;
import org.hibernate.sqm.query.predicate.MemberOfPredicate;
import org.hibernate.sqm.query.predicate.NegatedPredicate;
import org.hibernate.sqm.query.predicate.OrPredicate;
import org.hibernate.sqm.query.predicate.RelationalPredicate;
import org.hibernate.sqm.query.predicate.WhereClause;
import org.hibernate.sqm.query.select.DynamicInstantiation;
import org.hibernate.sqm.query.select.SelectClause;
import org.hibernate.sqm.query.select.Selection;
import org.hibernate.sqm.query.set.Assignment;
import org.hibernate.sqm.query.set.SetClause;

/**
 * @author Steve Ebersole
 * @author John O'Hara
 */
public class SelectStatementInterpreter implements SemanticQueryWalker {

	public static JdbcSelectPlan interpret(SelectStatement statement, QueryOptions queryOptions, Callback callback) {
		final SelectStatementInterpreter walker = new SelectStatementInterpreter( queryOptions, callback );
		walker.visitSelectStatement( statement );

		return null;
//		return new JdbcSelectPlanImpl(
//				walker.getSql(),
//				walker.getParameterBinders(),
//				walker.getOptionBinders(),
//				walker.getReturnDescriptors()
//		);
	}

	private final QueryOptions queryOptions;
	private final Callback callback;

	private final FromClauseIndex fromClauseIndex = new FromClauseIndex();

	private SelectQuery sqlAst;

	private final List<Return> returnDescriptors = new ArrayList<Return>();
	private List<QueryOptionBinder> optionBinders;
	private List<ParameterBinder> parameterBinders;

	private final SqlAliasBaseManager sqlAliasBaseManager = new SqlAliasBaseManager();

	private SelectStatementInterpreter(QueryOptions queryOptions, Callback callback) {
		this.queryOptions = queryOptions;
		this.callback = callback;
	}

	public SelectQuery getSelectQuery() {
		return sqlAst;
	}

	private List<ParameterBinder> getParameterBinders() {
		if ( parameterBinders == null ) {
			return Collections.emptyList();
		}
		else {
			return Collections.unmodifiableList( parameterBinders );
		}
	}

	private List<QueryOptionBinder> getOptionBinders() {
		if ( optionBinders == null ) {
			return Collections.emptyList();
		}
		else {
			return Collections.unmodifiableList( optionBinders );
		}
	}

	private List<Return> getReturnDescriptors() {
		return Collections.unmodifiableList( returnDescriptors );
	}



	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// walker


	@Override
	public Object visitUpdateStatement(UpdateStatement statement) {
		throw new AssertionFailure( "Not expecting UpdateStatement" );
	}

	@Override
	public Object visitDeleteStatement(DeleteStatement statement) {
		throw new AssertionFailure( "Not expecting DeleteStatement" );
	}

	@Override
	public SelectQuery visitSelectStatement(SelectStatement statement) {
		if ( sqlAst != null ) {
			throw new AssertionFailure( "SelectQuery already visited" );
		}

		sqlAst = new SelectQuery( visitQuerySpec( statement.getQuerySpec() ) );

		for ( SortSpecification sortSpecification : statement.getOrderByClause().getSortSpecifications() ) {
			sqlAst.addSortSpecification( visitSortSpecification( sortSpecification ) );
		}

		return sqlAst;
	}

	@Override
	public OrderByClause visitOrderByClause(OrderByClause orderByClause) {
		throw new AssertionFailure( "Unexpected visitor call" );
	}

	@Override
	public org.hibernate.sql.ast.sort.SortSpecification visitSortSpecification(SortSpecification sortSpecification) {
		return new org.hibernate.sql.ast.sort.SortSpecification(
				(org.hibernate.sql.ast.expression.Expression) sortSpecification.getSortExpression().accept( this ),
				sortSpecification.getCollation(),
				sortSpecification.getSortOrder()
		);
	}

	@Override
	public org.hibernate.sql.ast.QuerySpec visitQuerySpec(QuerySpec querySpec) {
		final org.hibernate.sql.ast.QuerySpec _querySpec = new org.hibernate.sql.ast.QuerySpec();

		fromClauseIndex.pushFromClause( _querySpec.getFromClause() );

		try {
			// we want to visit the from-clause first
			visitFromClause( querySpec.getFromClause() );

			visitSelectClause( querySpec.getSelectClause() );
			visitWhereClause( querySpec.getWhereClause() );

			return _querySpec;
		}
		finally {
			assert fromClauseIndex.popFromClause() == _querySpec.getFromClause();
		}
	}

	@Override
	public Void visitFromClause(FromClause fromClause) {
		for ( FromElementSpace fromElementSpace : fromClause.getFromElementSpaces() ) {
			visitFromElementSpace( fromElementSpace );
		}

		return null;
	}

	private TableSpace tableSpace;

	@Override
	public TableSpace visitFromElementSpace(FromElementSpace fromElementSpace) {
		tableSpace = fromClauseIndex.currentFromClause().makeTableSpace();
		try {
			visitRootEntityFromElement( fromElementSpace.getRoot() );
			for ( JoinedFromElement joinedFromElement : fromElementSpace.getJoins() ) {
				joinedFromElement.accept( this );
			}
			return tableSpace;
		}
		finally {
			tableSpace = null;
		}
	}

	@Override
	public Void visitRootEntityFromElement(RootEntityFromElement rootEntityFromElement) {
		final EntityTypeDescriptorImpl entityTypeDescriptor = (EntityTypeDescriptorImpl) rootEntityFromElement.getTypeDescriptor();
		final ImprovedEntityPersister entityPersister = entityTypeDescriptor.getPersister();

		final EntityTableSpecificationGroup group = entityPersister.getEntityTableSpecificationGroup(
				rootEntityFromElement,
				tableSpace,
				sqlAliasBaseManager,
				fromClauseIndex
		);

		tableSpace.setRootTableSpecificationGroup( group );

		return null;
	}

	@Override
	public Object visitCrossJoinedFromElement(CrossJoinedFromElement joinedFromElement) {
		throw new NotYetImplementedException();
	}

	@Override
	public Object visitTreatedJoinFromElement(TreatedJoinedFromElement joinedFromElement) {
		throw new NotYetImplementedException();
	}

	@Override
	public Object visitQualifiedEntityJoinFromElement(QualifiedEntityJoinFromElement joinedFromElement) {
		throw new NotYetImplementedException();
	}

	@Override
	public Object visitQualifiedAttributeJoinFromElement(QualifiedAttributeJoinFromElement joinedFromElement) {
		throw new NotYetImplementedException();
	}





	@Override
	public Object visitStatement(Statement statement) {
		return null;
	}

	@Override
	public Object visitSetClause(SetClause setClause) {
		return null;
	}

	@Override
	public Object visitAssignment(Assignment assignment) {
		return null;
	}

	@Override
	public Object visitSelectClause(SelectClause selectClause) {
		return null;
	}

	@Override
	public Object visitSelection(Selection selection) {
		return null;
	}

	@Override
	public Object visitDynamicInstantiation(DynamicInstantiation dynamicInstantiation) {
		return null;
	}

	@Override
	public Object visitWhereClause(WhereClause whereClause) {
		return null;
	}

	@Override
	public Object visitGroupedPredicate(GroupedPredicate predicate) {
		return null;
	}

	@Override
	public Object visitAndPredicate(AndPredicate predicate) {
		return null;
	}

	@Override
	public Object visitOrPredicate(OrPredicate predicate) {
		return null;
	}

	@Override
	public Object visitRelationalPredicate(RelationalPredicate predicate) {
		return null;
	}

	@Override
	public Object visitIsEmptyPredicate(IsEmptyPredicate predicate) {
		return null;
	}

	@Override
	public Object visitIsNullPredicate(IsNullPredicate predicate) {
		return null;
	}

	@Override
	public Object visitBetweenPredicate(BetweenPredicate predicate) {
		return null;
	}

	@Override
	public Object visitLikePredicate(LikePredicate predicate) {
		return null;
	}

	@Override
	public Object visitMemberOfPredicate(MemberOfPredicate predicate) {
		return null;
	}

	@Override
	public Object visitNegatedPredicate(NegatedPredicate predicate) {
		return null;
	}

	@Override
	public Object visitInTupleListPredicate(InTupleListPredicate predicate) {
		return null;
	}

	@Override
	public Object visitInSubQueryPredicate(InSubQueryPredicate predicate) {
		return null;
	}

	@Override
	public Object visitPositionalParameterExpression(PositionalParameterExpression expression) {
		return null;
	}

	@Override
	public Object visitNamedParameterExpression(NamedParameterExpression expression) {
		return null;
	}

	@Override
	public Object visitEntityTypeExpression(EntityTypeExpression expression) {
		return null;
	}

	@Override
	public Object visitUnaryOperationExpression(UnaryOperationExpression expression) {
		return null;
	}

	@Override
	public Object visitAttributeReferenceExpression(AttributeReferenceExpression expression) {
		return null;
	}

	@Override
	public Object visitFromElementReferenceExpression(FromElementReferenceExpression expression) {
		return null;
	}

	@Override
	public Object visitFunctionExpression(FunctionExpression expression) {
		return null;
	}

	@Override
	public Object visitAvgFunction(AvgFunction expression) {
		return null;
	}

	@Override
	public Object visitCountStarFunction(CountStarFunction expression) {
		return null;
	}

	@Override
	public Object visitCountFunction(CountFunction expression) {
		return null;
	}

	@Override
	public Object visitMaxFunction(MaxFunction expression) {
		return null;
	}

	@Override
	public Object visitMinFunction(MinFunction expression) {
		return null;
	}

	@Override
	public Object visitSumFunction(SumFunction expression) {
		return null;
	}

	@Override
	public Object visitLiteralStringExpression(LiteralStringExpression expression) {
		return null;
	}

	@Override
	public Object visitLiteralCharacterExpression(LiteralCharacterExpression expression) {
		return null;
	}

	@Override
	public Object visitLiteralDoubleExpression(LiteralDoubleExpression expression) {
		return null;
	}

	@Override
	public Object visitLiteralIntegerExpression(LiteralIntegerExpression expression) {
		return null;
	}

	@Override
	public Object visitLiteralBigIntegerExpression(LiteralBigIntegerExpression expression) {
		return null;
	}

	@Override
	public Object visitLiteralBigDecimalExpression(LiteralBigDecimalExpression expression) {
		return null;
	}

	@Override
	public Object visitLiteralFloatExpression(LiteralFloatExpression expression) {
		return null;
	}

	@Override
	public Object visitLiteralLongExpression(LiteralLongExpression expression) {
		return null;
	}

	@Override
	public Object visitLiteralTrueExpression(LiteralTrueExpression expression) {
		return null;
	}

	@Override
	public Object visitLiteralFalseExpression(LiteralFalseExpression expression) {
		return null;
	}

	@Override
	public Object visitLiteralNullExpression(LiteralNullExpression expression) {
		return null;
	}

	@Override
	public Object visitConcatExpression(ConcatExpression expression) {
		return null;
	}

	@Override
	public Object visitConstantEnumExpression(ConstantEnumExpression expression) {
		return null;
	}

	@Override
	public Object visitConstantFieldExpression(ConstantFieldExpression expression) {
		return null;
	}

	@Override
	public Object visitBinaryArithmeticExpression(BinaryArithmeticExpression expression) {
		return null;
	}

	@Override
	public Object visitSubQueryExpression(SubQueryExpression expression) {
		return null;
	}
}
