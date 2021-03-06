/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.sql.ast.from;

import java.util.List;

/**
 * Group together related TableSpecifications (generally related by EntityPersister or CollectionPersister),
 *
 * @author Steve Ebersole
 */
public interface TableSpecificationGroup {
	TableSpace getTableSpace();
	String getAliasBase();
	TableSpecification getRootTableSpecification();
	List<TableSpecificationJoin> getTableSpecificationJoins();
}
