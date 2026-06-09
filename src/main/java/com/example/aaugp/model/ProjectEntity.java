package com.example.aaugp.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable =  = false)
    private Number year;

    @Column(nullable = false, unique = true)
    private String description;

    @Column(nullable = false, unique = true)
    private List<String> links;

    @Column(nullable = false, unique = true)
    private List<String> images;

    @Column(nullable = false, unique = true)
    private Number star;

    @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private UserEntity user;  
    

@OneToMany(fetch = FetchType.LAZY)
@JoinColumn(name = "project_id")
private CommentEntity comment;


@OneToMany(fetch = FetchType.LAZY)
@JoinColumn(name = "project_id")
private DepartmentEntity department;


}
