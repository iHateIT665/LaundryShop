package vn.laundryshop.controller.admin;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.laundryshop.entity.PriceList;
import vn.laundryshop.repository.IPriceListRepository;
import vn.laundryshop.service.impl.ClothingTypeService;
import vn.laundryshop.service.ILaundryServiceService;
import vn.laundryshop.service.impl.PriceListService;

import java.util.List;

@Controller
@RequestMapping("/admin/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceListService priceService;
    private final IPriceListRepository priceRepo; // Inject thêm Repo
    private final ILaundryServiceService serviceLaundry;
    private final ClothingTypeService typeService;
    
    @GetMapping
    public String listPrices(Model model,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(required = false) String keyword) {
        
        // --- SỬA: Đổi 10 thành 3 để test phân trang (vì data mẫu chỉ có 6 dòng) ---
        Page<PriceList> pageResult = priceService.getPricesWithPaging(page, 5, keyword);

        model.addAttribute("prices", pageResult.getContent());
        model.addAttribute("pageData", pageResult);
        model.addAttribute("keyword", keyword);
        
        return "admin/price/price-list";
    }

    // ... Giữ nguyên các hàm add, save, edit, delete ...
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("priceList", new PriceList());
        model.addAttribute("services", serviceLaundry.getAllServices());
        model.addAttribute("types", typeService.getAllTypes());
        return "admin/price/price-form";
    }
    
    @PostMapping("/save")
    public String savePrice(@ModelAttribute PriceList priceList) {
        try {
            priceService.save(priceList);
        } catch (Exception e) {
            return "redirect:/admin/prices?error=duplicate";
        }
        return "redirect:/admin/prices";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        PriceList p = priceService.findById(id).orElseThrow();
        model.addAttribute("priceList", p);
        model.addAttribute("services", serviceLaundry.getAllServices());
        model.addAttribute("types", typeService.getAllTypes());
        return "admin/price/price-form";
    }

    @GetMapping("/delete/{id}")
    public String deletePrice(@PathVariable Long id) {
        priceService.delete(id);
        return "redirect:/admin/prices";
    }
}