package vn.laundryshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "order_details")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailId;

    // Liên kết ngược về Order
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude // Tránh vòng lặp vô tận khi in log
    private Order order;

    // Liên kết sang Bảng Giá (để biết là giặt cái gì)
    @ManyToOne
    @JoinColumn(name = "price_id", nullable = false)
    private PriceList priceList;

    private Float quantity;
    private Double unitPrice;
    private Double subtotal;
}