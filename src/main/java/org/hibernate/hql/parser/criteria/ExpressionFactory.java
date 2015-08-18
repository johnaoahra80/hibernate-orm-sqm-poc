package org.hibernate.hql.parser.criteria;

import org.hibernate.hql.parser.model.AttributeDescriptor;
import org.hibernate.hql.parser.model.CollectionTypeDescriptor;
import org.hibernate.hql.parser.model.CompositeTypeDescriptor;
import org.hibernate.hql.parser.model.EntityTypeDescriptor;
import org.hibernate.hql.parser.model.StandardBasicTypeDescriptors;
import org.hibernate.hql.parser.model.TypeDescriptor;
import org.hibernate.hql.parser.semantic.expression.EntityTypeExpression;
import org.hibernate.hql.parser.semantic.expression.Expression;
import org.hibernate.jpa.criteria.path.RootImpl;

import javax.persistence.criteria.Selection;
import java.util.Collection;
import java.util.Map;

/**
 * Created by johara on 17/08/15.
 */
public class ExpressionFactory {
	public static Expression getExpression(final Selection selection) {

/*
		switch (selection.getJavaType().getName()){

			case "":
				return new LiteralStringExpression( selection.toString() );
			default:
				break;

		}
		if(selection instanceof RootImpl){
			RootImpl root = (RootImpl) selection;
			System.out.println(root.getEntityType());
		}
*/




//		return new LiteralStringExpression( selection.toString() );
		return new EntityTypeExpression( new EntityTypeDescriptorImpl( ((RootImpl) selection).getEntityType().getName()));
	}


	public static abstract class AbstractTypeDescriptorImpl implements TypeDescriptor {
		private final String typeName;

		public AbstractTypeDescriptorImpl(String typeName) {
			this.typeName = typeName;
		}

		@Override
		public String getTypeName() {
			return typeName;
		}

		@Override
		public AttributeDescriptor getAttributeDescriptor(String attributeName) {
			if ( attributeName.startsWith( "basic" ) ) {
				return buildBasicAttribute( attributeName );
			}
			else if ( attributeName.startsWith( "composite" ) ) {
				return buildCompositeAttribute( attributeName );
			}
			else if ( attributeName.startsWith( "entity" ) ) {
				return buildEntityAttribute( attributeName );
			}
			else if ( attributeName.startsWith( "collection" ) ) {
				return buildCollectionAttribute( attributeName );
			}
			else if ( attributeName.startsWith( "basicCollection" ) ) {
				return buildBasicCollectionAttribute( attributeName );
			}
			else if ( attributeName.startsWith( "map" ) ) {
				return buildMapAttribute( attributeName );
			}
			else if ( attributeName.startsWith( "basicMap" ) ) {
				return buildBasicMapAttribute( attributeName );
			}

			return null;
		}

		protected AttributeDescriptor buildBasicAttribute(String attributeName) {
			return new AttributeDescriptorImpl(
					this,
					attributeName,
					StandardBasicTypeDescriptors.INSTANCE.LONG
			);
		}

		protected AttributeDescriptor buildCompositeAttribute(String attributeName) {
			return new AttributeDescriptorImpl(
					this,
					attributeName,
					new CompositeTypeDescriptorImpl("attribute: " + attributeName)
			);
		}

		protected AttributeDescriptor buildEntityAttribute(String attributeName) {
			return new AttributeDescriptorImpl(
					this,
					attributeName,
					new EntityTypeDescriptorImpl( "attribute: " + attributeName)
			);
		}

		protected AttributeDescriptor buildCollectionAttribute(String attributeName) {
			return new AttributeDescriptorImpl(
					this,
					attributeName,
					new CollectionTypeDescriptorImpl(
							new EntityTypeDescriptorImpl( "collection-value:" + attributeName )
					)
			);
		}

		protected AttributeDescriptor buildBasicCollectionAttribute(String attributeName) {
			return new AttributeDescriptorImpl(
					this,
					attributeName,
					new CollectionTypeDescriptorImpl( StandardBasicTypeDescriptors.INSTANCE.LONG )
			);
		}

		protected AttributeDescriptor buildMapAttribute(String attributeName) {
			return new AttributeDescriptorImpl(
					this,
					attributeName,
					new CollectionTypeDescriptorImpl(
							StandardBasicTypeDescriptors.INSTANCE.LONG,
							new EntityTypeDescriptorImpl( "map-element:" + attributeName )
					)
			);
		}

		protected AttributeDescriptor buildBasicMapAttribute(String attributeName) {
			return new AttributeDescriptorImpl(
					this,
					attributeName,
					new CollectionTypeDescriptorImpl(
							StandardBasicTypeDescriptors.INSTANCE.LONG,
							StandardBasicTypeDescriptors.INSTANCE.LONG
					)
			);
		}
	}

	public static class EntityTypeDescriptorImpl
			extends AbstractTypeDescriptorImpl
			implements EntityTypeDescriptor {
		public EntityTypeDescriptorImpl(String entityName) {
			super( entityName.contains( "." ) ? entityName :  "com.acme." + entityName );
		}
	}

	public static class AttributeDescriptorImpl implements AttributeDescriptor {
		private final TypeDescriptor source;
		private final String name;
		private final TypeDescriptor type;

		public AttributeDescriptorImpl(
				TypeDescriptor source,
				String name,
				TypeDescriptor type) {
			this.source = source;
			this.name = name;
			this.type = type;
		}

		@Override
		public TypeDescriptor getDeclaringType() {
			return source;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public TypeDescriptor getType() {
			return type;
		}
	}

	public static class CompositeTypeDescriptorImpl
			extends AbstractTypeDescriptorImpl
			implements CompositeTypeDescriptor {
		public CompositeTypeDescriptorImpl(String typeName) {
			super( typeName );
		}
	}

	public static class CollectionTypeDescriptorImpl
			extends AbstractTypeDescriptorImpl
			implements CollectionTypeDescriptor {
		private final TypeDescriptor indexType;
		private final TypeDescriptor elementType;

		public CollectionTypeDescriptorImpl(TypeDescriptor elementType) {
			this( Collection.class, null, elementType );
		}

		public CollectionTypeDescriptorImpl(TypeDescriptor indexType, TypeDescriptor elementType) {
			this( Map.class, indexType, elementType );
		}

		public CollectionTypeDescriptorImpl(Class collectionType, TypeDescriptor indexType, TypeDescriptor elementType) {
			super( collectionType.getName() );
			this.indexType = indexType;
			this.elementType = elementType;
		}

		@Override
		public AttributeDescriptor getAttributeDescriptor(String attributeName) {
			return null;
		}

		@Override
		public TypeDescriptor getIndexTypeDescriptor() {
			return indexType;
		}

		@Override
		public TypeDescriptor getElementTypeDescriptor() {
			return elementType;
		}
	}

}
