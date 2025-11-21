package com.fiap.nova.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fiap.nova.dto.RegisterRequest;
import com.fiap.nova.dto.UserResponse;
import com.fiap.nova.dto.UserUpdateRequest;
import com.fiap.nova.filters.UserFilters;
import com.fiap.nova.model.User;
import com.fiap.nova.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody RegisterRequest request) {
        var user = userService.createUser(request.toModel());
        return ResponseEntity.ok(UserResponse.from(user));
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

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        var updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(UserResponse.from(updatedUser));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}

