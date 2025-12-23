package vn.laundryshop.controller.admin;

import vn.laundryshop.entity.LaundryService;
import vn.laundryshop.service.ILaundryServiceService;
import vn.laundryshop.util.FileUploadUtil; // Import class tiện ích lưu file
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/admin/services")
public class ServiceController {

    @Autowired
    private ILaundryServiceService serviceLaundry;

    @GetMapping
    public String listServices(Model model) {
        model.addAttribute("services", serviceLaundry.getAllServices());
        return "admin/service/service-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("service", new LaundryService());
        return "service-form";
    }

    // --- SỬA LOGIC LƯU (Có xử lý ảnh) ---
    @PostMapping("/save")
    public String saveService(@ModelAttribute("service") LaundryService service,
                              @RequestParam("imageFile") MultipartFile multipartFile) throws IOException {
        
        // 1. Lưu thông tin text trước để có ID (nếu là thêm mới)
        // Tuy nhiên với JPA, save trả về object đã có ID
        LaundryService savedService = serviceLaundry.saveService(service);

        // 2. Xử lý file ảnh nếu có upload
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            savedService.setImage(fileName);
            serviceLaundry.saveService(savedService); // Cập nhật lại tên ảnh vào DB

            // Lưu file vật lý vào thư mục uploads/services/{id}/
            String uploadDir = "uploads/services/" + savedService.getServiceId();
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }

        return "redirect:/admin/services";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        LaundryService service = serviceLaundry.findServiceById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid service Id:" + id));
        model.addAttribute("service", service);
        return "service-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteService(@PathVariable("id") Long id) {
        serviceLaundry.deleteService(id);
        return "redirect:/admin/services";
    }
}