package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bezkoder.springjwt.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Autowired
  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

      if(userRepository.existsByUsername(username)) {
        System.out.println("User exists");
      }
      User user = userRepository.findByUsername(username).orElse(null);
      if(user == null) {
        throw new UsernameNotFoundException("User Not Found with username: " + username);
      }
      return UserDetailsImpl.build(user);
    }

    public User getCurrentUser() {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       var us = authentication.getDetails();
       var uss = authentication.getPrincipal();

        UserDetailsImpl usr = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(usr.getUsername()).orElse(null);
    }

}
