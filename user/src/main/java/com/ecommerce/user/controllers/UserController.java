package com.ecommerce.user.controllers;

import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    //@Autowired
    private final UserService userService;

    //@GetMapping("/api/users")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        //return ResponseEntity.ok(userService.fetchAllUsers());
        //return ResponseEntity<>(userService.fetchAllUsers(), HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(userService.fetchAllUsers());

    }


    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id){
        //ResponseEntity
       // HttpStatus
//        User user = userService.fetchUser(id);
//        if(user==null)
//                return ResponseEntity.notFound().build();
//        return ResponseEntity.ok(user);
        return userService.fetchUser(id).map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());

    }


    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserRequest userRequest){
        userService.addUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userRequest.toString());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> createUser(@PathVariable String id,
                                         @RequestBody UserRequest updatedUserRequest){
        boolean updated = userService.updateUser(id, updatedUserRequest);
        if(updated)
            return ResponseEntity.status(HttpStatus.OK).body("User added successfully");
        return ResponseEntity.notFound().build();
    }



}
