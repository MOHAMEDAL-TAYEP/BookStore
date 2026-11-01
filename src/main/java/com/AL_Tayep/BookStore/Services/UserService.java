package com.AL_Tayep.BookStore.Services;


import com.AL_Tayep.BookStore.Repositories.CartRepository;

import java.util.List;




import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AL_Tayep.BookStore.Dto.UserDtos.ChangePasswordDto;
import com.AL_Tayep.BookStore.Dto.UserDtos.LoginRequestDto;
import com.AL_Tayep.BookStore.Dto.UserDtos.UserCreateDto;
import com.AL_Tayep.BookStore.Dto.UserDtos.UserResponseDto;
import com.AL_Tayep.BookStore.Exceptions.BadRequestException;
import com.AL_Tayep.BookStore.Exceptions.ResourceNotFoundException;
import com.AL_Tayep.BookStore.Repositories.UserRepository;
import com.AL_Tayep.BookStore.models.Cart;
import com.AL_Tayep.BookStore.models.User;
import com.AL_Tayep.BookStore.models.Enums.Role;
import com.AL_Tayep.BookStore.models.Enums.UserStatus;



@Service 
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder)
    {
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    }

    
    public UserResponseDto toUserResponseDto(User user)
    {
        UserResponseDto userResponseDto=new UserResponseDto(user.getId(),user.getName(),user.getEmail());
        return userResponseDto;
    }

    public User toUser(UserCreateDto userCreateDto,Role role)
    {
        User user=new User();
        user.setName(userCreateDto.name());
        user.setEmail(userCreateDto.email());
        String hashPassword=passwordEncoder.encode(userCreateDto.password());
        user.setPassword(hashPassword);
        user.setRole(role);
        user.setStatus(UserStatus.Active);
        return user;
    }

    public  UserResponseDto addUser(UserCreateDto userCreateDto,Role role)
    {
        User user=toUser(userCreateDto,role);
       if(userRepository.existsByEmail(userCreateDto.email()))
           throw new BadRequestException("this email used already "+userCreateDto);
       if(role!=Role.ADMIN||role!=Role.MODERATOR) 
       {
       Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);
       }
        User savedUser=userRepository.save(user);
        return  toUserResponseDto(savedUser);
    }
    public UserResponseDto updateUser(UserCreateDto userCreateDto,User user)
    {
        user.setEmail(userCreateDto.email());
        user.setName(userCreateDto.name());
         User savedUser=userRepository.save(user);
         return  toUserResponseDto(savedUser);
    }


    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUser()
    {
       return userRepository.findAll().stream().map(this::toUserResponseDto) .toList();
    }
    public  void deleteUser(Long id)
    {
        User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        userRepository.deleteById(id);
    }

    public void updatePassword(ChangePasswordDto changePasswordDto,User user)
    {
        if (!passwordEncoder.matches(changePasswordDto.oldPassword(), user.getPassword()))
        throw new BadRequestException("Old password is incorrect");
         String newHashPassword=passwordEncoder.encode(changePasswordDto.newPassword());

        user.setPassword(newHashPassword);
    }



    // get user data by id
    public UserResponseDto getUserById(Long id)
    {
        return  toUserResponseDto(userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("there is no user with this id: "+id)));
    }
    
}
