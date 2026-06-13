package com.example.aaugp.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.aaugp.dto.user.UserRequest;
import com.example.aaugp.dto.user.UserResponse;
import com.example.aaugp.model.UserEntity;
import com.example.aaugp.repositories.UserRepository;

import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class UserServices{


 private UserRepository userRepository;

 
 private UserResponse toDTO(UserEntity user) {
 UserResponse dto = new UserResponse();
 dto.setFirstName(user.getFirstName());
 dto.setLastName(user.getLastName());
 dto.setEmail(user.getEmail());
 dto.setRole(user.getRole());
 dto.setId(user.getId());
        return dto;
    }

private UserEntity fromDTO(UserRequest request) {
    if (request == null) { return null; } 
     UserEntity user = new UserEntity();
     user.setFirstName(request.getFirstName());
     user.setLastName(request.getLastName());
     user.setStudentId(request.getStudentId());
     user.setEmail(request.getEmail());
     user.setPassword(request.getPassword() );
 
 return user;
    
    }


     public UserResponse createUser(UserRequest dto) {
        UserEntity user =  fromDTO(dto);
        UserEntity saved = userRepository.save(user);
        return toDTO(saved);
     }

    

  public UserResponse getUsersById(Long id){
        UserEntity  user = userRepository.findById(id)
                     .orElseThrow(() -> new ResponseStatusException( 
                        HttpStatus.NOT_FOUND  ,"User not found with id:" + id));
        return toDTO(user);
    }

     public UserResponse updateUser(Long id, UserRequest dto) {
        UserEntity user = userRepository.findById(id).orElse(null);
        if (user == null) { return null; }
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setStudentId(dto.getStudentId());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        UserEntity updated = userRepository.save(user);
        return toDTO(updated);
     }



    public void deleteUserById(Long id){
      UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
      userRepository.save(user);
    }


}