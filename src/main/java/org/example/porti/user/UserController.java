package org.example.porti.user;


import org.example.porti.common.model.BaseResponse;
import org.example.porti.common.model.BaseResponseStatus;
import org.example.porti.user.model.AuthUserDetails;
import org.example.porti.user.model.UserDto;
import org.example.porti.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody UserDto.SignupReq dto, @RequestParam("type") String type) {
        if (type.equals("personal")) {
            UserDto.SignupRes result =  userService.signup(dto);
            return ResponseEntity.ok(BaseResponse.success(result));
        }
        else{
            UserDto.SignupRes result =  userService.companySignup(dto);
            return ResponseEntity.ok(BaseResponse.success(result));
        }

    }

    @PostMapping("/signup/company")
    public ResponseEntity companySignup(@RequestBody UserDto.SignupReq dto) {
        UserDto.SignupRes result =  userService.companySignup(dto);

        return ResponseEntity.ok(BaseResponse.success(result));
    }


    @PostMapping("/login")
    public ResponseEntity login( @RequestBody UserDto.LoginReq dto) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword(), null);

        Authentication authentication = authenticationManager.authenticate(token);
        AuthUserDetails user = (AuthUserDetails) authentication.getPrincipal();

        if(user != null) {
            String jwt = jwtUtil.createToken(user.getIdx(), user.getUsername(), user.getRole(), user.getNickname());
            return ResponseEntity.ok().header("Set-Cookie", "ATOKEN=" + jwt + "; Path=/").body(BaseResponse.success("성공"));
        }

        return ResponseEntity.ok(BaseResponse.fail(BaseResponseStatus.LOGIN_INVALID_USERINFO));
    }

    @PostMapping("/nonessential")
    public ResponseEntity editNonEssential(@RequestBody UserDto.EditNonEssentialReq dto, @AuthenticationPrincipal AuthUserDetails user) {
        userService.editNonEssential(dto,user);
        return ResponseEntity.ok(BaseResponse.success(BaseResponseStatus.SUCCESS));
    }

}