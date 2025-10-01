package com.cmeza.spring.excel.repository.support.members;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.beans.AbstractNestablePropertyAccessor;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyValue;
import org.springframework.core.convert.TypeDescriptor;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@EqualsAndHashCode
public class EntityMember<T> extends ReflectionMember<T> {

    private final Map<String, AttributeMember> attributeMembers;
    private final Map<String, EntityMember<?>> attributes;

    public EntityMember(T target) {
        super(target);
        this.attributes = new HashMap<>();
        this.attributeMembers = new HashMap<>();
    }

    public Object getValue(String fieldName) {
        AttributeMember attributeMember = initializeAttributeMember(fieldName);

        if (attributeMember.isCalculatedValue()) {
            return attributeMember.getValue();
        }

        Object value = this.getPropertyValue(fieldName);
        attributeMember.setValue(value);
        attributeMember.setCalculatedValue(true);

        return value;
    }

    public void setValue(String fieldName, Object value) {
        AttributeMember attributeMember = initializeAttributeMember(fieldName);
        setPropertyValue(attributeMember.getPropertyTokenHolder(), new PropertyValue(fieldName, value));
    }

    public Class<?> getAttributeType(String fieldName) {
        AttributeMember attributeMember = initializeAttributeMember(fieldName);
        return attributeMember.getAttributeType();
    }

    public void addAttribute(String fieldName, EntityMember<?> attribute) {
        attribute.setParentType(this.getTarget().getClass());
        this.attributes.put(fieldName, attribute);
    }

    @SuppressWarnings("unchecked")
    public <N> EntityMember<N> getAttribute(String fieldName) {
        return (EntityMember<N>) this.attributes.get(fieldName);
    }

    public boolean isValidatable(String fieldName) {
        EntityMember<?> entityMember = this.getAttribute(fieldName);
        if (Objects.nonNull(entityMember)) {
            return entityMember.isValidatable(fieldName);
        }

        AttributeMember attributeMember = initializeAttributeMember(fieldName);
        return attributeMember.isValidatable();
    }

    private static boolean hasLocalAnnotations(Annotation[] annotations) {
        return Arrays.stream(annotations).anyMatch(a -> !a.annotationType().getPackageName().contains("com.cmeza.spring.excel.repository"));
    }

    private AttributeMember initializeAttributeMember(String attributeName) {
        AttributeMember attributeMember = attributeMembers.get(attributeName);
        if (Objects.nonNull(attributeMember)) {
            return attributeMember;
        }

        attributeMember = new AttributeMember(attributeName);
        try {
            AbstractNestablePropertyAccessor propertyAccessorForPropertyPath = getPropertyAccessorForPropertyPath(attributeName);
            attributeMember.setAccessor(propertyAccessorForPropertyPath);
            attributeMember.setPropertyTokenHolder(new PropertyTokenHolder(this.getFinalPath(propertyAccessorForPropertyPath, attributeName)));
            attributeMember.setAttributeType(getPropertyType(attributeName));

            TypeDescriptor typeDescriptor = this.getPropertyTypeDescriptor(attributeName);
            if (Objects.nonNull(typeDescriptor)) {
                attributeMember.setValidatable(hasLocalAnnotations(typeDescriptor.getAnnotations()));
            }

            attributeMembers.put(attributeName, attributeMember);

            return attributeMember;
        } catch (NotReadablePropertyException ex) {
            throw new NotWritablePropertyException(this.getRootClass(), attributeName, ex.getMessage(), ex);
        }
    }
}
