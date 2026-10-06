package lk.customsebid.userauth.dto;

import jakarta.validation.constraints.NotBlank;

public class RoleAssignmentRequest {

    @NotBlank(message = "Role name is required")
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
