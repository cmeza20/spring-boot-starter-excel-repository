package com.cmeza.spring.excel.repository.factories.model;

import com.cmeza.spring.excel.repository.support.factories.model.ItemFactory;
import com.cmeza.spring.excel.repository.support.factories.model.ModelFactory;
import com.cmeza.spring.excel.repository.support.factories.model.ModelMapFactory;
import com.cmeza.spring.excel.repository.support.exceptions.ModelValidatorException;
import com.cmeza.spring.excel.repository.support.extensions.ItemErrorExtension;
import com.cmeza.spring.excel.repository.support.extensions.ItemValueExtension;
import com.cmeza.spring.excel.repository.support.extensions.ModelValidatorExtension;
import com.cmeza.spring.excel.repository.support.mappers.ToModelMapper;
import com.cmeza.spring.excel.repository.support.maps.MapModel;
import com.cmeza.spring.excel.repository.support.exceptions.ModelException;
import com.cmeza.spring.excel.repository.mappers.DefaultToModelMapper;
import com.cmeza.spring.excel.repository.parsers.values.*;
import com.cmeza.spring.excel.repository.support.members.EntityMember;
import com.cmeza.spring.excel.repository.support.members.ValueObject;
import com.cmeza.spring.excel.repository.support.parsers.values.ValueParser;
import com.cmeza.spring.excel.repository.utils.ModelUtils;
import com.cmeza.spring.excel.repository.support.validations.ModelConstraintViolation;
import com.cmeza.spring.excel.repository.validations.ModelValidator;
import org.springframework.util.Assert;

import javax.validation.Validator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

public class ModelFactoryImpl<T, M> implements ModelMapFactory<T, M>, ModelValidatorExtension<T> {

    private static final Map<Class<?>, ValueParser<?>> valueParsers;

    static {
        valueParsers = new LinkedHashMap<>();
        valueParsers.put(Integer.class, new IntegerValueParser());
        valueParsers.put(Long.class, new LongValueParser());
        valueParsers.put(Double.class, new DoubleValueParser());
        valueParsers.put(Date.class, new DateValueParser());
        valueParsers.put(LocalDate.class, new LocalDateValueParser());
        valueParsers.put(LocalDateTime.class, new LocalDateTimeValueParser());
        valueParsers.put(Boolean.class, new BooleanValueParser());
        valueParsers.put(Object.class, new ObjectValueParser());
        valueParsers.put(String.class, new ObjectValueParser());
    }

    private final List<ItemFactory<T>> items = new LinkedList<>();
    private final List<M> mapItems = new LinkedList<>();
    private final List<T> successful = new LinkedList<>();
    private final List<T> errors = new LinkedList<>();
    private final Class<T> modelClass;
    private final Class<M> mapClass;
    private final ModelValidator modelValidator;
    private boolean hierarchical = true;
    private ToModelMapper<T> mapper;
    private boolean preBuilt;
    private boolean mapBuilt;
    private boolean saveErrors;
    private boolean saveSuccessful;
    private MapModel<T, M> mapModel;

    public ModelFactoryImpl(Class<T> modelClass) {
        this(modelClass, null);
    }

    public ModelFactoryImpl(Class<T> modelClass, Class<M> mapClass) {
        Assert.notNull(modelClass, "modelClass must not be null");
        this.modelClass = modelClass;
        this.mapClass = mapClass;
        this.modelValidator = ModelValidator.getInstance();
    }

    @Override
    public ModelFactory<T> withHierarchical(boolean hierarchical) {
        this.hierarchical = hierarchical;
        return this;
    }

    @Override
    public ModelValidatorExtension<T> withSaveErrors(boolean saveErrors) {
        this.saveErrors = saveErrors;
        return this;
    }

    @Override
    public ModelValidatorExtension<T> withSaveSuccessful(boolean saveSuccessful) {
        this.saveSuccessful = saveSuccessful;
        return this;
    }

    @Override
    public ModelValidatorExtension<T> withValidator(Validator validator) {
        this.modelValidator.withValidator(validator);
        return this;
    }

    @Override
    public ModelValidatorExtension<T> withValidator(jakarta.validation.Validator validator) {
        this.modelValidator.withValidator(validator);
        return this;
    }

    @Override
    public List<T> getSuccessful() {
        return this.successful;
    }

    @Override
    public List<T> getErrors() {
        return this.errors;
    }

    @Override
    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    @Override
    public ModelFactory<T> withModelMapper(ToModelMapper<T> mapper) {
        Assert.notNull(mapper, "mapper must not be null");
        this.mapper = mapper;
        return this;
    }

    @Override
    public ModelMapFactory<T, M> withMapModel(MapModel<T, M> mapModel) {
        Assert.notNull(mapModel, "mapModel must not be null");
        this.mapModel = mapModel;
        return this;
    }

    @Override
    public ItemFactory<T> item() {
        ItemFactory<T> itemFactory = new ItemFactoryImpl<>();
        items.add(itemFactory);
        return itemFactory;
    }

    @Override
    public ModelFactory<T> withItem(Consumer<ItemFactory<T>> itemModelFactoryConsumer) {
        Assert.notNull(itemModelFactoryConsumer, "Consumer must not be null");
        itemModelFactoryConsumer.accept(item());
        return this;
    }

    @Override
    public ToModelMapper<T> getModelMapper() {
        return this.mapper;
    }

    @Override
    public Class<T> getModelClass() {
        return this.modelClass;
    }

    @Override
    public void preBuilt() {
        if (!preBuilt) {
            preBuilt = true;

            if (Objects.isNull(mapper)) {
                this.mapper = new DefaultToModelMapper<>(modelClass);
            }
        }
    }

    @Override
    public ModelValidatorExtension<T> toValidator() {
        return this;
    }

    @Override
    public List<T> build() {
        if (!preBuilt) {
            preBuilt();
        }

        List<T> results = new ArrayList<>();

        int pos = 0;
        for (ItemFactory<T> itemFactory : items) {
            Map<String, ValueObject> attributes = itemFactory.build();
            ItemValueExtension<T> itemValueExtension = ModelUtils.convertTo(itemFactory);
            ItemErrorExtension<T> itemErrorExtension = ModelUtils.convertTo(itemFactory);

            T instance = mapper.toInstance();
            boolean isError = false;
            try {
                EntityMember<T> entityMember = new EntityMember<>(instance);
                ModelValidatorException modelValidatorException = this.iterateAttributes(entityMember, attributes);
                if (!modelValidatorException.getExceptions().isEmpty()) {
                    throw modelValidatorException;
                }

                instance = mapper.afterFactoryMap(entityMember);

                if (Objects.nonNull(mapModel)) {
                    M mapInstance = mapModel.map(pos, instance);
                    mapItems.add(mapInstance);
                }

            } catch (Exception e) {
                isError = true;
                this.buildCatch(e, itemErrorExtension);
            } finally {
                this.buildFinally(instance, itemValueExtension, itemErrorExtension, isError, results);
            }
            pos++;
        }
        this.mapBuilt = true;

        return results;
    }

    @Override
    public boolean isSaveSuccessful() {
        return this.saveSuccessful;
    }

    @Override
    public boolean isSaveErrors() {
        return this.saveErrors;
    }

    private ModelValidatorException iterateAttributes(EntityMember<T> entityMember, Map<String, ValueObject> attributes) {
        ModelValidatorException modelValidatorException = new ModelValidatorException("");

        for (Map.Entry<String, ValueObject> entry : attributes.entrySet()) {
            ValueObject valueObject = entry.getValue();
            if (hierarchical || !valueObject.isHierarchical()) {
                String modelAttribute = entry.getKey();

                if (valueObject.isHierarchical()) {
                    String[] nameParts = modelAttribute.split("\\.");

                    if (nameParts.length == 1) {
                        modelValidatorException.addViolation(this.bindValue(modelAttribute, valueObject, entityMember, false));
                        continue;
                    }

                    this.iterateParts(nameParts, 0, entityMember, valueObject, modelValidatorException);
                } else {
                    modelValidatorException.addViolation(this.bindValue(modelAttribute, valueObject, entityMember, false));
                }
            }
        }
        return modelValidatorException;
    }

    private void iterateParts(String[] nameParts, int pos, EntityMember<?> entityMember, ValueObject valueObject, ModelValidatorException modelValidatorException) {
        if (Objects.isNull(entityMember)) {
            return;
        }

        boolean isLast = pos == nameParts.length - 1;
        String part = nameParts[pos];

        if (isLast) {
            modelValidatorException.addViolation(this.bindValue(part, valueObject, entityMember, false));
        } else {
            EntityMember<?> hierarchicalEntityMember = entityMember.getAttribute(part);
            if (Objects.isNull(hierarchicalEntityMember)) {
                hierarchicalEntityMember = createHierarchicalEntityMember(part, entityMember);
            }

            modelValidatorException.addViolation(this.bindValue(part, new ValueObject(hierarchicalEntityMember.getTarget()), entityMember, true));

            iterateParts(nameParts, ++pos, hierarchicalEntityMember, valueObject, modelValidatorException);
        }
    }

    private EntityMember<?> createHierarchicalEntityMember(String attribute, EntityMember<?> parentEntityMember) {
        Object target = parentEntityMember.getValue(attribute);
        if (Objects.isNull(target)) {
            Class<?> type = parentEntityMember.getAttributeType(attribute);
            target = mapper.toInstance(type);
        }
        EntityMember<?> childEntityMember = new EntityMember<>(target);
        parentEntityMember.addAttribute(attribute, childEntityMember);
        return childEntityMember;
    }

    private Set<ModelConstraintViolation<T>> bindValue(String modelAttribute, ValueObject valueObject, EntityMember<?> entityMember, boolean isEmptyNewInstance) {
        Class<?> classCast = valueObject.getCastClass();
        Object value = valueObject.getValue();
        ValueParser<?> valueParser = Objects.nonNull(classCast) ? valueParsers.get(classCast) : null;

        if (isEmptyNewInstance) {
            entityMember.setValue(modelAttribute, value);
        } else {

            mapper.mapFactoryValue(entityMember, modelAttribute, value, classCast, valueParser);

            if (entityMember.isValidatable(modelAttribute)) {
               return modelValidator.validateValue(modelClass, modelAttribute, entityMember.getValue(modelAttribute));
            }
        }
        return Collections.emptySet();
    }


    @Override
    public List<M> buildMap() {
        if (Objects.isNull(this.mapClass)) {
            throw new ModelException("mapClass is null");
        }

        if (Objects.isNull(this.mapModel)) {
            throw new ModelException("Map model interface is null");
        }

        if (!mapBuilt) {
            build();
        }
        return this.mapItems;
    }

    private void buildCatch(Exception e, ItemErrorExtension<T> itemErrorExtension) {
        if (e instanceof ModelValidatorException) {
            ((ModelValidatorException)e).getExceptions().forEach(itemErrorExtension::withError);
        } else {
            itemErrorExtension.withError(e);
        }

        if (!this.saveErrors) {
            throw new ModelException(e.getMessage(), e);
        }
    }

    private void buildFinally(T instance, ItemValueExtension<T> itemValueExtension, ItemErrorExtension<T> itemErrorExtension, boolean isError, List<T> results) {
        itemValueExtension.withValue(instance);

        if (isError && this.saveErrors) {
            if (itemErrorExtension.hasErrors()) {
                mapper.bindError(instance, itemErrorExtension);
                itemErrorExtension.consumeErrorListener(itemErrorExtension);
            }

            this.errors.add(instance);
        }

        if (!isError) {
            this.successful.add(instance);
        }

        results.add(instance);
    }
}
