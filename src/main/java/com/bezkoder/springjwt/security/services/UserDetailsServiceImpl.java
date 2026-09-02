package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.User.ERole;
import com.bezkoder.springjwt.models.User.Role;
import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.repository.RoleRepository;
import com.bezkoder.springjwt.repository.RolesRepos;
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
    private final RoleRepository roleRepository;
    private final RolesRepos rolesRepos;

    @Autowired
  public UserDetailsServiceImpl(UserRepository userRepository, RoleRepository roleRepository, RolesRepos rolesRepos) {
    this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.rolesRepos = rolesRepos;
    }


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


      User user = userRepository.findByUsername(username).orElse(null);
      if(user == null) {
        throw new UsernameNotFoundException("User Not Found with username: " + username);
      }
      return UserDetailsImpl.build(user);
    }

    public User getCurrentUser() {
        UserDetailsImpl usr = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(usr.getUsername()).orElse(null);
    }

    public boolean hasNoRoles() {
      return roleRepository.findAll().isEmpty();
    }

    public void createBasicRoles() {
        rolesRepos.save(new Role(ERole.ROLE_USER));
        rolesRepos.save(new Role(ERole.ROLE_MODERATOR));
        rolesRepos.save(new Role(ERole.ROLE_ADMIN));
        rolesRepos.save(new Role(ERole.ROLE_SUPERADMIN));
    }

    public User GetUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }


}
