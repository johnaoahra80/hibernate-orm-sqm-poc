/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.hql.parser;

import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.hql.jpa.JpaBootstrap;
import org.hibernate.hql.parser.process.builder.CriteriaQueryParseTreeBuilder;
import org.hibernate.hql.parser.semantic.QuerySpec;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.jpa.AvailableSettings;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.junit.Test;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.Persistence;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.spi.PersistenceUnitTransactionType;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

/**
 * @author Steve Ebersole
 */
public class CriteriaQueryBuilderTest {

	@Test
	public void simpleCriteriaQuerTest() {


		Map settings = new HashMap();

		HibernatePersistenceProvider persistenceProvider = new HibernatePersistenceProvider();
		persistenceProvider.createContainerEntityManagerFactory(
				new JpaBootstrap.PersistenceUnitInfoImpl( "my-test" ) {
					@Override
					public URL getPersistenceUnitRootUrl() {
						// just get any known url...
						return HibernatePersistenceProvider.class.getResource( "/test/jpa/persistence_1_0.xsd" );
					}
				},
				settings
		);


		final String PERSISTENCE_UNIT_NAME = "Books";
		EntityManagerFactory factory = Persistence.createEntityManagerFactory( PERSISTENCE_UNIT_NAME );

		EntityManager em = factory.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Book> q = cb.createQuery( Book.class );
		Root<Book> b = q.from( Book.class );
		q.select( b ).orderBy( cb.desc( b.get( "id" ) ) );


		QuerySpec criteriaQuerySpec = null;

		try {
			criteriaQuerySpec = CriteriaQueryParseTreeBuilder.INSTANCE.parse( q);
		} catch (ParseException e) {
			e.printStackTrace();
		}


		assertNotNull( criteriaQuerySpec );

	}

	protected Dialect getDialect() {
		return Dialect.getDialect();
//		return dialect;
	}

	@Entity
	private class Book {
		@Id
		private int id;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
	}

	private PersistenceUnitDescriptor buildPersistenceUnitDescriptor() {
		return new TestingPersistenceUnitDescriptorImpl( getClass().getSimpleName() );
	}

	public static class TestingPersistenceUnitDescriptorImpl implements PersistenceUnitDescriptor {
		private final String name;

		public TestingPersistenceUnitDescriptorImpl(String name) {
			this.name = name;
		}

		@Override
		public URL getPersistenceUnitRootUrl() {
			return null;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getProviderClassName() {
			return HibernatePersistenceProvider.class.getName();
		}

		@Override
		public boolean isUseQuotedIdentifiers() {
			return false;
		}

		@Override
		public boolean isExcludeUnlistedClasses() {
			return false;
		}

		@Override
		public PersistenceUnitTransactionType getTransactionType() {
			return null;
		}

		@Override
		public ValidationMode getValidationMode() {
			return null;
		}

		@Override
		public SharedCacheMode getSharedCacheMode() {
			return null;
		}

		@Override
		public List<String> getManagedClassNames() {
			return null;
		}

		@Override
		public List<String> getMappingFileNames() {
			return null;
		}

		@Override
		public List<URL> getJarFileUrls() {
			return null;
		}

		@Override
		public Object getNonJtaDataSource() {
			return null;
		}

		@Override
		public Object getJtaDataSource() {
			return null;
		}

		@Override
		public Properties getProperties() {
			return null;
		}

		@Override
		public ClassLoader getClassLoader() {
			return null;
		}

		@Override
		public ClassLoader getTempClassLoader() {
			return null;
		}

		@Override
		public void pushClassTransformer(Collection<String> entityClassNames) {
		}
	}

	@SuppressWarnings("unchecked")
	protected Map buildSettings() {
		Map settings = getConfig();
		addMappings( settings );

		settings.put( org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO, "create-drop" );
		settings.put( org.hibernate.cfg.AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
		settings.put( org.hibernate.cfg.AvailableSettings.DIALECT, getDialect().getClass().getName() );
		return settings;
	}

	protected void addMappings(Map settings) {
		String[] mappings = getMappings();
		if ( mappings != null ) {
			settings.put( AvailableSettings.HBXML_FILES, StringHelper.join( ",", mappings ) );
		}
	}

	protected static final String[] NO_MAPPINGS = new String[0];

	protected String[] getMappings() {
		return NO_MAPPINGS;
	}

	protected Map getConfig() {
		Map<Object, Object> config = Environment.getProperties();
		ArrayList<Class> classes = new ArrayList<Class>();

		classes.addAll( Arrays.asList( new Class[0] ) );
		config.put( AvailableSettings.LOADED_CLASSES, classes );
		for (Map.Entry<Class, String> entry : new HashMap<Class, String>().entrySet()) {
			config.put( AvailableSettings.CLASS_CACHE_PREFIX + "." + entry.getKey().getName(), entry.getValue() );
		}
		for (Map.Entry<String, String> entry : new HashMap<String, String>().entrySet()) {
			config.put( AvailableSettings.COLLECTION_CACHE_PREFIX + "." + entry.getKey(), entry.getValue() );
		}

		return config;
	}

}
