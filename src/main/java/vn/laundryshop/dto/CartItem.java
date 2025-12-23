package vn.laundryshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private Long priceId;       // ID của bảng giá (để truy ra dịch vụ + loại đồ)
    private String serviceName; // Tên dịch vụ (lưu tạm để hiển thị)
    private String typeName;    // Tên loại đồ
    private Double unitPrice;   // Đơn giá
    private String unit;        // Đơn vị (Kg, Cái...)
    private Integer quantity;   // Số lượng khách chọn

    public Double getSubtotal() {
        return unitPrice * quantity;
    }
}