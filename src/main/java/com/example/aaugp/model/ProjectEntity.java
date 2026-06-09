package com.example.aaugp.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class ProjectEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable =  false)
    private Integer graduationYear;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, unique = true)
     private String githubLink;

    @Column(nullable = false, unique = true)
    private String imageUrl;

@Enumerated(EnumType.STRING)
    private Status status;


    @Column(nullable = false, unique = true)
     private String demoLink;

    @Column(nullable = false, unique = true)
    private Integer starCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private UserEntity user;  
    

@OneToMany(fetch = FetchType.LAZY)
@JoinColumn(name = "project_id")
  private List<CommentEntity> comments;


@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "project_id")
private DepartmentEntity department;


}
