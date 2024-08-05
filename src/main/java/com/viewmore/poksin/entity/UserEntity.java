package com.viewmore.poksin.entity;

import com.viewmore.poksin.dto.user.UpdateUserDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends MainUserEntity{

    // 전화번호
    private String phoneNum;
    // 긴급 연락처
    private String emergencyNum;
    // 주소
    private String address;
    // 전화번호 공개 비공개 여부
    private Boolean phoneOpen;
    // 긴급 연락처 공개 비공개 여부
    private Boolean emergencyOpen;
    // 주소 공개 비공개 여부
    private Boolean addressOpen;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<EvidenceEntity> evidences = new ArrayList<>();

    @Builder(builderMethodName = "userEntityBuilder")
    public UserEntity(String username, String password, String phoneNum, String emergencyNum, String address, boolean phoneOpen, boolean emergencyOpen, boolean addressOpen, String role) {
        super(username, password, role);
        this.phoneNum = phoneNum;
        this.emergencyNum = emergencyNum;
        this.address = address;
        this.phoneOpen = phoneOpen;
        this.emergencyOpen = emergencyOpen;
        this.addressOpen = addressOpen;
    }

    public void updateUser(UpdateUserDTO updateUserDTO) {
        this.address = updateUserDTO.getAddress() == null ? this.address : updateUserDTO.getAddress();
        this.emergencyNum = updateUserDTO.getEmergencyNum() == null ? this.emergencyNum : updateUserDTO.getEmergencyNum();
        this.phoneNum = updateUserDTO.getPhoneNum() == null ? this.phoneNum : updateUserDTO.getPhoneNum();

        this.phoneOpen = updateUserDTO.getPhoneOpen() == null ? this.phoneOpen : updateUserDTO.getPhoneOpen();
        this.emergencyOpen = updateUserDTO.getEmergencyOpen() == null ? this.emergencyOpen : updateUserDTO.getEmergencyOpen();
        this.addressOpen = updateUserDTO.getAddressOpen() == null ? this.addressOpen : updateUserDTO.getAddressOpen();
    }

}
