package com.example.api_crud.service;

import com.example.api_crud.entity.UserDetailsEntity;
import com.example.api_crud.entity.UserEntity;
import com.example.api_crud.model.request.UserDetailsRequest;
import com.example.api_crud.model.response.UserDetailsResponse;
import com.example.api_crud.repository.UserDetailsRepository;
import com.example.api_crud.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserDetailsService {

    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final FileStorageService fileStorageService;

    public UserDetailsService(UserRepository userRepository,
                             UserDetailsRepository userDetailsRepository,
                             FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.fileStorageService = fileStorageService;
    }

    public ResponseEntity<LinkedHashMap<String, Object>> getUserDetails(UUID userUuid) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            // Cek user exists
            Optional<UserEntity> userOpt = userRepository.findByUuid(userUuid);
            if (userOpt.isEmpty()) {
                response.put("status", "error");
                response.put("message", "User tidak ditemukan");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Cek details exists
            Optional<UserDetailsEntity> detailsOpt = userDetailsRepository.findByUser_Uuid(userUuid);
            if (detailsOpt.isEmpty()) {
                response.put("status", "success");
                response.put("message", "Belum ada data detail");
                return ResponseEntity.ok(response);
            }

            // Convert to response
            UserDetailsResponse detailsResponse = convertToResponse(detailsOpt.get());
            response.put("status", "success");
            response.put("data", detailsResponse);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Gagal mengambil data detail: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    public ResponseEntity<LinkedHashMap<String, Object>> updateUserDetails(
            UUID userUuid, 
            UserDetailsRequest request,
            MultipartFile photo) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            // Validasi user
            Optional<UserEntity> userOpt = userRepository.findByUuid(userUuid);
            if (userOpt.isEmpty()) {
                response.put("status", "error");
                response.put("message", "User tidak ditemukan");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            UserEntity user = userOpt.get();

            // Cari atau buat detail baru
            UserDetailsEntity userDetails = userDetailsRepository.findByUser_Uuid(userUuid)
                    .orElse(new UserDetailsEntity());

            // Update data
            if (request.getBirthdate() != null) {
                userDetails.setBirthdate(LocalDate.parse(request.getBirthdate()));
            }
            userDetails.setAddress(request.getAddress());
            userDetails.setHobby(request.getHobby());
            userDetails.setPhoneNumber(request.getPhoneNumber());

            // Handle upload foto
            if (photo != null && !photo.isEmpty()) {
                // Hapus foto lama jika ada
                if (userDetails.getPhotoPath() != null) {
                    fileStorageService.deleteFile(userDetails.getPhotoPath());
                }
                // Simpan foto baru
                String fileName = fileStorageService.storeFile(photo);
                userDetails.setPhotoPath(fileName);
            }

            // Set relasi user
            userDetails.setUser(user);
            
            // Simpan ke database
            userDetailsRepository.save(userDetails);

            // Response
            response.put("status", "success");
            response.put("message", "Detail user berhasil diperbarui");
            response.put("data", convertToResponse(userDetails));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Gagal memperbarui detail: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    private UserDetailsResponse convertToResponse(UserDetailsEntity entity) {
        UserDetailsResponse response = new UserDetailsResponse();
        response.setBirthdate(entity.getBirthdate());
        response.setAddress(entity.getAddress());
        response.setHobby(entity.getHobby());
        response.setPhoneNumber(entity.getPhoneNumber());
        response.setPhotoUrl(entity.getPhotoPath() != null ? 
            "/uploads/user-photos/" + entity.getPhotoPath() : 
            null);
        return response;
    }
}