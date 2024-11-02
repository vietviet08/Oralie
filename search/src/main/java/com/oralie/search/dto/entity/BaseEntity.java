//package com.oralie.search.dto.entity;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//import org.springframework.data.annotation.CreatedBy;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.annotation.LastModifiedBy;
//import org.springframework.data.annotation.LastModifiedDate;
//
//import java.time.LocalDateTime;
//
//@MappedSuperclass
//@EntityListeners(AuditingEntityListener.class)
//@Getter
//@Setter
//@ToString
//public class BaseEntity {
//
//    @CreatedDate
//    @Column(updatable = false)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
//    private LocalDateTime createdAt;
//
//    @CreatedBy
//    @Column(updatable = false)
//    private String createdBy;
//
//    @LastModifiedDate
//    @Column(updatable = false)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
//    private LocalDateTime updatedAt;
//
//    @LastModifiedBy
//    @Column(updatable = false)
//    private String updatedBy;
//}