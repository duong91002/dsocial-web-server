/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.dsocialserver.Controllers;

import com.example.dsocialserver.Utils.CustomResponse;
import com.example.dsocialserver.Models.GroupUser;
import com.example.dsocialserver.Models.Groups;
import com.example.dsocialserver.Services.GroupUserService;
import com.example.dsocialserver.Types.GroupBossType;
import com.example.dsocialserver.Types.GroupUserType;
import com.example.dsocialserver.Utils.JwtTokenProvider;
import static com.example.dsocialserver.Utils.ParseJSon.ParseJSon;
import com.example.dsocialserver.Utils.StatusUntilIndex;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author haidu
 */
@CrossOrigin
@RestController
@RequestMapping("/group-user")
public class GroupUserController {

    @Autowired
    private GroupUserService groupUserService;

    private final CustomResponse jsonRes = new CustomResponse();

    @PostMapping()
    public ResponseEntity joinGroupUser(@RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody GroupUserType gr) throws IOException {
        try {
            String groupId = gr.getGroupId();
            String userId = JwtTokenProvider.getIDByBearer(authorizationHeader).getSubject();

//        ----------------------------------
            Map<String, Object> groupUser = groupUserService.joinGroupUser(Integer.parseInt(groupId), Integer.parseInt(userId));
            if (!groupUser.isEmpty()) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", "Tham gia nhóm thành công");
                responseData.put("data", groupUser);
                return ResponseEntity.status(HttpStatus.OK).body(ParseJSon(responseData));
            }
            return StatusUntilIndex.showMissing();
        } catch (NumberFormatException e) {
            return StatusUntilIndex.showInternal(e);
        }
    }

    @DeleteMapping("/kick")
    public ResponseEntity kickUserGroupUser(@Valid @RequestBody GroupBossType gr, @RequestHeader("Authorization") String authorizationHeader) throws IOException {
        try {
            String userId_boss = JwtTokenProvider.getIDByBearer(authorizationHeader).getSubject();
            String groupId = gr.getGroupId();
            String userId = gr.getUserId();
            Groups isBoss = groupUserService.findByIdAndUserId(Integer.parseInt(groupId), Integer.parseInt(userId_boss));
            if (isBoss != null) {
                boolean friend = groupUserService.outGroupUser(Integer.parseInt(groupId), Integer.parseInt(userId));
                if (friend) {
                    jsonRes.setRes(true, "Đuổi thành viên thành công");
                    return ResponseEntity.status(HttpStatus.OK).body(ParseJSon(jsonRes));
                }
                return StatusUntilIndex.showMissing();
            }
            return StatusUntilIndex.showNotAuthorized();
        } catch (NumberFormatException e) {
            return StatusUntilIndex.showInternal(e);
        }
    }

    @DeleteMapping()
    public ResponseEntity outGroupUser(@Valid @RequestBody GroupUserType gr,
            @RequestHeader("Authorization") String authorizationHeader) throws IOException {
        try {
            String groupId = gr.getGroupId();
            String userId = JwtTokenProvider.getIDByBearer(authorizationHeader).getSubject();
            boolean friend = groupUserService.outGroupUser(Integer.parseInt(groupId),Integer.parseInt(userId));
            if (friend) {
                jsonRes.setRes(true, "Rời nhóm thành công");
                return ResponseEntity.status(HttpStatus.OK).body(ParseJSon(jsonRes));
            }
            return StatusUntilIndex.showMissing();
        } catch (NumberFormatException e) {
            return StatusUntilIndex.showInternal(e);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
