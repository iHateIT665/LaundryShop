package vn.laundryshop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.laundryshop.entity.ClothingType;
import vn.laundryshop.service.impl.ClothingTypeService;
import vn.laundryshop.util.FileUploadUtil;

import java.io.IOException;

@Controller
@RequestMapping("/admin/types")
@RequiredArgsConstructor
public class ClothingTypeController {

    private final ClothingTypeService typeService;

    @GetMapping
    public String listTypes(Model model) {
        model.addAttribute("types", typeService.getAllTypes());
        return "admin/type/type-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("type", new ClothingType());
        return "admin/type/type-form";
    }

    // --- SỬA LOGIC LƯU ---
    @PostMapping("/save")
    public String saveType(@ModelAttribute ClothingType type,
                           @RequestParam("imageFile") MultipartFile multipartFile) throws IOException {
        
        ClothingType savedType = typeService.save(type);

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            savedType.setImage(fileName);
            typeService.save(savedType);

            String uploadDir = "uploads/types/" + savedType.getTypeId();
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }
        
        return "redirect:/admin/types";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ClothingType type = typeService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Id:" + id));
        model.addAttribute("type", type);
        return "admin/type/type-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteType(@PathVariable Long id) {
        typeService.delete(id);
        return "redirect:/admin/types";
    }
}