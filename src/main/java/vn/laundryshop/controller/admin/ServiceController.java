package vn.laundryshop.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.laundryshop.entity.LaundryService;
import vn.laundryshop.service.ILaundryServiceService;
import vn.laundryshop.service.impl.LaundryServiceServiceImpl;
import vn.laundryshop.util.FileUploadUtil;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ILaundryServiceService laundryService;

    private final LaundryServiceServiceImpl laundryServiceImpl; 

    @GetMapping
    public String listServices(Model model, 
                              @RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "keyword", required = false) String keyword) {
        Page<LaundryService> pageService;
        if (keyword != null && !keyword.isEmpty()) {
            pageService = laundryService.findByIsActiveTrueAndServiceNameContaining(keyword, PageRequest.of(page, 5));
            model.addAttribute("keyword", keyword);
        } else {
            pageService = laundryServiceImpl.getAllServices(page);
        }
        model.addAttribute("services", pageService.getContent());
        model.addAttribute("pageData", pageService);
        return "admin/service/service-list";
    }

    @GetMapping("/add")
    public String showCreateForm(Model model) {
        model.addAttribute("laundryService", new LaundryService());
        return "admin/service/service-form"; // Lưu ý: file này trong thư mục admin/service/
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        LaundryService service = laundryService.findServiceById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Id:" + id));
        model.addAttribute("laundryService", service);
        return "admin/service/service-form";
    }

    @PostMapping("/save")
    public String saveService(@Valid @ModelAttribute("laundryService") LaundryService service,
                              BindingResult bindingResult,
                              @RequestParam("imageFile") MultipartFile multipartFile,
                              Model model) throws IOException {

        // 1. Kiểm tra lỗi Validation
        if (bindingResult.hasErrors()) {
            return "admin/service/service-form"; // Quay lại form nếu lỗi
        }

        // 2. Xử lý file ảnh
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        if (!fileName.isEmpty()) {
            service.setImage(fileName);
            LaundryService savedService = laundryService.saveService(service);
            String uploadDir = "uploads/services/" + savedService.getServiceId();
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            // Giữ ảnh cũ nếu đang edit mà không chọn ảnh mới
            if (service.getServiceId() != null) {
                LaundryService oldService = laundryService.findServiceById(service.getServiceId()).orElse(null);
                if (oldService != null) service.setImage(oldService.getImage());
            }
            laundryService.saveService(service);
        }

        return "redirect:/admin/services";
    }

    @GetMapping("/delete/{id}")
    public String deleteService(@PathVariable Long id) {
        laundryService.deleteService(id);
        return "redirect:/admin/services";
    }
}