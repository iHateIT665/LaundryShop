package vn.laundryshop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.laundryshop.config.CustomUserDetails;
import vn.laundryshop.entity.User;
import vn.laundryshop.service.impl.ComplaintService;

@Controller
@RequestMapping("/admin/complaints")
@RequiredArgsConstructor
public class AdminComplaintController {

    private final ComplaintService complaintService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("complaints", complaintService.getAllComplaints());
        return "admin/complaint/list";
    }

    @PostMapping("/respond")
    public String respond(@RequestParam("id") Long id, 
                          @RequestParam("response") String response) {
        complaintService.adminResponse(id, response);
        return "redirect:/admin/complaints";
    }
   
}