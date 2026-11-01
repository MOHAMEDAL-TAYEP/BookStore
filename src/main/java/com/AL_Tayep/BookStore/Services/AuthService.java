package com.AL_Tayep.BookStore.Services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import com.AL_Tayep.BookStore.Dto.AuthDtos.AuthResponseDto;
import com.AL_Tayep.BookStore.Dto.UserDtos.LoginRequestDto;
import com.AL_Tayep.BookStore.Dto.UserDtos.UserCreateDto;
import com.AL_Tayep.BookStore.Exceptions.ConflictException;
import com.AL_Tayep.BookStore.Repositories.UserRepository;
import com.AL_Tayep.BookStore.Security.JwtTokenProvider;
import com.AL_Tayep.BookStore.models.Cart;
import com.AL_Tayep.BookStore.models.User;
import com.AL_Tayep.BookStore.models.Enums.Role;
import com.AL_Tayep.BookStore.models.Enums.UserStatus;

@Service 
public class AuthService {
    
     private final AuthenticationManager authenticationManager;
        private final JwtTokenProvider jwtTokenProvider;
         private final PasswordEncoder passwordEncoder; 
         private final UserRepository userRepository;

        public AuthService(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
                
        this.authenticationManager=authenticationManager;
        this.jwtTokenProvider=jwtTokenProvider;
        this.passwordEncoder=passwordEncoder;
        this.userRepository=userRepository;
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

    

    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        // 1. Authenticate using Spring Security's AuthenticationManager.
        // This automatically checks if the user exists and verifies the password against your PasswordEncoder.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.email(), 
                        loginRequestDto.password()
                )
        );

        // 2. Extract the authenticated user details
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 3. Generate the JWT token
        String token = jwtTokenProvider.generateToken(userDetails);

        // 4. Return the response containing the token
        return new AuthResponseDto(token);
    }


    public AuthResponseDto register(UserCreateDto dto) {
        // 1. Check if email is already taken (Throws 409 Conflict)
        if (userRepository.existsByEmail(dto.email())) {
            throw new ConflictException("Email is already in use: " + dto.email());
        }

        // 2. Create the user entity
        User user = toUser(dto, Role.USER);
           
        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(userDetails);

        return new AuthResponseDto(token);
    }


}