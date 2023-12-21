package com.xiilab.moduleuser.vo;

import java.util.List;
import java.util.Map;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserReqVO {
    @NotBlank(message = "firstName은 필수 값입니다.")
    private String firstName;
    @NotBlank(message = "lastName은 필수 값입니다.")
    private String lastName;
    @NotBlank(message = "email은 필수 값입니다.")
    @Email
    private String email;
    @NotBlank(message = "userName 필수 값입니다.")
    private String username;
    @NotBlank(message = "password 필수 값입니다.")
    @Size(min = 10, max = 16, message = "비밀번호는 10 ~ 16 사이 글자여야합니다.")
    @Pattern(regexp = "^[\\p{Punct}\\w]*$")
    private String password;

    public UserRepresentation convertUserRep() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(this.firstName);
        userRepresentation.setLastName(this.lastName);
        userRepresentation.setEmail(this.email);
        userRepresentation.setUsername(username);
        userRepresentation.setEnabled(false);
        userRepresentation.setAttributes(Map.of("approvalYN", List.of(String.valueOf(false))));
        return userRepresentation;
    }

    public CredentialRepresentation createCredentialRep() {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(this.password);
        credentialRepresentation.setTemporary(false);
        return credentialRepresentation;
    }
}
