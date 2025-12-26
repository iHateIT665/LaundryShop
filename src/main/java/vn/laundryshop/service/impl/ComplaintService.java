package vn.laundryshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.laundryshop.entity.Complaint;
import vn.laundryshop.entity.User;
import vn.laundryshop.repository.IComplaintRepository;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final IComplaintRepository complaintRepo;

    public List<Complaint> getAllComplaints() {
        return complaintRepo.findAllByOrderByCreatedAtDesc();
    }

    public List<Complaint> getComplaintsByUser(User user) {
        return complaintRepo.findByUserOrderByCreatedAtDesc(user);
    }

    public void saveClientComplaint(Complaint complaint) {
        complaint.setCreatedAt(new Date());
        complaint.setStatus("PENDING");
        complaintRepo.save(complaint);
    }

    public void adminResponse(Long id, String response) {
        Complaint c = complaintRepo.findById(id).orElse(null);
        if (c != null) {
            c.setAdminResponse(response);
            c.setResponseAt(new Date());
            c.setStatus("RESPONDED");
            complaintRepo.save(c);
        }
    }
    
    public Complaint findById(Long id) {
        return complaintRepo.findById(id).orElse(null);
    }
}