package com.sympnet.app.api.models;

public class RegisterPatientRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String phoneNumber;
    private String gender;

    public RegisterPatientRequest(String email, String password,
                                  String firstName, String lastName, String dateOfBirth,
                                  String phoneNumber, String gender) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getGender() { return gender; }
}