package com.example.pizza_backend.api.controller;


import com.example.pizza_backend.api.dto.request.LoginRequest;
import com.example.pizza_backend.api.dto.request.ProfileRequest;
import com.example.pizza_backend.api.dto.response.ProfileResponse;
import com.example.pizza_backend.auth.JwtService;
import com.example.pizza_backend.persistence.entity.Profile;
import com.example.pizza_backend.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    private final JwtService jwtService;
    private final ProfileService profileService;

    @Autowired
    public ProfileController(JwtService jwtService, ProfileService profileService) {
        this.jwtService = jwtService;
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(HttpServletRequest request){
        String username = (String) request.getAttribute("username");
        Number roleNum = (Number) request.getAttribute("profile_role");
        Number idNum = (Number) request.getAttribute("profile_id");

        Integer role = roleNum != null ? roleNum.intValue() : null;
        Long id = idNum != null ? idNum.longValue() : null;

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("username", username);
        response.put("role", role);
        response.put("id", id);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/login")
    public ResponseEntity<?> signIn(@RequestBody LoginRequest req) {

        Optional<Profile> userOpt = profileService.checkLogIn(req);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "incorrect username or password"));
        }

        Profile user = userOpt.get();
        //เตรียม payload
        Map<String, Object> payload = Map.of(
                "profile_id", user.getProfileId(),
                "username", user.getUsername(),
                "profile_role", user.getProfileRole()
        );
        //สร้าง token
        String token = jwtService.generateToken(payload);
        //ใส่ cookie
        ResponseCookie cookie = ResponseCookie.from("tokenpizza", token)
                .httpOnly(true)
                .secure(false) // เปลี่ยนเป็น true ตอน production
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(30))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "success"));
    }

    @PostMapping("/user/signup")
    public ResponseEntity<?> userSignUp(@RequestBody ProfileRequest req) {
        if (profileService.checkDuplicateProfile(req)){
            return ResponseEntity.status(401).body(Map.of("message", "username already exists"));
        }
        String tokenUserSighUp = profileService.createProfileWithAddress(req,1);
        ResponseCookie cookie = ResponseCookie.from("tokenpizza", tokenUserSighUp)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofDays(30))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "signup success"));
    }

    @PostMapping("/admin/signup")
    public ResponseEntity<?> adminSighIn(@RequestBody ProfileRequest req) {
        if (profileService.checkDuplicateProfile(req)){
            return ResponseEntity.status(401).body(Map.of("message", "username already exists"));
        }
        String tokenUserSighUp = profileService.createProfileWithAddress(req,2);
        ResponseCookie cookie = ResponseCookie.from("tokenpizza", tokenUserSighUp)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofDays(30))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "signup success"));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        // สร้าง cookie ใหม่ชื่อเดียวกัน แต่ตั้ง maxAge = 0 เพื่อลบทิ้ง
        ResponseCookie cookie = ResponseCookie.from("tokenpizza", null)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "log out success"));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(
            HttpServletRequest request,
            @RequestBody ProfileRequest profileRequest){
        Long profileId = (Long) request.getAttribute("profile_id");
        String createLog = profileService.updateProfile(profileRequest,profileId);
        if (createLog == "success") {
            return  ResponseEntity.ok()
                    .body(Map.of("message", "update success"));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/list")
    public ResponseEntity<?> getProfile(HttpServletRequest request){
        Long profileId = (Long) request.getAttribute("profile_id");
        ProfileResponse profile = profileService.getProfileById(profileId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("profile", profile);

        return ResponseEntity.ok(response);
    }
}
