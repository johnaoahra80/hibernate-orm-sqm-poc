package org.hibernate.query.parser.hql;

import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.jpa.AvailableSettings;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.query.parser.jpa.JpaBootstrap;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by johara on 19/08/15.
 */
public class CriteriaQueryBuilderAbstractTest {
	protected static final String[] NO_MAPPINGS = new String[0];

	protected HibernatePersistenceProvider persistenceProvider;
	protected Map settings;

	protected Dialect getDialect() {
		return Dialect.getDialect();
//		return dialect;
	}


	public CriteriaQueryBuilderAbstractTest() {

		settings = new HashMap();

		persistenceProvider = new HibernatePersistenceProvider();
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
