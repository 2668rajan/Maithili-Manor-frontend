package com.rajan.Maithili_Manor.service;

import com.rajan.Maithili_Manor.entity.Role;
import com.rajan.Maithili_Manor.entity.User;
import com.rajan.Maithili_Manor.exception.RoleAlreadyExistsException;
import com.rajan.Maithili_Manor.exception.UserAlreadyExistsException;
import com.rajan.Maithili_Manor.exception.UserNotFoundException;
import com.rajan.Maithili_Manor.repository.RoleRepository;
import com.rajan.Maithili_Manor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role createRole(Role role) {
        String roleName = "ROLE_" + role.getName().toUpperCase();
        Role role1 = new Role(roleName);
        if(roleRepository.existsByName(roleName)){
            throw new RoleAlreadyExistsException(role1.getName() + " already exists");
        }
        return roleRepository.save(role1);
    }

    @Override
    public void deleteRole(Long roleId) {
        this.removeAllUsersFromRole(roleId);
        roleRepository.deleteById(roleId);
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name).get();
    }

    @Override
    public User removeUserFromRole(Long userId, Long roleId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Role> role = roleRepository.findById(roleId);

        if(role.isPresent() && role.get().getUsers().contains(user.get())){
            role.get().removeUserFromRole(user.get());
            roleRepository.save(role.get());
            return user.get();
        }
        throw new  UserNotFoundException("user not found!");
    }

    @Override
    public User assignRoleToUser(Long userId, Long roleId) {

        Optional<User> user = userRepository.findById(userId);
        Optional<Role> role = roleRepository.findById(roleId);

        if(user.isPresent() && user.get().getRoles().contains(role.get())){
            throw new UserAlreadyExistsException(user.get().getFirstName() + " is already assigned to the "
                + role.get().getName() + " role");
        }

        if (role.isPresent()){
            role.get().assignRoleToUser(user.get());
            roleRepository.save(role.get());
        }
        return user.get();
    }

    @Override
    public Role removeAllUsersFromRole(Long roleId) {
        Optional<Role> role = roleRepository.findById(roleId);
        role.ifPresent(Role::removeAllUsersFromRole);
        return roleRepository.save(role.get());
    }
}
