/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.query.parser.hql;

import org.hibernate.query.parser.Book;
import org.hibernate.query.parser.SemanticQueryInterpreter;
import org.hibernate.sqm.query.SelectStatement;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import static org.junit.Assert.assertNotNull;

/**
 * @author John O'Hara
 */
public class CriteriaQueryBuilderTest extends CriteriaQueryBuilderAbstractTest {

	public CriteriaQueryBuilderTest() {
		super();
	}

	@Test
	public void simpleCriteriaQuerTest() {

		final String PERSISTENCE_UNIT_NAME = "Books";
		EntityManagerFactory factory = Persistence.createEntityManagerFactory( PERSISTENCE_UNIT_NAME );

		EntityManager em = factory.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Book> q = cb.createQuery( Book.class );
		Root<Book> b = q.from( Book.class );
		q.select( b ).orderBy( cb.desc( b.get( "id" ) ) );


		SelectStatement criteriaQuerySelect = null;

		criteriaQuerySelect = SemanticQueryInterpreter.interpret( q, null );

		assertNotNull( criteriaQuerySelect );

	}

/*
	@Test
	public void joinCriteriaQuerTest() {

		final String PERSISTENCE_UNIT_NAME = "Books";
		EntityManagerFactory factory = Persistence.createEntityManagerFactory( PERSISTENCE_UNIT_NAME );

		EntityManager em = factory.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<Book> q = cb.createQuery( Book.class );
		Root<Book> book = q.from( Book.class );

		Join<Book, Author> authorJoin = book.join( Book_.author );

		q.select( book ).orderBy( cb.desc( book.get( "id" ) ) );


		QuerySpec criteriaQuerySpec = null;

		try {
			criteriaQuerySpec = CriteriaQueryParseTreeBuilder.INSTANCE.parse( q );
		} catch (ParseException e) {
			e.printStackTrace();
		}


		assertNotNull( criteriaQuerySpec );

	}

*/

}
