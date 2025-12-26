package vn.laundryshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StatsDTO {
    private String label;
    private Long value;

    // Constructor "bắt tất cả": Tự động ép kiểu về String và Long chuẩn
    public StatsDTO(Object label, Number value) {
        this.label = (label != null) ? String.valueOf(label) : "";
        this.value = (value != null) ? value.longValue() : 0L;
    }
}