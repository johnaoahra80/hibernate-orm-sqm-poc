/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.hql.parser.process;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Selection;

/**
 * @author Steve Ebersole
 */
public class CriteriaQuerySemanticQueryBuilder {

	private Selection selection;

	public CriteriaQuerySemanticQueryBuilder(CriteriaQuery query) {


		if ( query.getSelection() != null ){
			this.selection = query.getSelection();
		}

	}


	public Selection getSelection() {
		return selection;
	}



}
