package dev.stanislavskyi.feedback_bot_vgr.controller;

import dev.stanislavskyi.feedback_bot_vgr.dto.request.RegisterRequest;
import dev.stanislavskyi.feedback_bot_vgr.dto.response.UserResponse;
import dev.stanislavskyi.feedback_bot_vgr.service.UserService;
import dev.stanislavskyi.feedback_bot_vgr.utils.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(ApiPaths.API_V1_USERS)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @Operation(summary = "Create user",
            description = "Creates a new user.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created"),
                    @ApiResponse(responseCode = "400", description = "Validation error"),
                    @ApiResponse(responseCode = "409", description = "Conflict: user already exists")
            }
    )
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody RegisterRequest request){
        UserResponse response = userService.createUser(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
}
