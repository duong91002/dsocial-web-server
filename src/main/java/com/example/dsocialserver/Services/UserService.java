/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.dsocialserver.Services;

import static com.example.dsocialserver.Models.Pagination.getPagination;
import com.example.dsocialserver.Models.User;
import com.example.dsocialserver.Repositories.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author haidu
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByEmailAndPassword(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    public Map<String, Object> getInfoUser(Object id) {
        Map<String, Object> data = new HashMap<>();
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User list = optionalUser.get();

            data.put("id", list.getId());
            data.put("email", list.getEmail());
            data.put("name", list.getName());
            data.put("avatar", list.getAvatar());
            data.put("bio", list.getBio());
            data.put("birthday", list.getBirthday());
            data.put("cover_image", list.getCover_image());
            data.put("other_name", list.getOther_name());
        }
        return data;
    }

    public User findById(Object id) {
        Optional<User> optionalUser = userRepository.findById(id);
        User list = null;
        if (optionalUser.isPresent()) {
            list = optionalUser.get();
        }
        return list;
    }

    public User updateisActiveUser(Object id) {
        Optional<User> optionalUser = userRepository.findById(id);
        User list = null;
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // Cập nhật các trường của đối tượng user
            user.setIs_active(1);
            // ...
            list = userRepository.save(user);
        }
        return list;
    }

    public User updatePasswordUser(Object id, String password) {
        Optional<User> optionalUser = userRepository.findById(id);
        User list = null;
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // Cập nhật các trường của đối tượng user
            user.setPassword(password);
            // ...
            list = userRepository.save(user);
        }
        return list;
    }

    public User createUser(String email, String password, String name, String avatar) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setAvatar(avatar);
        return userRepository.save(user);
    }
    public Map<String, Object> getPeopleList(int page, int limit,int userId, String name) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<User> list = userRepository.findAllByName(pageable, userId, name, 0);
        return reponsDataPeopleList(page, list);
    }
    public Map<String, Object> reponsDataPeopleList(int page, Page<User> list) {
        List<Map<String, Object>> listdata = new ArrayList<>();
        for (User o : list.getContent()) {
            Map<String, Object> data = new HashMap<>();
            listdata.add(getUser(o));
        }
        Map<String, Object> dataResult = new HashMap<>();
        dataResult.put("data", listdata);
        dataResult.put("pagination", getPagination(page, list));

        return dataResult;
    }

    public static Map<String, Object> getUser(User list) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", list.getId());
        data.put("email", list.getEmail());
        data.put("name", list.getName());
        data.put("avatar", list.getAvatar());
        data.put("bio", list.getBio());
        data.put("birthday", list.getBirthday());
        data.put("cover_image", list.getCover_image());
        data.put("other_name", list.getOther_name());
        return data;
    }
}
