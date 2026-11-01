package com.AL_Tayep.BookStore.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

@MappedSuperclass 
@Data
@NoArgsConstructor 
@AllArgsConstructor 
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class GrandClass {

@Id 
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;     
@CreatedDate
private LocalDateTime createdAt;

@LastModifiedDate
private LocalDateTime lastModify;
}
