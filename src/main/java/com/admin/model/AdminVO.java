package com.admin.model;


import jakarta.persistence.*;

@Entity
@Table(name = "admin")
public class AdminVO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    public AdminVO(){

    }

    @Id
    @Column(name = "admin_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer adminId;

    @Column(name = "account")
    private String account;

    @Column(name = "password")
    private String password;

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
