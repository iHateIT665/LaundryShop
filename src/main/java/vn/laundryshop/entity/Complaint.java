package vn.laundryshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "Complaints")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complaintId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String image;

    @Column(name = "admin_response", columnDefinition = "TEXT")
    private String adminResponse;

    @Column(length = 50)
    private String status; // PENDING, RESPONDED

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", updatable = false)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "response_at")
    private Date responseAt;

    // Liên kết với User (Khách hàng)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Liên kết với Order (Có thể null nếu khiếu nại chung)
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    
    @Transient
    public String getImagePath() {
        if (image == null || complaintId == null) return null;
        return "/uploads/complaints/" + complaintId + "/" + image;
    }
}