package com.coopcredit.app.infrastructure.mappers;

import com.coopcredit.app.domain.model.User;
import com.coopcredit.app.infrastructure.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toDomain(UserEntity entity);

    UserEntity toEntity(User domain);
}
