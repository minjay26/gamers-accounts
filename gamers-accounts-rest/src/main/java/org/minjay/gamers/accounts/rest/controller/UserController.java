package org.minjay.gamers.accounts.rest.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.minjay.gamers.accounts.data.domain.User;
import org.minjay.gamers.accounts.data.domain.UserFocus;
import org.minjay.gamers.accounts.data.jackson.DataView;
import org.minjay.gamers.accounts.service.UserFocusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserFocusService userFocusService;

    @Autowired
    public UserController(UserFocusService userFocusService) {
        this.userFocusService = userFocusService;
    }

    @JsonView(DataView.Basic.class)
    @GetMapping("/{id}/focus")
    public ResponseEntity<List<UserFocus>> getFocus(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(userFocusService.getAllFocus(userId));
    }

    @GetMapping("/{id}/username")
    public ResponseEntity<String> getUsername(@PathVariable("id") User user) {
        return ResponseEntity.ok(user.getUsername());
    }
}
