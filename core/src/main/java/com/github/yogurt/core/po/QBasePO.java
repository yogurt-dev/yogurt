package com.github.yogurt.core.po;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QBasePO is a Querydsl query type for BasePO
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QBasePO extends EntityPathBase<BasePO<?>> {

    private static final long serialVersionUID = 381458586L;

    public static final QBasePO basePO = new QBasePO("basePO");

    public final NumberPath<Long> creatorId = createNumber("creatorId", Long.class);

    public final BooleanPath deleted = createBoolean("deleted");

    public final DateTimePath<java.time.LocalDateTime> gmtCreate = createDateTime("gmtCreate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> gmtModified = createDateTime("gmtModified", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> modifierId = createNumber("modifierId", Long.class);

    @SuppressWarnings({"all", "rawtypes", "unchecked"})
    public QBasePO(String variable) {
        super((Class) BasePO.class, forVariable(variable));
    }

    @SuppressWarnings({"all", "rawtypes", "unchecked"})
    public QBasePO(Path<? extends BasePO> path) {
        super((Class) path.getType(), path.getMetadata());
    }

    @SuppressWarnings({"all", "rawtypes", "unchecked"})
    public QBasePO(PathMetadata metadata) {
        super((Class) BasePO.class, metadata);
    }

}

