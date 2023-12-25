package com.mycompany.CafeManagementSystem.Entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@NamedQuery(name = "Bill.getAllBills", query = "SELECT b FROM Bill b ORDER BY b.id DESC")
@NamedQuery(name = "Bill.getAllBillsByUser", query = "SELECT b FROM Bill b WHERE b.createdBy = :username ORDER BY b.id DESC")

@Data
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "bill")
public class Bill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "contactNumber")
    private String contactNumber;

    @Column(name = "paymentMethod")
    private String paymentMethod;

    @Column(name = "total")
    private Double total;

    @Column(name = "productDetails", columnDefinition = "JSON")
    private String productDetails;

    @Column(name = "createdBy")
    private String createdBy;
}
