package com.fiap.nova.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;

import com.fiap.nova.dto.UserFilters;
import com.fiap.nova.model.User;
import com.fiap.nova.service.UserService;



@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public PagedModel<EntityModel<User>> getAll(
            UserFilters filters,
            @PageableDefault(size = 10, sort = "name") Pageable pageable,
            PagedResourcesAssembler<User> assembler
    ) {
        var page = userService.findAll(pageable, filters);
        return assembler.toModel(page, User::toEntityModel);
    }

    @GetMapping("/{id}")
    public EntityModel<User> getById(@PathVariable Long id) {
        var user = userService.findById(id);
        return user.toEntityModel();
    }
}

