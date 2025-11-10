package com.ecommerce.user.service;

import com.ecommerce.user.dto.AddressDTO;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.model.Address;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    //private List<User> userList = new ArrayList<>();
    //private Long nextId = 1L;

//    public UserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }


    public List<UserResponse> fetchAllUsers(){
        List<User> userList = userRepository.findAll();
        return userRepository.findAll().stream().map(this::mapToUserResponse).toList(); //userList;
    }

    public Optional<UserResponse> fetchUser(Long id){
        return userRepository.findById(id).map(this::mapToUserResponse);
        //userList.stream().filter(user -> user.getId().equals(id)).findFirst();
    }

    public void addUser(UserRequest userRequest){
        //user.setId(nextId++);
        User user = new User();
        updateUserFromRequest(user, userRequest);
        userRepository.save(user);//userList.add(user);
    }

    private void updateUserFromRequest(User user, UserRequest userRequest){
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());

        if(userRequest.getAddress()!=null){
            Address address= new Address();
            address.setCity(userRequest.getAddress().getCity());
            address.setStreet(userRequest.getAddress().getStreet());
            address.setState(userRequest.getAddress().getState());
            address.setZipcode(userRequest.getAddress().getZipcode());
            address.setCountry(userRequest.getAddress().getCountry());

            user.setAddress(address);

        }

    }




    public boolean updateUser(Long id, UserRequest updatedUserRequest){
//        return userList.stream().filter(user -> user.getId().equals(id))
//                .findFirst().map(existingUser-> {
//                    existingUser.setFirstName(updatedUser.getFirstName());
//                    existingUser.setLastName(updatedUser.getLastName());
//                    return true;
//                }).orElse(false);

        return userRepository.findById(id).map(existingUser-> {
//                    existingUser.setFirstName(updatedUser.getFirstName());
//                    existingUser.setLastName(updatedUser.getLastName());
                    updateUserFromRequest(existingUser, updatedUserRequest);
                    userRepository.save(existingUser);
                    return true;
                }).orElse(false);
    }

    private UserResponse mapToUserResponse(User user){
        UserResponse response = new UserResponse();
        response.setId(String.valueOf(user.getId()));
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());

        if(user.getAddress()!= null){
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setStreet(user.getAddress().getStreet());
            addressDTO.setCity(user.getAddress().getCity());
            addressDTO.setState(user.getAddress().getState());
            addressDTO.setCountry(user.getAddress().getCountry());
            addressDTO.setZipcode(user.getAddress().getZipcode());
            response.setAddress(addressDTO);
        }

        return response;

    }

}








/*

orElse is on the Optional (the return of map) — not on findFirst.

breakdown:

Step by step:

findFirst() returns:

Optional<User>


Then you do:

.map(existingUser -> { ...; return true; })


map will convert the Optional<User> into:

Optional<Boolean>


So the type is now Optional<Boolean>.

Then:

.orElse(false)


is called on that Optional<Boolean>.

Simple mental model
stream.findFirst()     -> Optional<T>
.map(x -> do stuff)    -> Optional<R>
.orElse(default)       -> R


So final value is either:

result of your map block ( true )

or false if nothing was found
 */