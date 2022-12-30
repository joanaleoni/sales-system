package model.domain;

import java.time.LocalDate;

/**
 *
 * @author joana
 */
public class Client {
    private int id;
    private String name;
    private String cpf;
    private String phone;
    private String adress;
    private LocalDate birthDate;

    public Client() {}

    public Client(String name, String cpf, String phone, String adress, LocalDate birthDate) {
        this.name = name;
        this.cpf = cpf;
        this.phone = phone;
        this.adress = adress;
        this.birthDate = birthDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return name;
    }    
}