/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.sql.gen.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.hibernate.sql.ast.from.FromClause;
import org.hibernate.sql.ast.from.TableSpecificationGroup;
import org.hibernate.sqm.query.from.FromElement;

import org.jboss.logging.Logger;

/**
 * An index of various FROM CLAUSE resolutions.
 *
 * @author Steve Ebersole
 */
public class FromClauseIndex {
	private static final Logger log = Logger.getLogger( FromClauseIndex.class );

	private final Stack<FromClauseStackNode> fromClauseStackNodes = new Stack<FromClauseStackNode>();

	private final Map<FromElement, TableSpecificationGroup> fromElementTableSpecificationGroupXref =
			new HashMap<FromElement, TableSpecificationGroup>();

	public void pushFromClause(FromClause fromClause) {
		final FromClauseStackNode parent = fromClauseStackNodes.peek();
		FromClauseStackNode node = new FromClauseStackNode( parent, fromClause );
		fromClauseStackNodes.push( node );
	}

	public FromClause popFromClause() {
		final FromClauseStackNode node = fromClauseStackNodes.pop();
		if ( node == null ) {
			return null;
		}
		else {
			return node.getCurrentFromClause();
		}
	}

	public FromClause currentFromClause() {
		final FromClauseStackNode currentNode = fromClauseStackNodes.peek();
		if ( currentNode == null ) {
			return null;
		}
		else {
			return currentNode.getCurrentFromClause();
		}
	}

	public void crossReference(FromElement fromElement, TableSpecificationGroup tableSpecificationGroup) {
		TableSpecificationGroup old = fromElementTableSpecificationGroupXref.put( fromElement, tableSpecificationGroup );
		if ( old != null ) {
			log.debugf(
					"FromElement [%s] was already cross-referenced to TableSpecificationGroup - old : [%s]; new : [%s]",
					fromElement,
					old,
					tableSpecificationGroup
			);
		}
	}

	public static class FromClauseStackNode {
		private final FromClauseStackNode parentNode;
		private final FromClause currentFromClause;

		public FromClauseStackNode(FromClause currentFromClause) {
			this( null, currentFromClause );
		}

		public FromClauseStackNode(FromClauseStackNode parentNode, FromClause currentFromClause) {
			this.parentNode = parentNode;
			this.currentFromClause = currentFromClause;
		}

		public FromClauseStackNode getParentNode() {
			return parentNode;
		}

		public FromClause getCurrentFromClause() {
			return currentFromClause;
		}
	}

}
