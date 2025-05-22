package com.themoneywallet.historyservice.entity;

import java.time.LocalDateTime;

import com.themoneywallet.historyservice.event.EventType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "history")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String sourceService;     
    private EventType eventType;        

    @Column(columnDefinition = "JSONB")
    private String eventData;       
    
    private String userId;            
    private LocalDateTime timestamp;  
    
   
    private String category;         
    private String status;            
    
}